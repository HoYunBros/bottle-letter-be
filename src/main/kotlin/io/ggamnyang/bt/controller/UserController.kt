package io.ggamnyang.bt.controller

import io.ggamnyang.bt.dto.common.LoginDto
import io.ggamnyang.bt.dto.common.UserDto
import io.ggamnyang.bt.dto.response.LoginResponse
import io.ggamnyang.bt.service.UserService
import io.ggamnyang.bt.service.userdetail.UserDetailsAdapter
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun signUp(
        @RequestBody loginDto: LoginDto
    ): ResponseEntity<UserDto> {
        if (userService.findByUsername(loginDto.username) != null) {
            throw DataIntegrityViolationException("Unique Column. ${loginDto.username} is existed")
        }

        return ResponseEntity(userService.save(loginDto).toUserDto(), HttpStatus.CREATED)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto
    ): ResponseEntity<LoginResponse> {
        return ResponseEntity(LoginResponse(userService.login(loginDto)), HttpStatus.OK)
    }

    @GetMapping("/me")
    fun getMe(
        @AuthenticationPrincipal userAdapter: UserDetailsAdapter
    ): ResponseEntity<UserDto> {
        return ResponseEntity(userAdapter.user.toUserDto(), HttpStatus.OK)
    }
}
