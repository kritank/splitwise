/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 *
 * @author KRITANK SINGH
 */
public class statusFieldAlert {
    Label statusField;
    String message;
    
    public statusFieldAlert(Label statusField,String message)
    {
        this.statusField=statusField;
        this.message=message;
    }
    
    void alert()
    {
        Platform.runLater(new Runnable() {
                                public void run() {
                                    statusField.setText(message);  
                                }
                        });
    }
      
}
