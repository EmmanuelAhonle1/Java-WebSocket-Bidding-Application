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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellEditEvent;
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
	
	Item testing;
	Item real;
	
	private Label responseTest = new Label();
	TableView<Item> auction = new TableView();
	TableColumn item;
	Item table;
	
	Boolean initialized = false;
	
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

		} catch (Exception e) {
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
					
					//System.out.println(message);
					Gson gson = new Gson();

					
					switch(command)
					{
					
						case "Init" :
							auction.getItems().clear();
							
							
							GsonItem item = gson.fromJson(l[1], GsonItem.class);
							
							Item ja = new Item(item);
							
							Platform.runLater(() -> {
								auction.getItems().add(ja);

							});
							
							System.out.println("yup");
							break;
						case "Message" :

							String m = l[1];
							Platform.runLater(()->{
						    //modify your javafx app here.
							responseTest.setText(m);
							
							//auction.getItems().add(new Item("yoo","Hardly used",50,100,1000,"One bid placed"));


			            	//auction.getItems().removeAll(auction.getSelectionModel().getSelectedItem());
			            	auction.refresh();
							});
							break;
						
						case "Update" :
							String updater = l[1];
							    //modify your javafx app here.
								GsonItem updateItem = gson.fromJson(updater, GsonItem.class);
								real = new Item(updateItem);
//								auction.getItems().set(0, real);
								
								synchronized(this) {
									for(Item v : auction.getItems()) {
										if(v.getName().equals(real.getName())) {
											Thread.sleep(100);
											v.setCurrBid(real.currBid);
											//v.setBidHistory(real.bidHistory.getText());
											v.bidHistory.setText(real.bidHistory.getText());
											auction.refresh();
											// TODO: find method for updating BidHistory in real-time
											
											
										}
									}
								}

								
								
//			
//				            	auction.getItems().removeAll(auction.getSelectionModel().getSelectedItem());
//								auction.getItems().add(real);

				            	//auction.refresh();
							break;


					}
					
					
					//System.out.println("Received from Server: " + message);

					

					
				}
			} catch (IOException ex) {
				System.out.println("Server has been closed");
				//ex.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		setUpNetworking();
		


		VBox v = new VBox(10);
		HBox buttonHBox = new HBox(10);
		
		responseTest = new Label("");
		
		auction = new TableView();
		
		auction.setEditable(true);
		
		item = new TableColumn("Item");
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
		
		
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
            new PropertyValueFactory<Item, String>("name"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(
            new EventHandler<CellEditEvent<Item, String>>() {
                @Override
                public void handle(CellEditEvent<Item, String> t) {
                    ((Item) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setBidHistory(t.getNewValue());
                }
            }
        );
        
        currBid.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        currBid.setOnEditCommit(new EventHandler<CellEditEvent<Item,Double>>(){

			@Override
			public void handle(CellEditEvent<Item, Double> t) {
				// TODO Auto-generated method stub
              ((Item) t.getTableView().getItems().get(
              t.getTablePosition().getRow())
              ).setCurrBid(t.getNewValue());
			}
        	
        });
        
//        bidHistory.setOnEditCommit(new EventHandler<CellEditEvent<Item,String>>(){
//
//			@Override
//			public void handle(CellEditEvent<Item, String> t) {
//				// TODO Auto-generated method stub
//              ((Item) t.getTableView().getItems().get(
//              t.getTablePosition().getRow())
//              ).setBidHistory(t.getNewValue());
//			}
//        	
//        });
        
            
            

		
		auction.getColumns().addAll(item,description,currBid,buyNow,timer,bidHistory);
		
		
		table = new Item("table","Hardly used",50,100,1000,"One bid placed");
		
		auction.getItems().add(table);
		auction.getItems().add(new Item("yoo","Hardly used",50,100,1000,"One bid placed"));
		
		Item obj = auction.getItems().get(0);
		
		obj.bidHistory.setText("Whaoooooo");
		
		Button sender = new Button("Send");
		

		

        sender.setOnAction(new EventHandler<ActionEvent>() {
         	 
            @Override
            public void handle(ActionEvent event) {
            	
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			testing = auction.getSelectionModel().getSelectedItem();
    			
    			GsonItem convert = new GsonItem(testing);
    			
    			
    			String string = gson.toJson(convert);
    			
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
    			
    			GsonItem convert = new GsonItem(testing);
    			
    			
    			String string = gson.toJson(convert);
    			
    			sendToServer("Bid Request -> " + string);
    			

    			


            } 
        });
		

		v.getChildren().add(auction);
		v.getChildren().add(responseTest);
		buttonHBox.getChildren().addAll(sender,placeBid);
		v.getChildren().add(buttonHBox);
		Scene s = new Scene(v,600,500);
		
	    primaryStage.setOnCloseRequest(e -> System.exit(0));

	    
        item.prefWidthProperty().bind(auction.widthProperty().multiply(0.2));
        description.prefWidthProperty().bind(auction.widthProperty().multiply(0.3));
        currBid.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        buyNow.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        currBid.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        timer.prefWidthProperty().bind(auction.widthProperty().multiply(0.1));
        bidHistory.prefWidthProperty().bind(auction.widthProperty().multiply(0.2));



        item.setResizable(false);
        description.setResizable(false);
		
        //music();
		primaryStage.setScene(s);
		primaryStage.show();
		writer.println("Initialize");
		writer.flush();
	}
	
	private void sendToServer(String string) {
		// TODO Auto-generated method stub
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
	

}



