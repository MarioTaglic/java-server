package mariotaglic.projectchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	private String username, ipAddress;
	private int port;

	private DatagramSocket socket;
	private InetAddress inetAddress;
	private Thread sendThread;

	private int ID = -1;

	public Client(String username, String ipAddress, int port) {
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public String getipAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	public boolean openConnection(String ipAddress) {
		try {
			socket = new DatagramSocket();
			inetAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String messageData = new String(packet.getData());
		return messageData;
	}

	public void send(final byte[] data) {
		sendThread = new Thread() {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

	public void quit() {
		new Thread() {
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		}.start();
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}
}
