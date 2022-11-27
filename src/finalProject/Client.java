package finalProject;

import java.awt.Color;
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
import javafx.concurrent.Task;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.beans.property.*;

import javafx.scene.control.*;


public class Client extends Application {
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Stage primaryStage;
	
	private Scene s;
	
	private GsonItem testing;
	
	private Label responseTest = new Label();
	private TableView<GsonItem> auction = new TableView();
	
	
	private TableColumn timer;
	
	private Users logger;
	private Label loginOutput;
	
	public static void main(String[] args) {
		try {
			new Client().run(args);

		} 	catch(NumberFormatException nfe) {
			System.out.println("oopsie");
		}	catch (Exception e) {
			e.printStackTrace();
		}

	}
	
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
						
						case "Valid Bid" :
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
								loginOutput.setText("Logging in as " + sub.getUser() + "...");
								music("login.mp3");
								delay(500, () -> {
									writer.println("Initialize");
									writer.flush();
									auctionScene();
								});
		
								
							});

						
							break;
						
						case "Winners" :
							updater = l[1];
							updateItem = gson.fromJson(updater, GsonItem.class);
							
								for(GsonItem v : auction.getItems()) {
									if(v.getName().equals(updateItem.getName())) {
										
										v.setBidHistory(updateItem.getBidHistory());
										System.out.println(v.toString());
											Platform.runLater(() -> {
											});
										v.setCurrBid(updateItem.getCurrBid());
										
										
										
										
										
										auction.refresh();
								}
							}
							break;
							
						
						case "Timer" :
							String timer = l[1];
							System.out.println(timer);
							
							Platform.runLater(() -> {
								responseTest.setTextFill(Paint.valueOf("red"));
								responseTest.setText("Auction Time Remaining: " + timer);
//								for(GsonItem g : auction.getItems()) {
//									g.setTimer(timer);
//								}
							});
							//auction.refresh();
							break;

					}
					

				}
			} catch (IOException ex) {
				System.out.println("Server has been closed");
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
    public void music(String file) {

    	Media h = new Media(Paths.get(file).toUri().toString());
    	mediaPlayer = new MediaPlayer(h);
    	mediaPlayer.setVolume(.2);
    	mediaPlayer.play();
    	
    }
    
    MediaPlayer backgroundMediaPlayer;
    public void backgroundMusic(String file) {

    	Media h = new Media(Paths.get(file).toUri().toString());
    	backgroundMediaPlayer = new MediaPlayer(h);
    	backgroundMediaPlayer.setVolume(.1);
    	backgroundMediaPlayer.play();
    	backgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    	
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
    	
    	Label loginTitle = new Label("Login");
    	
    	loginTitle.setFont(Font.font("Verdana", 30));
    	
    	
    	Label uLabel = new Label("Username:");
    	Label pLabel = new Label("Password:");
    	
    	TextField uTF = new TextField("");
    	PasswordField pTF = new PasswordField();
    	
    	
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
        guestSign.setOnAction(new EventHandler<ActionEvent>() {
       	 
            @Override
            public void handle(ActionEvent event) {
            	
    			GsonBuilder builder = new GsonBuilder();
    			Gson gson = builder.create();
    			
    			logger = new Users("guest","");
    			
    			
    			String string = gson.toJson(logger);
    			
    			sendToServer("Login Request -> " + string);
    			

    			


            } 
        });
        
    	
    	user.getChildren().addAll(uLabel,uTF);
    	pass.getChildren().addAll(pLabel,pTF);
    	buttons.getChildren().addAll(login,guestSign);
    	
    	storer.getChildren().addAll(loginTitle,user,pass,loginOutput,buttons);
    	
    	uTF.requestFocus();
	    primaryStage.setOnCloseRequest(e -> {
	    	System.exit(0);
	    });

    	
    	
    	
    	Scene loginScene = new Scene(storer,300,300);

    	primStage.setScene(loginScene);
    	primStage.show();
    	
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void auctionScene() {
		VBox v = new VBox(10);
		HBox buttonHBox = new HBox(10);
		
		
		buttonHBox.setAlignment(Pos.CENTER);
		
		responseTest = new Label("");
		responseTest.setAlignment(Pos.CENTER);
		
		auction = new TableView();
		
		auction.setEditable(true);
		
		TableColumn item = new TableColumn("Item");
		item.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn description = new TableColumn("Description");
		description.setCellValueFactory(new PropertyValueFactory<>("description"));

		TableColumn currBid = new TableColumn("Current Bid ($)");
		currBid.setCellValueFactory(new PropertyValueFactory<>("currBid"));


		TableColumn buyNow = new TableColumn("Buy Now");
		buyNow.setCellValueFactory(new PropertyValueFactory<>("buyNow"));

		timer = new TableColumn("Bid Time Left");
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
        
      
            

		
		auction.getColumns().addAll(item,description,currBid,buyNow,bidHistory);
		
		
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
        
        HBox rem = new HBox(responseTest);
        rem.setAlignment(Pos.CENTER);
        
        
	    Label title = new Label("Auction House :D");
	    title.setFont(Font.font("Cambria", 32));
	
		buttonHBox.getChildren().addAll(placeBid);
        v.getChildren().addAll(title,auction,rem,buttonHBox);
        v.setAlignment(Pos.CENTER);
		s = new Scene(v,1000,530);
		
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
        bidHistory.prefWidthProperty().bind(auction.widthProperty().multiply(0.3));
        
        
        for (TableColumn<GsonItem, ?> column : auction.getColumns()) {
            addTooltipToColumnCells(column);
        }
        
        item.setResizable(false);
        description.setResizable(false);
        currBid.setResizable(false);
        buyNow.setResizable(false);
        currBid.setResizable(false);
        timer.setResizable(false);
        bidHistory.setResizable(false);
        
        primaryStage.setTitle("Logged in as: " + logger.getUser());
		
        backgroundMusic("elevator.wav");
		primaryStage.setScene(s);
		
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



