package jogo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Cliente {

	public static void main(String args[]) throws Exception {
		String receiveSentence = "";
		DatagramSocket clientSocket = new DatagramSocket();
		byte[] receivedData = new byte[1024];
		InetAddress ipAddress = InetAddress.getByName("localhost");
		int port = 3000;

		conectar(clientSocket, ipAddress, port, receivedData, receiveSentence);

	}

	public static void conectar(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData,
			String receiveSentence) throws Exception {
		limparRecivedData(receivedData);
		enviarMensagem(clientSocket, "Conectar", ipAddress, port);
		String resposta = receberMensagem(clientSocket, receivedData);
		System.out.println(resposta);

		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println(" 🎮 Bem-vindo ao Jogo das Palavras! 🎮 ");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

		iniciarJogo(clientSocket, ipAddress, port, receivedData, receiveSentence);
		limparRecivedData(receivedData);
	}

	public static void iniciarJogo(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData,
			String receiveSentence) throws Exception {
		System.out.println(" 🔹 Pressione ENTER para iniciar...");
		new BufferedReader(new InputStreamReader(System.in)).readLine();
		enviarMensagem(clientSocket, "Iniciar", ipAddress, port);

		limparRecivedData(receivedData);
		jogoCliente(clientSocket, ipAddress, port, receivedData, receiveSentence);
		limparRecivedData(receivedData);
	}

	public static void jogoCliente(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData,
			String receiveSentence) throws Exception {
		BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
		
		do {
			receiveSentence = receberMensagem(clientSocket, receivedData);

			if (receiveSentence.startsWith("⏳ Aguardando outro jogador para iniciar...")) {
				System.out.println(" " + receiveSentence);
			} else if (receiveSentence.startsWith("Iniciando jogo...")) {
				System.out.println();
				System.out.println("##########" + " VAMOS JOGAR " + "##########");
				System.out.println();
			} else {
				System.out.println(receiveSentence);
			}
			
			if (receiveSentence.startsWith("Desenbaralhe a palavra: ")) {
				System.out.println("⌨️ Digite sua resposta: ");
				String sentence = keyboardReader.readLine();

				byte[] resposta = sentence.getBytes();
				DatagramPacket respostaPacket = new DatagramPacket(resposta, resposta.length, ipAddress, port);

				clientSocket.send(respostaPacket);
			}
			limparRecivedData(receivedData);
		} while (!receiveSentence.startsWith("Você Perdeu!") && !receiveSentence.startsWith("Você acertou a palavra:")
				&& !receiveSentence.startsWith("Nem todos os jogadores quiseram continuar. Encerrando o jogo."));

		if (!receiveSentence.equals("Nem todos os jogadores quiseram continuar. Encerrando o jogo.")) {
			reiniciarJogo(clientSocket, ipAddress, port, receivedData);
		}
		limparRecivedData(receivedData);
	}

	public static void reiniciarJogo(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData)
			throws Exception {
		
		BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));

		String mensagemReinicio = receberMensagem(clientSocket, receivedData);
		System.out.println(mensagemReinicio);
		System.out.println("Digite sua resposta (Sim/Não):");
		
		String resposta = keyboardReader.readLine();
		enviarMensagem(clientSocket, resposta, ipAddress, port);

		if (resposta.equalsIgnoreCase("Sim")) {
			jogoCliente(clientSocket, ipAddress, port, receivedData, null);
		} else {
			System.out.println("Jogo encerrado.");
		}
		limparRecivedData(receivedData);
	}

	private static void enviarMensagem(DatagramSocket socket, String mensagem, InetAddress ip, int port)
			throws Exception {
		byte[] sendData = mensagem.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
		socket.send(sendPacket);
	}

	private static String receberMensagem(DatagramSocket socket, byte[] buffer) throws Exception {
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(receivePacket);
		return new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
	}

	public static void limparRecivedData(byte[] receivedData) {
		Arrays.fill(receivedData, (byte) 0);
	}

}
