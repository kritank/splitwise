/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.util.ArrayList;
import javafx.collections.ObservableList;

/**
 *
 * @author KRITANK SINGH
 */
public class user {
    private static String uid;
    private static String userName;
    private static String phoneNo;
    private static String email;
    private static String password;
    private static ArrayList<String> retrivedFriendUid;
    private static ObservableList<String> retrivedFriends;
    private static Boolean isSettled;
    
    public user(String uid,String userName,String phoneNo,String email,String password,Boolean isSettled)
    {
        this.uid=uid;
        this.userName=userName;
        this.phoneNo=phoneNo;
        this.email=email;
        this.password=password;
        this.isSettled=isSettled;
    }
    
   public void setUid(String uid)
    {
        this.uid=uid;
    }
    public void setUserName(String userName)
    {
        this.userName=userName;
    }
    public void setPhoneNo(String phoneNo)
    {
        this.phoneNo=phoneNo;
    }
    public void setEmail(String email)
    {this.email=email;
        
    }
    public void setPassword(String password)
    {
        this.password=password;
    }
    public void setRetrivedFriendUid(ArrayList<String> retrivedFriendUid)
    {
        this.retrivedFriendUid=retrivedFriendUid;
    }
    public void setRetrivedFriend(ObservableList<String> retrivedFriend)
    {
        this.retrivedFriends=retrivedFriend;
    }
    public void setIsSettled(Boolean isSettled)
    {
        this.isSettled=isSettled;
    }
    
    public String getUid()
    {
        return uid;
    }
    
    
    public String getUserName()
    {
        return userName;
    }
    public String getPhoneNo()
    {
        return phoneNo;
    }
    public String getEmail()
    {
        return email;
    }
    public String getPassword()
    {
        return password;
    }
    
    public ArrayList<String> getRetrivedFriendUid()
    {
        return this.retrivedFriendUid;
    }
    public ObservableList<String> getRetrivedFriend()
    {
        return this.retrivedFriends;
    }
    public Boolean getIsSettled()
    {
        return this.isSettled=isSettled;
    }
}
