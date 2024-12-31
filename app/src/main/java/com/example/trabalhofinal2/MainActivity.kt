package com.example.trabalhofinal2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trabalhofinal2.ui.theme.TrabalhoFInal2Theme
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFInal2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("ecra01") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = {
                                    navController.navigate("signup")
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                navController = navController,
                                onSignUpSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        /*composable("mainApp") {
                            ProgramaPrincipal(navController = navController)
                        }*/
                        // Add routes for Ecra01 and Ecra02
                        composable("ecra01") {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .wrapContentSize(Alignment.Center)
                                ) {
                                    Text(
                                        text = "Lista de Itens",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        textAlign = TextAlign.Center,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                BottomNavigationBar(navController, appItems = Destino.toList)
                            }
                        }

                        composable("ecra02") {

                            var nomeDaLista by remember { mutableStateOf("") }
                            var nomeDoProduto by remember { mutableStateOf("") }
                            var quantidade by remember { mutableStateOf("") }

                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    // BottomNavigationBar no final do ecrã
                                    BottomNavigationBar(navController, appItems = Destino.toList)
                                }
                            ) { paddingValues ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues) // Para garantir que o conteúdo não sobreponha o BottomBar
                                ) {
                                    // Nome da Lista no topo do ecrã
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

                                    // Tabela para Nome do Produto e Quantidade
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Nome do Produto:",
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "Quantidade:",
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextField(
                                                value = nomeDoProduto,
                                                onValueChange = { nomeDoProduto = it },
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            )
                                            TextField(
                                                value = quantidade,
                                                onValueChange = { quantidade = it },
                                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Botão de Acrescentar abaixo dos campos
                                        Button(
                                            onClick = {
                                                // Ação do botão
                                                // Aqui você pode acessar os valores de nomeDaLista, nomeDoProduto e quantidade
                                            },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(text = "Acrescentar")
                                        }
                                    }
                                }
                            }
                        }









                    }
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    navController: NavHostController,
    onSignUpSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Seta para voltar ao login
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow),
                contentDescription = "Voltar"
            )
        }

        // Conteúdo da tela de criar conta
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Criar Conta", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de email
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de senha
            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de signup
            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(emailState.value, passwordState.value)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erro ao criar conta: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Conta")
            }
        }
    }
}


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val context = LocalContext.current

    // Layout para a tela de login
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de email
        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de senha
        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de login
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(emailState.value, passwordState.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(
                                context,
                                "Erro ao fazer login: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link para a tela de cadastro
        Text(
            text = "Não tem uma conta? Crie aqui!",
            modifier = Modifier.clickable { onNavigateToSignUp() },
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/*@Composable
fun ProgramaPrincipal(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, appItems = Destino.toList)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavigation(navController = navController)
        }
    }
}


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "ecra01") {
        composable("ecra01") {
            Ecra01()
        }
        composable("ecra02") {
            Ecra02()
        }
    }
}
*/
@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<Destino>) {
    BottomNavigation(backgroundColor = colorResource(id = R.color.purple_700), contentColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        appItems.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = item.title,
                        tint = if (currentRoute == item.route) Color.White else Color.White.copy(.4F)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (currentRoute == item.route) Color.White else Color.White.copy(.4F)
                    )
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}




