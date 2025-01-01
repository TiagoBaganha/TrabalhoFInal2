package com.example.trabalhofinal2

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class ListasViewModel : ViewModel() {
    val listas = mutableStateListOf<String>()

    fun adicionarLista(nomeDaLista: String) {
        if (nomeDaLista.isNotBlank() && !listas.contains(nomeDaLista)) {
            listas.add(nomeDaLista)
        }
    }
    fun removerLista(lista: String) {
        listas.remove(lista)
    }

}
