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
fun Ecra01(navController: NavController, listasViewModel: ListasViewModel) {
    val listas = listasViewModel.listas

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
                text = "Listas Criadas",
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )

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

                        Button(
                            onClick = {
                                navController.navigate("ecra03/${lista}")  // Navega para o Ecra03 passando o nome da lista
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .width(80.dp)
                                .height(40.dp)
                        ) {
                            Text(text = "Editar", color = Color.White)
                        }
                        Button(
                            onClick = {
                                listasViewModel.removerLista(lista)
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
fun Ecra02(navController: NavController, listasViewModel: ListasViewModel) {
    var nomeDaLista by remember { mutableStateOf("") }
    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val itens = remember { mutableStateListOf<Pair<String, String>>() }

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
                            listasViewModel.adicionarLista(nomeDaLista)
                            nomeDaLista = ""
                            itens.clear()
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
    // Recuperar a lista de itens da lista específica
    val listaItems = listasViewModel.obterItensDaLista(listaNome).toMutableList()

    var nomeDoProduto by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController, appItems = Destino.toList) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Título da tela
            Text(
                text = "Editar Lista: $listaNome",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )

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

            // Botões: Acrescentar Item e Salvar Lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (nomeDoProduto.isNotBlank() && quantidade.isNotBlank()) {
                            val novoItem = nomeDoProduto to quantidade
                            listasViewModel.adicionarItemNaLista(listaNome, novoItem)
                            nomeDoProduto = ""
                            quantidade = ""
                        }
                    }
                ) {
                    Text(text = "Acrescentar Item")
                }

                Button(
                    onClick = {
                        // Atualiza os itens da lista
                        listasViewModel.atualizarLista(listaNome, listaItems)
                        navController.popBackStack() // Volta para a tela anterior
                    }
                ) {
                    Text(text = "Salvar Lista")
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
                                // Remove o item
                                listasViewModel.removerItemDaLista(listaNome, item)
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




