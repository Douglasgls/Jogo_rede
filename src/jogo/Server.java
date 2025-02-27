package jogo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

	public static void main(String args[]) throws Exception {
		ArrayList<Jogador> jogadores = new ArrayList<>();
		DatagramSocket serverSocket = new DatagramSocket(3000);
		byte[] receivedData = new byte[1024];

		limparRecivedData(receivedData);
		estabelecerConexao(serverSocket, receivedData, jogadores);
		limparRecivedData(receivedData);
		JogoServidor(serverSocket, jogadores, receivedData);
	}

	public static void estabelecerConexao(DatagramSocket serverSocket, byte[] receivedData,
			ArrayList<Jogador> jogadores) throws Exception {
		int jogadoresProntos = 0;
		System.out.println("UDP server rodando!");

		while (jogadoresProntos < 4) {
			DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
			limparRecivedData(receivedData);
			serverSocket.receive(receivePacket);

			Jogador jogador = JogadorExiste(jogadores, receivePacket.getAddress(), receivePacket.getPort());

			if (jogador == null) {
				jogador = conectar(serverSocket, receivePacket);
				if (jogador != null) {
					jogadores.add(jogador);
				}
			} else {
				String mensagem = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
				if (mensagem.equals("Iniciar")) {
					jogador.setPronto();
					jogadoresProntos++;
					enviarMensagem(serverSocket, " ⏳ Aguardando outro jogador para iniciar... \n\n", jogador);
				}
			}
		}
		limparRecivedData(receivedData);
	}

	public static Jogador conectar(DatagramSocket serverSocket, DatagramPacket receivePacket) throws Exception {
		String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
		if (sentence.equals("Conectar")) {
			InetAddress ipAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.println("Jogador conectado: " + ipAddress + ":" + port);
			
			enviarMensagem(serverSocket, "✅ Conectado ao servidor!", ipAddress, port);
			return new Jogador(ipAddress, port);
		} 
		return null;
	}

	public static void JogoServidor(DatagramSocket serverSocket, ArrayList<Jogador> jogadores, byte[] receivedData)
			throws Exception {
		CorridaPalavras corridaPalavras = new CorridaPalavras();
		Boolean alguemGanhou = false;
		int erros = 0;

		enviarMensagemParaTodos(serverSocket, "Iniciando jogo...", jogadores);

		String palavraSelecionada = corridaPalavras.palavrasParaJogadores();
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("Palavra selecionada: " + palavraSelecionada);
		
		
		String palavraSelecionadaEmbaralhada = Embaralhador.shuffle(palavraSelecionada);
		enviaPalavraEmbaralhada(serverSocket, jogadores, palavraSelecionadaEmbaralhada);

		while (true) {
			erros = 0;
			for (int i = 0; i < jogadores.size(); i++) {
				DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
				serverSocket.receive(receivePacket);

				String receiveSentence = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

				InetAddress ipJogador = receivePacket.getAddress();
				int portaJogador = receivePacket.getPort();

				if (corridaPalavras.comparaPalavras(palavraSelecionada, receiveSentence) && !alguemGanhou) {
					alguemGanhou = true;
					System.out.println("Parabéns! O jogador na Porta " + portaJogador + " acertou a palavra!");
					 enviarMensagem(serverSocket, "Você acertou a palavra: " + palavraSelecionada + "! 🏆", ipJogador,portaJogador);
					 enviarMensagemParaTodosExceto(serverSocket, "Você Perdeu! O jogador de ip: " + ipJogador +
	                            " e porta: " + portaJogador + " acertou primeiro a palavra: " + palavraSelecionada + "!", jogadores, ipJogador, portaJogador);
				} else {
					erros++;
				}
				if (!alguemGanhou && erros == jogadores.size()) {
					enviarMensagemParaTodos(serverSocket, "Você Perdeu! Todos os + " + jogadores.size() + "jogadores erraram", jogadores);
				}
				limparRecivedData(receivedData);
			}
			reiniciarJogo(serverSocket, jogadores, receivedData);
		}
	}

	public static void reiniciarJogo(DatagramSocket serverSocket, ArrayList<Jogador> jogadores, byte[] receivedData)
			throws Exception {
		String mensagemReiniciar = "Quer jogar novamente? Responda com 'Sim' ou 'Nao'.";
		byte[] reiniciarData = mensagemReiniciar.getBytes();
		for (Jogador jogador : jogadores) {
			DatagramPacket reiniciarPacket = new DatagramPacket(reiniciarData, reiniciarData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(reiniciarPacket);
		}

		for (Jogador jogador : jogadores) {
			DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
			serverSocket.receive(receivePacket);
			String resposta = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

			// System.out.println("JOGADOR " + jogador.getIp() + ":" + jogador.getPort() + "
			// RESPONDEU: " + resposta);

			if (resposta.equalsIgnoreCase("Sim")) {
				jogador.setPronto();
			} else {
				jogador.setNaoPronto();
			}

			limparRecivedData(receivedData);
		}
		// System.out.println("TODOS PRONTOS JOGADORES " + todosProntos(jogadores));
		if (todosProntos(jogadores)) {
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
			System.out.println("Todos os jogadores querem continuar. Reiniciando o jogo!");
			JogoServidor(serverSocket, jogadores, receivedData);
		} else {
			enviarMensagemParaTodos(serverSocket, "Nem todos os jogadores quiseram continuar. Encerrando o jogo.",
					jogadores);
		}
	}
	
	public static void limparRecivedData(byte[] receivedData) {
		Arrays.fill(receivedData, (byte) 0);
	}
	
	public static boolean todosProntos(ArrayList<Jogador> jogadores) {
		for (Jogador jogador : jogadores) {
			if (jogador.getPronto() == false) {
				return false;
			}
		}
		return true;
	}

	public static void enviarMensagem(DatagramSocket serverSocket, String mensagem, Jogador jogador)
			throws IOException {
		enviarMensagem(serverSocket, mensagem, jogador.getIp(), jogador.getPort());
	}

	public static void enviarMensagem(DatagramSocket serverSocket, String mensagem, InetAddress ip, int porta)
			throws IOException {
		byte[] sendData = mensagem.getBytes();
		serverSocket.send(new DatagramPacket(sendData, sendData.length, ip, porta));
	}

	public static void enviarMensagemParaTodos(DatagramSocket serverSocket, String mensagem,
			ArrayList<Jogador> jogadores) throws IOException {
		for (Jogador jogador : jogadores) {
			enviarMensagem(serverSocket, mensagem, jogador);
		}
	}

	public static void enviarMensagemParaTodosExceto(DatagramSocket serverSocket, String mensagem,
			ArrayList<Jogador> jogadores, InetAddress ip, int porta) throws IOException {
		for (Jogador jogador : jogadores) {
			if (jogador.getIp() != ip && jogador.getPort() != porta) {
				enviarMensagem(serverSocket, mensagem, jogador);
			}
		}
	}
	
	public static void enviaPalavraEmbaralhada(DatagramSocket serverSocket, ArrayList<Jogador> jogadores,
			String palavraSelecionadaEmbaralhada) throws IOException {
		String Mensagem = "Desenbaralhe a palavra: " + palavraSelecionadaEmbaralhada;

		byte[] sendData = Mensagem.getBytes();

		for (Jogador jogador : jogadores) {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(sendPacket);
		}

	}

	public static Jogador JogadorExiste(ArrayList<Jogador> jogadores, InetAddress ipVerificar, int portaVerificar) {
		for (Jogador jogador : jogadores) {
			if (jogador.getIp().equals(ipVerificar) && jogador.getPort() == portaVerificar) {
				return jogador;
			}
		}
		return null;
	}
}
