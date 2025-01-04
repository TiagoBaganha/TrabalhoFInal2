package com.example.trabalhofinal2

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListasViewModel : ViewModel() {
    val listas = mutableStateListOf<String>()
    val itensPorLista = mutableMapOf<String, MutableList<Pair<String, String>>>()
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    init {
        createUserCollection()
    }

    fun createUserCollection() {
        auth.currentUser?.let { user ->
            val userData = hashMapOf(
                "userId" to user.uid,
                "email" to user.email,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(user.uid)
                .set(userData)
        }
    }

    fun adicionarLista(nomeDaLista: String, itensIniciais: List<Pair<String, String>> = emptyList()) {
        if (nomeDaLista.isNotBlank() && !listas.contains(nomeDaLista)) {
            listas.add(nomeDaLista)
            itensPorLista[nomeDaLista] = itensIniciais.toMutableList()
        }
    }

    fun removerLista(lista: String) {
        listas.remove(lista)
        itensPorLista.remove(lista)

        auth.currentUser?.let { user ->
            db.collection("listas")
                .whereEqualTo("userid", user.uid)
                .whereEqualTo("nomelistaid", lista)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        }
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

        auth.currentUser?.let { user ->
            db.collection("listas")
                .whereEqualTo("userid", user.uid)
                .whereEqualTo("nomelistaid", nomeDaLista)
                .whereEqualTo("nomeprodutoid", item.first)
                .whereEqualTo("quantidadeid", item.second)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        }
    }

    fun editarItemDaLista(nomeDaLista: String, itemAntigo: Pair<String, String>, itemNovo: Pair<String, String>) {
        val listaItens = itensPorLista[nomeDaLista]
        listaItens?.let {
            val index = it.indexOf(itemAntigo)
            if (index != -1) {
                it[index] = itemNovo
                atualizarItemFirebase(nomeDaLista, itemAntigo, itemNovo)
            }
        }
    }

    private fun atualizarItemFirebase(nomeDaLista: String, itemAntigo: Pair<String, String>, itemNovo: Pair<String, String>) {
        auth.currentUser?.let { user ->
            db.collection("listas")
                .whereEqualTo("userid", user.uid)
                .whereEqualTo("nomelistaid", nomeDaLista)
                .whereEqualTo("nomeprodutoid", itemAntigo.first)
                .whereEqualTo("quantidadeid", itemAntigo.second)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.update(
                            mapOf(
                                "nomeprodutoid" to itemNovo.first,
                                "quantidadeid" to itemNovo.second
                            )
                        )
                    }
                }
        }
    }

    fun atualizarLista(nomeDaLista: String, novosItens: List<Pair<String, String>>) {
        itensPorLista[nomeDaLista] = novosItens.toMutableList()
        salvarListaFirebase(nomeDaLista, novosItens)
    }

    fun salvarListaFirebase(nomeLista: String, items: List<Pair<String, String>>) {
        auth.currentUser?.let { user ->
            // Remove existing items
            db.collection("listas")
                .whereEqualTo("userid", user.uid)
                .whereEqualTo("nomelistaid", nomeLista)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }

                    // Add new items
                    items.forEach { (nomeProduto, quantidade) ->
                        db.collection("listas").add(
                            mapOf(
                                "userid" to user.uid,
                                "nomelistaid" to nomeLista,
                                "nomeprodutoid" to nomeProduto,
                                "quantidadeid" to quantidade
                            )
                        )
                    }
                }
        }
    }
}