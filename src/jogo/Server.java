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
		estabelecerConexao(serverSocket,receivedData,jogadores);
		limparRecivedData(receivedData);
		iniciarJogo(serverSocket, jogadores,receivedData);
		
	}
	
	
	public static void estabelecerConexao(DatagramSocket serverSocket, byte[] receivedData, ArrayList<Jogador> jogadores) throws Exception {
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

	public static void solicitaNum(DatagramSocket serverSocket, ArrayList<Jogador> jogadores) throws IOException {
		byte[] sendData = "Insira um numero de 0 a 3: ".getBytes();
		for (Jogador jogador : jogadores) {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(sendPacket);
		}

	}

	public static void iniciarJogo(DatagramSocket serverSocket, ArrayList<Jogador> jogadores,byte[] receivedData) throws Exception {
		limparRecivedData(receivedData);
		byte[] sendData = "Iniciando jogo...".getBytes();
		System.out.println("Todos os jogadores estão prontos. Iniciando o jogo!");

		for (Jogador jogador : jogadores) {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, jogador.getIp(),
					jogador.getPort());
			serverSocket.send(sendPacket);
		}

		solicitaNum(serverSocket, jogadores);
		
		limparRecivedData(receivedData);
		int respostasRecebidas = 0;
		while (respostasRecebidas < jogadores.size()) {
			System.out.println("Aguardando respostas de " + jogadores.size() + " jogadores...");
			
			
			DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
			serverSocket.receive(receivePacket);

			String resposta = new String(receivePacket.getData()).trim();

			System.out.println("Recebido do cliente: " + resposta);

			for (Jogador jogador : jogadores) {
				if (jogador.getIp().equals(receivePacket.getAddress())
						&& jogador.getPort() == receivePacket.getPort()) {
					System.out.println(
							"Jogador " + jogador.getIp() + ":" + jogador.getPort() + " respondeu: " + resposta);
					respostasRecebidas++;
					break;
				}
			}
		}
		System.out.println("Todas as respostas foram recebidas!");
	}
}
