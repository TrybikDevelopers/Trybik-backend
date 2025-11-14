package org.pkwmtt.moderator.controller;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.moderator.ModeratorService;
import org.pkwmtt.moderator.dto.AuthDto;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.authentication.dto.RefreshRequestDto;
import org.pkwmtt.studentCodes.StudentCodeService;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${apiPrefix}/moderator")
@RequiredArgsConstructor
public class ModeratorController {
    
    private final ModeratorService moderatorService;
    private final StudentCodeService studentCodeService;
    
    //username currently not used
    @PostMapping("/authenticate")
    public ResponseEntity<JwtAuthenticationDto> authenticate (@RequestBody AuthDto auth) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(auth.getPassword()));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDto> refresh (@RequestBody RefreshRequestDto requestDto) {
        return ResponseEntity.ok(moderatorService.refresh(requestDto));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout (@RequestBody RefreshRequestDto requestDto) {
        moderatorService.logout(requestDto);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> addUsers (@RequestBody List<StudentCodeRequest> studentCodeRequests) {
        if (studentCodeRequests == null || studentCodeRequests.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var failures = studentCodeService.sendStudentCode(studentCodeRequests);
        return (failures == null || failures.isEmpty())
          ? ResponseEntity.noContent().build()
          : ResponseEntity.status(207).body(failures);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<Representative>> getAllUsers () {
        return ResponseEntity.ok(moderatorService.getUsers());
    }
}
