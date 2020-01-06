package de.noisruker.server.gui;

import java.net.URL;
import java.util.ResourceBundle;

import de.noisruker.net.Side;
import de.noisruker.server.loconet.LocoNetConnection.PortNotOpenException;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageType;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class GUIAddTrain implements Initializable {

	@FXML
	public Spinner<Integer> spinner;

	public void onFinished(ActionEvent event) {
		if (Ref.side.equals(Side.SERVER)) {
			try {
				new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, spinner.getValue().byteValue()).send();
			} catch (SerialPortException | PortNotOpenException e) {
				e.printStackTrace();
			}

		}

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 128, 0));
	}

}
