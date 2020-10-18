
package ab.applications;

import ab.base.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import java.io.*;
import java.util.logging.*;


class SPCatalogMaker {
   public static String OWLEXT = ".owl";

   private final static Logger LOGGER = Logger.getLogger(LManager.class.getName());


   public static void main(String args[]){

      LManager.init();
      LOGGER.info("starting ...");

 
      if (args.length != 0) {

         PManager conf = new PManager();

         if (conf.init(args[0])) {

            String SCHEMA_FILE = conf.get("SCHEMA_FILE");
            String SRC_FOLDER = conf.get("SRC_FOLDER");
            String lst[] = conf.getAsArray("SRC_FILES");
            String OWL_FILE = conf.get("OWL_FILE");
            String OWL_FILE_TTL = conf.get("OWL_FILE_TTL");
            String OWL_IRI = conf.get("OWL_IRI");

            OManager manager = new OManager();    

            OWLOntology bO = manager.loadFromFile(SCHEMA_FILE);

            if (bO != null){

               OWLOntology m = null;
               for (int i=0;i<lst.length;i++){
                  m = manager.loadFromFile(SRC_FOLDER+lst[i]);
               }

               if (m != null){
                  O model = O.create(m);
                  model.flush();
                  if (model.isReasonerConsistent()){
                     model.fill();
                     manager.saveToFileTTL(model.get(),OWL_FILE_TTL);
                  } else { LOGGER.severe("failed: inconsistent ontology");}
               } else { LOGGER.severe("null ontology");}

             } else { LOGGER.severe("could not find the schema file"); }
 

         } else { LOGGER.severe("could not find the configuration file!"); }

      } else { LOGGER.severe("give the configuration file!"); }

      LOGGER.info("...done");
   }




}
