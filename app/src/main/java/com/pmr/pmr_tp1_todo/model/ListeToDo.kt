package com.pmr.pmr_tp1_todo.model

class ListeToDo(var titreListeToDo : String,var active:Boolean=false) //ce n'est pas exactement la structure que l'on a dans l'API, il faudra en tenir compte
{
    var listItemsToDo : MutableList<ItemToDo> = mutableListOf()
}
