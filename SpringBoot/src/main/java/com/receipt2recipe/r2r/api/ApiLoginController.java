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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class ApiLoginController {
    private final MemberService memberService;

    @PostMapping("/sign_in")
    public ResponseEntity<String> signIn(@RequestBody LoginMemberDTO loginMemberDto, HttpSession session) {
        Optional<Member> memberOptional = memberService.findByUserEmail(loginMemberDto.getUserEmail());

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (member.getUserPw().equals(loginMemberDto.getUserPw())) {
                session.setAttribute("user", member);
                return ResponseEntity.ok("{\"message\": \"Login successful\"}");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid credentials\"}");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"User not found\"}");
        }
    }

    @PostMapping("/sign_up")
    public ResponseEntity<String> signUp(@RequestBody SignUpMemberDTO signUpMemberDto) {
        Member member = new Member(
                signUpMemberDto.getUserEmail(),
                signUpMemberDto.getUserName(),
                signUpMemberDto.getUserPw(),
                signUpMemberDto.getUserPhone()
        );
        if (memberService.signUp(member)) {
            return ResponseEntity.ok("{\"message\": \"Sign up successful\"}");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Sign up failed\"}");
        }
    }

    @PostMapping("/sign_out")
    public ResponseEntity<String> signOut(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("{\"message\": \"Sign out successful\"}");
    }

    @GetMapping("/check_session")
    public ResponseEntity<String> checkSession(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member != null) {
            String responseJson = String.format("{\"message\": \"Session is valid\", \"userEmail\": \"%s\"}", member.getUserEmail());
            return ResponseEntity.ok(responseJson);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Session is invalid\"}");
        }
    }
}
