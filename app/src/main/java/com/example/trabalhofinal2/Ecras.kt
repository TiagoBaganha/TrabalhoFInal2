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
import androidx.compose.animation.animateContentSize


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun Ecra01(navController: NavController, listasViewModel: ListasViewModel) {
    val listas = listasViewModel.listas
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userName = remember { mutableStateOf("Usuário") }
    val allLists = remember { mutableStateListOf<String>() }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val userEmail = auth.currentUser?.email
                val extractedName = userEmail?.substringBefore("@") ?: "Usuário Padrão"

                firestore.collection("users").document(userId).set(
                    mapOf("name" to extractedName)
                ).await()

                userName.value = extractedName

                val sharedListsSnapshot = firestore.collection("shared_lists")
                    .whereEqualTo("sharedBy", userEmail)
                    .get().await()

                allLists.clear()
                allLists.addAll(listas)
                sharedListsSnapshot.documents.forEach { document ->
                    val lista = document.get("lista") as Map<*, *>
                    allLists.add(lista["nomeDaLista"].toString())
                }
            } catch (exception: Exception) {
                Log.e("Firestore", "Erro ao buscar ou salvar o nome do usuário: ${exception.message}")
            }
        } else {
            Log.e("Firestore", "Usuário não autenticado")
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        val user = auth.currentUser
                        user?.let { currentUser ->
                            // Delete user data from Firestore
                            userId?.let { uid ->
                                firestore.collection("users").document(uid)
                                    .delete()
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            // After deleting data, delete the account
                                            currentUser.delete()
                                                .addOnCompleteListener { authTask ->
                                                    if (authTask.isSuccessful) {
                                                        navController.navigate("login") {
                                                            popUpTo(navController.graph.startDestinationId) {
                                                                inclusive = true
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                    }
                            }
                        }
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Olá,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = userName.value,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Account Button
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Excluir Conta",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        // Logout Button
                        IconButton(
                            onClick = {
                                auth.signOut()
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.logout),
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Listas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allLists) { lista ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lista,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { navController.navigate("ecra03/$lista") }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit),
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        firestore.collection("shared_lists")
                                            .whereEqualTo("lista.nomeDaLista", lista)
                                            .get()
                                            .addOnSuccessListener { result ->
                                                for (document in result) {
                                                    firestore.collection("shared_lists")
                                                        .document(document.id)
                                                        .delete()
                                                }
                                                allLists.remove(lista)
                                            }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "Excluir",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra02(navController: NavController, listasViewModel: ListasViewModel) {
    var nomeDaLista by remember { mutableStateOf("") }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var emailParaCompartilhar by remember { mutableStateOf("") }
    val itens = remember { mutableStateListOf<Pair<String, String>>() }
    var isLoading by remember { mutableStateOf(false) }

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

            // Salvar a lista no documento compartilhado para o usuário atual
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

            // Se um email para compartilhar foi fornecido, compartilhar com esse usuário também
            if (emailParaCompartilhar.isNotBlank()) {
                firestore.collection("shared_lists").add(
                    mapOf(
                        "sharedBy" to emailParaCompartilhar,
                        "lista" to listaData,
                        "originalSharedBy" to userEmail
                    )
                ).addOnSuccessListener {
                    Log.d("Firestore", "Lista compartilhada com outro usuário com sucesso")
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao compartilhar a lista com outro usuário: ${e.message}")
                }
            }

            // Reset state
            nomeDaLista = ""
            emailParaCompartilhar = ""
            itens.clear()
            navController.navigate("ecra01")
            shouldSaveList = false
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Criar Nova Lista",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nome da Lista",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = nomeDaLista,
                        onValueChange = { nomeDaLista = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Digite o nome da lista") }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Compartilhar com (opcional)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = emailParaCompartilhar,
                        onValueChange = { emailParaCompartilhar = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Digite o email para compartilhar") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.email),
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Adicionar Item",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nomeDoProduto,
                            onValueChange = { nomeDoProduto = it },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Nome do produto") }
                        )
                        OutlinedTextField(
                            value = quantidade,
                            onValueChange = { quantidade = it },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Qtd") }
                        )
                    }
                    Button(
                        onClick = {
                            if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                                itens.add(nomeDoProduto to quantidade)
                                nomeDoProduto = ""
                                quantidade = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Adicionar",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Item")
                    }
                }
            }

            if (itens.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Itens na Lista",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        itens.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.first} - ${item.second}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { itens.removeAt(index) }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            if (index < itens.size - 1) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (nomeDaLista.isNotBlank() && itens.isNotEmpty()) {
                        shouldSaveList = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && nomeDaLista.isNotBlank() && itens.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Salvar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Salvar Lista",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Ecra03(navController: NavController, listasViewModel: ListasViewModel, listaNome: String) {
    val listaItems = remember { mutableStateListOf<Pair<String, String>>() }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val userEmail = auth.currentUser?.email

    LaunchedEffect(userId, listaNome) {
        if (userId != null) {
            try {
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

    fun salvarAlteracoes() {
        if (userId != null) {
            isLoading = true
            val listaData = hashMapOf(
                "nomeDaLista" to listaNome,
                "itens" to listaItems.associate { it.first to it.second }
            )

            firestore.collection("shared_lists")
                .whereEqualTo("lista.nomeDaLista", listaNome)
                .get()
                .addOnSuccessListener { sharedResult ->
                    if (!sharedResult.isEmpty) {
                        val documentId = sharedResult.documents[0].id
                        firestore.collection("shared_lists")
                            .document(documentId)
                            .update("lista", listaData)
                            .addOnSuccessListener {
                                isLoading = false
                                navController.navigate("ecra01")
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    } else {
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
                                        .addOnSuccessListener {
                                            isLoading = false
                                            navController.navigate("ecra01")
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                        }
                                }
                            }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Editar Lista: $listaNome",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Adicionar Item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Adicionar Item",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nomeDoProduto,
                            onValueChange = { nomeDoProduto = it },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Nome do produto") }
                        )
                        OutlinedTextField(
                            value = quantidade,
                            onValueChange = { quantidade = it },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Qtd") }
                        )
                    }
                    Button(
                        onClick = {
                            if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                                listaItems.add(nomeDoProduto to quantidade)
                                nomeDoProduto = ""
                                quantidade = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Adicionar",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Item")
                    }
                }
            }

            // Lista de Itens
            if (listaItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Itens na Lista",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        listaItems.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.first} - ${item.second}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { listaItems.removeAt(index) }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete),
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            if (index < listaItems.size - 1) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            // Botão Salvar Lista
            Button(
                onClick = { salvarAlteracoes() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && listaItems.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Salvar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Salvar Lista",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}