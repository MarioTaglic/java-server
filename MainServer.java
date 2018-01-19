package mariotaglic.projectchat.server;

public class MainServer {
	
	private static int port;
	private Server server;
	
	public MainServer(int port) {
		this.port = port;
		server = new Server(port);
	}

	public static void main (String[] args) {
		if(args.length != 1) {
			System.out.println("Usage: java -jar ProjectChatServer.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
		new MainServer(port);
	}
}
