package com.receipt2recipe.r2r.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpMemberDTO {
    private String userEmail;
    private String userName;
    private String userPw;
    private String userPhone;

    public SignUpMemberDTO() {
    }

    public SignUpMemberDTO(String userEmail, String userName, String userPw, String userPhone) {
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.userName = userName;
        this.userPhone = userPhone;
    }
}