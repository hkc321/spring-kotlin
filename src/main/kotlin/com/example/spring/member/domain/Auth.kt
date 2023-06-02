package com.example.spring.member.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Auth {
    var memId:String = ""
    @JsonIgnore
    var memPw:String = ""



}