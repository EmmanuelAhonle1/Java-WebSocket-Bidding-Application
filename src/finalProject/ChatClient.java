package finalProject;

import java.io.*;
import java.net.*;
import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import finalProject.LoginMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.layout.GridPane;
import java.awt.*;
import java.awt.event.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;


public class ChatClient extends Application {
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	
	
	private Stage pStage;
    BorderPane root2 = new BorderPane();
    Scene scene;
    Label loginOutput;
	

	public void run(String[] args) throws Exception {
		launch(args);
		
		//initView();
		
		

	}

	private void initView() {
		JFrame frame = new JFrame("Ludicrously Simple Chat Client");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(650, 500);
		frame.setVisible(true);

	}
	
	private void loginView(Stage primaryStage) {
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		Label uLabel = new Label("Username:");
		Label pLabel = new Label("Password:");
		
		loginOutput = new Label("");
		
		TextField username = new TextField("");
		PasswordField password = new PasswordField();
		
		
		uLabel.setMinWidth(50);
		uLabel.setMinHeight(50);
		
		gridPane.add(uLabel, 0, 0);
		gridPane.add(pLabel, 0, 1);
		gridPane.add(username, 1, 0);
		gridPane.add(password, 1, 1);
		
		
		GridPane buttonGrid = new GridPane();
		buttonGrid.setAlignment(Pos.CENTER);
		Button login = new Button("Login");
		Button cancel = new Button("Cancel");
		
		buttonGrid.add(login, 0, 0);
		buttonGrid.add(cancel, 1, 0);
		
		BorderPane root = new BorderPane(gridPane);
		BorderPane hello = new BorderPane(loginOutput);
		
		
		VBox v = new VBox(10,root,hello,buttonGrid);
		root.setCenter(gridPane);
		hello.setCenter(loginOutput);
		
        cancel.setOnAction(new EventHandler<ActionEvent>() {
       	 
            @Override
            public void handle(ActionEvent event) {
            	//PopUp.displayAllStats();
            	System.exit(0);
            }
        });
        
        login.setOnAction(new EventHandler<ActionEvent>() {
          	 
            @Override
            public void handle(ActionEvent event) {
            	//PopUp.displayAllStats();
//            	scene = new Scene(new VBox(),1000,1000);
//            	primaryStage.setScene(scene);
            	
            	String user = username.getText();
            	String pass = password.getText();
            	
    			LoginMessage loginRequest = new LoginMessage(user, pass);
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			
    			String string = gson.toJson(loginRequest);
    			
    		    
    			sendToServer(gson.toJson(loginRequest));


            }


        });
        

		
		
		scene = new Scene(v,250,150);
		
		primaryStage.setTitle("Login");
	}
	
	private void auctionScene(Stage primaryStage) {
		VBox v = new VBox(10);
		scene = new Scene(v,500,500);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	private void sendToServer(String string) {
		// TODO Auto-generated method stub
	    System.out.println("Sending to server: " + string);
	    writer.println(string);
	    writer.flush();
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("192.168.56.1", 4243);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		
		
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class SendButtonListener implements ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent ev) {
			writer.println(outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}


	}
	


	public static void main(String[] args) {
		try {
			new ChatClient().run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
						//incoming.append(message + "\n");
					
					System.out.println(message);
					final String m = message;
					
					if(message.equals("Logging in...")) {
                        Platform.runLater(() -> {
                        	loginOutput.setText(m);
                        	delay(1000,() -> {loginOutput.setText("");
                        	auctionScene(pStage);});
                        });
					}
					
					if(message.equals("Invalid Username or Password")) {
						
                        Platform.runLater(() -> {
    						loginOutput.setText(m);
                        	delay(500,() -> {loginOutput.setText("");});
                        });

					}
					
					
					
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected void loginRequest(String input) {
		return;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
//		loginView(primaryStage);
		setUpNetworking();
		
		loginView(primaryStage);
		
		pStage = primaryStage;

		
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
      }
}
