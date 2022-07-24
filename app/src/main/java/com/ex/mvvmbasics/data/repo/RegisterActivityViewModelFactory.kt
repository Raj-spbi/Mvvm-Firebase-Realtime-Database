package com.ex.mvvmbasics.data.repo


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.security.InvalidParameterException

class RegisterActivityViewModelFactory(val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userRepository) as T
        }
        throw InvalidParameterException("Unable to construct AuthViewModel")
    }

}
