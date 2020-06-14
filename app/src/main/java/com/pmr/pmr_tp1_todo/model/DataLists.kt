package com.pmr.pmr_tp1_todo.model

data class DataLists (val lists : List<ListeToDo>) //Attention lists est ici la clé du tableau json que l'on récupère

data class DataVersion (val version : Int) // a priori on ne peut pas accéder à version success ou status

data class DataUser (val hash:String) //fonctionne

data class DataItems (val items:List<ItemToDo>)

data class DataItem (val item:ItemToDo)