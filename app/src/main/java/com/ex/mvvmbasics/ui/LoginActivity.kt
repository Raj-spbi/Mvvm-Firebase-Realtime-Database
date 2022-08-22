package com.ex.mvvmbasics.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ex.mvvmbasics.RegisterRequest
import com.ex.mvvmbasics.data.models.LoginRequest
import com.ex.mvvmbasics.data.repo.AuthViewModel
import com.ex.mvvmbasics.data.repo.RegisterActivityViewModelFactory
import com.ex.mvvmbasics.data.repo.UserRepository
import com.ex.mvvmbasics.databinding.ActivityLoginBinding
import com.ex.mvvmbasics.utils.NetworkResult
import com.ex.mvvmbasics.utils.UserDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    var _context: Activity? = null
    private val context get() = _context

    private lateinit var authViewModel: AuthViewModel
    private lateinit var currentUserDetails: RegisterRequest
    lateinit var userDetails: UserDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _context = this;
        userDetails = UserDetails(context = context!!)

        authViewModel =
            ViewModelProvider(this, RegisterActivityViewModelFactory(UserRepository())).get(
                AuthViewModel::class.java
            )

        binding.registerBtn.setOnClickListener {
            val validateUser = validateUserInput()
            if (validateUser.first) {
                authViewModel.loginUser(getUserRequest())
            } else {
                Toast.makeText(context, validateUser.second, Toast.LENGTH_SHORT).show()
            }
        }


        bindObservals()
    }

    private fun bindObservals() {
        authViewModel.loginLiveData.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    currentUserDetails = it.data!!
/*
                    Toast.makeText(
                        context,
                        "Welcome ${currentUserDetails.username}",
                        Toast.LENGTH_SHORT
                    ).show()
*/

                    CoroutineScope(IO).launch {
                        Log.e("cbvhgdjk", "bindObservals: " + currentUserDetails.username)
                        userDetails.storeUser(
                            currentUserDetails.username.toString()
                        )
                    }
                    gotoMainPage(context, currentUserDetails)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun gotoMainPage(context: Activity?, currentUserDetails: RegisterRequest) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("name", currentUserDetails.username)
        startActivity(intent)
        finish()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getUserRequest()
        return authViewModel.validateCredentialsLogin(
            userRequest.email,
            userRequest.password,
        )
    }


    private fun getUserRequest(): LoginRequest {
        val emailAddress = binding.email.text.toString()
        val password = binding.password.text.toString()
        return LoginRequest(emailAddress, password)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}