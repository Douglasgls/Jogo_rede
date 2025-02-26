# Corrida Das Palavras

              

## Integrantes da Equipe
- Douglas Paz
- Helton Carneiro

## Descrição do Jogo

O Jogo das Palavras é um jogo multiplayer onde os jogadores recebem uma palavra embaralhada e devem adivinhar a palavra correta o mais rápido possível. O primeiro jogador a acertar ganha a rodada. O jogo é baseado em comunicação via UDP, utilizando sockets para interação entre clientes e servidor.

Como Executar o Projeto

1. Compilar o Servidor

Abra um terminal e compile o servidor:

```bash
javac jogo/Server.java
```
Execute o servidor:

```bash
java jogo.Server
```
2. Compilar e Executar os Clientes

Abra outro terminal e compile o cliente:

```bash
javac jogo/Cliente.java
```
Execute o cliente:

```bash
java jogo.Cliente
```

Repita esse processo para cada jogador que desejar participar do jogo.

## Como Jogar

Ao iniciar, cada jogador deve se conectar ao servidor enviando uma mensagem de conexão.

Assim que 4 jogadores estiverem conectados, o jogo iniciará automaticamente.

O servidor enviará uma palavra embaralhada para todos os jogadores.

Os jogadores devem tentar adivinhar a palavra correta e enviá-la ao servidor.

O primeiro jogador a acertar a palavra vence a rodada, e os demais recebem uma mensagem informando quem ganhou.

No final da rodada, os jogadores podem optar por jogar novamente ou sair do jogo.

Divirta-se!



