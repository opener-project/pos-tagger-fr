import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;


public class LemmatizationMap {

	private Map<String,String> lemmatizationMap;
	private String dir;
	public LemmatizationMap(String dir) {
		this.dir = dir;
	}
	public void readSerializedObject() {
		try {
			InputStream file;
			//if (System.getProperty("java.version").substring(0, 3).compareTo("1.7") == 0)
				file = new FileInputStream(dir + "FrenchLemmatizationMap.ser");
			//else
				//file = Class.class.getResourceAsStream(models_dir + "FrenchLemmatizationMap.ser");
			
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
	public String getLemma(String surfaceForm,String postag){
    	
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
