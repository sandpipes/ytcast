package com.sandpipes;

import java.awt.Color;
import java.io.IOException;

import org.jsoup.Jsoup;

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
	
	private GridPane mainGrid;
	private GridPane previewGrid;
	private GridPane controlsGrid;
		
	public ControlScreen(Stage stage) {
		instance = this;
		
		controlsGrid = new GridPane();
		mainGrid = new GridPane();
		previewGrid = new GridPane();
		
		this.stage = stage;
		input = new TextField();
				
		commandType = new ComboBox<String>();
		commandType.setItems(FXCollections.observableArrayList(
		        "Add Song"
			)
		);
		commandType.getSelectionModel().selectFirst();
		
		EventHandler<ActionEvent> sendEvent = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String url = input.getText().trim();
				if(url != "") {
					switch(commandType.getValue()) {
						case "Add Song":
							ytCast.client.send("QS" + url);
							VideoInfo vi = new VideoInfo(url);
							input.setText("");
							vi.updateInfo(previewGrid, 0, 0);
							break;
						default:
							System.out.println("Unimplemented command type.");
					}
				}
			}
		};
		
		input.setOnAction(sendEvent);
		
		send = new Button("Send");
		send.setOnAction(sendEvent);
		
		volume = new Slider();
		volume.setMin(0);
		volume.setMax(100);
		volume.setValue(50);
		volume.setBlockIncrement(1);
		
		volume.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
            	ytCast.client.send("VO" + String.valueOf((int)volume.getValue()));
            }
        });
	}
	
	public void showScene() {
		controlsGrid.setMinSize(400, 200); 
		controlsGrid.setPadding(new Insets(10, 10, 10, 10)); 
		controlsGrid.setVgap(5); 
		controlsGrid.setHgap(5);       
		controlsGrid.setAlignment(Pos.CENTER); 
		
		controlsGrid.add(volume, 1, 0); 
		controlsGrid.add(commandType, 0, 1);
		controlsGrid.add(input, 1, 1); 
		controlsGrid.add(send, 2, 1);       
	  
		mainGrid.add(previewGrid, 0, 0);
		mainGrid.add(controlsGrid, 1, 0);
		
		if(scene == null)
			scene = new Scene(mainGrid); 
		
		stage.setTitle("Control"); 
		stage.setScene(scene); 
	}
	
}
