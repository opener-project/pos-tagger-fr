package org.vicomtech.opener.postagging.training;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReadFrenchLemmatizationMap {

	private static Map<String,String> lemmatizationMap;
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		readSerializedObject();
		
		String key="Les";
		String s = lemmatizationMap.get(key);
		System.out.println("Values for "+key+" --> "+s);
		
		String lemma=getLemma(key, "nc");
		System.out.println("Lemma: "+lemma);
		

	}
	
	public static void readSerializedObject() throws IOException, ClassNotFoundException{
		InputStream file = new FileInputStream( "FrenchLemmatizationMap.ser" );
	    InputStream buffer = new BufferedInputStream( file );
	    ObjectInput input = new ObjectInputStream ( buffer );

	    lemmatizationMap = (Map<String,String>)input.readObject();
	    input.close();
	        
	    System.out.println("Lemmatization map loaded, size: "+lemmatizationMap.size());
	    
	    Set<String>tagset=new HashSet<String>();
	    for(String entry:lemmatizationMap.keySet()){
	    	String value=lemmatizationMap.get(entry);
	    	String[]valueSplit=value.split(" ");
	    	for(int i=0;i<valueSplit.length-1;i+=2){
	    		tagset.add(valueSplit[i+1]);
	    	}
	    }
	    
	    System.out.println("TAG SET");
	    for(String s:tagset){
	    	System.out.println(s);
	    }
	    
	}
	
	public static String getLemma(String surfaceForm,String postag){
		String lemmasAndTags=lemmatizationMap.get(surfaceForm);
		String[]splitLemmasAndTags=lemmasAndTags.split(" ");
		for(int i=0;i<splitLemmasAndTags.length-1;i+=2){
			String currentLemma=splitLemmasAndTags[i];
			String currentTag=splitLemmasAndTags[i+1];
			if(currentTag.equalsIgnoreCase(postag)){
				return currentLemma;
			}
		}
		return surfaceForm;
	}
	
	
}
