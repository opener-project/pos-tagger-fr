package org.vicomtech.opener.fr.postagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import eu.openerproject.kaf.layers.KafTerm;
import eu.openerproject.kaf.layers.KafTermComponent;
import eu.openerproject.kaf.reader.KafSaxParser;


public class KafTextLayer {
	
	private String[] words; // all words of the text layer.
	private String[] wids; // all wids of the text layer.
	private HashMap<String,ArrayList<String>> hashterm; // hashterm will have all words, and an array with components foreach of them
	private ArrayList<String> guessArray; //word list with compound terms separated
	private String[] kafTags; // pos of the text layer.
	private String[] morphofeat;
	private String[] kafTags_compound;  // pos of the text layer with compound words separated: porte-parole -> porte - parole
    private String[] multiword_tags; // multiword tags
    private String[] multiword_morphofeat;
    private KafSaxParser parser;

    
    public KafTextLayer(KafSaxParser parser) {
    	this.parser = parser;
    }
    
    // get two arrays,
    // words: all words of the text layer.
    // wids: all wids of the text layer.
    public void getKafWordsWids() {
		String word;
        String wid;
        words = new String[parser.getWordList().size()];
        wids = new String[parser.getWordList().size()];
        for (int i = 0; i < parser.getWordList().size(); i++) {
        	word = parser.getWordList().get(i).getWordform();
        	wid = parser.getWordList().get(i).getWid();
        	words[i] = word;
        	wids[i] = wid;
        }
	}
    
    public boolean isCompound(String word) {
    	if (word.indexOf("'") > 0) 
    		//d', l'...
    		if (word.indexOf("'") == word.length()-1)
    			return false;
    	    //d'Ivoire
    		else
    			return true;
    	else
    		//-
    		if (word.indexOf("-") < 0 || word.compareTo("-") == 0)
    			return false;
    	    //rendre-vous
    		else
    			return true;
    }
    
    // hashterm will have all words, and an array with components foreach of them
    public void getKafCompounds() {
    	String word;
    	hashterm = new HashMap<String,ArrayList<String>>();
    	guessArray = new ArrayList<String>();
    	boolean b = false;
    	for (int i = 0; i < getKafWordsLength(); i++){
    		b = false;
    		word = getKafWord(i);
    		hashterm.put(word+getKafWid(i), new ArrayList<String>());
    		if (!isCompound(word))
    			guessArray.add(word);
    		else
    		if (word.compareTo("-") != 0)
    			while (word.indexOf("-") > -1 || (word.indexOf("'") > -1 && word.indexOf("'") < word.length()-1)) {
    				b = true;
    				//Grande-Bretagne -> Grande - Bretagne
    				if (word.indexOf("'") < 0 || (word.indexOf("-") > -1 && word.indexOf("-") < word.indexOf("'"))) {
    					hashterm.get(getKafWord(i)+getKafWid(i)).add(word.substring(0, word.indexOf("-")));
    					guessArray.add(word.substring(0, word.indexOf("-")));
    					hashterm.get(getKafWord(i)+getKafWid(i)).add("-");
    					guessArray.add("-");
    					//word = Grande-Bretagne => word = Bretagne
    					word = word.substring(word.indexOf("-")+1, word.length());
    				}
    				//lorqu'elle -> lorqu' elle
    				else {
    					hashterm.get(getKafWord(i)+getKafWid(i)).add(word.substring(0, word.indexOf("'")+1));
    					guessArray.add(word.substring(0, word.indexOf("'")+1));
    					//word = lorqu'elle => word = elle
    					word = word.substring(word.indexOf("'")+1, word.length());
    				}
    			}
    		    //add last word's last component
				if (b && word.compareTo("") != 0) {
					hashterm.get(getKafWord(i)+getKafWid(i)).add(word);
					guessArray.add(word);
				}
    	}
    }
    
    // get 2 arrays with the pos of the text layer.
    // kafTags stores pos of the text layer.
    // kafTags_compound stores pos of the text layer with compound words separated: porte-parole -> porte - parole
    public void getKafPosTags() {
    	String[] guess = new String[guessArray.size()];
    	guessArray.toArray(guess);
    
    	PosTagger tagger = new PosTagger();     
    	String[] tags = tagger.postag(words);
    	String[] tags_compound = tagger.postag(guess);
    	kafTags = new String[tags.length];
    	morphofeat = new String[tags.length];
    	kafTags_compound = new String[tags_compound.length];
    	multiword_morphofeat = new String[tags_compound.length];
    	for (int i=0;i<kafTags_compound.length;i++) {
    		if (i < kafTags.length){
    			kafTags[i]=TagsetMappings.convertFromFtbToKaf(tags[i]);
    			morphofeat[i]=tags[i];
    		}
    		kafTags_compound[i]=TagsetMappings.convertFromFtbToKaf(tags_compound[i]);
    		multiword_morphofeat[i]=tags_compound[i];
    	}
    }
    
    // detects multiword items
    public void getKafMultiwordTags() {
    	PosTagger multiwordTagger = new PosTagger();
    	multiword_tags = multiwordTagger.multiwordTag(words);
    }
    
    // type: open or close
    public String getKafType(String pos) {
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
    
    //itr format <PoS#word_id>: itr stores all the span and component ids and pos
    public void setTermHeadPoS(KafTerm kt, Iterator<String> itr){
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
    
    public KafTermComponent builKafTermComponent(String cid, String pos, String lemma) {
    	KafTermComponent tc = new KafTermComponent();
		tc.setId(cid);
		tc.setPos(pos);
		tc.setLemma(lemma);
		return tc;
    }
    
    public int getKafWordsLength() {
    	return words.length;
    }
    
    public String getKafWord(int i) {
    	return words[i];
    }
    
    public String getKafWid(int i) {
    	return wids[i];
    }
    
    public boolean isCompound(int i) {
    	return !(hashterm.get(getKafWord(i)+getKafWid(i)).isEmpty());
    }
    
    public int getKafCompoundSize(int i) {
    	return hashterm.get(getKafWord(i)+getKafWid(i)).size();
    }
    
    public String getKafCompundComponent(int i, int j) {
    	return hashterm.get(getKafWord(i)+getKafWid(i)).get(j);
    }
    
    public String getKafPoS(int i) {
    	return kafTags[i];
    }
    
    public String getKafCompoundPoS(int i) {
    	return kafTags_compound[i];
    }
    
    public boolean isMW(int i) {
    	return multiword_tags[i].compareTo("C") == 0;
    }
    
    public int getKafMWLength() {
    	return multiword_tags.length;
    }
    
    public String[] getMorphofeat() {
 		return morphofeat;
 	}

 	public String[] getMultiword_morphofeat() {
 		return multiword_morphofeat;
 	}

}
