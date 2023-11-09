package com.alvintio.pedulipangan.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.alvintio.pedulipangan.data.remote.ApiService
import com.alvintio.pedulipangan.model.LoginResult
import com.alvintio.pedulipangan.model.RegisterResponse
import com.google.gson.Gson
import retrofit2.HttpException

class PanganRepository private constructor(
    private var apiService: ApiService,
    private val pref: UserPreferences
) {
    fun registerUser(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            Log.d("PanganRepository", "registerUser: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            val user = response.loginResult
            if (user != null) {
                emit(Result.Success(user))
            } else {
                emit(Result.Error("No Data"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            emit(Result.Error(errorBody.message.toString()))
        } catch (e: Exception) {
            Log.d("PanganRepository", "loginUser: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: PanganRepository? = null

        fun getInstance(apiService: ApiService,
                        pref: UserPreferences
        ): PanganRepository {
            return instance ?: synchronized(this) {
                instance ?: PanganRepository(apiService, pref)
            }.also { instance = it }
        }
    }
}