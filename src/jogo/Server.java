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
		Jogo(serverSocket, jogadores, receivedData);
	}

	public static void estabelecerConexao(DatagramSocket serverSocket, byte[] receivedData,
			ArrayList<Jogador> jogadores) throws Exception {
		int jogadoresProntos = 0;
		System.out.println("UDP server rodando!");

		while (jogadoresProntos < 2) {
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
					iniciar(serverSocket, "Esperando o outro jogador iniciar o jogo", jogador);
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
			byte[] sendData = "Conectado".getBytes();
			System.out.println("Jogador conectado: " + ipAddress + ":" + port);
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
			serverSocket.send(sendPacket);
			return new Jogador(ipAddress, port);
		} else {
			return null;
		}
	}

	public static void iniciar(DatagramSocket serverSocket, String mensagem, Jogador jogador) throws Exception {
		byte[] sendData = mensagem.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, jogador.getIp(), jogador.getPort());
		serverSocket.send(sendPacket);
	}

	public static void limparRecivedData(byte[] receivedData) {
		Arrays.fill(receivedData, (byte) 0);
	}

	public static Jogador JogadorExiste(ArrayList<Jogador> jogadores, InetAddress ipVerificar, int portaVerificar) {
		for (Jogador jogador : jogadores) {
			if (jogador.getIp().equals(ipVerificar) && jogador.getPort() == portaVerificar) {
				return jogador;
			}
		}
		return null;
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

	public static void Jogo(DatagramSocket serverSocket, ArrayList<Jogador> jogadores, byte[] receivedData)
			throws Exception {
		CorridaPalavras corridaPalavras = new CorridaPalavras();

		byte[] sendData = "Iniciando jogo...".getBytes();
		System.out.println("Todos os jogadores estão prontos. Iniciando o jogo!");

		for (Jogador jogador : jogadores) {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(sendPacket);
		}

		String palavraSelecionada = corridaPalavras.palavrasParaJogadores();

		System.out.println("Palavra selecionada: " + palavraSelecionada);

		String palavraSelecionadaEmbaralhada = Embaralhador.shuffle(palavraSelecionada);

		enviaPalavraEmbaralhada(serverSocket, jogadores, palavraSelecionadaEmbaralhada);

		while (true) {
			for (int i = 0; i < jogadores.size(); i++) {
				DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
				serverSocket.receive(receivePacket);
				String receiveSentence = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

				InetAddress ipJogador = receivePacket.getAddress();
				int portaJogador = receivePacket.getPort();

				if (corridaPalavras.comparaPalavras(palavraSelecionada, receiveSentence)) {
					System.out.println("Parabéns! O jogador na Porta " + portaJogador + " acertou a palavra!");

					String mensagemVencedor = "Você acertou a palavra: " + palavraSelecionada + "!";
					byte[] vencedorData = mensagemVencedor.getBytes();
					DatagramPacket vencedorPacket = new DatagramPacket(vencedorData, vencedorData.length, ipJogador,
							portaJogador);
					serverSocket.send(vencedorPacket);
					for (Jogador jogador : jogadores) {
						String mensagemPerdedor = "Você Perdeu! O jogador de ip: " + ipJogador + " e porta: "
								+ portaJogador + ",acertou primeiro a palavra: " + palavraSelecionada + "!";
						byte[] perdedorData = mensagemPerdedor.getBytes();
						if (!jogador.getIp().equals(ipJogador) || jogador.getPort() != portaJogador) {
							DatagramPacket perdedorPacket = new DatagramPacket(perdedorData, perdedorData.length,
									jogador.getIp(), jogador.getPort());
							serverSocket.send(perdedorPacket);
						}
					}
					// break;
				} else {
					for (Jogador jogador : jogadores) {
						String perderamTodos = "Você Perdeu! Os dois jogadores erraram";
						byte[] perderamTodosData = perderamTodos.getBytes();
						if (!jogador.getIp().equals(ipJogador) || jogador.getPort() != portaJogador) {
							DatagramPacket perdedorPacket = new DatagramPacket(perderamTodosData,
									perderamTodosData.length, jogador.getIp(), jogador.getPort());
							serverSocket.send(perdedorPacket);
						}
					}
				}
			}
			limparRecivedData(receivedData);
			reiniciarJogo(serverSocket, jogadores, receivedData);
		}
	}

	public static void reiniciarJogo(DatagramSocket serverSocket, ArrayList<Jogador> jogadores, byte[] receivedData)
			throws Exception {
		String mensagemReiniciar = "Quer jogar novamente? Responda com 'Sim' ou 'Não'.";
		byte[] reiniciarData = mensagemReiniciar.getBytes();
		for (Jogador jogador : jogadores) {
			DatagramPacket reiniciarPacket = new DatagramPacket(reiniciarData, reiniciarData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(reiniciarPacket);
		}
		int jogadoresQueQueremContinuar = 0;

		for (Jogador jogador : jogadores) {
			DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
			serverSocket.receive(receivePacket);
			String resposta = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

		    System.out.println("JOGADOR " + jogador.getIp() + ":" + jogador.getPort() + " RESPONDEU: " + resposta);
		    
			if (resposta.equalsIgnoreCase("Sim")) {
				jogador.setPronto();
			} else {
				jogador.setNaoPronto();
			}
			limparRecivedData(receivedData);
		}
		System.out.println("TODOS PRONTOS JOGADORES " + todosProntos(jogadores));
		if (todosProntos(jogadores)) {
			System.out.println("Todos os jogadores querem continuar. Reiniciando o jogo!");
			Jogo(serverSocket, jogadores, receivedData);
		} else {
			String mensagemEncerrar = "Nem todos os jogadores quiseram continuar. Encerrando o jogo.";
			byte[] MensagemEncerrarBytes = mensagemEncerrar.getBytes();
			for (Jogador jogador : jogadores) {
				DatagramPacket encerarJogo = new DatagramPacket(MensagemEncerrarBytes, mensagemEncerrar.length(),
						jogador.getIp(), jogador.getPort());
				serverSocket.send(encerarJogo);
			}
		}
	}

	public static boolean todosProntos(ArrayList<Jogador> jogadores) {
		for (Jogador jogador : jogadores) {
			if (jogador.getPronto() == false) {
				return false;
			}
		}
		return true;
	}
}
