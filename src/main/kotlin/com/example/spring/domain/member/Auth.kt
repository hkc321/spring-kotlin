package com.example.spring.domain.member

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class Auth {
    var id: String = ""

    @JsonIgnore
    var pw: String = ""


}