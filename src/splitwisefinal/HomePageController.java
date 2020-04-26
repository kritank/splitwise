/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author KRITANK SINGH
 */
public class HomePageController implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane sidePane;
    @FXML
    private ImageView profilePic;
    @FXML
    private Label userName;
    @FXML
    private Label phoneNo;
    @FXML
    private Button editButton;
    @FXML
    private ImageView closeButton;
    @FXML
    private ImageView minimiseButton;
    @FXML
    private Label creditField;
    @FXML
    private Label debitField;
    @FXML
    private Label balanceField;
    //private ProgressBar progressBar; 
    @FXML
    private Button addButton;
    @FXML
    private Label statusField;
     @FXML
    private Pane root;
     @FXML
    private AnchorPane scrollPaneAnchor;
    @FXML
    private AnchorPane displayPanel;
    
     
     
    /**
     * Initializes the controller class.
     */
    
     //define your offsets here
    private double xOffset = 0;
    private double yOffset = 0;
    private FXMLLoader loader;
    private DataInputStream dis ;
    private DataOutputStream dos;
    private Boolean isSettled;
    private ArrayList<panelData> billPanel;
    private retrivedBill retrivedBills;
    private double credit=0.00;
    private double debit=0.00;
    private double balance=0.00;
    private user client;
    ObservableList<String> retrivedFriends;
    ArrayList<String> retrivedFriendsUid;
    ArrayList<String> retrivedFriendsPhoneNo;
    friendsList fl;
    ArrayList<friendData> friendDataList;
    int IdRank=0;
    @FXML
    private Label billStatusField;
    @FXML
    private PieChart pieChart;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        friendDataList=new ArrayList<friendData>();
        retrivedFriendsUid=new ArrayList<String>();
        retrivedFriendsPhoneNo=new ArrayList<String>();
        retrivedFriends=FXCollections.observableArrayList();
//        progressBar.setStyle("-fx-accent: #008000;");
        loadStatusPanel(); //loads name phoneNo
        loadBills();      //loads bills
   } 
    
    @FXML
    private void editButtonClicked(MouseEvent event) {
        open("editPage.fxml");
        EditPageController controller=loader.getController();
        controller.setStream(this.dis,this.dos,this.client,this.retrivedBills);
        close();
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
    private void addButtonClicked(MouseEvent event) {
        // sendMessage thread 
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                            String queryType="28";
                                            String uid=client.getUid();
                                            dos.writeUTF(queryType);
                                            dos.writeUTF(uid);
                                            System.out.println("Retrive friends request Sent to Server!!");                     } 
                                         catch (Exception e) {  
                                            System.out.println(client.getUid()+" Problem in seding: Retrive friend request"+e); 
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
                                                 String queryType = dis.readUTF();
                                                 String serverName= dis.readUTF();
                                                 int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                
                                                switch(result){
                                                      case 1:
                                                                int count = Integer.parseInt(dis.readUTF());
                                                                System.out.println("Count: "+count);
                                                                for(int i=0;i<count;i++){
                                                                      String uid=dis.readUTF();
                                                                      String userName=dis.readUTF();
                                                                      String phoneNo=dis.readUTF();
                                                                      friendData temp=new friendData(uid, userName, phoneNo);
                                                                      friendDataList.add(temp);
                                                                }
                                                                    fl=new friendsList(friendDataList);
                                                                    System.out.println("uid: "+client.getUid()+" "+client.getUserName()+" "+client.getPhoneNo()+" "+client.getEmail()+" "+client.getPassword());
                                                                    Platform.runLater(new Runnable() {
                                                                    public void run() {
                                                                         open("addBillPage.fxml");
                                                                        addBillPageController contoller=loader.getController();
                                                                        contoller.setStream(dis,dos,client,fl,retrivedBills);
                                                                       close(); }
                                                                });
                                                                
                                                                break;
                                                      case 0:
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to Recive!!");
                                                                salert.alert();
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
                                                System.out.println("Got the retrive acknowlegement from server");
					} catch (Exception e) { 

						System.out.println("Problem: in reciving retrive conformation from server"); 
					} 
                                        
				}
		}); 
                readMessage.start(); 
    }
    
    //open a new page
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
    
     //used to set the data objects required
     public void setStream( DataInputStream dis,DataOutputStream dos,user client,retrivedBill retrivedBills)
    {
        this.dis=dis;
        this.dos=dos;
        this.client=client;
        this.retrivedBills=retrivedBills;
    } 
     
    //closing the stage
    void close()
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    //initialise the settled!
    void settled()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                statusField.setText("Bill Settled!!");
          }
        });
    }



    //gives the effect of bill at index
    double effectBillAmount(ArrayList<panelData> billData,int index)
    {
        double amount=Double.parseDouble(billData.get(index).getAmount());
        String mode=billData.get(index).getMode();
        int participantCount=billData.get(index).getParticipantsId().size()+1 ;
        //int payerCount=billData.get(index).getParticipantsId().size();
        //String effect="0";
        double ef;
        
        switch(mode)
        {
            case "Equally":
                
               ef=amount/participantCount;
                break;
            case "Paid by Other":
                //amount=(participantCount-1)*amount;
                amount/=participantCount;
                ef=-1*amount;
                break;
            case  "Paid by You":
                amount=(participantCount-1)*amount;
                amount/=participantCount;
                ef=amount;
                break;
            default:
                ef=-9999;
        }
        return ef;
    }
    
    private void IDEvt(Button btn,panelData billInfo){
        Cursor c=Cursor.cursor("HAND");
    btn.setId(String.valueOf(IdRank)); 
    btn.setCursor(c);
    btn.setOnMousePressed(new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

            System.out.println("button"+btn.getId()+" got clicked!");
            //settle the payment
            System.out.println("Settle button");
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
                                            String billId=billInfo.getBillId();
                                            dos.writeUTF(billId);
                                            String type=billInfo.getType();
                                            dos.writeUTF(type);
                                            dos.writeUTF(billInfo.getAmount());
                                            int count=billInfo.getParticipantsId().size();
                                            dos.writeUTF(Integer.toString(count));
                                            int payercount=billInfo.getPayerList().size();
                                           dos.writeUTF(Integer.toString(payercount));
                                            for(int i=0;i<payercount;i++)
                                            {
                                                System.out.println(billInfo.getPayerList().get(i));
                                                dos.writeUTF(billInfo.getPayerList().get(i));
                                            }
                                            for(int i=0;i<count;i++)
                                            {
                                                System.out.println(billInfo.getParticipantsId().get(i));
                                                dos.writeUTF(billInfo.getParticipantsId().get(i));
                                            }
                                            if(type.equals("otherBill")){
                                              //  dos.writeUTF("1");
                                                dos.writeUTF(client.getUid());
                                                
                                            }
                                            System.out.println(billId+" settle request Sent to Server!!");
                                           
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
                                                String queryType = dis.readUTF();
                                                String serverName= dis.readUTF();
                                                int result1 = Integer.parseInt(dis.readUTF());
                                                int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result1+" "+result);
                                                switch(result){
                                                    case   1:
                                                    {            
                                                                    retrivedBills.removeAllBill(Integer.parseInt(btn.getId()));
                                                                    Platform.runLater(new Runnable() {
                                                                    public void run() {
                                                                        open("homePage.fxml");
                                                                        client.setIsSettled(true);
                                                                        HomePageController contoller=loader.getController();
                                                                        contoller.setStream(dis,dos,client,retrivedBills);
                                                                        close(); }
                                                                });
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
            isSettled=true;

        }
    });

    IdRank++;
 }
    
 
    
    void loadBills( )
    {
       
        //retrive from the database
        Platform.runLater(new Runnable() {
                                public void run() {
         ArrayList<panelData> billData=retrivedBills.getAllBillData();
         
        if(client.getIsSettled())
            settled();
        int count=billData.size();
        if(count==0)
            billStatusField.setText("No Bill Entries!!");
        
        
        
        
        
        
        if(billData.size()>0){
        for(int index=0;index<count;index++) {
        int clicked=index;
        String billId=billData.get(index).getBillId();
        String description=billData.get(index).getDescription();
        String amount=billData.get(index).getAmount();
        double ef=effectBillAmount(billData,index);
        System.out.println(billId+" "+description+" "+amount+" "+billData.get(index).getType()+" "+ef+" "+billData.get(index).getPayerCount()+" "+billData.get(index).getPayerList()+" "+billData.get(index).getParticipantsId().size()+" "+billData.get(index).getParticipantsId());
        String effect=Double.toString(ef);
        if(ef>0)
            effect="+"+effect;
        
        
        
//        billEntry bill=new billEntry(this.statusField,this.scrollPaneAnchor,this.loader,this.dis,this.dos,billId,description,amount,effect,index);
//        billPanels.add(bill);
//        bill.generatePanel();
        FileInputStream input=null; 
        try{
        input = new FileInputStream("C:\\Users\\KRITANK SINGH\\Documents\\NetBeansProjects\\SplitwiseFXML\\resource\\bill2.png");
        }catch(Exception ex)
        {
            System.out.println("Unable to load image!");
        }
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(65);
        imageView.setFitWidth(60);

        
        Label descriptionLabel,d;
        Label amountLabel,a;
        Label effectLabel,e;
        Button settleButton;
        AnchorPane billPanel;
        
        billPanel=new AnchorPane();
        if(billData.get(index).getType()=="bill")
        billPanel.setStyle("-fx-background-color: #a0a2ab; -fx-border-width: 0 5 5 5; -fx-border-color: #2D3447;");
        else billPanel.setStyle("-fx-background-color: #a0a2bb; -fx-border-width: 0 5 5 5; -fx-border-color: #2D3447;");
        billPanel.setPrefSize(479.0,80.0);
        billPanel.setLayoutX(0);
        billPanel.setLayoutY(index*80.0);
        
        
        d=new Label("Description: ");
        d.setLayoutX(150);
        d.setLayoutY(10);
        d.setTextFill(Color.web("#ffffff", 1.0));
        descriptionLabel=new Label(description);
        descriptionLabel.setLayoutX(254);
        descriptionLabel.setLayoutY(10);
        descriptionLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        a=new Label("Amount: ");
        a.setLayoutX(150);
        a.setLayoutY(30);
        a.setTextFill(Color.web("#ffffff", 1.0));
        amountLabel=new Label(amount);
        amountLabel.setLayoutX(254);
        amountLabel.setLayoutY(30);
        amountLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        e=new Label("Effect: ");
        e.setLayoutX(150);
        e.setLayoutY(50);
        e.setTextFill(Color.web("#ffffff", 1.0));
        effectLabel=new Label(effect);
        effectLabel.setLayoutX(250);
        effectLabel.setLayoutY(50);
        effectLabel.setTextFill(Color.web("#ffffff", 1.0));
        
        settleButton = new Button("Settle");
        IDEvt(settleButton, billData.get(index));
        settleButton.setLayoutX(400);
        settleButton.setLayoutY(45);
        settleButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        billPanel.setRightAnchor(settleButton,10.0);
        billPanel.setLeftAnchor(imageView,10.0);
        billPanel.setTopAnchor(imageView,5.0);
        billPanel.setBottomAnchor(imageView,5.0);
        billPanel.getChildren().add(imageView);
        billPanel.getChildren().add(d);
        billPanel.getChildren().add(a);
        billPanel.getChildren().add(e);
        billPanel.getChildren().add(descriptionLabel);
        billPanel.getChildren().add(amountLabel);
        billPanel.getChildren().add(effectLabel);
        billPanel.getChildren().add(settleButton);
        scrollPaneAnchor.getChildren().add(billPanel);
             }
        }
                                } 
     });   
    }
    

    void loadStatusPanel()
    {
        Platform.runLater(new Runnable() {
                                public void run() {
                                    userName.setText(client.getUserName());
                                    phoneNo.setText(client.getPhoneNo());
                                    //bills
                                    for(int i=0;i<retrivedBills.getAllBillData().size();i++)
                                    {
                                        double ef=effectBillAmount(retrivedBills.getAllBillData(),i);
                                        if(ef>0)
                                        credit+=ef;
                                        else debit+=ef;
                                    }
                                    balance=credit+debit;
                                    double total=Math.abs(credit)+Math.abs(debit);
                                    if(total==0.0&&credit==0.0)
                                    {
                                        pieChart.setVisible(false);
                                    //    progressBar.setProgress(0.5);
                                    }
                                    
                                    else{
                                    double prog=Math.abs(credit)/total;
                                    //double prog=0.5;
                                 //   progressBar.setProgress(prog);
                                    }
                                    
                                    ObservableList<PieChart.Data> pieData=FXCollections.observableArrayList(
                                             new PieChart.Data("debit", Math.abs(Math.ceil(debit))),
                                            new PieChart.Data("credit", Math.ceil(credit))
                                           
                                    );
                                    pieChart.setData(pieData);
                                    creditField.setText(Double.toString(Math.ceil(credit)));
                                    debitField.setText(Double.toString(Math.abs(Math.ceil(debit))));
                                    balanceField.setText(Double.toString(Math.ceil(balance)));
                                }
                        });
    }
    
}



           