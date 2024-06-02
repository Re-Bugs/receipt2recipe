package com.receipt2recipe.r2r.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginMemberDTO {
    private String userEmail;
    private String userPw;

    public LoginMemberDTO(){
    }
}
