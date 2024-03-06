package com.naresh.pro.services.repository

import androidx.lifecycle.MutableLiveData
import com.naresh.pro.services.model.BloodModel
import com.naresh.pro.utils.DataState

interface PostInterface {

    fun postARequest(bloodModel: BloodModel, bloodMutableLiveData: MutableLiveData<DataState<String>>)

    suspend fun getPosts(bloodMutableLiveData: MutableLiveData<DataState<ArrayList<BloodModel>>>)

    fun deletePost(bloodModel: BloodModel)
}