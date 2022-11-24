package finalProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import com.google.gson.Gson;

import finalProject.LoginMessage;

public class ChatServer extends Observable{
	private ArrayList<PrintWriter> clientOutputStreams;

	public static void main(String[] args) {
		try {
			new ChatServer().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4243);
		while (true) {
			Socket clientSocket = serverSock.accept();
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			clientOutputStreams.add(writer);
			
			

			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection");
		}

	}

	private void notifyClients(String message) {


		for (PrintWriter writer : clientOutputStreams) {
			writer.println(message);
			writer.flush();
		}
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("read " + message);
					loginRequest(message);
					//notifyClients(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	  protected void loginRequest(String input) {
		  
		  String output;
		    Gson gson = new Gson();
		    LoginMessage message = gson.fromJson(input, LoginMessage.class);
			if (message.username.equals("root") &&  message.password.equals("pass")) {
			  output = "Logging in...";
		  }
		  else {
			  output = "Invalid Username or Password";
		  }
		  
		  this.setChanged();
		  //this.notifyObservers(output);
		  notifyClients(output);
		  
	  }

}
