package com.ex.mvvmbasics.data.repo

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ex.mvvmbasics.RegisterRequest
import com.ex.mvvmbasics.data.models.LoginRequest
import com.ex.mvvmbasics.utils.Helper
import com.ex.mvvmbasics.utils.NetworkResult
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val userResponseLiveData: LiveData<NetworkResult<String>>
        get() = userRepository.userResponseLiveData

    fun registerUser(userRequest: RegisterRequest) {
        viewModelScope.launch {
            userRepository.register(userRequest)
        }
    }

    val loginLiveData: LiveData<NetworkResult<RegisterRequest>>
        get() = userRepository.loginResponseLiveData

    fun loginUser(userRequest: LoginRequest) {
        viewModelScope.launch {
            userRepository.login(userRequest)
        }
    }


    fun validateCredentials(
        userName: String, emailAddress: String, password: String, cnfPass: String
    ): Pair<Boolean, String> {

        var result = Pair(true, "")
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(
                password
            ) || TextUtils.isEmpty(cnfPass)
        ) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        } else if (TextUtils.isEmpty(password) && password.length <= 5) {
            result = Pair(false, "Password should not be empty!")
        } else if (TextUtils.isEmpty(cnfPass) && cnfPass.length <= 5) {
            result = Pair(false, "Confirm Password should not be empty!")
        } else if (!cnfPass.equals(password, true)) {
            result = Pair(false, "Password not match!")
        }
        return result
    }

    fun validateCredentialsLogin(
        emailAddress: String, password: String
    ): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, "Email is invalid")
        } else if (TextUtils.isEmpty(password) && password.length <= 5) {
            result = Pair(false, "Password should not be empty!")
        }
        return result
    }

//    fun goToSignup(view: View) {
//        Intent(view.context, SignupActivity::class.java).also {
//            view.context.startActivity(it)
//        }
//    }

//    fun goToLogin(view: View) {
//        Intent(view.context, LoginActivity::class.java).also {
//            view.context.startActivity(it)
//        }
//    }

    val readLiveData: LiveData<NetworkResult<List<RegisterRequest>>>
        get() = userRepository.readResponseLiveData

    fun readAllUsers() {
        viewModelScope.launch {
            userRepository.readDataFromFirebase()
        }
    }


}