package com.ex.mvvmbasics.data

import com.ex.mvvmbasics.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("common/register")
    suspend fun registerVendor(
        @Body userRequest: RegisterRequest
    ): Response<ResponseBody>

    @POST("/users/signin")
    fun signin(@Body userRequest: RegisterRequest): Response<RegisterRequest>

    /* suspend fun loginUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signin(userRequest)
        handleResponse(response)

These will give you RespnseBody in Repository so that you can take any decision that's it simple
    }*/
}