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

public class SchemaMaker extends JacksonParser{

   private static final Logger LOGGER = Logger.getLogger(LManager.class.getName());

   public static String ORGANIZATION_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#OrganizationProperty";
   public static String COMMON_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#CommonProperty";
   public static String CONTEXT_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#ContextProperty";
   public static String SECURITY_PROPERTY = "http://www.grsu.by/net/SecurityPatternCatalogNaiveSchema#SecurityProperty";

   protected void addMetadata(O metamodel, ObjectNode metadataProperties, boolean shortMeta){
       Stream dataProperties = metamodel.getDataPropertyDeclarationAxioms(true); 
       for (Iterator<OWLAxiom> iterator = dataProperties.iterator(); iterator.hasNext(); ){
           OWLAxiom axiom = (OWLAxiom)iterator.next();
           OWLEntity ent = metamodel.getEntityFromDeclaration(axiom);
           String name = metamodel.getShortLikeForm(ent);
           String title = metamodel.getSeacherLabel(ent);
           String description = metamodel.getSeacherComment(ent);
           // use only "*Review*", "textName",and "textURL" fields
           if (shortMeta){
			  if ( (!name.contains("Review")) & (!name.contains("textName")) & (!name.contains("textURL")) & (!name.contains("textReference"))) name = null;
		   }
           if (name !=null){ 
			  // create a property like ObjectNode and use "put" to apply there pairs "key : value" 
			  ObjectNode property = JsonNodeFactory.instance.objectNode();
			  property.put("type","string");
			  if (title !=null) property.put("title",title);
			  if (description !=null) property.put("description",description);
			  // set created property to the properties. 
			  // the last is the ObjectNode connected to the JSON document
			  metadataProperties.set(name,property);
		   }
	   }	   
   }

   protected void addSection(O metamodel, O workmodel,ObjectNode properties, String propertyIRI){
	   
       Stream orgProperties = metamodel.getSearcherSubProperties(propertyIRI);
       
       
       for (Iterator<OWLPropertyExpression> iterator = orgProperties.iterator(); iterator.hasNext(); ){
           OWLEntity ent = (OWLEntity)iterator.next();
           String name = metamodel.getShortLikeForm(ent);
           String title = metamodel.getSeacherLabel(ent);
           String description = metamodel.getSeacherComment(ent);
           if (name !=null){ 
			  ObjectNode property = JsonNodeFactory.instance.objectNode();
			  
			  if (title !=null) property.put("title",title);
			  if (description !=null) property.put("description",description);
			  
			  ArrayNode enumeration = JsonNodeFactory.instance.arrayNode(); 
			  boolean hasItems = false;
			  Stream ranges = metamodel.getSearcherObjectPropertyRanges((OWLObjectProperty)ent);
			  for (Iterator<OWLClass> iterator1 = ranges.iterator(); iterator1.hasNext();){
			     OWLClass cls = (OWLClass)iterator1.next();	  
			     //System.out.println(cls.toString());
			     Stream instances = workmodel.getReasonerInstances(cls);
			     for (Iterator<OWLNamedIndividual> iterator2 = instances.iterator(); iterator2.hasNext();){
				    OWLNamedIndividual ind = (OWLNamedIndividual)iterator2.next();
				    hasItems = true;
				    enumeration.add(workmodel.getShortLikeForm(ind));
				    //System.out.println("xxxx " +ind.toString());	 
				 }
			  }
			  property.put("type","array");
			  property.put("uniqueItems", true);
			  ObjectNode array = property.putObject("items"); 
			  array.put("type", "string");
			  if (hasItems) {
				  array.put("enum",enumeration);
			  }
		
			  properties.set(name,property);
		   }           
       }	   
   }


   public boolean process(O metamodel, O workmodel, boolean shortMeta){
	   init();
	   
	   // put some root items to the document
	   put(document,"$schema","http://json-schema.org/draft-04/schema#");
	   put(document,"title","JSON schema");
 
       //apply an empty ObjectNode (properties) to the document (give only a name)
       ObjectNode properties = ((ObjectNode)document).putObject("properties");
       
       //apply pattern's name (id) property
       ObjectNode nm = properties.putObject("id");
       nm.put("title","ID");
       nm.put("description","Pattern's ID (compatible with OWL)");
       nm.put("type","string");
       
       // apply metadata section
       ObjectNode metadata = properties.putObject("metadata");
       metadata.put("title","Metadata section");
       metadata.put("type","object");
       // apply metadata properties
       ObjectNode metadataProperties = metadata.putObject("properties"); 
       addMetadata(metamodel,metadataProperties,shortMeta);
          
       // apply organization section
       ObjectNode organization = properties.putObject("organization");
       organization.put("title","Organization and scope section");
       organization.put("type","object");
       ObjectNode organizationProperties = organization.putObject("properties"); 
       addSection(metamodel, workmodel, organizationProperties, ORGANIZATION_PROPERTY);
          
       // apply common section
       ObjectNode common = properties.putObject("common");
       common.put("title","Common section");
       common.put("type","object");
       ObjectNode commonProperties = common.putObject("properties"); 
       addSection(metamodel, workmodel, commonProperties, COMMON_PROPERTY);   
          
       // apply context section
       ObjectNode context = properties.putObject("context");
       context.put("title","Context section");
       context.put("type","object");
       ObjectNode contextProperties = context.putObject("properties"); 
       addSection(metamodel, workmodel, contextProperties, CONTEXT_PROPERTY);   

       // apply security section
       ObjectNode security = properties.putObject("security");
       security.put("title","Security section");
       security.put("type","object");
       ObjectNode securityProperties = security.putObject("properties"); 
       addSection(metamodel, workmodel, securityProperties, SECURITY_PROPERTY);   

          
	   return true;
   }

}
