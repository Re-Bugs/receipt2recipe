package com.receipt2recipe.r2r.controller;

import com.receipt2recipe.r2r.dto.LoginMemberDTO;
import com.receipt2recipe.r2r.dto.SignUpMemberDTO;
import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/sign_in")
    public String showLoginForm(Model model) {
        model.addAttribute("loginMemberDto", new LoginMemberDTO());
        return "login/sign_in";
    }

    @PostMapping("/sign_in")
    public String login(@ModelAttribute LoginMemberDTO loginMemberDto, RedirectAttributes redirectAttributes, HttpSession session) {
        if (memberService.login(loginMemberDto)) {
            Member member = memberService.findByUserEmail(loginMemberDto.getUserEmail()).orElseThrow();
            session.setAttribute("user", member);
            redirectAttributes.addFlashAttribute("userEmail", member.getUserEmail());
            redirectAttributes.addFlashAttribute("userName", member.getUserName());
            return "redirect:/main";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인에 실패했습니다. 다시 시도해 주세요.");
            return "redirect:/sign_in";
        }
    }

    @GetMapping("/sign_up")
    public String showRegisterForm(Model model) {
        model.addAttribute("signUpMemberDto", new SignUpMemberDTO());
        return "login/sign_up";
    }

    @PostMapping("/sign_up")
    public String handleRegister(@ModelAttribute SignUpMemberDTO signUpMemberDto, RedirectAttributes redirectAttributes) {
        Member member = new Member(
                signUpMemberDto.getUserEmail(),
                signUpMemberDto.getUserName(),
                signUpMemberDto.getUserPw(),
                signUpMemberDto.getUserPhone()
        );

        if (memberService.signUp(member)) {
            redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다.");
            return "redirect:/sign_in";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "회원가입에 실패했습니다. 다시 시도해 주세요.");
            return "redirect:/sign_up";
        }
    }

    @PostMapping("/sign_out")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "redirect:/sign_in";
    }

    @GetMapping("/sign_out")
    public String signOutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/sign_in";
    }
}
