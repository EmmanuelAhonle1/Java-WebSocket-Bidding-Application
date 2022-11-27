package finalProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import javafx.scene.control.TextField;


public class Server {
	private static ArrayList<PrintWriter> clientOutputStreams;
	
	
	static Timer timer;
	private static int counter;
	
	private static String fileInput;
	
	private static ArrayList<GsonItem> tb = new ArrayList<GsonItem>();
	
	String currentTime;
	
	private static Boolean debug = false;
	private static ArrayList<Users> allUsers = new ArrayList<Users>();
	
	public static void main(String[] args) {
		try {
			//debugMode();
			
			setTimer(03,00);
			
			initializeItems();
			
			initializeLogins();
			
			new Server().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static void setTimer(int min, int sec) {
		// TODO Auto-generated method stub
		counter = (min * 60) + sec%60;
	}


	private void setUpNetworking() throws Exception {
		timerInit();
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
	
	
	private static void initializeItems() {
		try {
			Scanner o = new Scanner(new File("input.txt"));
			String name;
			String description;
			double currBid;
			double buyNow;
			String timer;
			String bidHistory;
			
			
			
			while(o.hasNextLine()) {
				
				
					name = o.next();
					o.nextLine();
					description = o.nextLine();
					currBid = o.nextDouble();
					buyNow = o.nextDouble();
					timer = o.next();
					bidHistory = o.nextLine();
					tb.add(new GsonItem(name,description,currBid,buyNow,timer,bidHistory));
					if(o.hasNextLine()) {
						o.nextLine();
					}
	
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void initializeLogins() {
		String username;
		String password;
		
		try {
			Scanner o = new Scanner(new File("users.txt"));
			
			while(o.hasNextLine()) {
				if(o.next().equals("Username:")) {
					username = o.next();
					if(o.next().equals("Password:")) {
						password = o.next();
						allUsers.add(new Users(username,password));
					}
					
					
				}
				o.nextLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void notifyClients(String message) {

		System.out.println("Sending to Client: " + message);

		for (PrintWriter writer : clientOutputStreams) {
			writer.println(message);
			writer.flush();
		}
	}
	
	private void initializeClient() {
		PrintWriter writer = clientOutputStreams.get(clientOutputStreams.size()-1);
			for(GsonItem g : tb) {
				
				String daf = g.toString();
				writer.println("Init -> " + g.toString());
				writer.flush();
			}
		

	}
	
	private void loginAttempt(Users possibleUser) {
		if(possibleUser.getUser().equals("guest") && possibleUser.getPass().equals("d41d8cd98f00b204e9800998ecf8427e")) {
			PrintWriter writer = clientOutputStreams.get(clientOutputStreams.size()-1);
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String valid = gson.toJson(possibleUser);
			System.out.println("Login Successful: " + possibleUser.getUser());
			writer.println("Valid Login -> " + valid);
			writer.flush();
		}
		for(Users user : allUsers) {
			if(user.getUser().equals(possibleUser.getUser()) && user.getPass().equals(possibleUser.getPass())) {
				PrintWriter writer = clientOutputStreams.get(clientOutputStreams.size()-1);
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				
				String valid = gson.toJson(possibleUser);
				System.out.println("Login Successful: " + possibleUser.getUser());
				writer.println("Valid Login -> " + valid);
				writer.flush();
				
			}
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
					
					String [] parsedMsg = message.split(" -> ");
					System.out.println(message);
	    			GsonBuilder builder = new GsonBuilder();
	    			Gson gson = builder.create();
				
					switch(parsedMsg[0]) {
					
					case "Login Request" :
						Users possibleUser = gson.fromJson(parsedMsg[1], Users.class);
						loginAttempt(possibleUser);
						break;
						
					case "Logout Request" :
						System.out.println("Logging out: " + parsedMsg[1]);
						break;
						
					case "Message" :
						notifyClients(message);
						break;
						
					
					case "Initialize" :
						
						System.out.println("initializing");
						initializeClient();
						break;
						
					case "Bid Request" :
						gson = new Gson();
						GsonItem j = gson.fromJson(parsedMsg[1], GsonItem.class);
						for(GsonItem item : tb) {
							if(item.getName().equals(j.getName())) {
								if( j.getCurrBid()> item.getCurrBid()) {
									
									String user = parsedMsg[2];
									item.setCurrBid(j.getCurrBid());
									item.setWinningBidder(user);
									System.out.println("Bid Made for " + j.getName() + " by " + user + ": " + j.getCurrBid());
									item.setBidHistory(item.getBidHistory() + ("Bid Made for " + j.getName() + " by " + user +  ": " + j.getCurrBid() + "\n"));
									String msg = gson.toJson(item);
									updateAuction("Valid Bid -> " + msg);
								}
							}

						}
						
						
					}
					
					

				}
			} catch (IOException e) {
				//e.printStackTrace();
				
				System.out.println("Client Closed...");
			}
		}
	}
	
	private static void timerInit() {
		
		timer = new Timer();
		
		TimerTask tt = new TimerTask() {  
		    @Override  
		    public void run() {  
		        //System.out.println("Task Timer on Fixed Rate");
		        //System.out.println(counter++);
		    	
		    	if(counter>=1) {
			    	counter--;
		    	}
		        String time = String.format("[%02d:%02d]", counter / 60, counter % 60);
		        System.out.println(time);
		        
		        updateCountdown(time);
		        if(time.equals("[00:00]") && !debug) {
		        	winningBid();
		        	System.out.println("Server Closing...");
		        	System.exit(0);
		        }
		    }

			private void updateCountdown(String message) {
				// TODO Auto-generated method stub
				for(PrintWriter writer : clientOutputStreams) {
					//System.out.println("Timer -> " + message);
					writer.println("Timer -> " + message);
					writer.flush();
				}
			};  
		};  
		timer.scheduleAtFixedRate(tt,0,1000); 

	}
	
	private static void debugMode() {
		debug = true;
	}
	
	private  void updateAuction(String message) {
		for (PrintWriter writer : clientOutputStreams) {
			System.out.println(message);
			writer.println(message);
			writer.flush();
		}
	}
	
	private static void winningBid() {
		System.out.println("Final Bid Results Sent...");

		for(GsonItem item : tb) {
			String def = "No one";
			if(item.getWinningBidder() != null) {
				def = item.getWinningBidder();
			}
			item.setBidHistory(item.getBidHistory() + def + " has won " + item.getName() + "\n");

		for (PrintWriter writer : clientOutputStreams) {

				String converted = item.toString();
				System.out.println("Winners -> " + converted);
				writer.println("Winners -> " + converted);
				writer.flush();
			}

		}
	}

}

