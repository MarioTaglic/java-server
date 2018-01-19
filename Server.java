package mariotaglic.projectchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

	private List<ClientServer> clients = new ArrayList<ClientServer>();
	private List<Integer> clientResponse = new ArrayList<Integer>();

	private int port;

	private DatagramSocket socket;

	private Thread runThread, clientManagerThread, sendThread, receiveThread;

	private boolean running = false;

	private final int MAX_ATTEMPS = 3;
	private Scanner reader;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		runThread = new Thread(this);
		runThread.start();
	}

	public void run() {
		running = true;
		System.out.println("Server started on port: " + port);
		ClientManager();
		receive();
		reader = new Scanner (System.in);
		while (running) {
			String command = reader.nextLine();
			if (!command.startsWith("/")) {
				sendAll("/m/Sever: " + command + "/e/");
				continue;
			}
			command = command.substring(1);
			if (command.startsWith("kick")) {
				String username = command.split(" ")[1];
				int ID = -1;
				boolean numCheck = true;
				try {
					ID = Integer.parseInt(command);
					numCheck = true;
				} catch (NumberFormatException e) {
					numCheck = false;
				}

				if (numCheck) {
					boolean exists = false;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getID() == ID) {
							exists = true;
						}
					}
					if (exists)
						disconnect(ID, true);
					else
						System.out.println("Client " + ID + " doesn't exist! CHECK ID number.");
				} else {
					for (int i = 0; i < clients.size(); i++) {
						if (username.equals(clients.get(i).username)) {
							disconnect(clients.get(i).getID(), true);
							break;
						}

					}
				}
			}
		}
	}

	private void ClientManager() {
		clientManagerThread = new Thread() {
			public void run() {
				while (running) {
					sendAll("/p/server");
					try {
						Thread.sleep(2000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						if (!clientResponse.contains(clients.get(i).getID())) {
							if (clients.get(i).attempt >= MAX_ATTEMPS) {
								disconnect(clients.get(i).getID(), false);
							} else {
								clients.get(i).attempt++;
							}
						} else {
							clientResponse.remove(new Integer(clients.get(i).getID()));
							clients.get(i).attempt = 0;
						}
					}
				}

			}
		};
		clientManagerThread.start();
	}

	private void receive() {
		receiveThread = new Thread() {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					processes(packet);
				}
			}
		};
		receiveThread.start();
	}

	private void send(final byte[] data, final InetAddress inetAddress, final int port) {
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

	private void sendAll(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ClientServer client = clients.get(i);
			send(message.getBytes(), client.inetAddress, client.port);
		}
	}

	private void convertSend(String message, InetAddress inetAddress, int port) {
		message += "/e/";
		send(message.getBytes(), inetAddress, port);
	}

	private void processes(DatagramPacket packet) {
		String string = new String(packet.getData());

		if (string.startsWith("/c/")) {
			int UID = UniqueIdentifier.getIdentifier();
			System.out.println("Idenitfier: " + UID);
			String username = string.split("/c/|/e/")[1];
			
			clients.add(
					new ClientServer(username, packet.getAddress(), packet.getPort(), UID));
			System.out.println(username + "(" + UID + ") connected!");
			String ID = "/c/" + UID;
			convertSend(ID, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			sendAll(string);
		} else if (string.startsWith("/d")) {
			String ID = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(ID), true);
		} else if (string.startsWith("/p/")) {
			clientResponse.add(Integer.parseInt(string.split("/p/|/e/")[1]));
		} else {
			System.out.println(string);
		}
	}

	private void disconnect(int ID, boolean status) {
		ClientServer client = null;
		boolean exist = false;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == ID) {
				client = clients.get(i);
				exist = true;
				clients.remove(i);
				break;
			}
		}
		if(!exist)
			return;
		String message = "";
		if (status) {
			message = "Client " + client.username + " (" + client.getID() + ")@ " + client.inetAddress.toString() + ": "
					+ client.port + " disconnected";
		} else {
			message = "Client " + client.username + " (" + client.getID() + ")@ " + client.inetAddress.toString() + ": "
					+ client.port + " timed out";
		}
		System.out.println(message);
	}
}
