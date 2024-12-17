package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    
    val email: LiveData<String> get() = _email
    val password: LiveData<String> get() = _password
    val name: LiveData<String> get() = _name

}
