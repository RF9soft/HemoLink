package com.naresh.pro.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naresh.pro.services.repository.AuthRepository
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState

class AuthViewModel : ViewModel() {

    private val repository : AuthRepository = AuthRepository()

    private val authMutableLiveData : MutableLiveData<DataState<String>> = Constants.getMutableDataStateOfString()
    val authLiveData : LiveData<DataState<String>>
        get() = authMutableLiveData

    fun sendVerificationCode(activity: Activity, number : String){
        repository.sendOtpCode("+91$number", activity, authMutableLiveData)
    }

    fun verifyOTP(verificationID: String, code : String){
        repository.verifyCode(verificationID, code, authMutableLiveData)
    }

    fun checkData(){
        repository.checkData(authMutableLiveData)
    }
}