package com.example.tiktokapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tiktokapp.data.db.entities.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun loginWithEmailAndPassword(email: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    suspend fun loginWithUsernameAndPassword(username: String, password: String): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE email = :email)")
    suspend fun isEmailTaken(email: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM user WHERE username = :username)")
    suspend fun isUsernameTaken(username: String): Boolean



}