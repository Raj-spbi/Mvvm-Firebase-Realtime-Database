package com.ex.mvvmbasics.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.ex.mvvmbasics.RegisterRequest
import com.ex.mvvmbasics.data.repo.AuthViewModel
import com.ex.mvvmbasics.data.repo.RegisterActivityViewModelFactory
import com.ex.mvvmbasics.data.repo.UserRepository
import com.ex.mvvmbasics.databinding.ActivityRegisterBinding
import com.ex.mvvmbasics.utils.NetworkResult
import com.ex.mvvmbasics.utils.UserDetails

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    var _context: Activity? = null
    private val context get() = _context

    lateinit var userDetails: UserDetails

    override fun onStart() {
        super.onStart()

        lifecycle.coroutineScope.launchWhenCreated {
            userDetails.getUserName().collect {
                if (!it.isNullOrEmpty()) {
                    val intent = Intent(context, MainActivity::class.java);
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _context = this
        userDetails = UserDetails(context = context!!)

        authViewModel =
            ViewModelProvider(this, RegisterActivityViewModelFactory(UserRepository())).get(
                AuthViewModel::class.java
            )

        bindingObservers()

        binding.registerBtn.setOnClickListener {
            val validateUser = validateUserInput()
            if (validateUser.first) {
                authViewModel.registerUser(getUserRequest())
            } else {
                Toast.makeText(context, validateUser.second, Toast.LENGTH_SHORT).show()
            }
        }


        binding.loginGotoBtn.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun bindingObservers() {
        authViewModel.userResponseLiveData.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {

                    Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
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

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getUserRequest()
        return authViewModel.validateCredentials(
            userRequest.username!!,
            userRequest.email!!,
            userRequest.password!!,
            userRequest.cnfPassword!!
        )
    }


    private fun getUserRequest(): RegisterRequest {
        val userName = binding.name.text.toString()
        val emailAddress = binding.email.text.toString()
        val password = binding.password.text.toString()
        val cnfPassword = binding.cnfPassword.text.toString()
        return RegisterRequest(userName, emailAddress, password, cnfPassword, "")
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}