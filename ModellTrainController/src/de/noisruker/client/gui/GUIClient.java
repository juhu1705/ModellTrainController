package de.noisruker.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.noisruker.client.Client;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class GUIClient implements Initializable {

    @FXML
    private Spinner<Integer> address;

    @FXML
    private Spinner<Integer> speed;
    
    @FXML
    private RadioButton foreward;

    @FXML
    void onSend(ActionEvent event) {
    	String command, speed, address = this.address.getValue().toString();
    	
    	if(this.speed.getValue() > 9)
    		speed = this.speed.getValue().toString();
    	else
    		speed = "0" + this.speed.getValue().toString();
    	
    	if(address.length() > 4)
    		return;
    	
    	for(int i = 0; address.length() == 4; i++)
    		address = "0" + address;
    	
    	if(foreward.isSelected())
    		command = "v" + address + "v" + speed;
		else
			command = "r" + address + "r" + speed;
    		
    	
    	
    	try {
			Client.getConnectionHandler().sendDatapacket(new Datapacket(DatapacketType.SEND_COMMAND, command));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		address.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 9));
		speed.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0));
	}

}
