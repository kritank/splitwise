/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import javafx.collections.ObservableList;

/**
 *
 * @author KRITANK SINGH
 */
public class panelData {   
       private  String billId;
       private  String amount;
       private  String description;
       private  String mode;
       private  ObservableList<String> payerList;
       private  ObservableList<String> participantsId;
       private  String type;
       
       public panelData( String billId,String amount,String description,String mode, ObservableList<String> payerList,ObservableList<String> participantsId,String type)
       {
           this.billId=billId;
           this.amount=amount;
           this.description=description;
           this.payerList=payerList;
           this.mode=mode;
           this.participantsId=participantsId;
           this.type=type;
       }
       
       public void setBillId(String billList)
       {
           this.billId=billList;
       }
       
       public void setAmount(String amountList)
       {
           this.amount=amountList;
       }
       
       public void setDescription(String descriptionList)
       {
           this.description=descriptionList;
       }
       public void setMode(String modeList)
       {
           this.mode=modeList;
       }
       
       public void setPayerList(ObservableList<String> payerList)
       {
           this.payerList=payerList;
       }
       
       public void setParticipantsId(ObservableList<String> participantsId)
       {
           this.participantsId=participantsId;
       }
       
       public String getBillId()
       {
           return this.billId;
       }
       
       public String getAmount()
       {
           return this.amount;
       }
       
       public String getDescription()
       {
           return this.description;
       }
       
       public String getMode()
       {
           return this.mode;
       }
       
       public ObservableList<String> getParticipantsId()
       {
           return this.participantsId;
       }
       
       public String getType()
       {
           return type;
       }
       
       public ObservableList<String> getPayerList()
       {
           return this.payerList;
       }
       
       public int getPayerCount()
       {
           return this.payerList.size();
       }
    
}
