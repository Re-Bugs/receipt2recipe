package com.receipt2recipe.r2r.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Member")
public class Member {

    @Id
    @Column(name = "user_email", nullable = false, length = 30)
    private String userEmail;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Column(name = "user_pw", nullable = false, length = 30)
    private String userPw;

    @Column(name = "user_phone", nullable = false, length = 30)
    private String userPhone;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Fridge fridge;

    public Member() {
    }

    public Member(String userEmail, String userName, String userPw, String userPhone) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPw = userPw;
        this.userPhone = userPhone;
        this.setFridge(new Fridge(this)); // Member 생성 시 Fridge도 함께 생성
    }

    public void setFridge(Fridge fridge) {
        this.fridge = fridge;
        if (fridge != null) {
            fridge.setMember(this);
        }
    }
}
