package com.receipt2recipe.r2r.repository;

import com.receipt2recipe.r2r.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByUserEmail(String userEmail);
}
