package mariotaglic.projectchat;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel loginPane;
	
	private JLabel lblUsername;
	private JTextField txtUsername;
	
	private JLabel lblIPAddress;
	private JTextField txtIPAddress;
	
	private JLabel lblPort;
	private JTextField txtPort;
	
	public Login() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,700);
		setResizable(false);
		setLocationRelativeTo(null);
		loginPane = new JPanel();
		loginPane.setBackground(new Color(255, 153, 102));
		loginPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(loginPane);
		loginPane.setLayout(null);
		
		JLabel lblWelcomeLabel = new JLabel("Java Chat Server");
		lblWelcomeLabel.setFont(new Font("Monotype Corsiva", Font.PLAIN, 42));
		lblWelcomeLabel.setBounds(26, 60, 442, 33);
		loginPane.add(lblWelcomeLabel);
		
		lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Calibri Light", Font.PLAIN, 27));
		lblUsername.setBounds(26, 139, 366, 33);
		loginPane.add(lblUsername);
		
		txtUsername = new JTextField();
		txtUsername.setBackground(new Color(255, 255, 255));
		txtUsername.setFont(new Font("DialogInput", Font.PLAIN, 27));
		txtUsername.setBounds(26, 176, 442, 39);
		loginPane.add(txtUsername);
		txtUsername.setColumns(10);
		
		lblIPAddress = new JLabel("IP ADDRESS:");
		lblIPAddress.setFont(new Font("Calibri Light", Font.PLAIN, 27));
		lblIPAddress.setBounds(26, 232, 366, 33);
		loginPane.add(lblIPAddress);
		
		txtIPAddress = new JTextField();
		txtIPAddress.setBackground(new Color(255, 255, 255));
		txtIPAddress.setFont(new Font("DialogInput", Font.PLAIN, 27));
		txtIPAddress.setText("127.0.0.1");
		txtIPAddress.setBounds(26, 271, 442, 39);
		loginPane.add(txtIPAddress);
		txtIPAddress.setColumns(10);
		
		lblPort = new JLabel("PORT:");
		lblPort.setFont(new Font("Calibri Light", Font.PLAIN, 27));
		lblPort.setBounds(26, 323, 366, 33);
		loginPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setBackground(new Color(255, 255, 255));
		txtPort.setFont(new Font("DialogInput", Font.PLAIN, 27));
		txtPort.setBounds(26, 359, 442, 39);
		loginPane.add(txtPort);
		txtPort.setColumns(10);
		
		Button btnConnect = new Button("Connect");
		btnConnect.setBackground(new Color(255, 255, 255));
		btnConnect.setFont(new Font("DialogInput", Font.BOLD | Font.ITALIC, 24));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = txtUsername.getText();
				String IPAddress = txtIPAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(username,IPAddress,port);	
			}
		});
		btnConnect.setBounds(183, 480, 151, 41);
		loginPane.add(btnConnect);
	}
	
	private void login(String username, String ipAddress, int port) {
		dispose();
		new ClientUI(username, ipAddress, port);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login loginFrame = new Login();
					loginFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
