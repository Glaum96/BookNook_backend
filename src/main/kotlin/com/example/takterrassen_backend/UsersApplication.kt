package com.example.user

import com.example.takterrassen_backend.model.UserMongo
import com.example.takterrassen_backend.model.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class UserService {
    @Autowired
    private val repository: UserRepository? = null

    fun saveUser(user: UserMongo) {
        repository!!.save<UserMongo>(user)
    }

    fun getUser(id: String): UserMongo? {
        return repository!!.findById(id).orElse(null)
    }

    val allUsers: List<UserMongo?>
        get() = repository!!.findAll()

    fun deleteUser(id: String) {
        repository!!.deleteById(id)
    }
}
