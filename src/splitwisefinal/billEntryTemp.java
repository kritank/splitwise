/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


public class billEntryTemp {
    
    private Label statusField;
    private AnchorPane scrollPaneAnchor;
    private FXMLLoader loader;
    private DataInputStream dis ;
    private DataOutputStream dos;
    private String billId;
    private String description;
    private String amount;
    private String effect;
    private int index;
    
    public billEntryTemp( Label statusField,AnchorPane scrollPaneAnchor,FXMLLoader loader,DataInputStream dis ,DataOutputStream dos,String billId,String description,String amount,String effect,int index)
    {
        this.statusField=statusField;
        this.scrollPaneAnchor=scrollPaneAnchor;
        this.loader=loader;
        this.dis=dis;
        this.dos=dos;
        this.billId=billId;
        this.description=description;
        this.amount=amount;
        this.effect=effect;
        this.index=index;
    }
    
    void generatePanel(){
        
        Label descriptionLabel,d;
        Label amountLabel,a;
        Label effectLabel,e;
        Button settleButton;
        AnchorPane billPanel;
        
        billPanel=new AnchorPane();
        billPanel.setStyle("-fx-background-color: #a0a2ab; -fx-border-width: 0 5 5 5; -fx-border-color: #2D3447;");
        billPanel.setPrefSize(471.0,80.0);
        billPanel.setLayoutX(0);
        billPanel.setLayoutY(index*80.0);
        
        
        d=new Label("Description: ");
        d.setLayoutX(20);
        d.setLayoutY(10);
        d.setTextFill(Color.web("#ffffff", 1.0));
        descriptionLabel=new Label(description);
        descriptionLabel.setLayoutX(154);
        descriptionLabel.setLayoutY(10);
        descriptionLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        a=new Label("Amount: ");
        a.setLayoutX(20);
        a.setLayoutY(30);
        a.setTextFill(Color.web("#ffffff", 1.0));
        amountLabel=new Label(amount);
        amountLabel.setLayoutX(154);
        amountLabel.setLayoutY(30);
        amountLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        e=new Label("Effect: ");
        e.setLayoutX(20);
        e.setLayoutY(50);
        e.setTextFill(Color.web("#ffffff", 1.0));
        effectLabel=new Label(effect);
        effectLabel.setLayoutX(150);
        effectLabel.setLayoutY(50);
        effectLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        settleButton = new Button("Settle");
        settleButton.setLayoutX(400);
        settleButton.setLayoutY(45);
        settleButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: #ffffff;");
        settleButton.setOnAction((event) -> {
            //settle the payment
            System.out.println("Settle button"+index);
              // sendMessage thread 
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                         
                                             // trying to send to server
                                            String queryType="27";
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(billId);
                                            System.out.println("bill settle request Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: bill settle request"+e); 
                                        }  
			} 
		}); 
                
                try{
                sendMessage.start(); 
                sendMessage.join();
                }catch(Exception ex)
                {
                    System.out.println("Error : "+ex);
                }

                // readMessage thread 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

                                            
					try { 
						// read the message sent to this client 
                                               // String clientId = dis.readUTF();
                                                String queryType = dis.readUTF();
                                                 int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(queryType+" "+result);
                                                switch(result){
                                                    case   1:
                                                    {
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Bill settled!!");
                                                                 salert.alert();
                                                                break;
                                                    }
                                                      case 0:
                                                      {
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to settle!!");
                                                                 salert.alert();
                                                                break;
                                                      }
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                          break;
        
                                                }
                                                 System.out.println("Got the closing acknowlegement from server");
					} catch (Exception e) { 

						System.out.println("Problem: in reciving settle bill conformation from server"); 
                                                
					} 
                                        
				}
		}); 
                readMessage.start(); 
               
            
        });
        billPanel.setRightAnchor(settleButton,10.0);
        billPanel.getChildren().add(d);
        billPanel.getChildren().add(a);
        billPanel.getChildren().add(e);
        billPanel.getChildren().add(descriptionLabel);
        billPanel.getChildren().add(amountLabel);
        billPanel.getChildren().add(effectLabel);
        billPanel.getChildren().add(settleButton);
        this.scrollPaneAnchor.getChildren().add(billPanel);
    }
}
  
