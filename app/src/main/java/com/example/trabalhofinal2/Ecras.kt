package com.example.trabalhofinal2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.tasks.await
import android.util.Log


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun Ecra01(navController: NavController, listasViewModel: ListasViewModel) {
    val listas = listasViewModel.listas
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userName = remember { mutableStateOf("Usuário") }
    val sharedLists = remember { mutableStateListOf<String>() }

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val userEmail = auth.currentUser?.email
                val extractedName = userEmail?.substringBefore("@") ?: "Usuário Padrão"

                firestore.collection("users").document(userId).set(
                    mapOf("name" to extractedName)
                ).await()

                userName.value = extractedName

                // Buscar listas partilhadas
                firestore.collection("shared_lists")
                    .whereEqualTo("sharedBy", userEmail)
                    .get()
                    .addOnSuccessListener { result ->
                        sharedLists.clear()
                        for (document in result) {
                            val lista = document.get("lista") as Map<*, *>
                            sharedLists.add(lista["nomeDaLista"].toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao buscar listas partilhadas: ${e.message}")
                    }

            } catch (exception: Exception) {
                Log.e("Firestore", "Erro ao buscar ou salvar o nome do usuário: ${exception.message}")
            }
        } else {
            Log.e("Firestore", "Usuário não autenticado")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Olá, ${userName.value}",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Listas Partilhadas:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sharedLists) { lista ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lista,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { navController.navigate("ecra03/$lista") }) {
                            Text(text = "Editar")
                        }
                        Button(onClick = {
                            firestore.collection("shared_lists")
                                .whereEqualTo("lista.nomeDaLista", lista)
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        firestore.collection("shared_lists").document(document.id).delete()
                                    }
                                    sharedLists.remove(lista)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao apagar lista: ${e.message}")
                                }
                        }) {
                            Text(text = "Apagar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listas) { lista ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lista,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Button(onClick = { navController.navigate("ecra03/${lista}") }) {
                            Text(text = "Editar")
                        }

                        Button(onClick = {
                            firestore.collection("listas")
                                .whereEqualTo("nomeDaLista", lista)
                                .get()
                                .addOnSuccessListener { result ->
                                    for (document in result) {
                                        firestore.collection("listas").document(document.id).delete()
                                    }
                                    listasViewModel.removerLista(lista)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao apagar lista: ${e.message}")
                                }
                        }) {
                            Text(text = "X")
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun Ecra02(navController: NavController, listasViewModel: ListasViewModel) {
    var nomeDaLista by remember { mutableStateOf("") }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val itens = remember { mutableStateListOf<Pair<String, String>>() }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userEmail = auth.currentUser?.email ?: "usuario_padrao@gmail.com"

    var shouldSaveList by remember { mutableStateOf(false) }

    LaunchedEffect(shouldSaveList) {
        if (shouldSaveList && userId != null) {
            val listaData = hashMapOf(
                "nomeDaLista" to nomeDaLista,
                "itens" to itens.map { it.first to it.second }.toMap()
            )

            // Salvar a lista no documento do usuário autenticado
            firestore.collection("users").document(userId).collection("listas")
                .add(listaData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Lista adicionada com sucesso")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao adicionar a lista: ${e.message}")
                }

            // Salvar a lista no documento compartilhado
            firestore.collection("shared_lists").add(
                mapOf(
                    "sharedBy" to userEmail,
                    "lista" to listaData
                )
            ).addOnSuccessListener {
                Log.d("Firestore", "Lista compartilhada adicionada com sucesso")
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao compartilhar a lista: ${e.message}")
            }

            // Reset state
            nomeDaLista = ""
            itens.clear()
            shouldSaveList = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController, appItems = Destino.toList)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Nome da Lista
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nome da Lista:",
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                TextField(
                    value = nomeDaLista,
                    onValueChange = { nomeDaLista = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome do Produto e Quantidade
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Nome do Produto
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nome Produto:",
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    TextField(
                        value = nomeDoProduto,
                        onValueChange = { nomeDoProduto = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp)) // Espaço entre os campos

                // Quantidade
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantidade:",
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    TextField(
                        value = quantidade,
                        onValueChange = { quantidade = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botões: Acrescentar Item e Acrescentar Lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                            itens.add(nomeDoProduto to quantidade)
                            nomeDoProduto = ""
                            quantidade = ""
                        }
                    }
                ) {
                    Text(text = "Acrescentar Item")
                }

                Button(
                    onClick = {
                        if (nomeDaLista.isNotBlank() && itens.isNotEmpty()) {
                            shouldSaveList = true
                        }
                    }
                ) {
                    Text(text = "Acrescentar Lista")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exibir itens na lista
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Itens na Lista:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                itens.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.first} - ${item.second}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                itens.removeAt(index)
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(text = "X", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun Ecra03(navController: NavController, listasViewModel: ListasViewModel, listaNome: String) {
    val listaItems = remember { mutableStateListOf<Pair<String, String>>() }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userEmail = auth.currentUser?.email

    LaunchedEffect(userId, listaNome) {
        if (userId != null) {
            try {
                // Primeiro, tenta buscar da coleção de listas compartilhadas
                firestore.collection("shared_lists")
                    .whereEqualTo("lista.nomeDaLista", listaNome)
                    .get()
                    .addOnSuccessListener { sharedResult ->
                        if (!sharedResult.isEmpty) {
                            val document = sharedResult.documents[0]
                            val lista = document.get("lista") as Map<*, *>
                            val itens = lista["itens"] as Map<*, *>

                            listaItems.clear()
                            itens.forEach { (produto, quantidade) ->
                                listaItems.add(Pair(produto.toString(), quantidade.toString()))
                            }
                        } else {
                            // Se não encontrar nas listas compartilhadas, busca nas listas normais
                            firestore.collection("users")
                                .document(userId)
                                .collection("listas")
                                .whereEqualTo("nomeDaLista", listaNome)
                                .get()
                                .addOnSuccessListener { result ->
                                    if (!result.isEmpty) {
                                        val document = result.documents[0]
                                        val itens = document.get("itens") as Map<*, *>

                                        listaItems.clear()
                                        itens.forEach { (produto, quantidade) ->
                                            listaItems.add(Pair(produto.toString(), quantidade.toString()))
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao buscar lista: ${e.message}")
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao buscar lista compartilhada: ${e.message}")
                    }
            } catch (exception: Exception) {
                Log.e("Firestore", "Erro ao carregar itens: ${exception.message}")
            }
        }
    }

    // Função para salvar alterações no Firebase
    fun salvarAlteracoes() {
        if (userId != null) {
            val listaData = hashMapOf(
                "nomeDaLista" to listaNome,
                "itens" to listaItems.associate { it.first to it.second }
            )

            // Atualizar nas listas compartilhadas
            firestore.collection("shared_lists")
                .whereEqualTo("lista.nomeDaLista", listaNome)
                .get()
                .addOnSuccessListener { sharedResult ->
                    if (!sharedResult.isEmpty) {
                        val documentId = sharedResult.documents[0].id
                        firestore.collection("shared_lists")
                            .document(documentId)
                            .update("lista", listaData)
                    } else {
                        // Se não encontrar nas compartilhadas, atualiza nas listas normais
                        firestore.collection("users")
                            .document(userId)
                            .collection("listas")
                            .whereEqualTo("nomeDaLista", listaNome)
                            .get()
                            .addOnSuccessListener { result ->
                                if (!result.isEmpty) {
                                    val documentId = result.documents[0].id
                                    firestore.collection("users")
                                        .document(userId)
                                        .collection("listas")
                                        .document(documentId)
                                        .update(listaData as Map<String, Any>)
                                }
                            }
                    }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editar Lista: $listaNome",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Button(
                    onClick = { navController.navigate("ecra01") },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(text = "←", fontSize = 20.sp)
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nome Produto:",
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    TextField(
                        value = nomeDoProduto,
                        onValueChange = { nomeDoProduto = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantidade:",
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    TextField(
                        value = quantidade,
                        onValueChange = { quantidade = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                            listaItems.add(Pair(nomeDoProduto, quantidade))
                            nomeDoProduto = ""
                            quantidade = ""
                        }
                    }
                ) {
                    Text(text = "Acrescentar Item")
                }

                Button(
                    onClick = {
                        salvarAlteracoes()
                        navController.navigate("ecra01")
                    }
                ) {
                    Text(text = "Salvar Lista")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Itens na Lista:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                listaItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.first} - ${item.second}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                listaItems.removeAt(index)
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(text = "X", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}