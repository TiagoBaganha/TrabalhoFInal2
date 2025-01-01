package com.example.trabalhofinal2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun Ecra01(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lista de Itens",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun Ecra02(navController: NavController) {

    var nomeDaLista by remember { mutableStateOf("") }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val itens = remember { mutableStateListOf<Pair<String, String>>() }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo para Nome da Lista
            Text("Nome da Lista:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            TextField(
                value = nomeDaLista,
                onValueChange = { nomeDaLista = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para Nome do Produto e Quantidade
            Text("Itens:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextField(
                    value = nomeDoProduto,
                    onValueChange = { nomeDoProduto = it },
                    label = { Text("Nome do Produto") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                TextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botões para Acrescentar Item e Lista
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                        itens.add(Pair(nomeDoProduto, quantidade))
                        nomeDoProduto = ""
                        quantidade = ""
                    }
                }) {
                    Text("Acrescentar Item")
                }
                Button(onClick = {
                    // Ação para criar uma nova lista
                }) {
                    Text("Acrescentar Lista")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exibição da Lista
            Text("Itens Adicionados:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            LazyColumn {
                items(itens) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.first)
                        Text(text = item.second)
                    }
                }
            }
        }
    }
}
