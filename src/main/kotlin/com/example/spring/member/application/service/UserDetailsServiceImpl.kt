package com.example.spring.member.application.service

import com.example.spring.member.application.port.out.MemberPort
import com.example.spring.member.domain.Member
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl( private val memberPort: MemberPort): UserDetailsService {
    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the `UserDetails`
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never `null`)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     * GrantedAuthority
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val member: Member = memberPort.findMemberById(username)
            ?: throw UsernameNotFoundException("존재하지 않는 username 입니다.")

        return UserDetailsImpl(member)
    }
}