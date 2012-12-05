package org.vicomtech.opener.postagging.frenchPosTagger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafTerm;
import eu.kyotoproject.kaf.KafWordForm;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

public class KafFrenchPosTagger {

	private static Map<String,String> lemmatizationMap;
	private static POSModel posModel;
	private POSTaggerME posTagger;

	
	static{
		try {
			readSerializedObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{			

		KafSaxParser parser = new KafSaxParser();
		parser.parseFile(System.in);
		//parser.parseFile("KafExample.xml");
		ArrayList<KafWordForm> kafWordFormList = parser.kafWordFormList;
		String[]tokens=new String[kafWordFormList.size()];
		for(int i=0;i<kafWordFormList.size();i++){
			String wf = kafWordFormList.get(i).getWf();
		//	System.out.println(wf);
			tokens[i]=wf;
		}
		
		KafFrenchPosTagger kafFrenchPosTagger=new KafFrenchPosTagger();
		String[]tags=kafFrenchPosTagger.postagTokens(tokens);
		String[]kafTags=new String[tags.length];
		for(int i=0;i<kafTags.length;i++){
			kafTags[i]=TagsetMappings.convertFromFtbToKaf(tags[i]);
		}
		String[]lemmas=new String[tags.length];
		for(int i=0;i<tokens.length;i++){
			lemmas[i]=kafFrenchPosTagger.getLemma(tokens[i], kafTags[i]);
		}
		
		for(int i=0;i<kafWordFormList.size();i++){
			KafTerm kafTerm=new KafTerm();
			kafTerm.setTid("t"+(i+1));
			kafTerm.setLemma(lemmas[i]);
			kafTerm.setPos(tags[i]);
			
			ArrayList<String>spanWords=new ArrayList<String>();
			spanWords.add(kafWordFormList.get(i).getWid());
			kafTerm.setSpans(spanWords);
			
			parser.kafTermList.add(kafTerm);
		}

		String xmlString=	parser.getXML();
		System.out.println(xmlString);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public KafFrenchPosTagger(){
		if(needToLoadModel()){
			loadModel();
		}
		posTagger=new POSTaggerME(posModel);

	}

	private static void readSerializedObject() throws IOException, ClassNotFoundException{
		
		InputStream is=Class.class.getResourceAsStream("/FrenchLemmatizationMap.ser");
		
		//InputStream file = new FileInputStream( "FrenchLemmatizationMap.ser" );
	    InputStream buffer = new BufferedInputStream( is );
	    ObjectInput input = new ObjectInputStream ( buffer );

	    lemmatizationMap = (Map<String,String>)input.readObject();
	    input.close();
	        
	   // System.out.println("Lemmatization map loaded, size: "+lemmatizationMap.size());
	}
	
	private static boolean needToLoadModel(){
		if(posModel==null){
			return true;
		}else{
			return false;
		}
	}
	
	private static void loadModel(){
		InputStream modelIn=null;
		try {
			String pathToModelFile="/openNLP_models/french-pos-treetagger.bin";
			modelIn=Class.class.getResourceAsStream(pathToModelFile);
			posModel=new POSModel(modelIn);
		} catch (InvalidFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			closeInputStream(modelIn);
		}
	}
	
	private static void closeInputStream(InputStream is){
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
	

	public synchronized String[] postagTokens(String[] tokens) {
		String[] postags=posTagger.tag(tokens);
		return postags;
	}

	
	private String getLemma(String surfaceForm,String postag){
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
	
}
