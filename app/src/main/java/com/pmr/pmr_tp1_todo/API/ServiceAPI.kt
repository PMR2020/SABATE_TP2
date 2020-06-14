package com.pmr.pmr_tp1_todo.API

import com.pmr.pmr_tp1_todo.model.*
import retrofit2.http.*

interface ServiceAPI {

    //@GET("request=authenticate&user=tom&password=web")
    //suspend fun getListProfils() : List<ProfilListeToDo>

    //@POST("todo-api/index.php?request=authenticate&user=tom&password=web")
    //@GET("index.php?request=lists&hash=10bca641466d835d3db9be02ab6e1d08") //fonctionne dans le navigateur de l'émulateur & ici maintenant
//    @GET("{baseURL}index.php?request=lists")
//    suspend fun getListProfils(@Path("baseURL") baseURL:String,@Query("hash") hash:String) : DataLists


    //vérification du lien avec l'API
    @GET("index.php") //
    suspend fun getConnexion() : DataVersion

    //vérification d'un user
    //@POST("index.php?request=authenticate&user=tom&password=web")
//    @POST("{baseURL}index.php?request=authenticate")
//    suspend fun getHash(@Path("baseURL") baseURL:String,@Query("user")  pseudo :String, @Query("password") password:String) : DataUser
//    //avoir les items d'une liste
//    @GET("{baseURL}index.php?")
//    suspend fun getItems(@Path("baseURL") baseURL:String,@Query("request")  requete :String, @Query("hash") hash:String) : DataItems //pluriel
//    //Changer la valeur d'un item
//    @PUT("{baseURL}index.php?")
//    suspend fun checkItem(@Path("baseURL") baseURL:String,@Query("request")  requete :String,  @Query("check") check:Int,@Query("hash") hash:String) : DataItem //singulier
//    //Création d'un item
//    @POST("{baseURL}index.php?")
//    suspend fun createItem(@Path("baseURL") baseURL:String,@Query("request")  requete :String,  @Query("label") label:String,@Query("hash") hash:String) : DataItem //singulier
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