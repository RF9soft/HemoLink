package com.naresh.pro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naresh.pro.services.model.BloodModel
import com.naresh.pro.services.repository.PostRepository
import com.naresh.pro.utils.Constants
import com.naresh.pro.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    private val bloodMutableLiveData = Constants.getMutableDataStateOfString()
    val bloodLiveData : LiveData<DataState<String>>
        get() = bloodMutableLiveData

    private val postMutableLiveData = Constants.getMutableDataStateOfBloodMoel()
    val postLiveData : LiveData<DataState<ArrayList<BloodModel>>>
        get() = postMutableLiveData

    fun postABloodRequest(bloodModel: BloodModel){
        repository.postARequest(bloodModel, bloodMutableLiveData)
    }

    fun getBloodPosts(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPosts(postMutableLiveData)
        }
    }

    fun deletePost(bloodModel: BloodModel){
        repository.deletePost(bloodModel)
    }
}