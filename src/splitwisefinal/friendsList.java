/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author KRITANK SINGH
 */
public class friendsList {
    
    private static  ArrayList<friendData> friends;
    private static ObservableList<String>    friendName;
    private static ObservableList<String> displayList;
    
    friendsList(ArrayList<friendData> friends)
    {
        this.friends=friends;
        upDate();
    }
    
    void add(ArrayList<friendData> data)
    {
        for(int i=0;i<data.size();i++){
        friends.add(data.get(i));
        friendData temp=data.get(i);
        displayList.add(temp.getUserName()+"-"+temp.getPhoneNo());
        friendName.add(temp.getUserName());
        }
    }
    
    void upDate()
    {
        displayList=FXCollections.observableArrayList();
        friendName=FXCollections.observableArrayList();
        for(int i=0;i<this.friends.size();i++)
        {
            friendData temp=friends.get(i);
            displayList.add(temp.getUserName()+"-"+temp.getPhoneNo());
            friendName.add(temp.getUserName());
        }
    }
    
    void setFriends(ArrayList<friendData> friends)
    {
        this.friends=friends;
    }
    
    ArrayList<friendData> getFriends()
    {
        return this.friends;
    }
    
    ObservableList<String> getFriendName()
    {
        return this.friendName;
    }
    
    ObservableList<String> getDisplayList()
    {
        return this.displayList;
    }
    
    
}
