package com.example.spring.application.service.member

import com.example.spring.adapter.jpa.member.mapper.MemberJpaMapper
import com.example.spring.adapter.jpa.member.entity.MemberJpaEntity
import com.example.spring.application.port.out.member.MemberJpaPort
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val memberJpaPort: MemberJpaPort) : UserDetailsService {
    val memberJpaMapper = MemberJpaMapper.INSTANCE

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
        val member: MemberJpaEntity = memberJpaPort.findMemberByEmail(username)
            ?: throw UsernameNotFoundException("존재하지 않는 username 입니다.")

        return UserDetailsImpl(memberJpaMapper.toMember(member))
    }
}