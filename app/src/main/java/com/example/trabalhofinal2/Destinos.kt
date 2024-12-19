package com.example.trabalhofinal2

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.lista_3, title = "Listas")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.newlist, title = "Criar")
    companion object {
        val toList = listOf(Ecra01, Ecra02)
    }
}