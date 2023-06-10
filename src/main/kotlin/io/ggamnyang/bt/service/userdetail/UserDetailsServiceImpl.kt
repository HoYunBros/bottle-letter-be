package io.ggamnyang.bt.service.userdetail

import io.ggamnyang.bt.domain.entity.User
import io.ggamnyang.bt.exception.UserNotFoundException
import io.ggamnyang.bt.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username)
            ?: throw UserNotFoundException(username)

        return UserDetailsAdapter(user)
    }
}
