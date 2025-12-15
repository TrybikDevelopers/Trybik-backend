package org.pkwmtt.security.authorization;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.exams.repository.ExamRepository;
import org.pkwmtt.exceptions.NoSuchElementWithProvidedIdException;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static org.pkwmtt.calendar.exams.mapper.GroupMapper.extractSuperiorGroup;

//TODO: handle AccessDeniedException

@Service
@RequiredArgsConstructor
public class PreAuthorizationService {

    private final ExamRepository examRepository;

    /**
     * verifies if user has authorities to add new resource
     *
     * @param newGroups set of provided groups
     */
    public boolean verifyGroupPermissionsForNewResource(Set<String> newGroups) {
        String userGroup = getUserGroup();
        return extractSuperiorGroup(newGroups).equals(userGroup);
    }

    /**
     * verifies if user has authorities to modify existing resource
     * also check if resource exists
     *
     * @param examId id of existing resource
     * @throws NoSuchElementWithProvidedIdException when resource don't exist
     */
    public boolean verifyGroupPermissionsForExistingResource(Integer examId) throws NoSuchElementWithProvidedIdException {
        String userGroup = getUserGroup();
        Set<String> generalGroupsOfExam = examRepository.findGroupsByExamId(examId)
                .stream()
                .filter(group -> group.matches("^\\d.*"))
                .collect(Collectors.toSet());
        return extractSuperiorGroup(generalGroupsOfExam).equals(userGroup);
    }


    /**
     * verifies if user had authorities to replace existing resource with new one
     * also check if modified exam exists
     *
     * @param newGroups set of groups of new resource
     * @param examId    id of existing resource
     * @throws NoSuchElementWithProvidedIdException when resource don't exist
     */
    public boolean verifyGroupPermissionsForModifiedResource(Set<String> newGroups, Integer examId) throws NoSuchElementWithProvidedIdException {
        examRepository.findById(examId).orElseThrow(() -> new NoSuchElementWithProvidedIdException(examId));
        return verifyGroupPermissionsForNewResource(newGroups) && verifyGroupPermissionsForExistingResource(examId);
    }

    /**
     * @return superior group identifier (e.g. 12K) of currently authenticated user
     * @throws AccessDeniedException when user doesn't have assigned group
     */
    private String getUserGroup() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            String group = jwtAuthentication.getSuperiorGroup();
            if (group != null && !group.isBlank())
                return group;
        }
        throw new AccessDeniedException("You don't have permission to access this group");
    }
}