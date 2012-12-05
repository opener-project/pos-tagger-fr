package org.vicomtech.opener.postagging.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrenchLemmatizationMap {

	private static Map<String,String> lemmatizationMap;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		List<String> l = readFile();
		loadLemmatizationMap(l);
		serializeLemmatizationMap();

	}

	public static List<String> readFile() throws IOException{
		File f=new File("French_lemmas.txt");
		FileReader fr=new FileReader(f);
		BufferedReader br=new BufferedReader(fr);
		String line="";
		ArrayList<String>result=new ArrayList<String>();
		while((line=br.readLine())!=null){
			if(!line.trim().equalsIgnoreCase("")){
				result.add(line);
			}
		}
		br.close();
		return result;
	}
	
	public static void loadLemmatizationMap(List<String>entries){
		lemmatizationMap=new HashMap<String,String>();
		for(String entry:entries){
			String[] splitted=entry.split(" ");
			String surfaceform=splitted[0];
			String lemmasAndTags="";
			for(int i=1;i<splitted.length;i++){
				lemmasAndTags+=splitted[i]+" ";
			}
			lemmatizationMap.put(surfaceform, lemmasAndTags.trim());
		}
	}
	
	public static void serializeLemmatizationMap() throws IOException{
		System.out.println("Writing lemmatization map, size: "+lemmatizationMap.size());
		FileOutputStream fout = new FileOutputStream("FrenchLemmatizationMap.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);   
		oos.writeObject(lemmatizationMap);
		oos.close();
	}
	
}
