/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import javafx.scene.control.Label;

/**
 *
 * @author KRITANK SINGH
 */
public class closeWindow {
    private DataInputStream dis;
    private DataOutputStream dos;
    private Label statusField;
    
    public closeWindow(DataInputStream dis,DataOutputStream dos,Label statusField)
    {
        this.dis=dis;
        this.dos=dos;
        this.statusField=statusField;
    }
    public void close()
    {
        // sendMessage thread 
		Thread sendMessage = new Thread(new Runnable() 
		{ 
			@Override
			public void run() { 
                            try 
                            { 
                                         
                                             // trying to send to server
                                             // write on the output stream
                                            String queryType="23";
                                            dos.writeUTF(queryType);
                                            System.out.println("Close connection request Sent to Server!!");
                                           
                                        } 
                                         catch (Exception e) { 
                                            System.out.println("Problem in seding: close connection request"+e); 
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
                                               // String clientId = dis.readUTF();
                                                String queryType = dis.readUTF();
                                                String serverName= dis.readUTF();
                                                int result = Integer.parseInt(dis.readUTF());
                                                System.out.println(serverName+": "+queryType+" "+result);
                                                switch(result){
                                                      case 1:
                                                                break;
                                                      case 0:
                                                                statusFieldAlert salert=new statusFieldAlert(statusField,"Unable to Exit!!");
                                                                salert.alert();
                                                                break;
                                                      default:  
                                                          System.out.println("Invalid Result obtained!!");
                                                }
                                                System.out.println("Got the closing acknowlegement from server");
						System.exit(0);
					} catch (Exception e) { 
						System.out.println("Problem: in reciving closing conformation from server"); 
                                                System.exit(0);
					} 
                                        
				}
		}); 
                readMessage.start(); 
    }
}
