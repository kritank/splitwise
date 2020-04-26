/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.scene.input.InputMethodEvent;

/**
 * FXML Controller class
 *
 * @author KRITANK SINGH
 */

public class EditPageController implements Initializable {

    @FXML
    private ImageView closeButton;
    @FXML
    private ImageView minimiseButton;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField phoneNoField;
    @FXML
    private TextField emailField;
    @FXML
    private Label statusField;
    @FXML
    private Button resetButton;
    @FXML
    private PasswordField passwordField;
     @FXML
    private Button sendButton;
    @FXML
    private TextField otpField;
    @FXML
    private Button verifyButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button saveButton;
    @FXML
    private PasswordField rePasswordField;
   
    
    

    /**
     * Initializes the controller class.
     */
    
     //define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean verified=false;
    private boolean sent=false;
    String otp;
    FXMLLoader loader;
    DataInputStream dis ;
    DataOutputStream dos;
    Boolean isRegistered=false;
    Boolean isChecked=false;
    user client=null;
    String lastPhoneNo;
    String clientPhoneNo;
    private retrivedBill retrivedBills;
//    private ArrayList<panelData> billData;
//    private ArrayList<panelData> otherBillData;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       loadInitial(); 
       setPhoneNoValidityChecker();
    }    
    
    void loadInitial()
    {
        passwordField.setEditable(false);  
        rePasswordField.setEditable(false);
        otpField.setEditable(false);
       // lastPhoneNo=client.getPhoneNO();
         Platform.runLater(new Runnable() {
                                public void run() {  
                                userNameField.setText(client.getUserName());
                                phoneNoField.setText(client.getPhoneNo());
                                emailField.setText(client.getEmail());
                                }
                        });
    }
    
    @FXML
    private void closeButtonClicked(MouseEvent event) {
        closeWindow cw=new closeWindow(dis,dos,statusField);
        cw.close();             
    }

    @FXML
    private void minimiseButtonClicked(MouseEvent event) {
         ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
    }


    @FXML
    private void resetButtonClicked(MouseEvent event) {
        emailField.setEditable(false);
        passwordField.setEditable(false);  
        rePasswordField.setEditable(false);  
        otpField.setEditable(false);
        statusField.setText("");
        userNameField.setText(client.getUserName());
        phoneNoField.setText(client.getPhoneNo());
        emailField.setText(client.getEmail());
        passwordField.setText("");
        rePasswordField.setText("");
        otpField.setText("");
        passwordField.setPromptText("************");
        rePasswordField.setPromptText("************");
        otpField.setPromptText("SPLTWS979799");
    }
    
    @FXML
    private void homeButtonClicked(MouseEvent event) {
        open("homePage.fxml");
        HomePageController controller=loader.getController();
        controller.setStream(this.dis, this.dos,this.client,this.retrivedBills);
        close();
    }

    @FXML
    private void saveButtonClicked(MouseEvent event) {
        statusField.setText("");
        if(!isRegistered){
        int u=0,p=0,m=0,e=0,rp=0,c=0;
        if(phoneNoField.getText().equals(""))
        {
            statusField.setText("Enter Mobile No!");
        }else m=1;
        if(emailField.getText().equals(""))
        {
            statusField.setText("Enter Email!");
        }else e=1;
        if(passwordField.getText().equals(""))
        {
            statusField.setText("Enter Password!");
        }else p=1;
        if(rePasswordField.getText().equals(""))
        {
            statusField.setText("Enter Re-Type Password!");
        }else rp=1;
        if(!passwordField.getText().equals(rePasswordField.getText()))
        {
            statusField.setText("Passwords don't match!");
        }else c=1;
        if(m==1&&p==1&&rp==1&&c==1)
        {
                  // sendMessage thread 
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            
					// read the message to deliver. 
                                        String queryType = "24";
					String userName = userNameField.getText();
                                        String phoneNo  = phoneNoField.getText();
                                        String email    = emailField.getText();
                                        String password = passwordField.getText();
					String encryptedPassword;
                                        
                                         try 
                                        { 
                                            //encrypting the password
                                            encryptedPassword=shaEncrypter.toHexString(shaEncrypter.getSHA(password));
                                            System.out.print("HashCode Generated by SHA-256 for: ");  
                                            System.out.println(password + " : " +encryptedPassword);
                                            
                                             // trying to send to server
                                             // write on the output stream
                                            
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(client.getUid());
                                            dos.writeUTF(userName);
                                            dos.writeUTF(phoneNo);
                                            dos.writeUTF(email);
                                            dos.writeUTF(encryptedPassword);
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: userName password"+e); 
                                        }  
			} 
		}); 
                sendMessage.start(); 
                System.out.println("New Registration Details Sent to Server!!");
                // readMessage thread 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

                                            
					try { 
						// read the message sent to this client 
                                                String serverName= dis.readUTF();
                                                String queryType = dis.readUTF();
                                                int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                switch(result){
                                                      case 1:
                                                                String userName = userNameField.getText();
                                                                String phoneNo  = phoneNoField.getText();
                                                                String email    = emailField.getText();
                                                                String password = passwordField.getText();
                                                                client.setUserName(userName);
                                                                client.setPhoneNo(phoneNo);
                                                                client.setEmail(email);
                                                                client.setPassword(password);
                                                                isRegistered =true;
                                                                loadInitial();
                                                                Platform.runLater(new Runnable() {
                                                                    public void run() {
                                                                    statusField.setText("Saved!!!");
                                                                    passwordField.setText("");
                                                                    rePasswordField.setText("");
                                                                    otpField.setText("");
                                                                    passwordField.setPromptText("************");
                                                                    rePasswordField.setPromptText("************");
                                                                    otpField.setPromptText("SPLTWS979799");
                                                                    }
                                                                });
                                                                
                                                                break;
                                                      case 0:
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to Save!!"); 
                                                                salert.alert();
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
						
					} catch (Exception e) { 

						System.out.println("Problem: in reciving from server"); 
					} 
                                        
				}
		}); 
                readMessage.start(); 
                System.out.println("Got the message from server");
        }
      }else{
            statusField.setText("Already Saved!!");
        }
    }
    
    
    @FXML
    private void sendButtonClicked(MouseEvent event) {
        if(isChecked)
        {
            if(sent==false)
            {
            try {
			// Construct data
                        String phoneNo=phoneNoField.getText();
                        String userName=userNameField.getText();
                        otp=otpGenerator.sendOTP(userName, phoneNo);
                        System.out.println(otp);
                        sent=true;
                        otpField.setEditable(true);
                        statusFieldAlert salert=new statusFieldAlert(statusField,"OTP sent succesfully!!");
                            salert.alert();
		} catch (Exception e) {
			System.out.println("Error SMS "+e);
                        statusFieldAlert salert=new statusFieldAlert(statusField,"OTP not sent succesfully!!"); 
                            salert.alert();
		}
        }
            else
            {
                statusFieldAlert salert=new statusFieldAlert(statusField,"Already Sent!!");
                salert.alert();
            }
        }   
        else
        {
             statusFieldAlert salert=new statusFieldAlert(statusField,"Please VALID phone number first");
             salert.alert();
        }
            
        }

    @FXML
    private void verifyButtonClicked(MouseEvent event) {
         if(sent==true)
        {
            if(otp.equals(otpField.getText()))
            {
                            verified=true;
                            emailField.setEditable(true);
                            passwordField.setEditable(true);
                            rePasswordField.setEditable(true);
                            statusFieldAlert salert=new statusFieldAlert(statusField,"Verified!!");
                            salert.alert();
                           
            }
            else 
            {
                statusFieldAlert salert=new statusFieldAlert(statusField,"Ivalid OTP!!");
                salert.alert();
            }
        }
         else  {
            statusFieldAlert salert=new statusFieldAlert(statusField,"Please undergo OTP verification!");;
            salert.alert();
         }
    }
    
    
    private void checkButtonClicked(MouseEvent event) {
        if(!phoneNoField.getText().equals("")&&isChecked==false){
        try{
                // sendMessage thread 
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                         
                                             // trying to send to server
                                             // write on the output stream
                                            String queryType="29";
                                            String phoneNo=phoneNoField.getText();
                                            System.out.println(queryType+" "+phoneNo);
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(phoneNo);
                                            System.out.println("Check No request Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: Check No request request"+e); 
                                        }  
			} 
		}); 
                sendMessage.start(); 
        try {
            sendMessage.join();
        } catch (Exception ex) {
             System.out.println("Unabe to wait for send thread in close Button "+ex); 
        }
                // readMessage thread 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

                                            
					try { 
						// read the message sent to this client
                                                String queryType = dis.readUTF();
                                                String serverName= dis.readUTF();
                                                 int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                switch(result){
                                                      case 1:
                                                      {
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Another client with same mobile no exist!!");
                                                                salert.alert();
                                                                Platform.runLater(new Runnable() {
                                                                public void run() {
                                                                   phoneNoField.setText("");
                                                                   phoneNoField.setPromptText("9797979797");
                                                                     }
                                                                });
                                                      }
                                                                break;
                                                      case 0:
                                                      {
                                                                isChecked=true;
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Valid no!!");
                                                                salert.alert();
                                                      }
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
                                                System.out.println("Check No request acknowlegement from server");
					} catch (Exception e) { 

						System.out.println("Problem: in reciving Check No request conformation from server"); 
					} 
                                        
				}
		}); 
                readMessage.start();
        }catch(Exception ex)
        {
            System.out.println("Error in DuplicatePhoneNo "+ex);
        }
        
      }
        else{
            if(isChecked==false)
            {
                statusFieldAlert salert=new statusFieldAlert(statusField,"Already Checked!!");
                salert.alert();
            }
        }
    }
    
     void open(String s) 
    {
        try{
         loader=new FXMLLoader();
         loader.setLocation(getClass().getResource(s));
         loader.load();
         Parent root = loader.getRoot();
         Stage stage = new Stage();
        //you can use underdecorated or transparent.
        stage.initStyle(StageStyle.TRANSPARENT);
        //stage.initStyle(StageStyle.UNDERDECORATED);
       
       //grab your root here
             root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        //move around here
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        }
        catch(IOException e)
        {
            System.out.println("Can't open "+s);
        }
    }
    
     public void setStream( DataInputStream dis,DataOutputStream dos,user client,retrivedBill retrivedBills)
    {
        this.dis=dis;
        this.dos=dos;
        this.client=client;
        this.retrivedBills=retrivedBills;
       // this.billData=billData;
        this.clientPhoneNo=client.getPhoneNo();
        this.lastPhoneNo=clientPhoneNo;
      //  this.otherBillData=otherBillData;
    } 
     
    void close()
    {
        Stage stage = (Stage) resetButton.getScene().getWindow();
        stage.close();
    }

    
    void setPhoneNoValidityChecker()
    {        
                phoneNoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length()==10)
                {
                    
                    System.out.println("textfield changed from " + oldValue + " to " + newValue);
                    System.out.println(phoneNoField.getText()+" "+clientPhoneNo);
                    if(!(phoneNoField.getText().equals("")||phoneNoField.getText().equals(lastPhoneNo))){
                    try{
                     // sendMessage thread 
                    Thread sendMessage = new Thread(new Runnable() 
		    { 
			@Override
			public void run() { 
                            try 
                            { 
                                         
                                             // trying to send to server
                                             // write on the output stream
                                            String queryType="29";
                                            String phoneNo=phoneNoField.getText();
                                            System.out.println(queryType+" "+phoneNo);
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(phoneNo);
                                            System.out.println("Check No request Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: Check No request request"+e); 
                                        }  
			} 
		}); 
                sendMessage.start(); 
        try {
            sendMessage.join();
        } catch (Exception ex) {
             System.out.println("Unabe to wait for send thread in close Button "+ex); 
        }
                // readMessage thread 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

                                            
					try { 
						// read the message sent to this client
                                                String queryType = dis.readUTF();
                                                String serverName= dis.readUTF();
                                                 int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                switch(result){
                                                      case 1:
                                                      {
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Another client with same mobile no exist!!");
                                                                salert.alert();
                                                      }
                                                                break;
                                                      case 0:
                                                      {
                                                                isChecked=true;
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Valid Phone Number!!");
                                                                salert.alert();
                                                      }
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
                                                System.out.println("Check No request acknowlegement from server");
					} catch (Exception e) { 

						System.out.println("Problem: in reciving Check No request conformation from server"); 
					} 
                                        
				}
		}); 
                readMessage.start();
        }catch(Exception ex)
        {
            System.out.println("Error in DuplicatePhoneNo "+ex);
        }
        
      }
        else{
              
            if(phoneNoField.getText().equals(lastPhoneNo)&&!phoneNoField.getText().equals(client.getPhoneNo()))
            {
               
                statusFieldAlert salert=new statusFieldAlert(statusField,"Already Checked!!");
                salert.alert();
               
            }
            else if(phoneNoField.getText().equals(client.getPhoneNo()))
                        {
                            
                            statusFieldAlert salert=new statusFieldAlert(statusField,"");
                            salert.alert();
                        }
        }
                }
            else if(newValue.length()>10)
                {
                statusFieldAlert salert=new statusFieldAlert(statusField,"Please check the no!!");
                salert.alert();
                }           
     });
        
    }

    @FXML
    private void phoneNoChanged(InputMethodEvent event) {
    }
}  

