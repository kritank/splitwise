/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitwisefinal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 *
 * @author KRITANK SINGH
 */
public class otpGenerator {
    
    static String sendOTP(String userName,String phoneNo) throws Exception
    {
                        String ran=generateOTP();
                        System.out.println("KEY: "+ran);
                        String otp="SPLTWS"+ran;
                        String messagei="Hey "+userName+" your ONE TIME PASSWORD is "+otp;
                        System.out.println(messagei);
			String apiKey = "apikey=" + "Jt7c4Opwu+k-HTPauJTGLyZizuNiGTwyZ31eidIiKT";
			String message = "&message=" + messagei;
			String sender = "&sender=" + "txtlcl";
			String numbers = "&numbers=" + phoneNo;
			
			// Send data
			HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
			String data = apiKey + numbers + message + sender;
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				stringBuffer.append(line);
                             //  JOptionPane.showMessageDialog(null, "Message : "+line);
			}
			rd.close();
                        return otp;
    }
    
    
      static String generateOTP()  
    {  //int randomPin declared to store the otp 
        //since we using Math.random() hence we have to type cast it int 
        //because Math.random() returns decimal value 
        int randomPin   =(int) (Math.random()*900000)+100000; 
        String otp  = String.valueOf(randomPin); 
        return otp; //returning value of otp 
    }
}
