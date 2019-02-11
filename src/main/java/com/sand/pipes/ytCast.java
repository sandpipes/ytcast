package com.sand.pipes;

import javafx.application.Application;
import javafx.stage.Stage;

public class ytCast extends Application{

	public static ytCast instance;
	
	private LoginScreen loginScreen;
	private ControlScreen controlScreen;
	
	public static SocketClient client;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
	    setLoginScene(primaryStage);
		primaryStage.show(); 
	}
	
	public void setLoginScene(Stage stage) {
		if(loginScreen == null)
			loginScreen = new LoginScreen(stage);
		loginScreen.showScene();
	}
	
	public void setControlScene(Stage stage) {
		if(controlScreen == null)
			controlScreen = new ControlScreen(stage);
		controlScreen.showScene();
	}
	
	public static void main(String args[]) {
		launch(args);
	}

}
