package org.pkwmtt.studentCodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.exceptions.WrongArgumentException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.exams.entity.StudentCode;
import org.pkwmtt.calendar.exams.entity.Representative;
import org.pkwmtt.calendar.exams.repository.SuperiorGroupRepository;
import org.pkwmtt.calendar.exams.repository.RepresentativeRepository;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.mail.EmailService;
import org.pkwmtt.mail.dto.MailDTO;
import org.pkwmtt.security.jwt.JwtService;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.pkwmtt.studentCodes.repository.StudentCodeRepository;
import org.pkwmtt.security.authentication.JwtAuthenticationService;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.timetable.TimetableService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentCodeService {
    private final StudentCodeRepository studentCodeRepository;
    private final RepresentativeRepository representativeRepository;
    private final SuperiorGroupRepository superiorGroupRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final TimetableService timetableService;
    
    /**
     * Generate authentication tokens for a user that provides a valid student code.
     * The method checks the code existence and format, verifies usage limits,
     * maps the code to a representative and returns JWT tokens for that representative.
     *
     * @param code student code string (expected format validated by {@link #validateCode(String)})
     * @return {@link JwtAuthenticationDto} containing access and refresh tokens
     * @throws StudentCodeNotFoundException           if the code does not exist in the repository
     * @throws WrongStudentCodeFormatException        if the code does not match expected format
     * @throws UserNotFoundException                  if no representative is associated with the code's group
     * @throws MaxUsageForStudentCodeReachedException if the code's usage reached its usage limit
     */
    public JwtAuthenticationDto generateTokenForUser (String code)
      throws StudentCodeNotFoundException, WrongStudentCodeFormatException, UserNotFoundException, MaxUsageForStudentCodeReachedException {
        var codeEntity = this.getEntityByCode(code);
        
        checkUsageLimit(codeEntity);
        
        var representative = findRepresentativeForCode(codeEntity);
        
        var jwtDto = createTokensForRepresentative(representative);
        
        increaseUsage(code);
        
        return jwtDto;
    }
    
    /**
     * Validate that the provided code entity has not exceeded its usage limit.
     *
     * @param codeEntity {@link StudentCode} entity to check
     * @throws MaxUsageForStudentCodeReachedException when usage is >= usageLimit
     */
    private void checkUsageLimit (StudentCode codeEntity) throws MaxUsageForStudentCodeReachedException {
        if (codeEntity.getUsage() >= codeEntity.getUsageLimit()) {
            throw new MaxUsageForStudentCodeReachedException("This code has reached its maximum usage limit.");
        }
    }
    
    /**
     * Find the representative assigned to the superior group referenced by the student code.
     *
     * @param codeEntity {@link StudentCode} that contains a reference to {@link SuperiorGroup}
     * @return {@link Representative} associated with the group
     * @throws UserNotFoundException if no representative is assigned to the group
     */
    private Representative findRepresentativeForCode (StudentCode codeEntity) throws UserNotFoundException {
        return representativeRepository
          .findBySuperiorGroup(codeEntity.getSuperiorGroup())
          .orElseThrow(() -> new UserNotFoundException("No representative is assigned to this code."));
    }
    
    /**
     * Create access and refresh tokens for the provided representative.
     *
     * @param representative the representative for whom tokens will be issued
     * @return {@link JwtAuthenticationDto} containing access and refresh tokens
     */
    private JwtAuthenticationDto createTokensForRepresentative (Representative representative) {
        var accessToken = jwtService.generateAccessToken(representative);
        var refreshToken = jwtAuthenticationService.getNewUserRefreshToken(representative);
        
        return JwtAuthenticationDto
          .builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .build();
    }
    
    /**
     * Increment usage counter for the student code identified by the code string.
     *
     * @param code code to increment usage for
     */
    private void increaseUsage (String code) {
        studentCodeRepository.increaseUsageByCode(code);
    }
    
    /**
     * Send student codes for multiple requests. This method processes each {@link StudentCodeRequest}
     * independently and collects failures (per-request) into a list of {@link SendStudentCodeFailure}.
     *
     * @param requests list of requests to process
     * @return list of failures encountered while processing requests; empty list indicates all succeeded
     */
    public List<SendStudentCodeFailure> sendStudentCode (List<StudentCodeRequest> requests) {
        // Collect per-group failures and return them to the caller so they can decide what to do.
        var failures = new java.util.ArrayList<SendStudentCodeFailure>();
        for (StudentCodeRequest request : requests) {
            try {
                sendStudentCode(request);
            } catch (Exception e) {
                String group = request.getSuperiorGroupName();
                String reason = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                reason = reason.replaceAll("\\r?\\n", " ");
                failures.add(new SendStudentCodeFailure(group, reason, e.getClass().getSimpleName()));
            }
        }
        
        return failures;
    }
    
    /**
     * Send a student code to a single {@link StudentCodeRequest}. This method:
     * - generates a new unique code,
     * - validates the provided general group name,
     * - ensures a superior group exists (creates if missing),
     * - ensures the email is not already assigned to another representative,
     * - assigns a representative to the group, saves the code and representative,
     * - sends out an email with the code.
     *
     * @param request request containing recipient email, group name and mail template
     * @throws MailCouldNotBeSendException            when sending the email fails
     * @throws WrongArgumentException                 when provided group name format is invalid
     * @throws SpecifiedSubGroupDoesntExistsException when subgroup is specified instead of general group
     * @throws IllegalArgumentException               for other invalid arguments
     * @throws JsonProcessingException                when timetable service fails to provide the group list
     */
    public void sendStudentCode (StudentCodeRequest request)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedSubGroupDoesntExistsException, IllegalArgumentException, JsonProcessingException {
        var code = generateNewCode();
        var mail = createMail(request, code);
        var groupName = request.getSuperiorGroupName();
        
        validateGroupNameFormat(groupName);
        
        if (!generalGroupExists(groupName)) { // Check if general group with provided name exists
            throw new SpecifiedGeneralGroupDoesntExistsException();
        }
        
        var superiorGroup = findOrCreateSuperiorGroup(groupName);
        ensureNoExistingRepresentativeByEmail(request.getEmail());
        
        var representative = buildRepresentative(request.getEmail(), superiorGroup.get());
        replaceExistingRepresentativeForGroup(superiorGroup.get());
        representativeRepository.save(representative);
        studentCodeRepository.save(new StudentCode(code, superiorGroup.get()));
        
        sendEmailOrThrow(mail, groupName);
    }
    
    /**
     * Validate that the provided group name is formatted as a general group (not subgroup).
     *
     * @param groupName name to validate
     * @throws WrongArgumentException when the name appears to include subgroup suffix (ends with digit)
     */
    private void validateGroupNameFormat (String groupName) throws WrongArgumentException {
        var groupNameLength = groupName.length();
        if (groupNameLength > 3 && Character.isDigit(
          groupName.charAt(groupNameLength - 1))) { //Check general group name
            throw new WrongArgumentException(
              "Wrong general group provided. Make sure you are not providing subgroup. (f.e 12K1 -> wrong, 12K -> good)");
        }
    }
    
    /**
     * Find an existing {@link SuperiorGroup} by name or create and persist a new one.
     * If a student code already exists for the found group it will be removed to avoid duplicates.
     *
     * @param groupName name of the superior group
     * @return {@link Optional} containing the found or newly created {@link SuperiorGroup}
     */
    private Optional<SuperiorGroup> findOrCreateSuperiorGroup (String groupName) {
        var superiorGroup = superiorGroupRepository.findByName(groupName);
        if (superiorGroup.isPresent()) {
            if (studentCodeRepository.existsBySuperiorGroup(superiorGroup.get())) {
                studentCodeRepository.deleteBySuperiorGroup(superiorGroup.get());
            }
            return superiorGroup;
        } else {
            return Optional.of(superiorGroupRepository.save(new SuperiorGroup(null, groupName)));
        }
    }
    
    /**
     * Ensure that no other representative is already registered with the provided email.
     *
     * @param email email address to check
     * @throws UserAlreadyAssignedException when another representative exists for the email
     */
    private void ensureNoExistingRepresentativeByEmail (String email) {
        var representativeByEmail = representativeRepository.findByEmail(email);
        if (representativeByEmail.isPresent()) {
            throw new UserAlreadyAssignedException(
              "Representative with email: " + email + " already has assigned different group.");
        }
    }
    
    /**
     * Send the email using {@link EmailService}. Wrapes low-level {@link MessagingException}
     * into a domain-specific {@link MailCouldNotBeSendException}.
     *
     * @param mail      mail DTO to be sent
     * @param groupName group name used to provide contextual error message
     * @throws MailCouldNotBeSendException when underlying mail sending fails
     */
    private void sendEmailOrThrow (MailDTO mail, String groupName) throws MailCouldNotBeSendException {
        try {
            emailService.send(mail);
        } catch (MessagingException e) {
            throw new MailCouldNotBeSendException("Couldn't send mail for group: " + groupName);
        }
    }
    
    /**
     * Helper to build a {@link Representative} entity from provided email and group.
     *
     * @param email         representative email
     * @param superiorGroup group to assign to representative
     * @return constructed {@link Representative}
     */
    private Representative buildRepresentative (String email, SuperiorGroup superiorGroup) {
        return Representative
          .builder()
          .email(email)
          .superiorGroup(superiorGroup)
          .isActive(true)
          .build();
    }
    
    /**
     * Replace (delete) an existing representative assigned to the specified superior group.
     *
     * @param superiorGroup target group for which existing representative should be removed
     */
    private void replaceExistingRepresentativeForGroup (SuperiorGroup superiorGroup) {
        representativeRepository
          .findBySuperiorGroup(superiorGroup)
          .ifPresent(value -> representativeRepository.deleteRepresentativeByEmail(value.getEmail()));
    }
    
    
    /**
     * Retrieve {@link StudentCode} entity by its code string after validating its format.
     *
     * @param code code to lookup
     * @return {@link StudentCode} entity associated with code
     * @throws StudentCodeNotFoundException    when no entity is found
     * @throws WrongStudentCodeFormatException when provided code format is invalid
     */
    private StudentCode getEntityByCode (String code)
      throws StudentCodeNotFoundException, WrongStudentCodeFormatException {
        this.validateCode(code);
        
        Optional<StudentCode> result = studentCodeRepository.findByCode(code);
        
        if (result.isEmpty()) {
            throw new StudentCodeNotFoundException();
        }
        
        return result.get();
    }
    
    /**
     * Validate code length and allowed characters.
     *
     * @param code code string to validate
     * @throws WrongStudentCodeFormatException when code is not exactly 6 characters or contains invalid chars
     */
    private void validateCode (String code) throws WrongStudentCodeFormatException {
        if (code.length() != 6) {
            throw new WrongStudentCodeFormatException("Code should be 6 characters long.");
        }
        
        String regex = "^[A-Z0-9]{6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);
        
        if (!matcher.find()) {
            throw new WrongStudentCodeFormatException("Wrong format of provided code.");
        }
    }
    
    
    /**
     * Create a {@link MailDTO} to be sent for a generated student code.
     *
     * @param request original request containing recipient and mail message template
     * @param code    generated student code to be included in the mail
     * @return configured {@link MailDTO}
     */
    private MailDTO createMail (StudentCodeRequest request, String code) {
        return new MailDTO()
          .setTitle("Kod Starosty " + request.getSuperiorGroupName())
          .setRecipient(request.getEmail())
          .setDescription(request.getMailMessage(code));
    }
    
    /**
     * Generate a new unique 6-character alphanumeric code. The method loops until
     * a code not present in the repository is produced.
     *
     * @return newly generated unique code
     */
    private String generateNewCode () {
        String AVAILABLE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        
        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(AVAILABLE_CHARS.charAt(random.nextInt(AVAILABLE_CHARS.length())));
            }
        } while (studentCodeRepository.findByCode(code.toString()).isPresent());
        
        return code.toString();
    }
    
    /**
     * Check whether the provided general group name exists in the timetable service.
     * The timetable returns group strings which may include subgroup suffixes; this
     * method normalizes those to their general group names before checking.
     *
     * @param name general group name to verify
     * @return true when the general group exists; false otherwise
     * @throws JsonProcessingException when the timetable service cannot provide or parse group data
     */
    private boolean generalGroupExists (String name) throws JsonProcessingException {
        Set<String> list = timetableService
          .getGeneralGroupList()
          .stream()
          .map(item -> {
              var lastIndex = item.length() - 1;
              if (Character.isDigit(item.charAt(lastIndex))) {
                  return item.substring(0, lastIndex);
              }
              return item;
          }).collect(Collectors.toSet());
        
        return list.contains(name);
    }
    
}