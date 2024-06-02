package com.receipt2recipe.r2r.service;

import com.receipt2recipe.r2r.domain.Member;
import com.receipt2recipe.r2r.dto.LoginMemberDTO;
import com.receipt2recipe.r2r.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean login(LoginMemberDTO loginMemberDto) {
        Optional<Member> memberOptional = memberRepository.findByUserEmail(loginMemberDto.getUserEmail());
        return memberOptional.isPresent() && memberOptional.get().getUserPw().equals(loginMemberDto.getUserPw());
    }

    public boolean signUp(Member member) {
        if (memberRepository.existsById(member.getUserEmail())) {
            return false;
        }
        memberRepository.save(member);
        return true;
    }

    public Optional<Member> findByUserEmail(String userEmail) {
        return memberRepository.findByUserEmail(userEmail);
    }
}
