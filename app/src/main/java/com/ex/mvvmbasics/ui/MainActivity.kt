package com.ex.mvvmbasics.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ex.mvvmbasics.R
import com.ex.mvvmbasics.data.adapters.AdapterUsers
import com.ex.mvvmbasics.data.repo.AuthViewModel
import com.ex.mvvmbasics.data.repo.RegisterActivityViewModelFactory
import com.ex.mvvmbasics.data.repo.UserRepository
import com.ex.mvvmbasics.databinding.ActivityMainBinding
import com.ex.mvvmbasics.utils.NetworkResult
import com.ex.mvvmbasics.utils.UserDetails
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    var _context: Activity? = null
    private val context get() = _context

    lateinit var userDetails: UserDetails
    private lateinit var authViewModel: AuthViewModel

    lateinit var adapterUsers: AdapterUsers
    lateinit var userName: String
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _context = this
        userDetails = UserDetails(context!!)
        adapterUsers = AdapterUsers()
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapterUsers
//        adapterUsers = AdapterUsers(::onNoteClicked)

        userName = intent.getStringExtra("name").toString()
        binding.textView.setText("Welcome Onboard\nMr. $userName")
        binding.btnLogout.setOnClickListener {
            gotoLogin()
        }

        authViewModel =
            ViewModelProvider(this, RegisterActivityViewModelFactory(UserRepository())).get(
                AuthViewModel::class.java
            )

        authViewModel.readAllUsers()

        bindObservables()

    }

    private fun bindObservables() {
        authViewModel.readLiveData.observe(this, Observer {
            binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    adapterUsers.submitList(it.data)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    progressBar.isVisible = true
                }
            }

        })
    }


    private fun gotoLogin() {
        lifecycle.coroutineScope.launch {
            userDetails.clearRecords()
        }
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}