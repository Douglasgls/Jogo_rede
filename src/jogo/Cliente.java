package jogo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {

    public static void main(String args[]) throws Exception {
    	String receiveSentence = "";
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] receivedData = new byte[1024];
        InetAddress ipAddress = InetAddress.getByName("localhost");
        int port = 3000;

        conectar(clientSocket, ipAddress, port, receivedData, receiveSentence);

    }

    public static void conectar(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData, String receiveSentence) throws Exception {
        limparRecivedData(receivedData);
        byte[] sendData = "Conectar".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
        
        DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
        clientSocket.receive(receivePacket);
        receiveSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(receiveSentence);
        iniciarJogo(clientSocket, ipAddress, port, receivedData,receiveSentence);
    }

    public static void iniciarJogo(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData, String receiveSentence) throws Exception {
        System.out.println("Pressione Enter para iniciar o jogo");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        
        byte[] sendData = "Iniciar".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
        limparRecivedData(receivedData);
        jogo(clientSocket, ipAddress, port, receivedData, receiveSentence);
    }
    public static void jogo(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData, String receiveSentence) throws Exception {
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));  
        do{
    		DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
    		clientSocket.receive(receivePacket);
    		receiveSentence = new String(receivePacket.getData(),0, receivePacket.getLength()).trim();
	        System.out.println(receiveSentence);//"Nem todos os jogadores quiseram continuar. Encerrando o jogo." deveria para com essa string
	        
	        if(receiveSentence.startsWith("Desenbaralhe a palavra: ")) {
	    		System.out.println("Digite o texto a ser enviado");
	    		String sentence = keyboardReader.readLine();
	    		
	    		byte[] resposta = sentence.getBytes();
	    		DatagramPacket respostaPacket = new DatagramPacket(resposta, resposta.length, ipAddress, port);

	    		clientSocket.send(respostaPacket);
	    		limparRecivedData(receivedData);
	        }
        }while(!receiveSentence.startsWith("Você Perdeu!") && !receiveSentence.startsWith("Você acertou a palavra:"));
        reiniciarJogo(clientSocket, ipAddress, port, receivedData);
    }

    
    public static void limparRecivedData(byte[] receivedData) {
        Arrays.fill(receivedData, (byte) 0);
    }
    public static void reiniciarJogo(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData) throws Exception {
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
        DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
        clientSocket.receive(receivePacket);
        String mensagemReinicio = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
        System.out.println(mensagemReinicio);
        System.out.println("Digite sua resposta (Sim/Não):");
        String resposta = keyboardReader.readLine();
        byte[] respostaData = resposta.getBytes();
        DatagramPacket respostaPacket = new DatagramPacket(respostaData, respostaData.length, ipAddress, port);
        clientSocket.send(respostaPacket);
        limparRecivedData(receivedData);
        if (resposta.equalsIgnoreCase("Sim")) {
            jogo(clientSocket, ipAddress, port, receivedData, "");
        }else {
        	System.out.println("Nem todos os jogadores quiseram continuar. Encerrando o jogo.");
        }
    }

}
