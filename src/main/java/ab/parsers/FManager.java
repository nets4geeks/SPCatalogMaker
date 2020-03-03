package ab.parsers;

import java.util.Properties;
import java.io.*;

public class FManager{

   public static String NAME = "FManager";
   public boolean DEBUG = true;

   public FManager(){
      log ("initializing me");
   }

   
   public File[] getFileDescriptors (String folder,String extension){
     try {
        File f = new File(folder);

        FilenameFilter filter = new FilenameFilter() {
           @Override
           public boolean accept(File f, String name) {
              // We want to find only .c files
              return name.endsWith(extension);
           }
        };

        File[] files = f.listFiles(filter);

        return files;
     } catch (Exception e){
       return null;
     }

   }


/*    try {
            File f = new File("D:/Programming");

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    // We want to find only .c files
                    return name.endsWith(".c");
                }
            };

            // Note that this time we are using a File class as an array,
            // instead of String
            File[] files = f.listFiles(filter);

            // Get the names of the files by using the .getName() method
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }*/


   public void log(String msg){
     String logMsg = "["+NAME+"]: "+msg;
     if (DEBUG) {
        System.out.println(logMsg);
     }
   }


}
