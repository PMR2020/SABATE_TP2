package com.pmr.pmr_tp1_todo

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.pmr_tp1_todo.API.ServiceAPI
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


object DataProvider {
    var BASE_URL = "http://10.0.2.2:8888/todo-api/" //10.0.2.2 ip virtual device

    private var service = Retrofit.Builder() // implémentation de Service API en runtime
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServiceAPI::class.java)

    fun reStartService() {
        service = Retrofit.Builder() // implémentation de Service API en runtime
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServiceAPI::class.java)
    }

//    suspend fun getListsFromApi(baseURL:String,hash:String) : List<ListeToDo> = service.getListProfils(baseURL,hash).lists
//
//    suspend fun getApiConnexion(): Int = service.getConnexion().version
//
//    suspend fun getHashFromApi(baseURL:String,pseudo:String,password:String):String = service.getHash(baseURL,pseudo,password).hash
//
//    suspend fun getItemsFromApi(baseURL:String,requete:String,hash:String)=service.getItems(baseURL,requete,hash).items
//
//    suspend fun checkItemFromApi(baseURL:String,requete:String,check:Int,hash:String)=service.checkItem(baseURL,requete,check,hash).item
//
//    suspend fun createItemFromApi(baseURL:String,requete:String,label:String,hash:String)=service.createItem(baseURL,requete,label,hash).item
    suspend fun getListsFromApi(hash:String) : List<ListeToDo> = service.getListProfils(hash).lists


    suspend fun getHashFromApi(pseudo:String,password:String):String = service.getHash(pseudo,password).hash

    suspend fun getItemsFromApi( requete:String,hash:String)=service.getItems(requete,hash).items

    suspend fun checkItemFromApi( requete:String,check:Int,hash:String)=service.checkItem(requete,check,hash).item

    suspend fun createItemFromApi( requete:String,label:String,hash:String)=service.createItem(requete,label,hash).item

}
