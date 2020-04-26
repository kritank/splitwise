/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.util.ArrayList;

/**
 *
 * @author KRITANK SINGH
 */
public  class effectGen {
    
    public static double share(String Amount,String mode,String Count)
    {
        double amount=Double.parseDouble(Amount);
        int participantCount= Integer.parseInt(Count)+1;
        return amount/participantCount;
    }
    
    public static double  effectBillAmount(String Amount,String mode,String Count)
    {
        double ef=share(Amount, mode, Count);
        switch(mode)
        {
            case "bill":
                break;
            case  "otherBill":
                ef=ef*-1;
                break;
            default:
                ef=-9999;
        }
        System.out.println("Effect in : "+ef);
        return ef;
    }
    
    public static boolean check(String Amount,String mode,String Count)
    {
        boolean result=false;
        double t=newAmount(Amount,mode,Count);
        if(t>share(Amount, mode, Count))
            result=true;
        return result;
        
    }
    
    public static double newAmount(String Amount,String mode,String Count)
    {
        double result=0;
        double ef=effectBillAmount(Amount,mode,Count);
        double amount=Double.parseDouble(Amount);
        System.out.println("amount: "+amount+" effect: "+ef);
        result=ef+amount;
        System.out.println("Amount: "+result);
        return result;
    }
    
}
