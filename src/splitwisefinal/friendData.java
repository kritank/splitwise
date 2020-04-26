/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author KRITANK SINGH
 */
public class friendData {
    private String userId;
    private String userName;
    private String phoneNo;
    
    public friendData(String userId,String userName,String phoneNo)
    {
       
        this.userId=userId;
        this.userName=userName;
        this.phoneNo=phoneNo;
    }
    
    public String getUserId()
    {
        return this.userId;
    }
    
    public String getUserName()
    {
        return this.userName;
    }
    
    public String getPhoneNo()
    {
        return this.phoneNo;
    }
    
    
}
