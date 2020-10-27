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


class SPCatalogMaker {
   public static String OWLEXT = ".owl";

   private final static Logger LOGGER = Logger.getLogger(LManager.class.getName());


   public static void main(String args[]){

      LManager.init();
      LOGGER.info("starting ...");

 
      if (args.length != 0) {

         PManager conf = new PManager();

         if (conf.init(args[0])) {

            String ACTION = conf.get("ACTION");

            String SCHEMA_FILE = conf.get("SCHEMA_FILE");
            String SRC_FOLDER = conf.get("SRC_FOLDER");
            String lst[] = conf.getAsArray("SRC_FILES");
            String JSONSCHEMA_FILE = conf.get("JSONSCHEMA_FILE");
            String OWL_FILE = conf.get("OWL_FILE");
            String OWL_FILE_TTL = conf.get("OWL_FILE_TTL");
            String OWL_IRI = conf.get("OWL_IRI");
            String SHORT_METADATA = conf.get("SHORT_METADATA");

            if (ACTION==null){ 
              // legacy approach
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
 
		     } else {
				 
				 // get the schema ontology (SCHEMA_FILE)
	             OManager manager = new OManager();    
                 OWLOntology bO = manager.loadFromFile(SCHEMA_FILE);

                 if (bO == null) {
					 LOGGER.severe("failed to init the schema ontology " + SCHEMA_FILE);
					 System.exit(0);
				 }
                 // schema is the main metamodel 
                 O metamodel = O.create(bO);
                                  
                 // get additional ontologies one by one (SRC_FILES)
                 OWLOntology m = null;
                 for (int i=0;i<lst.length;i++){
                     m = manager.loadFromFile(SRC_FOLDER+lst[i]);
                     if (m == null){
  					    LOGGER.severe("failed to init an ontology " + lst[i]);
					    System.exit(0);
					 }
                 }
                                  
                 // the last one is work model                 
                 O workmodel = O.create(m);
                 // flushing work model
                 workmodel.flush();
				 if (!workmodel.isReasonerConsistent()){
				    LOGGER.severe("failed because work model is inconsistent");
				    System.exit(0);					 
				 }
				 				 
				 // generate schema from basic ontologies
				 LOGGER.info("going to start schema generation...");
	             SchemaMaker schemamaker = new SchemaMaker();
	             if ( (SHORT_METADATA!=null) && (SHORT_METADATA.equals("true")) ) schemamaker.process(metamodel, workmodel, true);
	             else schemamaker.process(metamodel, workmodel, false);
	             schemamaker.saveToFile(SRC_FOLDER+JSONSCHEMA_FILE); 
				 LOGGER.info("done schema generation...");
				 	    
				 // generate ontologies from the json files
				 LOGGER.info("going to start ontology generation...");				 
				 // create an empty ontology
				 LOGGER.info("... creating ontology of patterns");				 
				 if (OWL_IRI == null) {
				    LOGGER.severe("failed because OWL_IRI is empty");
				    System.exit(0);					 	 
				 }
				 OWLOntology m1 = manager.create(OWL_IRI);
				 O patternmodel = O.create(m1);
                 // import work model by pattern model
				 patternmodel.addImportDeclaration(workmodel.getIRI().toString());
				 
				 // create Ontology maker
				 OntologyMaker ontologymaker = new OntologyMaker();
				 LOGGER.info("... processing JSON files");		
				 ontologymaker.process(SRC_FOLDER,workmodel,patternmodel);
				 
				 // save ontology to a file
				 if (OWL_FILE == null) { 
				    LOGGER.severe("failed because OWL_FILE is empty");
				    System.exit(0);					 	 				    
			     } 
			     LOGGER.info("... saving the ontology to "+OWL_FILE);				 
				 manager.saveToFile(m1,SRC_FOLDER+OWL_FILE);
				   
				 // save RDF to a file
				 if (OWL_FILE_TTL == null) { 
				    LOGGER.severe("failed because OWL_FILE_TTL is empty");
				    System.exit(0);					 	 				    
			     } 
			     LOGGER.info("... flushing the ontology");
			     patternmodel.flush();
			     if (!patternmodel.isReasonerConsistent()){
				    LOGGER.severe("failed because pattern model is inconsistent");
				    System.exit(0);					 
				 }
			     // fill the ontology
			     LOGGER.info("... filling the ontology");
			     patternmodel.fill();
			     LOGGER.info("... saving RDF to "+OWL_FILE_TTL);				 
				 manager.saveToFileTTL(patternmodel.get(),SRC_FOLDER+OWL_FILE_TTL);  
				   
			   }  
				 

           } else { LOGGER.severe("could not find the configuration file!"); }

        } else { LOGGER.severe("give the configuration file!"); }

      LOGGER.info("...done");
   }




}
