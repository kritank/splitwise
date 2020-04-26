/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author KRITANK SINGH
 */
public class retrivedBill {
    static ArrayList<panelData> billData;
    static ArrayList<panelData> otherBillData;
    static ArrayList<panelData> allBillData;
    //static HashMap<String,ArrayList<panelData>> hash;
    static user client;
            
    public retrivedBill(ArrayList<panelData> billData,ArrayList<panelData> otherBillData)
    {
        this.client=client;
        allBillData=new ArrayList<panelData>();
        this.billData=billData;
        this.otherBillData=otherBillData;
        allBillData.addAll(billData);
        allBillData.addAll(otherBillData);
        
    }
    
    void removeOtherBill(int index)
    {
        panelData bill=otherBillData.get(index);
        //hash.get(bill.get)
        otherBillData.remove(index);
    }
   
     void removeBill(int index)
    {
        billData.remove(index);
    }
    void removeAllBill(int index)
    {
        allBillData.remove(index);
    }
    void addOtherBill(panelData temp)
    {
        otherBillData.add(temp);
        allBillData.add(temp);
    }
    
    void addBill(panelData temp)
    {
        billData.add(temp);
        allBillData.add(temp);
    }
    
    void setBillData(ArrayList<panelData> billData)
    {
        this.billData=billData;
    }
    void setOtherBillData(ArrayList<panelData> otherBillData)
    {
        this.otherBillData=otherBillData;
    }
    
    ArrayList<panelData> getBillData()
    {
        return this.billData;
    }
    
    ArrayList<panelData> getOtherBillData()
    {
        return this.otherBillData;
    }
    
    ArrayList<panelData> getAllBillData()
    {
        return this.allBillData;
    }
   
}
