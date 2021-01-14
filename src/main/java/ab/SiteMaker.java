package ab.applications;

import ab.base.*;
import ab.ext.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import java.io.*;
import java.util.logging.*;


class SiteMaker {

   private final static Logger LOGGER = Logger.getLogger(LManager.class.getName());


   public static void main(String args[]){

      LManager.init();
      LOGGER.info("starting ...");

      if (args.length != 0) {

         PManager conf = new PManager();

         if (conf.init(args[0])) {

            String SRC_FOLDER = conf.get("SRC_FOLDER");
            String DST_FOLDER = conf.get("DST_FOLDER");
            String DST_FILE = conf.get("DST_FILE");
  
            PlainTextMaker maker = new PlainTextMaker(SRC_FOLDER,DST_FOLDER,DST_FILE);
            if (maker.process()){
				
               LOGGER.info("...done");		
			} else { LOGGER.severe("failed to process"); }

         } else { LOGGER.severe("could not find the configuration file!"); }

      } else { LOGGER.severe("give the configuration file!"); }

   }




}
