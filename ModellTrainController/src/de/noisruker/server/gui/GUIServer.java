package de.noisruker.server.gui;

import de.noisruker.server.ModellRailroad;
import de.noisruker.server.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import jssc.SerialPortException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
	
	public void onDriveFar(ActionEvent event) {
		Thread t = new Thread(new Runnable() {
			boolean increase = true;
			byte speed = 0;
			@Override
			public void run() {
				while(true) {
					if(increase) {
						speed++;
						try {
							ModellRailroad.getInstance().setSpeedOfTrain((byte) 9, speed, true);
						} catch (SerialPortException e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(speed == 9) {
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							this.increase = false;
						}
					} else {
						speed--;
						try {
							ModellRailroad.getInstance().setSpeedOfTrain((byte) 9, speed, true);
						} catch (SerialPortException e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(speed == 0) {
							this.increase = true;
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
//							return;
						}
					}
				}
					
			}
		});
		t.start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Thread modellRailroadController = new Thread(ModellRailroad.getInstance());
		modellRailroadController.start();

		new Thread(() -> {
			try {
				Server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}

}
