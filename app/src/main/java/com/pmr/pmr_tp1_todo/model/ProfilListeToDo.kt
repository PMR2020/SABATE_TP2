package com.pmr.pmr_tp1_todo.model

class ProfilListeToDo( var login : String, var mesListToDo: MutableList<ListeToDo> = mutableListOf(),var active:Boolean=false)