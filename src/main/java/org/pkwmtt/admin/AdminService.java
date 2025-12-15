package org.pkwmtt.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.moderator.entities.Moderator;
import org.pkwmtt.moderator.repositories.ModeratorRepository;
import org.pkwmtt.security.password.PasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final PasswordGenerator passwordGenerator;
    private final ModeratorRepository moderatorRepository;
    private final PasswordEncoder passwordEncoder;

    public String addModerator() {
        String rawPassword = passwordGenerator.generateUniquePassword(moderatorRepository, Moderator::getPassword, 8);
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        Moderator moderator = new Moderator(encryptedPassword);
        moderatorRepository.save(moderator);
        return rawPassword;
    }
}
