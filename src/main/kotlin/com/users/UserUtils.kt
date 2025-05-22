package com.users

import com.login.TokenService
import com.login.UserService
import com.users.model.User
import com.users.model.getUserFromDB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserUtil {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    private fun getUserFromAuthorizationHeader(authorizationHeader: String): User {
        val authorizationHeaderWithoutPrefix = authorizationHeader.split(" ")[1]
        val requestingUserName = tokenService.getUsernameFromToken(authorizationHeaderWithoutPrefix)
        val requestingUserId = userService.findUserByUsername(requestingUserName)?.id
            ?: throw Exception("Requesting user ID not found")
        val user = getUserFromDB(requestingUserId) ?: throw Exception("Requesting user not found")

        return user
    }

    fun validateAdminOrSelfAction(userId: String, authorizationHeader: String): Boolean {
        return !(!validateAdminAction(authorizationHeader) && !requestingUserIsSameAsTargetUser(userId, authorizationHeader))
    }

    fun validateAdminAction (authorizationHeader: String): Boolean {
        val requestingUser = getUserFromAuthorizationHeader(authorizationHeader)
        return requestingUser.isAdmin
    }

    fun requestingUserIsSameAsTargetUser (userId: String, authorizationHeader: String): Boolean {
        val requestingUser = getUserFromAuthorizationHeader(authorizationHeader)
        return requestingUser.id == userId
    }
}