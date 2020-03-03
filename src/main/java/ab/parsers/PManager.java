package ab.parsers;

import java.util.Properties;
import java.io.*;

public class PManager{

   private Properties props;
   public static String NAME = "PManager";
   public boolean DEBUG = true;

   public PManager(){
   }

   
   public boolean init (String fname){
     try {
        props = new Properties();
        if (fname !=null)props.load(new FileInputStream(fname));
        log ("initializing me");
        return true;
     } catch (Exception e){
       return false;
     }

   }

   public String get(String name){
     log ("get "+name+" as "+props.getProperty(name));
     return props.getProperty(name); 
   }


   public void log(String msg){
     String logMsg = "["+NAME+"]: "+msg;
     if (DEBUG) {
        System.out.println(logMsg);
     }
   }


}
