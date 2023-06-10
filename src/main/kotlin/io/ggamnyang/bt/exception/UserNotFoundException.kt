package io.ggamnyang.bt.exception

data class UserNotFoundException(
    val username: String
) : RuntimeException() {
    override val message: String
        get() = "Username $username not found."
}
