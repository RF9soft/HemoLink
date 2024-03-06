package com.naresh.pro.services.repository

import androidx.lifecycle.MutableLiveData
import com.naresh.pro.services.model.UserDetails
import com.naresh.pro.utils.DataState

interface UserInterface {

    fun updateData(user : UserDetails, userMutableLiveData: MutableLiveData<DataState<String>>)

    suspend fun getUserData(userMutableLiveData: MutableLiveData<DataState<ArrayList<UserDetails>>>)

    suspend fun getMyInfo(userMutableLiveData: MutableLiveData<DataState<UserDetails>>)
}