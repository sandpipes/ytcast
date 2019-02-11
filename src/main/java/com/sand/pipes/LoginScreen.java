package com.sand.pipes;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginScreen {
	public static LoginScreen instance;
	
	private Text host;
	private TextField hostField;
	
	private Text port;
	private TextField portField;
	
	private Text status;
	
	private Button connect;
	
	private GridPane gridPane;
	
	private Scene scene;
	
	private Stage stage;
	
	public LoginScreen(Stage stage) {
		instance = this;
		
		host = new Text("Hostname:");       
		port = new Text("Port:"); 
		status = new Text("");  
		
		hostField = new TextField("192.168.2.97");        
		portField = new TextField("8888");  
		   
		connect = new Button("Connect"); 
		
		connect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				status.setText("Connecting.");
				if(ytCast.client == null)
					ytCast.client = new SocketClient();
				
				int p = -1;
				try {
					p = Integer.parseInt(portField.getText().trim());
					if(p < 0 || p > 65535) throw new Exception();
				} catch(Exception e) {
					status.setText("Invalid Port.");
					return;
				}
				
				int r = ytCast.client.connect(hostField.getText().trim(), p);
				
				if(r == -1 || r == -3)
					status.setText("Unknown Hostname.");
				else if(r == -2)
					status.setText("IOException Occurred.");
				else {
					status.setText("");
					ytCast.instance.setControlScene(stage);
				}
			}
		});
		  
		gridPane = new GridPane(); 
		
		this.stage = stage;
	}
	
	public void showScene() {   
		gridPane.setMinSize(400, 200); 
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		gridPane.setVgap(5); 
		gridPane.setHgap(5);       
		gridPane.setAlignment(Pos.CENTER); 

		gridPane.add(host, 0, 0); 
		gridPane.add(hostField, 1, 0); 
		gridPane.add(port, 0, 1);       
		gridPane.add(portField, 1, 1); 
		gridPane.add(connect, 0, 2); 
		gridPane.add(status, 1, 3);
	  
		if(scene == null)
			scene = new Scene(gridPane);  
		
		stage.setTitle("Login"); 
		stage.setScene(scene);
	}
}
