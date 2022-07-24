package com.ex.mvvmbasics

data class RegisterRequest(
    var username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val cnfPassword: String? = null,
    var firebaseUserId: String? = null
)
