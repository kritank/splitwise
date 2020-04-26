package SplitwiseServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KRITANK SINGH
 */

import java.sql.*;
import java.io.*; 
import java.util.*; 
import java.net.*; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splitwisefinal.effectGen;
import splitwisefinal.friendData;
import splitwisefinal.panelData;
import splitwisefinal.user;

public class SplitwiseServer {
    
    // Vector to store active clients 
	static Vector<ClientHandler> ar = new Vector<>(); 
	
	// counter for clients 
	static int i = 0,serverSocketPort=1234;
        

	public static void main(String[] args) throws IOException 
	{ 
		// server is listening on port 1234 
		ServerSocket ss = new ServerSocket(serverSocketPort); 
		
		Socket s; 
                
                System.out.println("Server Started!");
		
		// running infinite loop for getting 
		// client request 
		while (true) 
		{ 
                        System.out.println("Server waiting for new Client!");
			// Accept the incoming request 
			s = ss.accept(); 

			System.out.println("New client request received : " + s); 
			
			// obtain input and output streams 
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
			
			System.out.println("Creating a new handler for this client..."); 

			// Create a new handler object for handling this request. 
			ClientHandler mtch = new ClientHandler(s,"Handler "+i, dis, dos); 

			// Create a new Thread with this object. 
			Thread t = new Thread(mtch); 
			
			System.out.println("Adding this client to active client list"); 

			// add this client to active clients list 
			ar.add(mtch); 

			// start the thread. 
			t.start(); 

			// increment i for new client. 
			// i is used for naming only, and can be replaced 
			// by any naming scheme 
			i++; 
                        

		} 
	} 
    
}


// ClientHandler class 
class ClientHandler implements Runnable 
{ 
	Scanner scn = new Scanner(System.in); 
        private final String name;
       // private String username;
       // private String password;
	final DataInputStream dis; 
	final DataOutputStream dos; 
	Socket s; 
	boolean isloggedin; 
        boolean isStopped;
        private Connection con;
        private Statement stmt;
        private ResultSet rs;
        private PreparedStatement ps;
	
	// constructor 
	public ClientHandler(Socket s, String name,DataInputStream dis, DataOutputStream dos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.s = s; 
		this.isloggedin=true; 
                this.isStopped=false;
	} 

	@Override
	public void run() { 

		String queryType; 
                System.out.println("Client Handler started: ");
		while (true) 
		{ 
                    if(!isStopped){
			try
			{       
                                //establishing connection with database
                                Class.forName("com.mysql.cj.jdbc.Driver"); //cj class
                                //now timezone needed to be specified
                                con=DriverManager.getConnection("jdbc:mysql://localhost/splitwiseDB?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","splitwiseDB","fs2Xmh9CBv1GiinL"); 
                                
                                
                                // receive the string 
                                System.out.println("Client Handler"+name+" waiting for query: ");
				queryType= dis.readUTF(); 
				System.out.println("Client Name "+name+" "+"Query Type: "+ queryType); 
                                
                             
                                switch(queryType){
                                    case "21": //login request
                                    {   
                                        String phoneNo="";
                                        String uid="";
                                        String email="";
                                        String billId="";
                                        String amount="";
                                        String mode="";
                                        String description="";
                                        
                                        //reading userName & password
                                        String userName = dis.readUTF();
                                        String password = dis.readUTF();
                                        System.out.println("UserName "+userName+" "+"Password "+ password); 
                                        
                                        String result="0"; //login
                                        String result2="1"; //retrive bills
                                        String result3="1";//retrive other bills
                                        ArrayList<panelData> billData=new ArrayList<panelData>();//bill data
                                        ArrayList<panelData> otherBillData=new ArrayList<panelData>();//other bill data
                                        //logging in
                                        try{  
                                            String query="select * from user where userName = ?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, userName);
                                            rs=ps.executeQuery();  
                                            
                                            while(rs.next())  
                                            {
                                                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(5));
                                                if(password.equals(rs.getString(5)))
                                                {
                                                   uid=Integer.toString(rs.getInt(1));
                                                   phoneNo=rs.getString(3);
                                                   email=rs.getString(4);
                                                   result="1"; //login success
                                                   break;
                                                }
                                            } 
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            } 
                                        if(result.equals("1")){
                                        //getting all the bills generated by logged user
                                        try{  
                                            String query="select * from bill inner join billpayers on bill.billId = billpayers.billId where billpayers.uid = ?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, uid);
                                            rs=ps.executeQuery();  
                                            
                                            while(rs.next())  
                                            {
                                                   System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(4));
                                                
                                                   billId=Integer.toString(rs.getInt(1));
                                                   amount=rs.getString(2);
                                                   mode=rs.getString(3);
                                                   description=rs.getString(4);
                                                   ObservableList<String> participantId=FXCollections.observableArrayList();
                                                   ObservableList<String> payerList=FXCollections.observableArrayList();
                                                   try{  
                                                    String query1="select * from billParticipants where billId = ?";
                                                    PreparedStatement ps1=con.prepareStatement(query1);  
                                                    ps1.setString(1, billId);
                                                    ResultSet rs1=ps1.executeQuery();  
                                                    System.out.print("Participants: ");
                                                    while(rs1.next())  
                                                    {
                                                    System.out.print(rs1.getInt(1)+"  "+rs1.getString(2)+" ");
                                                    String participantUid=Integer.toString(rs1.getInt(2));
                                                    participantId.add(participantUid);
                                                    //result="1"; 
                                                    }
                                                    System.out.print("\n");
                                                    }catch(Exception e){ 
                                                       System.out.println(e);
                                                    } 
                                                   
                                                    try{  
                                                    String query1="SELECT * FROM `billpayers` WHERE billId=?";
                                                    PreparedStatement ps1=con.prepareStatement(query1);  
                                                    ps1.setString(1, billId);
                                                    ResultSet rs1=ps1.executeQuery();  
                                                    System.out.print("Payers: ");
                                                    while(rs1.next())  
                                                    {
                                                    System.out.print(rs1.getInt(1)+"  "+rs1.getString(2)+" ");
                                                    String payerUid=Integer.toString(rs1.getInt(2));
                                                    payerList.add(payerUid);
                                                    //result="1"; 
                                                    }
                                                    System.out.print("\n");
                                                    }catch(Exception e){ 
                                                       System.out.println(e);
                                                    }        
                                                   panelData temp=new panelData(billId,amount,description,mode,payerList,participantId,"bill");
                                                   System.out.println(temp.getPayerList());
                                                   billData.add(temp);  
                                                   System.out.println(billData.get(billData.size()-1).getPayerList());
                                            }
                                           // con.close();  
                                            }catch(Exception e){ 
                                                result2="0";   //some error occured
                                                System.out.println(e);
                                            }
                                            //retrive bills of which user is a part
                                            try{  
                                            String query="select * from bill inner join billparticipants on bill.billId = billparticipants.billId where billparticipants.uid = ?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, uid);
                                            rs=ps.executeQuery();  
                                            
                                            while(rs.next())  
                                            {
                                                   System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(4)+"  "+rs.getString(5)+"  "+rs.getString(6));
                                                
                                                   billId=Integer.toString(rs.getInt(1));
                                                   amount=rs.getString(2);
                                                   mode=rs.getString(3);
                                                   //String payerUid=rs.getString(4);
                                                   description=rs.getString(4);
                                                   ObservableList<String> participantId=FXCollections.observableArrayList();
                                                   ObservableList<String> payerList=FXCollections.observableArrayList();
                                                   try{  
                                                    String query1="select * from billParticipants where billId = ?";
                                                    PreparedStatement ps1=con.prepareStatement(query1);  
                                                    ps1.setString(1, billId);
                                                    ResultSet rs1=ps1.executeQuery();  
                                                    System.out.print("Participants: ");
                                                    while(rs1.next())  
                                                    {
                                                    System.out.print(rs1.getInt(1)+"  "+rs1.getString(2)+" ");
                                                    String participantUid=Integer.toString(rs1.getInt(2));
                                                    participantId.add(participantUid);
                                                    //result="1"; 
                                                    } 
                                                    System.out.print("\n");
                                                    }catch(Exception e){ 
                                                       System.out.println(e);
                                                    } 
                                                    try{  
                                                    String query1="SELECT * FROM `billpayers` WHERE billId=?";
                                                    PreparedStatement ps1=con.prepareStatement(query1);  
                                                    ps1.setString(1, billId);
                                                    ResultSet rs1=ps1.executeQuery();  
                                                    System.out.print("Payers: ");
                                                    while(rs1.next())  
                                                    {
                                                    System.out.print(rs1.getInt(1)+"  "+rs1.getString(2)+" ");
                                                    String payerUid=Integer.toString(rs1.getInt(2));
                                                    payerList.add(payerUid);
                                                    //result="1"; 
                                                    } 
                                                    System.out.print("\n");
                                                    }catch(Exception e){ 
                                                       System.out.println(e);
                                                    }
                                                    panelData temp=new panelData(billId,amount,description,mode,payerList,participantId,"otherBill");
                                                    System.out.println(temp.getPayerList());
                                                    otherBillData.add(temp);
                                                    System.out.println(otherBillData.get(otherBillData.size()-1).getPayerList());
                                            }
                                            con.close();  
                                            }catch(Exception e){ 
                                                result3="0";   //some error occured
                                                System.out.println(e);
                                            }
    
                                        }
                                        //sending retrived Data
                                        //checking of user online
                                       if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sending to "+name);
                                                this.dos.writeUTF("11");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                                this.dos.writeUTF(result2);
                                                this.dos.writeUTF(result3);
                                                if(result.equals("1"))
                                                {
                                                    this.dos.writeUTF(uid);
                                                    this.dos.writeUTF(userName);
                                                    this.dos.writeUTF(phoneNo);
                                                    this.dos.writeUTF(email);
                                                    this.dos.writeUTF(password);
                                                }
                                                if(result2.equals("1"))
                                                {
                                                    int count=billData.size();
                                                    this.dos.writeUTF(Integer.toString(count));
                                                    for(int i=0;i<count;i++)
                                                    {
                                                        this.dos.writeUTF(billData.get(i).getBillId());
                                                        this.dos.writeUTF(billData.get(i).getAmount());
                                                        this.dos.writeUTF(billData.get(i).getDescription());
                                                        this.dos.writeUTF(billData.get(i).getMode());
                                                        int participantsCount=billData.get(i).getParticipantsId().size();
                                                        this.dos.writeUTF(Integer.toString(participantsCount));
                                                        System.out.println(billData.get(i).getParticipantsId());
                                                        for(int j=0;j<participantsCount;j++)
                                                        {
                                                            this.dos.writeUTF(billData.get(i).getParticipantsId().get(j));
                                                            System.out.println(billData.get(i).getParticipantsId().get(j));
                                                        }
                                                        int payerCount=billData.get(i).getPayerCount();
                                                        this.dos.writeUTF(Integer.toString(payerCount));
                                                        System.out.println(billData.get(i).getPayerList());
                                                        for(int j=0;j<payerCount;j++)
                                                        {
                                                            this.dos.writeUTF(billData.get(i).getPayerList().get(j));
                                                            System.out.println(billData.get(i).getPayerList().get(j));
                                                        }
                                                   
                                                    }
                                                }
                                                if(result3.equals("1"))
                                                {
                                                    
                                                    int count=otherBillData.size();
                                                    this.dos.writeUTF(Integer.toString(count));
                                                    for(int i=0;i<count;i++)
                                                    {
                                                        this.dos.writeUTF(otherBillData.get(i).getBillId());
                                                        this.dos.writeUTF(otherBillData.get(i).getAmount());
                                                        this.dos.writeUTF(otherBillData.get(i).getDescription());
                                                        this.dos.writeUTF(otherBillData.get(i).getMode());
                                                        //this.dos.writeUTF(otherBillData.get(i).getUid());
                                                        int participantsCount=otherBillData.get(i).getParticipantsId().size();
                                                        this.dos.writeUTF(Integer.toString(participantsCount));
                                                        System.out.println(otherBillData.get(i).getParticipantsId());
                                                        for(int j=0;j<participantsCount;j++)
                                                        {
                                                            this.dos.writeUTF(otherBillData.get(i).getParticipantsId().get(j));
                                                            System.out.println(otherBillData.get(i).getParticipantsId().get(j));
                                                        }
                                                        int payerCount=otherBillData.get(i).getPayerList().size();
                                                        this.dos.writeUTF(Integer.toString(payerCount));
                                                        System.out.println(otherBillData.get(i).getPayerList());
                                                        for(int j=0;j<payerCount;j++)
                                                        {
                                                            this.dos.writeUTF(otherBillData.get(i).getPayerList().get(j));
                                                            System.out.println(otherBillData.get(i).getPayerList().get(j));
                                                        }
                                                    }
                                                }
                                            } 
                                        
                                        break;
                                    }
                                    case "22": //registration request
                                    { 
                                        //reading the values
                                        String userName =  dis.readUTF();
                                        String phoneNo  =  dis.readUTF();
                                        String email    =  dis.readUTF();
                                        String password =  dis.readUTF();
                                        
                                        System.out.println("UserName "+userName+" "+" Phone No: "+phoneNo+" Email : "+email+" Password "+ password); 
                                        
                                        String result="0"; //update request
                                         try{
                                            String query="INSERT INTO `user`(`userName`, `phoneNo`, `email`, `password`) VALUES (?,?,?,?)";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, userName);
                                            ps.setString(2, phoneNo);
                                            ps.setString(3, email);
                                            ps.setString(4, password);
                                              
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println(userName+" Record inserted!!");
                                                result="1";
                                            }  
                                            con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            } 
                                        
                                             if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sent to "+name);
                                                this.dos.writeUTF("12"); //registration request
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                            } 
                                    }
                                        break;
                                    case "23": //close conection Request
                                    {
                                        
                                        System.out.println("Close connection request"); 
                                        isStopped=true;
                                        String result="1";
                                       if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sent to "+name);
                                                this.dos.writeUTF("13");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                            } 
                                        break;
                                    }
                                    case "24": //edit Page Request
                                    {
                                        //reading values
                                        String uid      =  dis.readUTF();
                                        String userName =  dis.readUTF();
                                        String phoneNo  =  dis.readUTF();
                                        String email    =  dis.readUTF();
                                        String password =  dis.readUTF();
                                        
                                        System.out.println("User Id "+uid+" UserName "+userName+" "+" Phone No: "+phoneNo+" Email : "+email+" Password "+ password); 
                                        
                                        String result="0";
                                        //check the database to check the entry using initail then modify 
                                        try{  
                                            String query="UPDATE `user` SET `userName`=?,`phoneNo`=?,`email`=?,`password`=? WHERE uid =? ";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, userName);
                                            ps.setString(2, phoneNo);
                                            ps.setString(3, email);
                                            ps.setString(4, password);
                                            ps.setString(5, uid);
                                              
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println(userName+" Record Updated!!");
                                                result="1";
                                            }  
                                            con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            }
                                        
                                            if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sent to "+name);
                                                this.dos.writeUTF(name); 
                                                this.dos.writeUTF("14"); //edit page request
                                                this.dos.writeUTF(result);
                                                break;
                                            }
                                    }
                                        break;
                                    case "25": //Friend request
                                        {
                                         ArrayList <friendData> retriveFriends= new  ArrayList <friendData > ();
                                         String clientuid=dis.readUTF();
                                         int count=Integer.parseInt(dis.readUTF());
                                         System.out.println("Friend request by "+clientuid+": for : " +count+" items"); 
                                         for(int i=0;i<count;i++)
                                         {
                                             String fuid=dis.readUTF();
                                             String userName=dis.readUTF();
                                             String phoneNo=dis.readUTF();
                                             friendData temp=new friendData(fuid,userName,phoneNo);
                                             retriveFriends.add(temp);
                                         }
                                        String result="0";
                                            try{  
                                            String query="INSERT INTO `isfriend` (`uid`, `fuid`) VALUES (?, ?)";
                                            ps=con.prepareStatement(query);  
                                            for(friendData t :retriveFriends){
                                            ps.setString(1,clientuid );
                                            ps.setString(2, t.getUserId());
                                            if(ps.executeUpdate()>0)  
                                              {
                                                System.out.println(clientuid+" Record ( 1 ) inserted!!");
                                              
                                              }  
                                           // }
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, t.getUserId());
                                            ps.setString(2, clientuid);
                                            if(ps.executeUpdate()>0)  
                                              {
                                                System.out.println(t.getUserId()+" Record (  2 ) inserted!!");
                                                result="1";
                                              }
                                            }
                                            con.close();
                                            }catch(Exception e){ 
                                                result="0";
                                                System.out.println(e);
                                            }
                                            
                                            if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sent to "+name);
                                                this.dos.writeUTF("15");
                                                this.dos.writeUTF(name); 
                                                this.dos.writeUTF(result);
                                            } 
                                        break;
                                    }
                                    case "26": //add Bill
                                     {
                                         ArrayList<String> payerList=new ArrayList<>();
                                         String count_payer=dis.readUTF();
                                         for(int i=0;i<Integer.parseInt(count_payer);i++)
                                         {
                                             String uid        =dis.readUTF();
                                             payerList.add(uid);
                                             
                                         }
                                         String description=dis.readUTF();
                                         String amount=     dis.readUTF();
                                         String type=       dis.readUTF();
                                         String count =     dis.readUTF();
                                         String selectedParticiants=dis.readUTF();
                                         System.out.println("Desc: "+description+" amount: "+amount+" type: "+type+" count: "+count+" Participants :"+selectedParticiants); 
                                         StringTokenizer token2= new StringTokenizer(selectedParticiants,"#");
                                         ObservableList<String> selectedParticipant=FXCollections.observableArrayList();
                                         while(token2.hasMoreTokens())
                                         {
                                             String string=token2.nextToken();
                                             System.out.println(string);
                                             selectedParticipant.add(string);
                                         }
                                        
                                        String result="0";
                                        String billId="-1";
                                        String result2="0";
                                        //Add to the database
                                        //adding to bill table
                                        try{  
                                            
                                            String query="INSERT INTO `billentry`() VALUES ()";
                                            ps=con.prepareStatement(query);  
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("Bill entry Record inserted!!");
                                               //  result="1";
                                            } 
                                            
                                            query="SELECT LAST_INSERT_ID()";
                                            Statement st=con.createStatement();
                                            st=con.createStatement();
                                            rs=st.executeQuery(query);
                                            if(rs.next())
                                            {
                                                result="1";
                                                billId=rs.getString(1);
                                                System.out.println("Bill Id: "+billId);
                                            }
                                            
                                             if(result.equals("1")){   
                                               query="INSERT INTO `billpayers`(`billId`, `uid`) VALUES (?,?)";
                                               ps=con.prepareStatement(query); 
                                               ps.setString(1, billId);
                                               int i=0;
                                               for( i=0;i<payerList.size();i++)
                                               {
                                                   ps.setString(2, payerList.get(i));
                                                    if(ps.executeUpdate()>0)  
                                                    {
                                                    System.out.println(billId+" Bill Record inserted!!");
                                                    } 
                                               }
                                               if(i<payerList.size())
                                                   result="0";
                                                   
                                                
                                            }  
                                                
                                                
                                             if(result.equals("1")){   
                                                
                                                
                                             query="INSERT INTO `bill`(`billId`, `amount`, `mode`, `description`) VALUES (?,?,?,?)";
                                            ps=con.prepareStatement(query); 
                                            ps.setString(1, billId);
                                            ps.setString(2, amount);
                                            ps.setString(3, type);
                                            ps.setString(4, description);
                                            
                                              
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println(billId+" Bill Record inserted!!");
                                                 result="1";
                                            }  
                                            }
                                           // con.close();  
                                            }catch(Exception e){ 
                                                result="0";
                                                System.out.println(e);
                                            }
                                        if(result.equals("1")){
                                        //add to billparticipants
                                        try{  
                                            String query="INSERT INTO `billparticipants`(`billId`, `uid`) VALUES (?,?)";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, billId);
                                            for(int i=0;i<selectedParticipant.size();i++){
                                            ps.setString(2, selectedParticipant.get(i));
                                            if(ps.executeUpdate()>0)
                                            {
                                                System.out.println("Participant Record Inserted!!");
                                                result2="1";
                                                
                                            }
                                            }
                                            con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            }
                                        }
                                        
                                       if (this.isloggedin==true) 
                                            { 
                                                System.out.println("Sent to "+name);
                                                this.dos.writeUTF("16");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                                if(result.equals("1"))
                                                {
                                                  this.dos.writeUTF(billId);
                                                  this.dos.writeUTF(result2);
                                                }
                                            } 
                                        break;
                                    }
                                    case "27": //settle Bill
                                    {
                                       String otherBillparticipant="";
                                       System.out.println("1");
                                       ArrayList<String> participantsId=new ArrayList<String>();
                                       ArrayList<String> payerId=new ArrayList<String>();
                                       String result1 ="0";
                                       String result="0";
                                       String billId = dis.readUTF();
                                       String type   = dis.readUTF();
                                       String amount = dis.readUTF();
                                       String count  = dis.readUTF();
                                       String payerCount  = dis.readUTF();
                                       
                                       System.out.println("Bill No: "+billId+" "+count+" "+payerCount);
                                       for(int i=0;i<Integer.parseInt(payerCount);i++)
                                       {
                                           String payerid=dis.readUTF();
                                           payerId.add(payerid);
                                       }
                                       
                                       for(int i=0;i<Integer.parseInt(count);i++)
                                       {
                                           String participantsUid=dis.readUTF();
                                           participantsId.add(participantsUid);
                                       }
                                       if(type.equals("otherBill"))
                                       {
                                           otherBillparticipant=dis.readUTF();
                                       }
                                       
                                        try{  
                                            System.out.println("2");
                                            String query1="DELETE FROM `billpayers` WHERE billId = ? AND uid = ?";
                                            ps=con.prepareStatement(query1);  
                                            int i;
                                            for(i=0;i<Integer.parseInt(payerCount);i++){
                                            ps.setString(1, billId);
                                            ps.setString(2, payerId.get(i));
                                            System.out.println(ps);
                                            //System.out.println("3");
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("BillPayer with billId: "+billId+" deleted!!");
                                                result1="1";
                                            }
                                            }
                                            if(i<Integer.parseInt(payerCount))
                                                result1="0";
                                            
                                            System.out.println("3");
                                            if(result1.equals("1")){
                                            String query="DELETE FROM `billparticipants` WHERE billId = ? AND uid = ?";
                                            ps=con.prepareStatement(query);  
                                            for(i=0;i<Integer.parseInt(count);i++){
                                            ps.setString(1, billId);
                                            if(type.equals("bill")){
                                            ps.setString(2, participantsId.get(i));
                                            System.out.println(ps);
                                            //System.out.println("3");
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("BillParticipants with billId: "+billId+" deleted!!");
                                                result1="1";
                                            }  
                                            System.out.println("4");
                                            }else{
                                                ps.setString(2, otherBillparticipant);
                                                System.out.println(ps);
                                            //System.out.println("3");
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("BillParticipants with billId: "+billId+" deleted!!");
                                                result1="1";
                                            } 
                                            }
                                            }
                                        }
                                           // con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            }
                                        
                                        if(result1.equals("1")){
                                        
                                        try{  
                                            boolean f= effectGen.check(amount,type,count);
                                            System.out.println(f);
                                            if(type.equals("otherBill")&&f){
                                            amount=Double.toString(effectGen.newAmount(amount, type, count));
                                            String query="UPDATE `bill` SET `amount`=? WHERE billId =?";
                                            ps=con.prepareStatement(query); 
                                            ps.setString(1,amount);
                                            ps.setString(2, billId);
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("Bill with billId: "+billId+" updated!!");
                                                result="1";
                                            }  
                                            }
                                            else{
                                            String query="DELETE FROM `bill` WHERE billId =?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, billId);
                                            
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("Bill with billId: "+billId+" deleted!!");
                                                result="1";
                                            }  
                                            query="DELETE FROM `billentry` WHERE billId =?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, billId);
                                            if(ps.executeUpdate()>0)  
                                            {
                                                System.out.println("Billentry with billId: "+billId+" deleted!!");
                                                result="1";
                                            } 
                                            }
                                            con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            }
                                         
                                        }
                                       
                                       if (this.isloggedin==true) 
                                            { 
                                                this.dos.writeUTF("17");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result1);
                                                this.dos.writeUTF(result);
                                            } 
                                       
                                      break;
                                    }
                                    case "28": //retrive friends
                                    {
                                        String userId=dis.readUTF();
                                       // System.out.println("User Id: "+userId); 
                                        
                                        String result="1";
                                        //check the database for the friend and return the request
                                        ArrayList <String> retriveFriendsUid= new  ArrayList <String > ();
                                        ArrayList <String> retriveFriendsName= new ArrayList <String > ();
                                        ArrayList <String> retriveFriendsPhoneNo= new ArrayList <String > ();
                                        
                                        try{  
                                            String query="SELECT * FROM isfriend INNER JOIN user ON isfriend.fuid = user.uid WHERE isfriend.uid= ?";
                                            //String query="select * from isfriend where uid = ?";
                                            ps=con.prepareStatement(query);  
                                            ps.setString(1, userId);
                                            rs=ps.executeQuery();  
                                            
                                            while(rs.next())  
                                            {
                                                System.out.println(rs.getInt(1)+"  "+rs.getInt(2)+"  "+rs.getString(4)+"  "+rs.getString(5));
                                                retriveFriendsUid.add(rs.getString(2));
                                                retriveFriendsName.add(rs.getString(4));
                                                retriveFriendsPhoneNo.add(rs.getString(5));
                                            }  
                                            con.close(); 
                                            }catch(Exception e){ 
                                                result="0";
                                                System.out.println(e);
                                            }
                                        
                                            System.out.println("after1 "+retriveFriendsUid.size()+" "+result);
                                             if (this.isloggedin==true) 
                                            { 
                                                this.dos.writeUTF("18");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                                if(result.equals("1"))
                                                {
                                                    System.out.println("after2");
                                                    int size=retriveFriendsUid.size();
                                                    this.dos.writeUTF(Integer.toString(size));
                                                    for(int i=0;i<size;i++)
                                                    {
                                                        System.out.println("in");
                                                        this.dos.writeUTF(retriveFriendsUid.get(i));
                                                        this.dos.writeUTF(retriveFriendsName.get(i));
                                                        this.dos.writeUTF(retriveFriendsPhoneNo.get(i));
                                                    }
                                                }
                                            } 
                                      break;
                                    }
                                    case "29": //check for duplicate no
                                    {
                                        String phoneNo  =  dis.readUTF();
                                        System.out.println("Phone No: "+phoneNo); 
                                        
                                        String result="0";
                                        //check the database to initialise result
                                         try{
                                            String query="SELECT * FROM `user` WHERE phoneNo =?";
                                            ps=con.prepareStatement(query);
                                            ps.setString(1, phoneNo);
                                            rs=ps.executeQuery();
                                            if(rs.next())  
                                            {
                                                System.out.println("Duplicate: "+phoneNo+" !!");
                                                result="1";
                                            }  
                                            con.close();  
                                            }catch(Exception e){ 
                                                System.out.println(e);
                                            } 
                                        
                                        if (this.isloggedin==true) 
                                            { 
                                                this.dos.writeUTF("19");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                            }
                                    }
                                    break;
                                    case "30":
                                        String phoneNo="";
                                        String uid="";
                                        String userName="";
                                        String email="";
                                        ArrayList<friendData> userList=new ArrayList<friendData>();
                                        String result="1";
                                        uid=dis.readUTF();
                                        System.out.println("all user request for: "+uid);
                                        //check the database to initialise result
                                         try{
                                             
                                            String query="SELECT uid,userName,phoneNo FROM user WHERE uid !=? AND uid NOT IN (SELECT fuid FROM isfriend WHERE uid = ?)";
                                            ps=con.prepareStatement(query);
                                            ps.setString(1, uid);
                                            ps.setString(2, uid);
                                            rs=ps.executeQuery();
                                            while(rs.next())  
                                            {
                                                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
                                                
                                                   uid=Integer.toString(rs.getInt(1));
                                                   userName=rs.getString(2);
                                                   phoneNo=rs.getString(3);
                                                   friendData temp=new friendData(uid,userName,phoneNo);
                                                   userList.add(temp);
                                                   //result="1"; 
                                            }  
                                            con.close();  
                                            }catch(Exception e){ 
                                                result="0";
                                                System.out.println(e);
                                            } 
                                        
                                        if (this.isloggedin==true) 
                                            { 
                                                this.dos.writeUTF("10");
                                                this.dos.writeUTF(name);
                                                this.dos.writeUTF(result);
                                                if(result.equals("1"))
                                                {
                                                    int count=userList.size();
                                                    this.dos.writeUTF(Integer.toString(count));
                                                    for(int i=0;i<count;i++){
                                                        this.dos.writeUTF(userList.get(i).getUserId());
                                                        this.dos.writeUTF(userList.get(i).getUserName());
                                                        this.dos.writeUTF(userList.get(i).getPhoneNo());
                                                      //  this.dos.writeUTF(userList.get(i).getEmail());
                                                        
                                                    }
                                                    
                                                }
                                                
                                            }
                                        System.out.println(name+" "+result);
                                        break;
                                    default: 
                                    System.out.println("Service Unavaliable"+" "+queryType);
                                    isStopped=true;
                                }             
			}
                        catch(ClassNotFoundException e)
                        {
                            System.out.println("Unable to locate the JDBC connector "+e);
                        }
                        catch(SQLException e)
                        {
                            System.out.println("Unable to get the connection "+e);
                        }
                        catch(SocketException s)
                        {
                            System.out.println("Client exited abruptly "+name);
                            isStopped=true;
                        }
                        catch (Exception e){ 
                            System.out.println("Server Unable to process request!! for "+name+" "+e);
			} 
                    }
                    else{
                        break;
                    }
                        
		} 
                try{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(Exception e){ 
			System.out.println("Server Unable to close resources!! for "+name);
		} 
                System.out.println("Server Handler"+name+" Closing");
	} 
        
} 
