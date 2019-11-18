package de.noisruker.server.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.noisruker.server.ModellRailroad;
import de.noisruker.server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import jssc.SerialPortException;

public class GUIServer implements Initializable {

	@FXML
	public TextField command;
	
	public void onSend(ActionEvent event) {
		command.getText();
		
		try {
			ModellRailroad.getInstance().sendCommand(command.getText(), null);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Thread modellRailroadController = new Thread(ModellRailroad.getInstance());
		modellRailroadController.start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Server.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
}
