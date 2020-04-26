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
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author KRITANK SINGH
 */
public class addBillPageController implements Initializable {

    @FXML
    private Pane sidePane;
    @FXML
    private ImageView closeButton;
    @FXML
    private ImageView minimiseButton;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> typeBox;
    @FXML
    private TextField participantsField;
    @FXML
    private Button resetButton;
    @FXML
    private Button addFriendButton;
    @FXML
    private ListView<String> participantsSelectionBox;
    @FXML
    private ListView<String> friendList;
    @FXML
    private Button backButton;
    @FXML
    private Label statusField;
    private Label friendStatusField;
    
    
    
    
    //define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;
    FXMLLoader loader;
    DataInputStream dis ;
    DataOutputStream dos;
    friendsList fl;
    private static ObservableList<String> typeChoice ;
    private retrivedBill retrivedBills;
    int selectedItemsIndex ;
    int selectedParticipant;
    ObservableList<String> selectedItems;
    user client=null;
    HashSet<String> h=null;
    @FXML
    private Button addBillButton;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedItems=FXCollections.observableArrayList();
        loadType();       //selection Box
        retriveFriends(); //retrive friends
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
        statusField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        participantsField.setText("");
        descriptionField.setPromptText("PVR: Bahubali");
        amountField.setPromptText("2500");
        typeBox.getSelectionModel().select(0);
       // typeBox.setPromptText();
        
    }

    @FXML
    private void addFriendButtonClicked(MouseEvent event) {
        Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            // trying to send to server
                            try {
                                      // write on the output stream
                                      String queryType="30";
                                      dos.writeUTF(queryType);
                                      dos.writeUTF(client.getUid());
                                      System.out.println(client.getUid());
                                      System.out.println("Get all user request Sent to Server!!");
                                           
                                } 
                                catch (Exception e) {  
                                    System.out.println("Problem in seding: all user request request"+e); 
                                }  
			} 
		}); 
                sendMessage.start(); 

                // readMessage thread 
		Thread readMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 

                                            
					try { 
						// read the message sent to this client 
                                                ArrayList<friendData> userList=new ArrayList<friendData>();
                                                String queryType = dis.readUTF();
                                                String serverName= dis.readUTF();
                                                int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                switch(result){
                                                      case 1:
                                                          int count = Integer.parseInt(dis.readUTF());
                                                          for(int i=0;i<count;i++ ){
                                                          String friendUid = dis.readUTF();
                                                          String friendName = dis.readUTF();
                                                          String friendPhoneNo = dis.readUTF();
                                                          friendData temp=new friendData(friendUid,friendName,friendPhoneNo);
                                                          userList.add(temp);
                                                          }
                                                          Platform.runLater(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Page p=new Page(xOffset,yOffset,loader,closeButton);
                                                        open("addFriend.fxml");
                                                         AddFriendController controller=loader.getController();
                                                         controller.setStream(dis, dos,client,fl,retrivedBills,userList);
                                                         close();
                                                       }
                                                          });
                                                          
                                                         break;
                                                      case 0:
                                                            {
                                                                statusFieldAlert salert=new statusFieldAlert(friendStatusField,"Unable to get all users!");
                                                                salert.alert();
                                                                break;
                                                            }
                                                      default:  
                                                          statusFieldAlert salert=new statusFieldAlert(friendStatusField,"Users doesnot exist!!");
                                                          salert.alert();
                                                          break;
                                                }
                                            System.out.println("Got the get user acknowlegement from server");   
					} catch (Exception e) { 

						System.out.println("Problem: in reciving get user conformation from server"+e); 
					} 
                                        
				}
		}); 
                readMessage.start(); 

    }

    @FXML
    private void backButtonClicked(MouseEvent event) {
        open("homePage.fxml");
        HomePageController controller=loader.getController();
        controller.setStream(this.dis, this.dos,this.client,this.retrivedBills);
        close();
    }
    
    @FXML
    private void selectFriend(MouseEvent event) {
        selectedItemsIndex =  participantsSelectionBox.getSelectionModel().getSelectedIndex();
        selectedParticipant=selectedItemsIndex;
        participantsField.setText(Integer.toString(participantsSelectionBox.getSelectionModel().getSelectedItems().size()));
        System.out.println("selected item " + fl.getFriendName().get(selectedItemsIndex));
        selectedItems.add(fl.getFriends().get(selectedItemsIndex).getUserId());
                        
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
    
    public void setStream( DataInputStream dis,DataOutputStream dos,user client,friendsList fl, retrivedBill retrivedBills)
    {
        this.dis=dis;
        this.dos=dos;
        this.client=client;
        this.fl=fl;
        this.retrivedBills=retrivedBills;
    } 
    
    void close()
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    void loadType()
    {
        typeChoice=FXCollections.observableArrayList(
                "Paid by You",
               "Paid by Other"
                );
        typeBox.setItems(typeChoice);
//        System.out.println(typeBox.getValue());
        typeBox.getSelectionModel().select(0);
//        System.out.println(typeBox.getValue());
//        typeBox.getSelectionModel().selectFirst();
//        System.out.println(typeBox.getValue());
        typeBox.setVisibleRowCount(2);
       
    }
    
    void retriveFriends()
    {
        //after db
        Platform.runLater(new Runnable() {
                                public void run() {
                                  friendList.setItems(fl.getFriendName());
                                  participantsSelectionBox.setItems(fl.getDisplayList());
                                    participantsSelectionBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                }
                        });
        
    }

    @FXML
    private void addBillButtonClicked(MouseEvent event) {
         System.out.println("add bill button clicked");
//        friendStatusField.setText("");
           System.out.println("1");
           int d=0,a=0,c=0,s=0;
         if(descriptionField.getText().equals(""))
         {
             statusField.setText("Enter a description!");
         }else d=1;
         if(amountField.getText().equals(""))
         {
             statusField.setText("Enter an amount!");
         }else a=1;
         if(participantsField.getText().equals(""))
         {
             statusField.setText("Enter no of participants!");
         }else c=1;
         //add condition for selected Participants
         if(typeBox.getValue()==null)
         {
             statusField.setText("Select the type of bill!");
         }
         else s=1;   
          System.out.println("2");
         if(d==1&&a==1&&c==1&&s==1){
            // sendMessage thread 
                System.out.println("add bill button clicked");
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                         
                                             // trying to send to server
                                             // write on the output stream
                                            String queryType="26";
                                            String description=descriptionField.getText();
                                            String amount=      amountField.getText();
                                            String type=typeBox.getValue();
                                            System.out.println("type: "+type);
                                            String count =participantsField.getText();
                                            String selectedParticiants="";
                                            h=new HashSet<>();
                                            for(String s:selectedItems){
                                                h.add(s);
                                            }
                                            selectedItems.clear();
                                            selectedItems.addAll(h);
                                            System.out.println("updated: "+selectedItems);
                                            String uid;
                                            ArrayList<String> payerId=new ArrayList<>();
                                            System.out.println("before type");
                                            String count_payer="1";
                                            switch(type)
                                            {
                                               case "Paid by You" :
                                                    //payerId.add(client.getUid());
                                                    uid=client.getUid();
                                                    //count_payer="1";
                                                    System.out.println("Payer: "+uid+" Sending Selected Items: "+selectedItems);
                                                   break;
                                               case "Paid by Other":
                                                    uid=selectedItems.get(0);
                                                    //payerId.addAll(h);
//                                                    count_payer=count;
//                                                    count="1";
                                                    System.out.println(selectedItems);
                                                    selectedItems.remove(0);
                                                    System.out.println(selectedItems);
                                                    selectedItems.add(0, client.getUid());
                                                    System.out.println(selectedItems);
//                                                      selectedItems.clear();
//                                                      selectedItems.add(client.getUid());        
                                                    System.out.println("Payer: "+uid+" Sending Selected Items: "+selectedItems);
                                                    
                                                     break;
                                               default:
                                                   System.out.println("Eror");
                                                   uid="";
                                            }
                                            System.out.println("After type");
                                            for(String s:selectedItems)
                                            {
                                                selectedParticiants+=s+"#";
                                            }
                                            if(type.equals("Paid by Other"))
                                                selectedItems.add(0,uid);

                                            dos.writeUTF(queryType);
                                            dos.writeUTF(count_payer);
                                            for(int i=0;i<Integer.parseInt(count_payer);i++)
                                            dos.writeUTF(uid);
                                            //correct in add success bill too
                                            dos.writeUTF(description);
                                            dos.writeUTF(amount);
                                            dos.writeUTF(type);
                                            dos.writeUTF(count);
                                            dos.writeUTF(selectedParticiants);
                                            System.out.println("Bill Information Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: Bill Information "+e); 
                                        }  
			} 
		}); 
                sendMessage.start(); 
               

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
                                                                String billId=dis.readUTF();
                                                                String result2=dis.readUTF();
                                                                String uid,type;
                                                                panelData temp=null;
                                                                
                                                                switch(typeBox.getValue())
                                                                {
                                                                    case "Equally":
                                                                    case  "Paid by You" :
                                                                    {
                                                                        uid=client.getUid();
                                                                        type="bill";
                                                                        //only unique
                                                                        System.out.println("Paid by: "+uid+" Participants: "+selectedItems);
                                                                        ObservableList<String> t=FXCollections.observableArrayList();
                                                                        ObservableList<String> f=FXCollections.observableArrayList();
                                                                        f.add(client.getUid());
                                                                        t.addAll(selectedItems);
                                                                        
                                                                        temp=new panelData(billId,amountField.getText(),descriptionField.getText(),typeBox.getValue(),f,t,type);
                                                                        //System.out.println("Added Items: "+temp.getParticipantsId());
                                                                        System.out.println(billId+" "+temp.getDescription()+" "+temp.getAmount()+" "+temp.getType()+" "+temp.getPayerCount()+" "+temp.getParticipantsId().size()+" "+temp.getParticipantsId());
                                                                        retrivedBills.addBill(temp);
                                                                    }
                                                                        break;
                                                                    case  "Paid by Other":
                                                                        //uid=Integer.toString(selectedParticipant);
                                                                        uid=selectedItems.get(0);
                                                                        //System.out.println(h);
                                                                        selectedItems.remove(0);
//                                                                        System.out.println(selectedItems);
                                                                        type="otherBill";
//                                                                      
                                                                        System.out.println("Paid by: "+uid+" Participants: "+selectedItems);
                                                                        ObservableList<String> t=FXCollections.observableArrayList();
                                                                        ObservableList<String> f=FXCollections.observableArrayList();
                                                                        t.addAll(selectedItems);
                                                                        f.add(uid);
                                                                        temp=new panelData(billId,amountField.getText(),descriptionField.getText(),typeBox.getValue(),f,t,type);
                                                                        //System.out.println("Added Items: "+temp.getParticipantsId());
                                                                        System.out.println(billId+" "+temp.getDescription()+" "+temp.getAmount()+" "+temp.getType()+" "+temp.getPayerCount()+" "+temp.getParticipantsId().size()+" "+temp.getParticipantsId());
                                                                        retrivedBills.addOtherBill(temp);
                                                                        break;
                                                                    default:
                                                                        uid="-1";
                                                                        type="none";
                                                                }
                                                               // System.out.println(type);
                                                                //retrivedBillsbillData.add(temp);
                                                                //retrivedBills.addBill(temp);
                                                               // selectedItems.clear();
                                                                Platform.runLater(new Runnable() {
                                                                    public void run() {
                                                                      statusField.setText("Bill Added!!");
                                                                      descriptionField.setText("");
                                                                      amountField.setText("");
                                                                      participantsField.setText("");
                                                                      descriptionField.setPromptText("PVR: Bahubali");
                                                                      amountField.setPromptText("2500");
                                                                      typeBox.getSelectionModel().select(0);
                                                                      selectedItems.clear();
                                                                      participantsSelectionBox.setItems(fl.getDisplayList());
                                                                    }
                                                                });
                                                                break;
                                                      case 0:
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to add Bill!!");
                                                                salert.alert();
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
						System.out.println("Got the bill acknowlegement from server");
					} catch (Exception e) { 

						System.out.println("Problem: in reciving add bill conformation from server"); 
					} 
                                        
				}
		}); 
                readMessage.start(); 
                
         }
    }

    @FXML
    private void selectType(MouseEvent event) {
        
    }
    
   
}
