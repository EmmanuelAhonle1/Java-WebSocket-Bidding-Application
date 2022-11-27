package finalProject;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.beans.property.*;
import java.awt.*;
import java.awt.event.*;

import javafx.scene.control.*;


public class Client extends Application {
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Stage primaryStage;
	
	private Scene s;
	
	private GsonItem testing;
	private Item real;
	
	private Label responseTest = new Label();
	private TableView<GsonItem> auction = new TableView();
	
	
	private GsonItem table;
	private Users logger;
	private Label loginOutput;
	
	public void run(String[] args) throws Exception {
		launch(args);
	}



	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4243);
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
			new Client().run(args);

		} 	catch(NumberFormatException nfe) {
			System.out.println("oopsie");
		}	catch (Exception e) {
			e.printStackTrace();
		}

	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {

					
					String [] l = message.split(" -> ");
					
					String command = l[0];
					
					Gson gson = new Gson();

					
					switch(command)
					{
					
						case "Init" :
							auction.getItems().clear();
							
							
							GsonItem item = gson.fromJson(l[1], GsonItem.class);
														
							Platform.runLater(() -> {
								auction.getItems().add(item);

							});
							
							System.out.println("Initializing: " + l[1]);
							break;
						case "Message" :

							String m = l[1];
							Platform.runLater(()->{
							responseTest.setText(m);
							
							//auction.getItems().add(new Item("yoo","Hardly used",50,100,1000,"One bid placed"));


			            	//auction.getItems().removeAll(auction.getSelectionModel().getSelectedItem());
			            	auction.refresh();
							});
							break;
						
						case "Update" :
							String updater = l[1];
								GsonItem updateItem = gson.fromJson(updater, GsonItem.class);
								
									for(GsonItem v : auction.getItems()) {
										if(v.getName().equals(updateItem.getName())) {
											
											v.setBidHistory(updateItem.getBidHistory());
											System.out.println(v.toString());
											synchronized(this) {
												Platform.runLater(() -> {
												});
											}
											v.setCurrBid(updateItem.getCurrBid());
											
											auction.refresh();
									}
								}

							break;
						
						case "Valid Login" :
							Users sub = gson.fromJson(l[1], Users.class);
							System.out.println("Login succeeded for " + sub.getUser());
							Platform.runLater(() -> {
								try {
									loginOutput.setText("Logging in as " + sub.getUser() + "...");
									Thread.sleep(1000);
									writer.println("Initialize");
									writer.flush();
									auctionScene();

								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							});

						
						break;

					}
					

				}
			} catch (IOException ex) {
				System.out.println("Server has been closed");
				//ex.printStackTrace();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		setUpNetworking();
		

		this.primaryStage = primaryStage;

		loginScene(this.primaryStage);
	}
	
	private void sendToServer(String string) {
	    System.out.println("Sending to server: " + string);
	    writer.println(string);
	    writer.flush();
	}
	
    MediaPlayer mediaPlayer;
    public void music() {
    	String s = "drift.wav";

    	Media h = new Media(Paths.get(s).toUri().toString());
    	mediaPlayer = new MediaPlayer(h);
    	mediaPlayer.setVolume(.1);
    	mediaPlayer.play();
    	
    }
    
    private <T> void addTooltipToColumnCells(TableColumn<GsonItem,T> column) {

        Callback<TableColumn<GsonItem, T>, TableCell<GsonItem,T>> existingCellFactory 
            = column.getCellFactory();

        column.setCellFactory(c -> {
            TableCell<GsonItem, T> cell = existingCellFactory.call(c);

            Tooltip tooltip = new Tooltip();
            // can use arbitrary binding here to make text depend on cell
            // in any way you need:
            tooltip.textProperty().bind(cell.itemProperty().asString());

            cell.setTooltip(tooltip);
            return cell ;
        });
    }
    
    
    private void loginScene(Stage primStage) {
    	
    	

    	VBox storer = new VBox();
    	HBox user = new HBox();
    	HBox pass = new HBox();
    	
    	HBox buttons = new HBox();
    	
    	storer.setSpacing(10);
    	user.setSpacing(5);
    	pass.setSpacing(5);
    	
    	buttons.setSpacing(20);
    	
    	storer.setAlignment(Pos.CENTER);
    	user.setAlignment(Pos.CENTER);
    	pass.setAlignment(Pos.CENTER);
    	buttons.setAlignment(Pos.CENTER);
    	
    	loginOutput = new Label("");
    	
    	Label uLabel = new Label("Username:");
    	Label pLabel = new Label("Password:");
    	
    	TextField uTF = new TextField("");
    	TextField pTF = new TextField("");
    	
    	
    	Button login = new Button("Login");
        login.setOnAction(new EventHandler<ActionEvent>() {
       	 
            @Override
            public void handle(ActionEvent event) {
            	
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			
    			logger = new Users(uTF.getText(),pTF.getText());
    			
    			String login = gson.toJson(logger);
    			
    			sendToServer("Login Request -> " + login);
    			
    			
    			

    			


            } 
        });
    	Button guestSign = new Button("Login as Guest");
    	
    	user.getChildren().addAll(uLabel,uTF);
    	pass.getChildren().addAll(pLabel,pTF);
    	buttons.getChildren().addAll(login,guestSign);
    	
    	storer.getChildren().addAll(user,pass,loginOutput,buttons);
    	
    	uTF.requestFocus();
	    primaryStage.setOnCloseRequest(e -> {
	    	System.exit(0);
	    });

    	
    	
    	
    	Scene loginScene = new Scene(storer,300,150);

    	primStage.setScene(loginScene);
    	primStage.show();
    	
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void auctionScene() {
		VBox v = new VBox(10);
		HBox buttonHBox = new HBox(10);
		
		responseTest = new Label("");
		
		auction = new TableView();
		
		auction.setEditable(true);
		
		TableColumn item = new TableColumn("Item");
		item.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn description = new TableColumn("Description");
		description.setCellValueFactory(new PropertyValueFactory<>("description"));

		TableColumn currBid = new TableColumn("Current Bid");
		currBid.setCellValueFactory(new PropertyValueFactory<>("currBid"));


		TableColumn buyNow = new TableColumn("Buy Now");
		buyNow.setCellValueFactory(new PropertyValueFactory<>("buyNow"));

		TableColumn timer = new TableColumn("Timer");
		timer.setCellValueFactory(new PropertyValueFactory<>("timer"));
		
		TableColumn bidHistory = new TableColumn("Bid History");
		bidHistory.setCellValueFactory(new PropertyValueFactory<>("bidHistory"));
		

        
        currBid.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        currBid.setOnEditCommit(new EventHandler<CellEditEvent<GsonItem,Double>>(){

			@Override
			public void handle(CellEditEvent<GsonItem, Double> t) {
              ((GsonItem) t.getTableView().getItems().get(
              t.getTablePosition().getRow())
              ).setCurrBid(t.getNewValue());
			}
        	
        });
        
        bidHistory.setOnEditCommit(new EventHandler<CellEditEvent<Item,String>>(){

			@Override
			public void handle(CellEditEvent<Item, String> t) {
              ((Item) t.getTableView().getItems().get(
              t.getTablePosition().getRow())
              ).setBidHistory(t.getNewValue());
			}
        	
        });
        
      
            

		
		auction.getColumns().addAll(item,description,currBid,buyNow,timer,bidHistory);
		
		
		
		Button sender = new Button("Send");
		

		

        sender.setOnAction(new EventHandler<ActionEvent>() {
         	 
            @Override
            public void handle(ActionEvent event) {
            	
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			testing = auction.getSelectionModel().getSelectedItem();
    			
    			//GsonItem convert = new GsonItem(testing);
    			
    			
    			String string = gson.toJson(testing);
    			
    			sendToServer("Message -> " + string);
    			
				auction.refresh();

    			


            } 
        });
    
		
		
		
        Button placeBid = new Button("Place Bid");
        placeBid.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
            	
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			testing = auction.getSelectionModel().getSelectedItem();
    			
    			//GsonItem convert = new GsonItem(testing);
    			
    			
    			String string = gson.toJson(testing);
    			
    			sendToServer("Bid Request -> " + string + " -> " + logger.getUser());
    			

    			


            } 
        });
		

		v.getChildren().add(auction);
		v.getChildren().add(responseTest);
		buttonHBox.getChildren().addAll(sender,placeBid);
		v.getChildren().add(buttonHBox);
		s = new Scene(v,1000,500);
		
	    primaryStage.setOnCloseRequest(e -> {
	    	writer.println("Logout Request -> " + logger.getUser());
	    	writer.flush();
	    	System.exit(0);
	    	});

	    
        item.prefWidthProperty().bind(auction.widthProperty().multiply(0.2));
        description.prefWidthProperty().bind(auction.widthProperty().multiply(0.3));
        currBid.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        buyNow.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        currBid.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        timer.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        bidHistory.prefWidthProperty().bind(auction.widthProperty().multiply(0.2));
        
        
        for (TableColumn<GsonItem, ?> column : auction.getColumns()) {
            addTooltipToColumnCells(column);
        }
        
        item.setResizable(false);
        description.setResizable(false);
		
        //music();
		primaryStage.setScene(s);
		
    }
	

}



