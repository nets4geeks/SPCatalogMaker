package ab.ext;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.reasoner.structural.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.model.providers.*;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.OWLOntologyXMLNamespaceManager;
import java.util.stream.*;
import java.io.*;
import java.util.Iterator;

import java.util.logging.*;

import ab.base.*;

public class OntologyMaker{

   private static final Logger LOGGER = Logger.getLogger(LManager.class.getName());

   public static String ORGANIZATION_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#OrganizationProperty";
   public static String COMMON_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#CommonProperty";
   public static String CONTEXT_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#ContextProperty";
   public static String SECURITY_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#SecurityProperty";
   
   public static String PATTERN_CLASS = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#Pattern";

   public boolean processSection(IRI patternIRI, JsonNode section, O wmodel, O pmodel){	 
      //	  "ns:hasTemplate": [
      //      "ns:template_POSA"
      //    ],
	  Iterator<String> iterator = section.fieldNames();
      while (iterator.hasNext()) {
		 // like  ns:hasTemplate  
		 String propertyName = iterator.next();
		 IRI propertyIRI = wmodel.getIRIForm(propertyName);
		 ArrayNode items = (ArrayNode) section.get(propertyName);
         for(int i = 0; i < items.size(); i++) {
		    // like ns:template_POSA	 
            String itemName = items.get(i).textValue();
            IRI itemIRI = wmodel.getIRIForm(itemName);
            // if no prefix use the default prefix of pmodel
            if (itemIRI == null) itemIRI = IRI.create(pmodel.getDefaultPrefix()+itemName);
            OWLAxiom ax = pmodel.getObjectPropertyAssertionAxiom(propertyIRI, patternIRI, itemIRI);
            pmodel.addAxiom(ax);
         }
         
      }
	  return true;
   }


   public boolean process(String folder, O wmodel, O pmodel){
	   
	   // get list of json files
	   File f = new File(folder);
       String[] pathnames = f.list();
       for (String pathname : pathnames) {
		  if ( (pathname.endsWith(".json")) && (!pathname.endsWith(".schema.json")) ){ 
			 JacksonParser p = new JacksonParser();
			 if (p.init(folder+pathname,true)){
				 // get document
				 JsonNode document = p.getDocument();
				 // get pattern's id (name)
				 String id = document.path("id").textValue();
				 if (id != null){
					 // no namespace for pattern
					 IRI patternIRI = IRI.create(pmodel.getDefaultPrefix()+id);
					 OWLAxiom ax = pmodel.getClassAssertionAxiom(IRI.create(PATTERN_CLASS), patternIRI);
					 pmodel.addAxiom(ax);
					 
					 JsonNode organization = document.path("organization");
					 processSection(patternIRI,organization,wmodel,pmodel);
					 
					 JsonNode common = document.path("common");
					 processSection(patternIRI,common,wmodel,pmodel);
					 
					 JsonNode context = document.path("context");
					 processSection(patternIRI,context,wmodel,pmodel);
					 
					 JsonNode security = document.path("security");
					 processSection(patternIRI,security,wmodel,pmodel);
					 
					 
				 }
				 
			     //System.out.println(id + " ::: " +pathname);	 
			     
			     
			     
		     } else {
				 LOGGER.severe ("failed to init "+pathname);
				 return false; 
			 } 
             
          }
       }
        
	   return true;
   }

}
