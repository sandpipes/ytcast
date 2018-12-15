package com.sandpipes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ControlScreen {

	public static ControlScreen instance;
	
	private Stage stage;
	
	private Slider volume;
	private TextField input;
	private Button send;
	
	private ComboBox<String> commandType;
	
	private Scene scene;
	private GridPane gridPane;
		
	public ControlScreen(Stage stage) {
		instance = this;

		this.stage = stage;
		input = new TextField();
				
		commandType = new ComboBox<String>();
		commandType.setItems(FXCollections.observableArrayList(
		        "Add Song"
			)
		);
		commandType.getSelectionModel().selectFirst();
		
		input.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(input.getText().trim() != "") {
					switch(commandType.getValue()) {
						case "Add Song":
							ytCast.client.send("QS" + input.getText().trim());
							input.setText("");
							break;
						default:
							System.out.println("Unimplemented command type.");
					}
				}
			}
		});
		
		send = new Button("Send");
		send.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(input.getText().trim() != "") {
					switch(commandType.getValue()) {
						case "Add Song":
							ytCast.client.send("QS" + input.getText().trim());
							input.setText("");
							break;
						default:
							System.out.println("Unimplemented command type.");
					}
				}
			}
		});
		
		volume = new Slider();
		volume.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
            	ytCast.client.send("VO" + String.valueOf((int)volume.getValue()));
            }
        });
		
		volume.setMin(0);
		volume.setMax(100);
		volume.setValue(50);
		volume.setBlockIncrement(1);
		
		gridPane = new GridPane();
	}
	
	public void showScene() {
		gridPane.setMinSize(400, 200); 
		gridPane.setPadding(new Insets(10, 10, 10, 10)); 
		gridPane.setVgap(5); 
		gridPane.setHgap(5);       
		gridPane.setAlignment(Pos.CENTER); 

		gridPane.add(volume, 1, 0); 
		gridPane.add(commandType, 0, 1);
		gridPane.add(input, 1, 1); 
		gridPane.add(send, 2, 1);       
	  
		if(scene == null)
			scene = new Scene(gridPane); 
		
		stage.setTitle("Control"); 
		stage.setScene(scene); 
	}
	
}
