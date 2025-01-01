package com.example.trabalhofinal2

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class ListasViewModel : ViewModel() {
    val listas = mutableStateListOf<String>()
    val itensPorLista = mutableMapOf<String, MutableList<Pair<String, String>>>()

    fun adicionarLista(nomeDaLista: String) {
        if (nomeDaLista.isNotBlank() && !listas.contains(nomeDaLista)) {
            listas.add(nomeDaLista)
        }
    }

    fun removerLista(lista: String) {
        listas.remove(lista)
        itensPorLista.remove(lista)
    }

    fun adicionarItemNaLista(nomeDaLista: String, item: Pair<String, String>) {
        val listaItens = itensPorLista.getOrPut(nomeDaLista) { mutableListOf() }
        listaItens.add(item)
    }

    fun obterItensDaLista(nomeDaLista: String): List<Pair<String, String>> {
        return itensPorLista[nomeDaLista] ?: emptyList()
    }

    fun removerItemDaLista(nomeDaLista: String, item: Pair<String, String>) {
        val listaItens = itensPorLista[nomeDaLista]
        listaItens?.remove(item)
    }
}

