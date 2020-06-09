package com.pmr.pmr_tp1_todo.API

import com.pmr.pmr_tp1_todo.model.DataProfils
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceAPI {


    //@GET("request=authenticate&user=tom&password=web")
    //suspend fun getListProfils() : List<ProfilListeToDo>

    //@POST("todo-api/index.php?request=authenticate&user=tom&password=web")
    @GET("/todo-api/index.php?request=lists&hash=10bca641466d835d3db9be02ab6e1d08") //fonctionne dans le navigateur de l'émulateur & ici maintenant
    suspend fun getListProfils() : DataProfils
}