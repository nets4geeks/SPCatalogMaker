
package ab.applications;

import ab.parsers.*;
import ab.owl.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import java.io.*;


class SPCatalogMaker {
   public static String NAME = "SPCatalogMaker";
   public static boolean DEBUG = true;
   public static String OWLEXT = ".owl";

   public static void main(String args[]){
 
      if (args.length != 0) {

         log("starting ...");
         PManager conf = new PManager();

         if (conf.init(args[0])) {

            String SCHEMA_FILE = conf.get("SCHEMA_FILE");
            String SRC_FOLDER = conf.get("SRC_FOLDER");
            String OWL_FILE = conf.get("OWL_FILE");
            String OWL_FILE_TTL = conf.get("OWL_FILE_TTL");
            String OWL_IRI = conf.get("OWL_IRI");

            MyOWLManager manager = new MyOWLManager();    

            OWLOntology bO = manager.loadFromFile(SCHEMA_FILE);

            if (bO != null){

               FManager fmanager = new FManager();
               File[] files = fmanager.getFileDescriptors(SRC_FOLDER,OWLEXT);
               if (files != null){
                  for (int i = 0; i < files.length; i++) {
                     log("found "+files[i].getAbsolutePath());
                     OWLOntology cO = manager.loadFromFile(files[i]);
                  }
                  OWLOntology res = manager.merge(OWL_IRI);

                  if (res != null){
                     manager.saveToFile(res,OWL_FILE);
                     manager.reason(res);
                     manager.saveToFileTTL(res,OWL_FILE_TTL);
                  }

               } else { log("invalid folder "+SRC_FOLDER);}


             } else { log("could not find the schema file"); }
 

         } else { log("could not find the configuration file!"); }

      } else { log("give the configuration file!"); }

      log("...done");
   }


   public static void log(String msg){
      String logMsg = "["+NAME+"]: "+msg;
      if (DEBUG) {
         System.out.println(logMsg);
      }
   }


}
