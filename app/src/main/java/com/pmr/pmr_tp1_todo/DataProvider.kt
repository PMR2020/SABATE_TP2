package com.pmr.pmr_tp1_todo

import com.pmr.pmr_tp1_todo.API.ServiceAPI
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object DataProvider {
    val BASE_URL = "http://10.0.2.2:8888" //10.0.2.2 ip virtual device

    private val service = Retrofit.Builder() // implémentation de Service API en runtime
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServiceAPI::class.java)

    suspend fun getProfilFromApi() : List<ListeToDo> = service.getListProfils().lists

}
