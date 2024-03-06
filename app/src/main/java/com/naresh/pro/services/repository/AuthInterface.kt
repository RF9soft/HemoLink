package com.naresh.pro.services.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.naresh.pro.utils.DataState
import com.google.firebase.auth.PhoneAuthCredential

interface AuthInterface {

    fun sendOtpCode(number : String, activity: Activity, authMutableLiveData: MutableLiveData<DataState<String>>)
    fun signInWithCredential(credential: PhoneAuthCredential, authMutableLiveData: MutableLiveData<DataState<String>>)
    fun checkData(authMutableLiveData: MutableLiveData<DataState<String>>)
    fun verifyCode(verificationID : String, code: String, verifyCodeMutableLiveData: MutableLiveData<DataState<String>>)
}