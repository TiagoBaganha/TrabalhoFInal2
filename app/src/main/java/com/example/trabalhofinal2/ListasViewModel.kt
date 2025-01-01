package com.example.trabalhofinal2

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class ListasViewModel : ViewModel() {
    val listas = mutableStateListOf<String>()
    val itensPorLista = mutableMapOf<String, MutableList<Pair<String, String>>>()

    // Adiciona uma nova lista, se não existir
    fun adicionarLista(nomeDaLista: String) {
        if (nomeDaLista.isNotBlank() && !listas.contains(nomeDaLista)) {
            listas.add(nomeDaLista)
        }
    }

    // Remove uma lista
    fun removerLista(lista: String) {
        listas.remove(lista)
        itensPorLista.remove(lista)
    }

    // Adiciona um item a uma lista
    fun adicionarItemNaLista(nomeDaLista: String, item: Pair<String, String>) {
        val listaItens = itensPorLista.getOrPut(nomeDaLista) { mutableListOf() }
        listaItens.add(item)
    }

    // Obtém os itens de uma lista
    fun obterItensDaLista(nomeDaLista: String): List<Pair<String, String>> {
        return itensPorLista[nomeDaLista] ?: emptyList()
    }

    // Remove um item de uma lista
    fun removerItemDaLista(nomeDaLista: String, item: Pair<String, String>) {
        val listaItens = itensPorLista[nomeDaLista]
        listaItens?.remove(item)
    }

    // Edita um item de uma lista
    fun editarItemDaLista(nomeDaLista: String, itemAntigo: Pair<String, String>, itemNovo: Pair<String, String>) {
        val listaItens = itensPorLista[nomeDaLista]
        listaItens?.let {
            val index = it.indexOf(itemAntigo)
            if (index != -1) {
                it[index] = itemNovo
            }
        }
    }

    // Atualiza a lista de itens de uma lista
    fun atualizarLista(nomeDaLista: String, novosItens: List<Pair<String, String>>) {
        itensPorLista[nomeDaLista] = novosItens.toMutableList()
    }
}
