package com.receipt2recipe.r2r.api;

import com.receipt2recipe.r2r.dto.LoginMemberDTO;
import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.dto.SignUpMemberDTO;
import com.receipt2recipe.r2r.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class ApiLoginController {
    private final MemberService memberService;

    @PostMapping("/sign_in")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody LoginMemberDTO loginMemberDto, HttpSession session) {
        Optional<Member> memberOptional = memberService.findByUserEmail(loginMemberDto.getUserEmail());

        Map<String, String> response = new HashMap<>();

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (member.getUserPw().equals(loginMemberDto.getUserPw())) {
                session.setAttribute("user", member);
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/sign_up")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpMemberDTO signUpMemberDto) {
        Member member = new Member(
                signUpMemberDto.getUserEmail(),
                signUpMemberDto.getUserName(),
                signUpMemberDto.getUserPw(),
                signUpMemberDto.getUserPhone()
        );

        Map<String, String> response = new HashMap<>();

        if (memberService.signUp(member)) {
            response.put("message", "Sign up successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Sign up failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/sign_out")
    public ResponseEntity<Map<String, String>> signOut(HttpSession session) {
        session.invalidate();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sign out successful");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check_session")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();

        if (member != null) {
            response.put("message", "Session is valid");
            response.put("userEmail", member.getUserEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Session is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
