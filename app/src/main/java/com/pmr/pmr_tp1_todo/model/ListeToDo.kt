package com.pmr.pmr_tp1_todo.model

class ListeToDo(val id:Int,var label : String,var active:Boolean=false) //ce n'est pas exactement la structure que l'on a dans l'API, il faudra en tenir compte
//titreListeTodo changé en label pour coller à l'API
{
    var listItemsToDo : MutableList<ItemToDo> = mutableListOf()
}
