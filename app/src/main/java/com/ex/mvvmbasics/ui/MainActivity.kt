package com.ex.mvvmbasics.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ex.mvvmbasics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    var _context: Activity? = null
    private val context get() = _context

    lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _context = this
        userName = intent.getStringExtra("name").toString()
        binding.textView.setText("Welcome Onboard\nMr. $userName")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}