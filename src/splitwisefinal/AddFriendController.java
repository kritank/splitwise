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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author KRITANK SINGH
 */
public class AddFriendController implements Initializable {

    @FXML
    private Pane sidePane;
    @FXML
    private ImageView closeButton;
    @FXML
    private ImageView minimiseButton;
    @FXML
    private Button addFriendButton;
    @FXML
    private ListView<String> friendList;
    @FXML
    private Button backButton;
    @FXML
    private Label statusField;
    @FXML
    private ListView<String> userListView;
    
    
    
    //define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;
    FXMLLoader loader;
    DataInputStream dis ;
    DataOutputStream dos;
    friendsList fl;
    private retrivedBill retrivedBills;
    int selectedItemsIndex ;
    int selectedParticipant;
    ArrayList<friendData> selectedItems;
    ArrayList<friendData> userList;
    user client=null;
    ObservableList displayList;
    Page page;
    @FXML
    private Label noneLabel;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedItems=new ArrayList<friendData>();
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
    private void addFriendButtonClicked(MouseEvent event) {
             //   friendStatusField.setText("");
                // sendMessage thread 
                if(selectedItems.size()>0){
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                             // trying to send to server
                                             // write on the output stream
                                            String queryType="25";
                                            int count=selectedItems.size();
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(client.getUid());
                                            dos.writeUTF(Integer.toString(count));
                                            for(int i=0;i<count;i++){
                                            String fuid    = selectedItems.get(i).getUserId();
                                            String userName= selectedItems.get(i).getUserName();
                                            String phoneNo= selectedItems.get(i).getPhoneNo();
                                            dos.writeUTF(fuid);
                                            dos.writeUTF(userName);
                                            dos.writeUTF(phoneNo);
                                            }
                                            System.out.println("Add friend request Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) {  
                                            System.out.println("Problem in seding: friend request"+e); 
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
                                                            { 
                                                               Platform.runLater(new Runnable() {
                                                               public void run() {
                                                               userList.removeAll(selectedItems);
                                                               if(userList.size()==0)
                                                                   noneLabel.setVisible(true);
                                                               fl.add(selectedItems);
                                                               selectedItems.clear();
                                                               statusFieldAlert salert=new statusFieldAlert(statusField,"New Friend Added!");
                                                               salert.alert();
                                                               retriveFriends();
                                                                         }
                                                                    }        
                                                               );
                                                               break;
                                                            }
                                                      case 0:
                                                            {
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to Add Friend!!");
                                                                salert.alert();
                                                                break;
                                                            }
                                                      default:  
                                                          statusFieldAlert salert=new statusFieldAlert(statusField,"Person doesnot exist!!");
                                                          salert.alert();
                                                          break;
                                                }
                                            System.out.println("Got the add friend acknowlegement from server");   
					} catch (Exception e) { 

						System.out.println("Problem: in reciving closing conformation from server"); 
					} 
                                        
				}
		}); 
                readMessage.start(); 
            }
                else{
                    statusField.setText("No selection made!!");
                }

    }

    @FXML
    private void backButtonClicked(MouseEvent event) {
        open("addBillPage.fxml");
        addBillPageController contoller=loader.getController();
        contoller.setStream(dis,dos,client,fl,retrivedBills);
        close();
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
    
    public void setStream( DataInputStream dis,DataOutputStream dos,user client,friendsList fl, retrivedBill retrivedBills,ArrayList<friendData> userList)
    {
        this.dis=dis;
        this.dos=dos;
        this.client=client;
        this.fl=fl;
        this.retrivedBills=retrivedBills;
        this.userList=userList;
       // this.page=p;
    } 
    
    void close()
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    
    
    void retriveFriends()
    {
        //after db
        Platform.runLater(new Runnable() {
                                public void run() {
                                  try{
                                  friendList.setItems(fl.getFriendName());
                                  displayList=FXCollections.observableArrayList();
                                  int size=userList.size();
                                  if(userList.size()==0)
                                    noneLabel.setVisible(true);
                                  for(int i=0;i<size;i++)
                                  {
                                      displayList.add(userList.get(i).getUserName()+"-"+userList.get(i).getPhoneNo());
                                  }
                                  userListView.setItems(displayList);
                                  userListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                                }
                                  catch(Exception e)
                {
                 System.out.println("retrive friends "+e );
                }
                                }
                                
                        });
        
    }

    @FXML
    private void userSelected(MouseEvent event) {
        selectedItemsIndex =  userListView.getSelectionModel().getSelectedIndex();
        selectedParticipant=selectedItemsIndex;
        System.out.println("selected item " + userList.get(selectedItemsIndex).getUserName());
        selectedItems.add(userList.get(selectedItemsIndex));
      //  System.out.println("test");
    }

   
}
