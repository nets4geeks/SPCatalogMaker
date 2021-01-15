package ab.ext;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.stream.*;
import java.io.*;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.logging.*;
import java.io.FileWriter;

import ab.base.*;

public class PlainTextMaker{

   private static final Logger LOGGER = Logger.getLogger(LManager.class.getName());

   protected String inFolder;
   protected String outFolder;
   protected String outFile;

   public PlainTextMaker(String _inFolder, String _outFolder, String _outFile){
	  inFolder = _inFolder;
	  outFolder = _outFolder;
	  outFile = _outFile; 
   }

   public boolean saveFile(ArrayList<String> data,String name){
      try (
        FileWriter fw = new FileWriter(name)) {
		for (int i=0;i<data.size();i++) fw.write(data.get(i)+'\n');
        fw.close();
      } catch (Exception e) {
        e.printStackTrace();		  
		return false;
      }
	  return true;
   }

   public String get(JsonNode node, String name){
	  String res = node.path(name).textValue();
	  if (res !=null) return res;
	  return "";
   }

   public String getList(JsonNode node, String name){
	  StringBuffer bf = new StringBuffer();
	  bf.append(""); 
      ArrayNode items = (ArrayNode) node.get(name);
      for(int i = 0; i < items.size(); i++) {
         String itemName = items.get(i).textValue();
         if (i!=0) bf.append("; ");
         bf.append(itemName);
      }
	   
	  return bf.toString();
   }


   public String breaks(String in){
	   return in.replace(";",";<br />");
   }

   public boolean process(){

       ArrayList<String> catalog = new ArrayList<String>();
       catalog.add("/ [Home](/acctp/) /");
       catalog.add("");
       catalog.add("## Threat Patterns");
       catalog.add("AXXX - Architecture profile, BXXX - Compliance profile, CXXX - Privacy profile");
       catalog.add("");

	   // get list of json files
	   File f = new File(inFolder);
       String[] pathnames = f.list();
       Arrays.sort(pathnames);
       for (String pathname : pathnames) {
		  if ( (pathname.endsWith(".json")) && (!pathname.endsWith(".schema.json")) ){ 
			 JacksonParser p = new JacksonParser();
			 if (!p.init(inFolder+pathname,true)){
   			    LOGGER.severe ("failed to init "+pathname);
				return false; 
			 }		
				     	 
			 // get JSON document
			 JsonNode document = p.getDocument();
			 // get id (name)
			 String id = get(document,"id");
			 if (id.equals("")){
   			    LOGGER.severe ("no id in "+pathname);
				return false; 				 
			 }

             // create a file to save document
		     ArrayList<String> plain = new ArrayList<String>();
             plain.add("/ [Home](/acctp/) / [Catalog](/acctp/catalog/) /");
             plain.add("");

             JsonNode metadata = document.path("metadata");
             String name = get(metadata,"ns:textName");
             if (name.equals("")){
				LOGGER.severe("empty name in "+ pathname); 
				return false;
			 }
             plain.add("## " + name); 
             plain.add("");
             
             plain.add("|Context|"+get(metadata,"ns:textReviewContext")+"|");
             plain.add("|Problem|"+get(metadata,"ns:textReviewProblem")+"|");
             plain.add("|Solution|"+breaks(get(metadata,"ns:textReviewSolution")+"|"));
             plain.add("|References|"+breaks(get(metadata,"ns:textReference")+"|"));
             
             JsonNode organization = document.path("organization");
             plain.add("|Type|"+breaks(getList(organization,"ns:hasType")+"|"));
             
             JsonNode context = document.path("context");
             plain.add("|Victim|"+breaks(getList(context,"ns:hasAffectedComponent")+"|"));
             
             JsonNode security = document.path("security");
             plain.add("|Aggressor|"+breaks(getList(security,"ns:hasAggressor")+"|"));
             plain.add("|Aggr. role|"+breaks(getList(security,"ns:hasAggressorRole")+"|"));

             plain.add("|STRIDE|"+breaks(getList(security,"ns:hasSTRIDE")+"|"));

             
             

             plain.add("");
             plain.add("/ [Home](/acctp/) / [Catalog](/acctp/catalog/) /");

             String fileName = outFolder+id+".md";
             if (!saveFile(plain,fileName)){
   			    LOGGER.severe ("failed to save "+fileName);
				return false; 				 				 
			 }
             catalog.add("* ["+name+"](/acctp/catalog/"+id+".html)");			 
			 
//             System.out.println(" sss "+fileName);
              
			 
//			 JsonNode metadata = document.path("metadata");
//			 String str = metadata.path("xxx").textValue();
//			 System.out.println("___"+str+"____");
					 
             
          }
       }

       catalog.add("");
       catalog.add("/ [Home](/acctp/) /");
       if (!saveFile(catalog,outFile)){
		  LOGGER.severe ("failed to save "+outFile);
  		  return false; 				 				 
	   }
       
        
	   return true;
   }

}
