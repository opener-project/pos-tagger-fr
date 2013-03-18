import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.kyotoproject.kaf.*;

public class kaf {
	
	private static String models_dir;
	private static String pos_model;
	private static String multiword_model;
	private static Map<String,String> lemmatizationMap;
	private static String[] words;
	private static String[] wids;
	private static HashMap<String,ArrayList<String>> hashterm; //compound terms and its components
	private static ArrayList<String> guessArray; //word list with compound terms separated
	private static String[] kafTags;
    private static String[] kafTags_compound;
    private static String[] multiword_tags;
		
    static public void main (String[] args) {
	
    	if (args.length < 1) {
    		System.out.println("\nAplication needs 1 parameters:");
            System.out.println("1. PoS and multiword model's directory path.");
            System.out.println("You can also specify a 2nd parameter to use static timestamp at KAF header: -n.\n");
    	}
    	else {
    		models_dir = args[0];
    		InputStream file = System.in;
    		String outfile = file+".total.kaf";
		
    		pos_model = models_dir + "french-pos-treetagger.bin";
    		multiword_model = models_dir + "fr-multiword-maxent.bin";
    		
    		readSerializedObject();
    		
    		Boolean timestamp = true;
	        if (args.length > 1)
	        	timestamp = false;
	        
    		KafSaxParser parser = new KafSaxParser();

    		parser.parseFile(file);
    		
    		if (timestamp) {
    			parser.addLP("terms", "opennlp-pos-treetagger-fr", "1.0");
    			parser.addLP("terms", "opennlp-multiword-fr", "1.0");
    		}
    		else {
    			parser.addLP_notimestamp("terms", "opennlp-pos-treetagger-fr", "1.0");
    			parser.addLP_notimestamp("terms", "opennlp-multiword-fr", "1.0");
    		}
    		
        
    		//get words
    		getWordsWids(parser);
        
    		//compounds
    		getCompounds();
        
    		//tags
    		getKafTags();
		
    		//multiwords
    		getMultiwordTags();
		
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
	        for (int i = 0; i < words.length; i++) {
	        	kt = new KafTerm();
	        	kt.setTid("t"+(tid));
	        	
	        	//word is a multiword part
	        	if (multiword_tags[i].compareTo("C") == 0){
	        		mw = 1;
	        		multiword_lemma = "";
	        		kt.setPos(kafTags[i]);
	        		kt.setType(getType(kt.getPos()));
	        		kt.setHead("t"+(tid)+".1");
	        		head_word = "";
	        		termPoSTags = new ArrayList<String>();
	        		
	        		//for all multiword components do
	        		while (i < multiword_tags.length && multiword_tags[i].compareTo("C") == 0){
	        			termPoSTags.add(kafTags[i]+"#"+"t"+(tid)+"."+mw);
	        			lemma = getLemma(words[i], kafTags[i]);
	        			
	        			kt.addSpans(wids[i]);
	        			//if multiword component is a compound
	        			if (hashterm.get(words[i]+wids[i]).isEmpty() == false) {
	        				compound_lemma = "";
	        				for (int z = 0; z < hashterm.get(words[i]+wids[i]).size(); z++){
	        					//find compound's head
	        					if (kafTags[i].compareTo(kafTags_compound[c]) == 0 && 
	                					hashterm.get(words[i]+wids[i]).get(z).length() > head_word.length()) {
	        						termPoSTags.remove(termPoSTags.size()-1);
	                				termPoSTags.add(kafTags[i]+"#"+"t"+(tid)+"."+(z+1));
	                				head_word = hashterm.get(words[i]+wids[i]).get(z);
	                			}
	        					tc = new TermComponent();
	        					tc.setId("t"+(tid)+"."+(mw));
	        					tc.setPos(kafTags_compound[c]);
	        					if (hashterm.get(words[i]+wids[i]).get(z).matches("[A-Z].*") )
	        						tc.setLemma(hashterm.get(words[i]+wids[i]).get(z));
	        					else
	        						tc.setLemma(getLemma(hashterm.get(words[i]+wids[i]).get(z), kafTags_compound[c]));
	        					compound_lemma += tc.getLemma();
	        					kt.addComponents(tc);
	        					c++;
	        					mw++;
	        				}
	        				multiword_lemma += compound_lemma+"_";
	        			}
	        			//word is not a compound
	        			else {
	        				tc = new TermComponent();
	        				tc.setId("t"+(tid)+"."+(mw));
	        				tc.setPos(kafTags[i]);
	        				if (words[i].matches("[A-Z].*") ) {
	        					tc.setLemma(words[i]);
	        					multiword_lemma += words[i]+"_";
	        				} else {
	        					tc.setLemma(lemma);
	        					multiword_lemma += lemma+"_";
	        				}
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
	        			setHeadPoS(kt, itr);
	        			kt.setType(getType(kt.getPos()));
	        		}
	        		kt.setLemma(multiword_lemma.substring(0, multiword_lemma.length()-1));
	        		i--;
	        	}
	        	//word is not multiword component
	        	else {
	        		kt.setPos(kafTags[i]);
	        		kt.setType(getType(kt.getPos()));
	            	kt.setLemma(getLemma(words[i], kafTags[i]));
	            	kt.addSpans(wids[i]);
	            	//word is a compound
	            	if (hashterm.get(words[i]+wids[i]).isEmpty() == false) {
	            		compound_lemma = "";
	                	kt.setHead("t"+(tid)+".1");
	                	head_word = "";
	            		for (int z = 0; z < hashterm.get(words[i]+wids[i]).size(); z++){
	            			tc = new TermComponent();
	            			//find compound's head
	            			if (kafTags[i].compareTo(kafTags_compound[c]) == 0 && 
	            					hashterm.get(words[i]+wids[i]).get(z).length() > head_word.length()) {
	            				kt.setHead("t"+(tid)+"."+(z+1));
	            				head_word = hashterm.get(words[i]+wids[i]).get(z);
	            			}
	            			tc.setId("t"+(tid)+"."+(z+1));
	            			tc.setPos(kafTags_compound[c]);
	            			if (hashterm.get(words[i]+wids[i]).get(z).matches("[A-Z].*") )
	            				tc.setLemma(hashterm.get(words[i]+wids[i]).get(z));
        					else
        						tc.setLemma(getLemma(hashterm.get(words[i]+wids[i]).get(z), kafTags_compound[c]));
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
	        parser.writeKafToStream(System.out);
	        System.out.close();
    	}
    }
    
    private static String getLemma(String surfaceForm,String postag){
    	
		String lemmasAndTags=lemmatizationMap.get(surfaceForm.toLowerCase());
		if(lemmasAndTags==null){
			return surfaceForm;
		}
		String[]splittedLemmasAndTags=lemmasAndTags.split(" ");
		for(int i=0;i<splittedLemmasAndTags.length-1;i+=2){
			String currentLemma=splittedLemmasAndTags[i];
			String currentTag=TagsetMappings.convertFromLemmatagsToKaf(splittedLemmasAndTags[i+1]);
			
		//	System.out.println("Comparing "+currentTag+" and "+postag);
			
			if(currentTag.equalsIgnoreCase(postag)){
				return currentLemma;
			}
		}
		return surfaceForm;
	}
    
    private static void getWordsWids(KafSaxParser parser) {
		String word;
        String wid;
        words = new String[parser.getKafWordFormList().size()];
        wids = new String[parser.getKafWordFormList().size()];
        for (int i = 0; i < parser.getKafWordFormList().size(); i++) {
        	word = parser.getKafWordFormList().get(i).getWf();
        	wid = parser.getKafWordFormList().get(i).getWid();
        	words[i] = word;
        	wids[i] = wid;
        }
	}
    
    private static void getCompounds() {
    	String word;
    	hashterm = new HashMap<String,ArrayList<String>>();
    	guessArray = new ArrayList<String>();
    	boolean b = false;
    	for (int i = 0; i < words.length; i++){
    		b = false;
    		word = words[i];
    		hashterm.put(word+wids[i], new ArrayList<String>());
    		if (word.indexOf("'") > 0) {
    			//d', l'...
    			if (word.indexOf("'") == word.length()-1){
    				guessArray.add(word);
    			}
    		}else if (word.indexOf("-") < 0 || word.compareTo("-") == 0) {
    			guessArray.add(word);
    		}
    		if (word.compareTo("-") != 0)
    			while (word.indexOf("-") > -1 || (word.indexOf("'") > -1 && word.indexOf("'") < word.length()-1)) {
    				b = true;
    				//Grande-Bretagne -> Grande - Bretagne
    				if (word.indexOf("'") < 0 || (word.indexOf("-") > -1 && word.indexOf("-") < word.indexOf("'"))) {
    					hashterm.get(words[i]+wids[i]).add(word.substring(0, word.indexOf("-")));
    					guessArray.add(word.substring(0, word.indexOf("-")));
    					hashterm.get(words[i]+wids[i]).add("-");
    					guessArray.add("-");
    					//word = Grande-Bretagne => word = Bretagne
    					word = word.substring(word.indexOf("-")+1, word.length());
    				}
    				//lorqu'elle -> lorqu' elle
    				else {
    					hashterm.get(words[i]+wids[i]).add(word.substring(0, word.indexOf("'")+1));
    					guessArray.add(word.substring(0, word.indexOf("'")+1));
    					//word = lorqu'elle => word = elle
    					word = word.substring(word.indexOf("'")+1, word.length());
    				}
    			}
    		    //add last word's last component
				if (b && word.compareTo("") != 0) {
					hashterm.get(words[i]+wids[i]).add(word);
					guessArray.add(word);
				}
    	}
    }
    
    private static void getKafTags() {
    	String[] guess = new String[guessArray.size()];
    	guessArray.toArray(guess);
    
    	PoS_Tagger tagger = new PoS_Tagger(pos_model);     
    	String[] tags = tagger.tag(words);
    	String[] tags_compound = tagger.tag(guess);
    	kafTags = new String[tags.length];
    	kafTags_compound = new String[tags_compound.length];
    	for (int i=0;i<kafTags_compound.length;i++) {
    		if (i < kafTags.length)
    			kafTags[i]=TagsetMappings.convertFromFtbToKaf(tags[i]);
    		kafTags_compound[i]=TagsetMappings.convertFromFtbToKaf(tags_compound[i]);
    	}
    }
    
    private static void getMultiwordTags() {
    	PoS_Tagger multiword_tagger = new PoS_Tagger(multiword_model);
    	multiword_tags = multiword_tagger.tag(words);
    }
    
    //itr format <PoS#word_id>
    private static void setHeadPoS(KafTerm kt, Iterator<String> itr){
    	boolean found = false;
		String itr_string;
		String itr_id;
		String itr_PoS;
		while (itr.hasNext() && !found) {
			itr_string = (String)itr.next();
			itr_PoS = itr_string.substring(0, 1);
			itr_id = itr_string.substring(2, itr_string.length());
			if (itr_PoS.compareTo("N") == 0){
				found = true;
				kt.setHead(itr_id);
				kt.setPos("N");
			}
			else if (itr_PoS.compareTo("V") == 0) {
				if (kt.getPos().compareTo("V") != 0) {
					kt.setHead(itr_id);
					kt.setPos("V");
				}
			}
			else if (itr_PoS.compareTo("G") == 0 && kt.getPos().compareTo("V") != 0) {
				if (kt.getPos().compareTo("G") != 0) {
					kt.setHead(itr_id);
					kt.setPos("G");
				}
			}
			else if (itr_PoS.compareTo("A") == 0 && kt.getPos().compareTo("V") != 0 && 
					kt.getPos().compareTo("G") != 0) {
				if (kt.getPos().compareTo("A") != 0) {
					kt.setHead(itr_id);
					kt.setPos("A");
				}
			}
			else if (itr_PoS.compareTo("D") == 0 && kt.getPos().compareTo("V") != 0 && 
					kt.getPos().compareTo("G") != 0 && kt.getPos().compareTo("A") != 0) {
				if (kt.getPos().compareTo("D") != 0) {
					kt.setHead(itr_id);
					kt.setPos("D");
				}
			}
		}
    }
    
    private static String getType(String pos) {
		if (pos.equalsIgnoreCase("N") ||
				pos.equalsIgnoreCase("V") ||
				pos.equalsIgnoreCase("A") ||
				pos.equalsIgnoreCase("G")) {
			return "open";
		}
		else {
			return "close";
		}
	}
	
	private static void readSerializedObject() {
		try {
			InputStream file;
			if (System.getProperty("java.version").substring(0, 3).compareTo("1.7") == 0)
				file = new FileInputStream(models_dir + "FrenchLemmatizationMap.ser");
			else
				file = Class.class.getResourceAsStream(models_dir + "FrenchLemmatizationMap.ser");
			
			InputStream buffer = new BufferedInputStream( file );
		    ObjectInput input = new ObjectInputStream ( buffer );
	
		    lemmatizationMap = (Map<String,String>)input.readObject();
		    input.close();
		    
		    // System.out.println("Lemmatization map loaded, size: "+lemmatizationMap.size());
		} catch (ClassNotFoundException ex){
			ex.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
