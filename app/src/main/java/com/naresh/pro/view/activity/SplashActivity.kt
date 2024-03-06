package com.naresh.pro.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.naresh.pro.R
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState
import com.naresh.pro.viewmodel.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel : AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Constants.auth.currentUser != null){
            if (Constants.internetAvailable(this)){
                viewModel.checkData()
            }
            else {
                Constants.showSnackBar(this, window.decorView, "Check your internet connection", Constants.SNACK_LONG)
            }
        }

        else {
            Handler(Looper.getMainLooper()).postDelayed({
                Constants.intentToActivity(this, AuthenticationActivity::class.java)
            }, 1500)
        }

        viewModel.authLiveData.observe(this){
            when (it) {
                is DataState.Loading -> {}

                is DataState.Success -> {
                    if (it.data == "noInfo") {
                        Constants.intentToActivity(this, AuthenticationActivity::class.java)
                    }
                    if (it.data == "hasInfo"){
                        Constants.intentToActivity(this, HomeActivity::class.java)
                    }
                }

                is DataState.Failed -> {
                    Constants.showSnackBar(this, window.decorView, it.message!!, Constants.SNACK_LONG)
                }
            }
        }
    }
}