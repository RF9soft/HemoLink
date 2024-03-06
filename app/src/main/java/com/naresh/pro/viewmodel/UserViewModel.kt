package com.naresh.pro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naresh.pro.services.model.UserDetails
import com.naresh.pro.services.repository.UserRepository
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel(){

    private val repository = UserRepository()

    private val userMutableLiveData = Constants.getMutableDataStateOfString()
    val userLiveData : LiveData<DataState<String>>
        get() = userMutableLiveData

    private val donorMutableLiveData = Constants.getMutableDataStateOfUserDetails()
    val donorLiveData : LiveData<DataState<ArrayList<UserDetails>>>
        get() = donorMutableLiveData

    private val myMutableLiveData = Constants.getMutableDataStateOfUserDetailsObject()
    val myLiveData : LiveData<DataState<UserDetails>>
        get() = myMutableLiveData

    fun updateData(user : UserDetails){
        repository.updateData(user, userMutableLiveData)
    }

    fun getDonors(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserData(donorMutableLiveData)
        }
    }

    fun getMyInfo(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyInfo(myMutableLiveData)
        }
    }
}