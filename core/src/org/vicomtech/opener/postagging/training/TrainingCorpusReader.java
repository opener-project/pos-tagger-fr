package org.vicomtech.opener.postagging.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TrainingCorpusReader {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		TrainingCorpusReader trainingCorpusReader= new TrainingCorpusReader();
		List<File> fileList = trainingCorpusReader.readCorpusFileList();
//		for(File file:fileList){
//			System.out.println(file.getName());
//			trainingCorpusReader.parseXmlFile(file);
//		}
//		System.out.println(fileList.size()+" files");
		
		ArrayList<String>allSentences=new ArrayList<String>();
		int count=0;
		for(File file:fileList){
			System.out.println("Number of files processed: "+(++count));
			Document doc=parseXmlFile(file);
			String formmatedCorpus=getOpenNlpTrainFormattedText(doc);
			String[]sentences=formmatedCorpus.split("\n");
		//	allSentences.add("FILE: "+file.getName());
			for(String sentence:sentences){
				String charsetModifiedSentence=sentence;//new String(sentence.getBytes(),Charset.forName(doc.getXmlEncoding()));
				
				allSentences.add(charsetModifiedSentence);
			}
		}
		System.out.println("Numer of total sentences: "+allSentences.size());
		writeToFile(allSentences);
	}

	public  List<File>readCorpusFileList(){
		File corpusDir=new File("src/main/resources/frenchTreeBankCorpus");
		if(!corpusDir.exists()){
			throw new RuntimeException("NO EXISTE");
		}
		File[]files=corpusDir.listFiles();
		ArrayList<File>fileList=new ArrayList<File>();
		for(File f:files){
			fileList.add(f);
		}
		return fileList;
	}
	
	public static Document parseXmlFile(File file ){
		
		try {
			  //File file = new File("c:\\MyXMLFile.xml");
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			// System.out.println("ENCONDIG: "+doc.getXmlEncoding());
			  doc.getDocumentElement().normalize();
			  return doc;
			 // System.out.println("Root element " + doc.getDocumentElement().getNodeName());
			  
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static String getOpenNlpTrainFormattedText(Document doc){
		String result="";
		NodeList sentenceNodes=doc.getElementsByTagName("SENT");
		for(int i=0;i<sentenceNodes.getLength();i++){
			Element sentenceNode=(Element) sentenceNodes.item(i);
			String openNlpFormattedSentence=getOpenNlpTrainFormattedSentence(sentenceNode);
			result+=openNlpFormattedSentence+"\n";
		}
		return result.trim();
	}
	
	public static String getOpenNlpTrainFormattedSentence(Element sentenceNode){
		String result="";
		NodeList wordNodes=sentenceNode.getElementsByTagName("w");
		for(int i=0;i<wordNodes.getLength();i++){
			Element wordNode=(Element) wordNodes.item(i);
//			if(wordNode.getAttribute("compound").equalsIgnoreCase("yes")){
//				continue;
//			}
//			if(wordNode.hasChildNodes() && wordNode.getFirstChild() instanceof Element){
//				continue;
//			}
			if(containsSubwords(wordNode)){
				continue;
			}
			String surfaceForm=wordNode.getTextContent();
			surfaceForm=surfaceForm.replace(" ", "_");
//			if(surfaceForm==null || surfaceForm.trim().equalsIgnoreCase("")){
//				System.out.println("No surface form: continuing");
//				continue;
//			}else{
//				System.out.println("Not continuing: surface form = "+surfaceForm);
//			}
			String postag;//=wordNode.getAttribute("cat");
			if(wordNode.hasAttribute("cat")){
				postag=wordNode.getAttribute("cat");
			}else{
				postag=wordNode.getAttribute("catint");
			}
			
			result+=surfaceForm+"_"+postag+" ";
		}
		return result.trim();
	}
	
	public static void writeToFile(ArrayList<String> formattedSentences) throws IOException{
		File f=new File("french-pos-treetagger.train");
		//FileWriter fw=new FileWriter(f);
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
		BufferedWriter bw=new BufferedWriter(out);
		for(String formattedSentence:formattedSentences){
//			for(String sentence:annotatedSentences){
				bw.write(formattedSentence+"\n");
//			}
//			bw.write("\n");
			bw.flush();
		}
		bw.flush();
		bw.close();
		out.close();
		//fw.close();
	}
	
	private static boolean containsSubwords(Element wordNode){
		if(wordNode.hasAttribute("compound")){
			return true;
		}else{
			NodeList childs = wordNode.getChildNodes();
			for(int i=0;i<childs.getLength();i++){
				try{
				Element child=(Element) childs.item(i);
				child.hasAttribute("catint");
				return true;
				}catch(Exception e){
					
				}
			}
			return false;
		}		
	}
}
