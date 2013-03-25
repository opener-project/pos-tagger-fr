import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.kyotoproject.kaf.*;

public class kaf {
	
	private static String models_dir;
	private static KafTextLayer ktl;
		
    static public void main (String[] args) {
	
    	if (args.length < 1) {
    		System.out.println("\nAplication needs 1 parameters:");
            System.out.println("1. PoS and multiword model's directory path.");
            System.out.println("You can also specify a 2nd parameter to use static timestamp at KAF header: -n.\n");
    	}
    	else {
    		models_dir = args[0];
    		
    		LemmatizationMap lm = new LemmatizationMap(models_dir);
    		lm.readSerializedObject();
    		
    		Boolean timestamp = true;
	        if (args.length > 1)
	        	timestamp = false;
	        
    		KafSaxParser parser = new KafSaxParser();

    		parser.parseFile(System.in);
    		//parser.parseFile("/home/VICOMTECH/aazpeitia/workspace/kaf/src/french.kaf");
    		
    		if (timestamp) {
    			parser.addLP("terms", "opennlp-pos-treetagger-fr", "1.0");
    			parser.addLP("terms", "opennlp-multiword-fr", "1.0");
    		}
    		else {
    			parser.addLP_notimestamp("terms", "opennlp-pos-treetagger-fr", "1.0");
    			parser.addLP_notimestamp("terms", "opennlp-multiword-fr", "1.0");
    		}
    		
    		String pos_model = models_dir + "french-pos-treetagger.bin";
    		String multiword_model = models_dir + "fr-multiword-maxent.bin";
    		
    		ktl = new KafTextLayer(parser, pos_model, multiword_model);
    		
    		//get words
    		ktl.getKafWordsWids();
        
    		//compounds
    		ktl.getKafCompounds();
        
    		//tags
    		ktl.getKafPosTags();
		
    		//multiwords
    		ktl.getKafMultiwordTags();
		
    		//Kaf construction
    		KafTerm kt;
    		TermComponent tc;
    		ArrayList<String> termPoSTags = new ArrayList<String>(); //<PoS#word_id>
	        int c = 0;
	        String head_word;
	        String multiword_lemma;
	        String compound_lemma;
	        String lemma;
	        int mw = 1;
	        int tid = 1;
	        Iterator<String> itr;
	        
	        for (int i = 0; i < ktl.getKafWordsLength(); i++) {
	        	kt = new KafTerm();
	        	kt.setTid("t"+(tid));
	        	
	        	//word is a multiword part
	        	if (ktl.isMW(i)){
	        		mw = 1;
	        		multiword_lemma = "";
	        		kt.setPos(ktl.getKafPoS(i));
	        		kt.setType(ktl.getKafType(kt.getPos()));
	        		kt.setHead("t"+(tid)+".1");
	        		head_word = "";
	        		termPoSTags = new ArrayList<String>();
	        		
	        		//for all multiword components do
	        		while (i < ktl.getKafMWLength() && ktl.isMW(i)){
	        			termPoSTags.add(ktl.getKafPoS(i)+"#"+"t"+(tid)+"."+mw);
	        			lemma = lm.getLemma(ktl.getKafWord(i), ktl.getKafPoS(i));
	        			
	        			kt.addSpans(ktl.getKafWid(i));
	        			//if multiword component is a compound
	        			if (ktl.isCompound(i)) {
	        				compound_lemma = "";
	        				for (int z = 0; z < ktl.getKafCompoundSize(i); z++){
	        					//find compound's head
	        					if (ktl.getKafPoS(i).compareTo(ktl.getKafCompoundPoS(c)) == 0 && 
	                					ktl.getKafCompundComponent(i, z).length() > head_word.length()) {
	        						termPoSTags.remove(termPoSTags.size()-1);
	                				termPoSTags.add(ktl.getKafPoS(i)+"#"+"t"+(tid)+"."+(z+1));
	                				head_word = ktl.getKafCompundComponent(i, z);
	                			}
	        					//build TermComponent object
	        					if (ktl.getKafCompundComponent(i, z).matches("[A-Z].*") )
		            				tc = ktl.builKafTermComponent("t"+(tid)+"."+(mw), ktl.getKafCompoundPoS(c), ktl.getKafCompundComponent(i, z));
	        					else
	        						tc = ktl.builKafTermComponent("t"+(tid)+"."+(mw), ktl.getKafCompoundPoS(c), lm.getLemma(ktl.getKafCompundComponent(i, z), ktl.getKafCompoundPoS(c)));
	        					compound_lemma += tc.getLemma();
	        					kt.addComponents(tc);
	        					c++;
	        					mw++;
	        				}
	        				multiword_lemma += compound_lemma+"_";
	        			}
	        			//word is not a compound
	        			else {
	        				
	        				if (ktl.getKafWord(i).matches("[A-Z].*") )
	            				tc = ktl.builKafTermComponent("t"+(tid)+"."+(mw), ktl.getKafPoS(i), ktl.getKafWord(i));
        					else
        						tc = ktl.builKafTermComponent("t"+(tid)+"."+(mw), ktl.getKafPoS(i), lemma);
	        				multiword_lemma += tc.getLemma()+"_";
	        				kt.addComponents(tc);
	        				mw++;
	        				c++;
	        			}
	        			i++;
	        		}
	        		//word was not multiword fix
	        		if (mw == 2) {
	        			kt.setComponents(new ArrayList<TermComponent>());
	        			kt.setHead("");
	        		}
	        		//set term's head and PoS. The candidates are at the termPoSTags array
	        		else {
	        			itr = termPoSTags.iterator();
	        			ktl.setTermHeadPoS(kt, itr);
	        			kt.setType(ktl.getKafType(kt.getPos()));
	        		}
	        		kt.setLemma(multiword_lemma.substring(0, multiword_lemma.length()-1));
	        		i--;
	        	}
	        	//word is not multiword component
	        	else {
	        		kt.setPos(ktl.getKafPoS(i));
	        		kt.setType(ktl.getKafType(kt.getPos()));
	            	kt.setLemma(lm.getLemma(ktl.getKafWord(i), ktl.getKafPoS(i)));
	            	kt.addSpans(ktl.getKafWid(i));
	            	//word is a compound
	            	if (ktl.isCompound(i)) {
	            		compound_lemma = "";
	                	kt.setHead("t"+(tid)+".1");
	                	head_word = "";
	            		for (int z = 0; z < ktl.getKafCompoundSize(i); z++){
	            			//find compound's head
	            			if (ktl.getKafPoS(i).compareTo(ktl.getKafCompoundPoS(c)) == 0 && 
	            					ktl.getKafCompundComponent(i, z).length() > head_word.length()) {
	            				kt.setHead("t"+(tid)+"."+(z+1));
	            				head_word = ktl.getKafCompundComponent(i, z);
	            			}
	            			//build TermComponent object
	            			if (ktl.getKafCompundComponent(i, z).matches("[A-Z].*") )
	            				tc = ktl.builKafTermComponent("t"+(tid)+"."+(z+1), ktl.getKafCompoundPoS(c), ktl.getKafCompundComponent(i, z));
        					else
        						tc = ktl.builKafTermComponent("t"+(tid)+"."+(z+1), ktl.getKafCompoundPoS(c), lm.getLemma(ktl.getKafCompundComponent(i, z), ktl.getKafCompoundPoS(c)));
	            			compound_lemma += tc.getLemma();
	            			kt.addComponents(tc);
	            			c++;
	            		}
	            		kt.setLemma(compound_lemma);
	            	}
	            	else {
	            		c++;
	            	}
	        	}
	        	tid++;
	        	parser.kafTermList.add(kt);
	        }
	        
	        //try {
	        parser.writeKafToStream(System.out);
	        	//FileOutputStream fos = new FileOutputStream("/home/VICOMTECH/aazpeitia/workspace/kaf/src/french.total2.kaf");
	        	//parser.writeKafToFile(fos);
	        	//fos.close();
	        System.out.close();
	        //} catch (IOException e) {
	        //	e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	        //}
    	}
    }
	
}
