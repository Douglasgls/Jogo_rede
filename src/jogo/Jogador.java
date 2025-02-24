package jogo;

import java.net.InetAddress;

public class Jogador {
	private InetAddress ip;
	private int port;
	private boolean pronto = false;
	
	public Jogador(InetAddress ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public boolean getPronto() {
		return pronto;
	}
	
	public void setPronto() {
		this.pronto = true;
	}
	
	public void setNaoPronto() {
		this.pronto = false;
	}
}
