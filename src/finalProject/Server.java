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

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import javafx.scene.control.TextField;


public class Server {
	private ArrayList<PrintWriter> clientOutputStreams;
	
	
	static Timer timer;
	private static int counter = 0;
	
	private static String fileInput;
	
	private static ArrayList<GsonItem> tb = new ArrayList<GsonItem>();
	
	String currentTime;
	
	private static Boolean debug = false;
	
	
	public static void main(String[] args) {
		try {
			debugMode();
			
		
			
			//initializeItems();
			initializeItems2();
			new Server().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void setUpNetworking() throws Exception {
		//timerInit();
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
		GsonItem change = new GsonItem("yesiirrr","Hardly used",50,100,1000,"One bid placed");
		tb.add(change);
		tb.add(new GsonItem("bruhhhh","Hardly used",50,100,1000,"One bid placed"));
	}
	
	private static void initializeItems2() {
		try {
			Scanner o = new Scanner(new File("input.txt"));
			String name;
			String description;
			double currBid;
			double buyNow;
			int timer;
			String bidHistory;
			
			
			
			while(o.hasNextLine()) {
				
				
					name = o.nextLine();
					description = o.nextLine();
					currBid = o.nextDouble();
					buyNow = o.nextDouble();
					timer = o.nextInt();
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
					System.out.println("read " + message);

				
					switch(parsedMsg[0]) {
						
					case "Message" :
						notifyClients(message);
						break;
						
					
					case "Initialize" :
						
						System.out.println("initializing");
						initializeClient();
						break;
						
					case "Bid Request" :
						Gson gson = new Gson();
						GsonItem j = gson.fromJson(parsedMsg[1], GsonItem.class);
						for(GsonItem item : tb) {
							if(item.name.equals(j.name)) {
								if( j.currBid> item.currBid) {
									item.currBid = j.currBid;
									System.out.println("Bid Made for " + j.name + ": " + j.currBid);
									item.bidHistory += ("Bid Made for " + j.name + ": " + j.currBid + " | ");
									
									updateAuction("Update -> " + parsedMsg[1]);
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
		    	counter++;
		        String time = String.format("[%02d:%02d]", counter / 60, counter % 60);
		        System.out.println(time);
		        if(time.equals("[00:04]") && !debug) {
		        	System.out.println("Server Closing...");
		        	System.exit(0);
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
			writer.println(message);
			writer.flush();
		}
	}

}

