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
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] receivedData = new byte[1024];
        InetAddress ipAddress = InetAddress.getByName("localhost");
        int port = 3000;

        conectar(clientSocket, ipAddress, port, receivedData);

    }

    public static void conectar(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData) throws Exception {
        limparRecivedData(receivedData);
        byte[] sendData = "Conectar".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
        
        DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
        clientSocket.receive(receivePacket);
        String receiveSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println(receiveSentence);
        iniciar(clientSocket, ipAddress, port, receivedData);
    }

    public static void iniciar(DatagramSocket clientSocket, InetAddress ipAddress, int port, byte[] receivedData) throws Exception {
        limparRecivedData(receivedData);
        System.out.println("Pressione Enter para iniciar o jogo");
        System.in.read();
        
        byte[] sendData = "Iniciar".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
 
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
                
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
            clientSocket.receive(receivePacket);
            
            String receiveSentence = new String(receivePacket.getData(),0, receivePacket.getLength()).trim();
            
            System.out.println(receiveSentence);

            if (receiveSentence.equals("Insira um numero de 0 a 3: ")) {
				String numero = keyboardReader.readLine();
				byte[] numeroBytes = numero.getBytes();
                DatagramPacket sendPacketServer = new DatagramPacket(numeroBytes, numeroBytes.length, ipAddress, port);
                clientSocket.send(sendPacketServer);
            }
        }
        
    }

    
    public static void limparRecivedData(byte[] receivedData) {
        Arrays.fill(receivedData, (byte) 0);
    }
}
