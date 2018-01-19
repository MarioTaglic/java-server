package mariotaglic.projectchat.server;

import java.net.InetAddress;

public class ClientServer {
	
	public String username;
	public InetAddress inetAddress;
	public int port;
	private final int ID;
	public int attemps = 0;
	protected int attempt;
	
	public ClientServer(String username, InetAddress inetAddress, int port, final int ID) {
		this.username = username;
		this.inetAddress = inetAddress;
		this.port = port;
		this.ID = ID;
	}
	
	public int getID() {
		return ID;
	}

}
