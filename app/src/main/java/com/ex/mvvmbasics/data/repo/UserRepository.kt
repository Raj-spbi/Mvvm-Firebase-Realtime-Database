package com.ex.mvvmbasics.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ex.mvvmbasics.RegisterRequest
import com.ex.mvvmbasics.data.models.LoginRequest
import com.ex.mvvmbasics.utils.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class UserRepository {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<String>>()
    val userResponseLiveData: LiveData<NetworkResult<String>>
        get() = _userResponseLiveData


    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database: DatabaseReference by lazy {
        Firebase.database.reference
    }

    suspend fun register(userRequest: RegisterRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        firebaseAuth.createUserWithEmailAndPassword(userRequest.email!!, userRequest.password!!)
            .addOnCompleteListener {
                val key: String = database.child("RegisteredUsers").key.toString()
                userRequest.firebaseUserId = key
                database.child("RegisteredUsers").child(firebaseAuth.currentUser!!.uid)
                    .setValue(userRequest).addOnSuccessListener {
                        _userResponseLiveData.postValue(NetworkResult.Success("Registered Successfully"))
                    }.addOnFailureListener {
                        _userResponseLiveData.postValue(NetworkResult.Error("Something went Wrong"))
                    }
            }.addOnFailureListener {
                _userResponseLiveData.postValue(NetworkResult.Error("Registration Failed"))
            }
    }

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<RegisterRequest>>()
    val loginResponseLiveData: LiveData<NetworkResult<RegisterRequest>>
        get() = _loginResponseLiveData


    suspend fun login(userRequest: LoginRequest) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())
        firebaseAuth.signInWithEmailAndPassword(userRequest.email, userRequest.password)
            .addOnCompleteListener {
                val currentUser = firebaseAuth.currentUser
                database.child("RegisteredUsers")
                    .child(currentUser!!.uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val loginResponse = dataSnapshot.getValue<RegisterRequest>()
                            _loginResponseLiveData.postValue(NetworkResult.Success(loginResponse!!))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _loginResponseLiveData.postValue(NetworkResult.Error("Failed to read value"))
                        }
                    })
            }.addOnFailureListener {
                _loginResponseLiveData.postValue(NetworkResult.Error("Something went Wrong"))
            }
    }


    /*    private val _readResponseLiveData = MutableLiveData<NetworkResult<RegisterRequest>>()
        val readResponseLiveData: LiveData<NetworkResult<RegisterRequest>>
            get() = _readResponseLiveData*/
    private val _readResponseLiveData = MutableLiveData<NetworkResult<List<RegisterRequest>>>()
    val readResponseLiveData get() = _readResponseLiveData
    suspend fun readDataFromFirebase() {
        _readResponseLiveData.postValue(NetworkResult.Loading())
        database.child("RegisteredUsers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<RegisterRequest> = mutableListOf<RegisterRequest>()
//                val loginResponse = snapshot.getValue<RegisterRequest>()
                for (snap in snapshot.children) {
                    list.add(snap.getValue<RegisterRequest>()!!)
                }
                _readResponseLiveData.postValue(NetworkResult.Success(list!!))
            }

            override fun onCancelled(error: DatabaseError) {
                _loginResponseLiveData.postValue(NetworkResult.Error("Failed to read value"))
            }
        })
    }
}