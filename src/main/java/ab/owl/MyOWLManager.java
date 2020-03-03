
package ab.owl;

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
import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.time.Duration;
import java.time.Instant;


// a simple processor for anything you want
public class MyOWLManager{

   public boolean DEBUG;

   protected OWLOntologyManager man;
   //protected OWLDataFactory df;

   private String myName;

   public MyOWLManager(){
      myName = getClass().getName();
      DEBUG = true;
      log("initialized me");
      man = OWLManager.createOWLOntologyManager();
   }


   public OWLOntology loadFromFile (String fileName){
      File file;

      try {
         file = new File(fileName);
      } catch (Exception e){
         e.printStackTrace();
         return null;
      }
      return loadFromFile(file);
   }


   public OWLOntology loadFromFile (File file){
      long startTime = System.nanoTime();
      OWLOntology o;
      log("loadFromFile: source file: " + file.getAbsolutePath());
      try {
         o = man.loadOntologyFromOntologyDocument(file);
      } catch (Exception e){
         e.printStackTrace();
         return null;
      }
      long stopTime = System.nanoTime();
      log("loadFromFile: IRI: "+ getIRI(o).toString());
      log("loadFromFile: worked: "+ getms(startTime,stopTime));

      return o;
   }



   public OWLOntology merge(String iriName){
      long startTime = System.nanoTime();
      OWLOntology merged;
      try {
         log("merge: uniting all the ontologies...");
         OWLOntologyMerger merger = new OWLOntologyMerger(man);
         merged = merger.createMergedOntology(man,IRI.create(iriName));
      } catch (OWLOntologyCreationException e) {
         e.printStackTrace();
         return null;
      }
      long stopTime = System.nanoTime();
      log("merge: worked: "+ getms(startTime,stopTime));
      return merged;
   }


   public void reason(OWLOntology o){
      log("reason: starting ...");
      OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(o);
      String reasonerName = reasoner.getReasonerName()+" "+reasoner.getReasonerVersion().toString();
      log("reason: "+ reasonerName);

      log("reason: before:");
      showStat(o);
      InferredOntologyGenerator generator = new InferredOntologyGenerator(reasoner);
      OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();

      log("reason: filling ...");
      long startTime = System.nanoTime();
      reasoner.flush();
      generator.fillOntology(df, o);
      long stopTime = System.nanoTime();
      log("reason: worked "+ getms(startTime,stopTime));
      log("reason: after:");
      showStat(o);
      log("reason: done");


   }


   public IRI getIRI(OWLOntology _o){
      return _o.getOntologyID().getOntologyIRI().get();
   }


   public void showStat(OWLOntology o){
      log("-----------------------");

      int i1=0;
      Set<AxiomType<?>> s = AxiomType.TBoxAxiomTypes;
      for (AxiomType a : s){
         i1=i1+o.getAxiomCount(a);
      }
      log("showStat: tbox axioms "+ i1 );

      int i2=0;
      s = AxiomType.ABoxAxiomTypes;
      for (AxiomType a : s){
          i2=i2+ o.getAxiomCount(a);
      }
      log("showStat: abox axioms "+ i2 );

      int i3=0;
      s = AxiomType.RBoxAxiomTypes;
      for (AxiomType a : s){
         i3=i3+ o.getAxiomCount(a);
      }
      log("showStat: rbox axioms "+ i3 );

      int i = i1+i2+i3;
      // the differnce is annotations & declarations etc.
      log("showStat: abox+rbox+tbox "+ i);
      log("showStat: total axioms "+ o.getAxiomCount());
      log("-----------------------");

   }


   public boolean saveToFile(OWLOntology o, String filepath){
      log("saveToFile: target file: "+ filepath);
      try {
         File fileout = new File(filepath);
         long startTime = System.nanoTime();
         man.saveOntology(o, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
         long stopTime = System.nanoTime();
         log("saveToFile: struggled: "+ getms(startTime,stopTime));
         //showStat(o);
      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      return true;
   } 


  // save an ontology to a file in the Turtle format
   public boolean saveToFileTTL(OWLOntology o, String filepath){
      log("saveToFileTTL: "+ filepath);
      try {
         File fileout = new File(filepath);
         long startTime = System.nanoTime();
         man.saveOntology(o, new TurtleDocumentFormat(), new FileOutputStream(fileout));
         long stopTime = System.nanoTime();
         log("saveToFileTTL: "+ getms(startTime,stopTime));
         //showStat(o);

      } catch (Exception e){
         e.printStackTrace();
         return false;
      }
      return true;
   } 



   protected String getms(long start, long stop){
      long diff = (stop-start)/1000000;
      return Long.toString(diff) + " ms";
   }


   protected void log(String msg){
     String logMsg = "["+myName+"]: "+msg;
     if (DEBUG) {
        System.out.println(logMsg);
     }
   }




}


