# Aplicação Android com Kotlin

Este projeto é uma aplicação Android desenvolvida em Kotlin para a disciplina de computação móvel, utilizando o Jetpack Compose para a interface e integrada ao Firebase para autenticação e banco de dados.
Este projeto tem como objetivo compartilhar listas entre duas pessoas, onde cada uma delas pode criar, editar e apagar listas conectadas ao firebase.

## Funcionalidades

### Ecra01:
- Exibe as listas do utilizador e as listas compartilhadas.
- Permite editar e apagar listas.
- Permite excluir a conta e fazer logout da mesma.
- Utiliza FirebaseAuth e FirebaseFirestore para utilização dos dados em tempo real.

### Ecra02:
- Tela para criação de listas, com campos para nome da lista, gmail da pessoa que vamos partilhar, nome dos produtos e quantidades.
- As listas criadas são armazenadas no Firestore e podem ser compartilhadas com outros utilizadores.

### Ecra03:
- Tela para edição de uma lista existente.
- Permite adicionar e remover itens e quantidades da lista.
- Utiliza Firebase para atualização dos dados em tempo real.

## Tecnologias Utilizadas
- Kotlin
- Jetpack Compose
- Firebase (Auth e Firestore)

## Melhorias
- Ter um ecrã para ter o perfil do utilizador para implementar as funcionalidades que estão no ecra01 como apagar a conta e sair da conta.
- Conseguir partilhar entre dois ou mais utilizadores.



