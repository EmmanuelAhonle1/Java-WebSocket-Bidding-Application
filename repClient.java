package finalProject;

import java.util.Scanner;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;



class Client extends Application {

  private static String host = "192.168.56.1";
  private BufferedReader fromServer;
  private PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);
	private boolean isLoggedIn;

  
	private JTextArea incoming;
	private JTextField username;
	private JPasswordField password;
	private JLabel userLabel;
	private JLabel passLabel;
	private JLabel loginOutput;
	private JFrame loginFrame;
	
	private JFrame biddingFrame;
	
	
	Timer timer = new Timer();


  public static void main(String[] args) {
    try {
      new Client().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    launch(args);

  }
  
  public void run(String[] args) throws Exception {
		initView();
		setUpNetworking();
		
	}

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    Socket socket = new Socket(host, 5000);
    System.out.println("Connecting to... " + socket);
    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toServer = new PrintWriter(socket.getOutputStream());

    Thread readerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        String input;
        try {
          while ((input = fromServer.readLine()) != null) {
            System.out.println("From server: " + input);
            processRequest(input);
            if(input.equals("Logging in...")) {
            	//frame.setVisible(false);
            	loginOutput.setForeground(Color.green);
            	loginOutput.setText(input);
            	isLoggedIn = true;
            	Thread.sleep(500);
            	loginOutput.setForeground(Color.black);
            	loginFrame.setVisible(false);
            }
            else {
            	loginOutput.setForeground(Color.red);
            	loginOutput.setText(input);
            	Thread.sleep(500);
            	loginOutput.setForeground(Color.black);
            	loginOutput.setText("");
            }
            
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Thread writerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          String input = consoleInput.nextLine();
          String[] variables = input.split(",");
          Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
          GsonBuilder builder = new GsonBuilder();
          Gson gson = builder.create();
          sendToServer(gson.toJson(request));
        }
      }
    });

    readerThread.start();
    writerThread.start();
  }

  protected void processRequest(String input) {
    return;
  }

  protected void sendToServer(String string) {
    System.out.println("Sending to server: " + string);
    toServer.println(string);
    toServer.flush();
  }
  
  
	private void initView() {
		loginScreen();
		if(!isLoggedIn) {
			loginFrame.setVisible(true);
		}
		else {
			loginFrame.setVisible(false);
		}


	}
	
	class loginListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			
//	          String input = username.getText();
//	          String[] variables = input.split(",");
//	          Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
//	          GsonBuilder builder = new GsonBuilder();
//	          Gson gson = builder.create();
//	          sendToServer(gson.toJson(request));
//	          isLoggedIn = true;
			
			String pass = password.getText();
			String user = username.getText();
			
			LoginMessage loginRequest = new LoginMessage(user, pass);
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			sendToServer(gson.toJson(loginRequest));
			
			int x = 6;
	          
		}
	}
	
	
	private void loginScreen() {
		loginFrame = new JFrame("Ludicrously Simple Chat Client");
		JPanel mainPanel = new JPanel();
		mainPanel = new JPanel(new GridLayout(3,2,1,1));
		JPanel buttons_panel = new JPanel(new FlowLayout());
		
		
		userLabel = new JLabel("Username: ");
		username = new JTextField(20);
		
		passLabel = new JLabel("Password: ");
		password = new JPasswordField(20);
		loginOutput = new JLabel("");
		
		JButton loginButton = new JButton("Login");
		JButton cancelButton = new JButton("Cancel");
	
		
		buttons_panel.add(loginButton);
		buttons_panel.add(cancelButton);
		
		
		loginButton.addActionListener(new loginListener());
		cancelButton.addActionListener(new exitProgram());
		mainPanel.add(userLabel);
		mainPanel.add(username);
	    mainPanel.add(passLabel);
	    mainPanel.add(password);
	    mainPanel.add(loginOutput);
		loginFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	    loginFrame.getContentPane().add(buttons_panel, BorderLayout.PAGE_END);

		loginFrame.setSize(300, 150);
		loginFrame.setResizable(false);
	}
	
	
	private void biddingScreen() {
		biddingFrame = new JFrame("Auction Hall: (Logged in as '" + username.getText() + "')");
		JPanel mainPanel = new JPanel();
		mainPanel = new JPanel(new GridLayout(3,2,1,1));
		JPanel buttons_panel = new JPanel(new FlowLayout());
		
		
		

		biddingFrame.setSize(1000, 600);
		biddingFrame.setResizable(false);
		biddingFrame.setVisible(true);
	}
	
	
	class exitProgram implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
//			toServer.println(outgoing.getText());
//			toServer.flush();
//			outgoing.setText("");
//			outgoing.requestFocus();
			
	         System.exit(0);
	          
		}
	}
	
	class delay implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent ae) {                    
			
			
        }
	}
	
    private BorderPane root2 = new BorderPane();


	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("myCritterGUI");
        Scene scene = new Scene(root2, 1000, 1000);
        
        Stage stage = (Stage) root2.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	


}