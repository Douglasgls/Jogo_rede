package jogo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    public static void main(String args[]) throws Exception {
        ArrayList<Jogador> jogadores = new ArrayList<>();
        DatagramSocket serverSocket = new DatagramSocket(3000);
        System.out.println("UDP server rodando!");
        byte[] receivedData = new byte[1024];
        // Espera que 4 jogadores se conectem
        while (jogadores.size() < 4) {
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
                iniciar(serverSocket, jogador);
            }
        }
        limparRecivedData(receivedData);
        // aqui vamos iniciar o jogo em si
    }

    public static Jogador conectar(DatagramSocket serverSocket, DatagramPacket receivePacket) throws Exception {
        String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        if (sentence.equals("Conectar")) {
            InetAddress ipAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            byte[] sendData = "Conectado".getBytes();
            System.out.println("Jogador conectado: "+ipAddress+":"+port);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
            serverSocket.send(sendPacket);
            return new Jogador(ipAddress, port);
        } else {
            return null;
        }
    }

    public static void iniciar(DatagramSocket serverSocket, Jogador jogador) throws Exception {
        jogador.setPronto();
        byte[] sendData = "Esperando o outro jogador iniciar o jogo".getBytes();
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
}
