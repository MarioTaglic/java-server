package mariotaglic.projectchat;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;

public class ClientUI extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;

	private JPanel clientPane;
	private JTextField txtMessage;
	private JTextArea txtrChatHistory;

	private Thread runThread, listenThread;
	private boolean running = false;

	private Client client;

	public ClientUI(String username, String ipAddress, int port) {
		client = new Client(username, ipAddress, port);
		boolean connect = client.openConnection(ipAddress);
		if (connect) {
			System.err.println("connection failure");
			console("connection failed");
		}
		clientWindow();
		console(username + " attempting to connect to " + ipAddress + ": " + port);
		String connection = "/c/" + username + "/e/";
		client.send(connection.getBytes());
		runThread = new Thread(this);
		running = true;
		runThread.start();
	}

	private void clientWindow() {
		setTitle("ProjectChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 1000);
		setLocationRelativeTo(null);
		clientPane = new JPanel();
		clientPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(clientPane);

		GridBagLayout gbl_clientPane = new GridBagLayout();
		gbl_clientPane.columnWidths = new int[] { 500, 0 };
		gbl_clientPane.rowHeights = new int[] { 159, 0 };
		gbl_clientPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_clientPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		clientPane.setLayout(gbl_clientPane);

		txtrChatHistory = new JTextArea();
		txtrChatHistory.setBackground(new Color(255, 153, 102));
		txtrChatHistory.setFont(new Font("Monospaced", Font.PLAIN, 30));
		txtrChatHistory.setEditable(false);
		JScrollPane scroll = new JScrollPane(txtrChatHistory);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.gridwidth = 2;
		scrollConstraints.insets = new Insets(0, 0, 5, 0);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		clientPane.add(scroll, scrollConstraints);

		txtMessage = new JTextField();
		txtMessage.setBackground(new Color(255, 255, 255));
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage(txtMessage.getText(), true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 1;
		clientPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);

		JButton btnSend = new JButton("Send >>");
		btnSend.setBackground(new Color(51, 102, 204));
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(txtMessage.getText(), true);
			}
		});
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 1;
		clientPane.add(btnSend, gbc_btnSend);

		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				String disconnect = "/d/" + client.getID() + "/e/";
				sendMessage(disconnect, false);
				client.quit();
				running = false;
			}
		});

		setVisible(true);
		txtMessage.requestFocusInWindow();
	}

	private void sendMessage(String message, boolean checkMessage) {
		message = txtMessage.getText();
		if (checkMessage) {
			message = client.getUsername() + ": " + message;
			message = "/m/" + message;
		}
		client.send(message.getBytes());
		txtMessage.setText("");
		txtMessage.requestFocusInWindow();
	}

	public void listen() {
		listenThread = new Thread() {
			public void run() {
				while (running) {
					String messageData = client.receive();
					if (messageData.startsWith("/c/")) {
						client.setID(Integer.parseInt(messageData.split("/c/|/e/")[1]));
						console("Successfuly connected to the server!\nID:" + client.getID());
					} else if (messageData.startsWith("/m/")) {
						String extractText = messageData.substring(3).split("/e/")[0];
						console(extractText);
					} else if(messageData.startsWith("/p/")) {
						String timedOutText = "/p" +client.getID() + "/e";
						sendMessage(timedOutText, false);
					}
				}
			}
		};
		listenThread.start();
	}

	public void run() {
		listen();
	}

	public void console(String message) {
		txtrChatHistory.append(message + "\n");
	}
}