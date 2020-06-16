package com.pmr.pmr_tp1_todo.API

import com.pmr.pmr_tp1_todo.model.*
import retrofit2.http.*

interface ServiceAPI {
    @GET("index.php?request=lists")
    suspend fun getListProfils(@Query("hash") hash:String) : DataLists

    @POST("index.php?request=authenticate")
    suspend fun getHash(@Query("user")  pseudo :String, @Query("password") password:String) : DataUser
    //avoir les items d'une liste
    @GET("index.php?")
    suspend fun getItems(@Query("request")  requete :String, @Query("hash") hash:String) : DataItems //pluriel
    //Changer la valeur d'un item
    @PUT("index.php?")
    suspend fun checkItem(@Query("request")  requete :String,  @Query("check") check:Int,@Query("hash") hash:String) : DataItem //singulier
    //Création d'un item
    @POST("index.php?")
    suspend fun createItem(@Query("request")  requete :String,  @Query("label") label:String,@Query("hash") hash:String) : DataItem //singulier

}