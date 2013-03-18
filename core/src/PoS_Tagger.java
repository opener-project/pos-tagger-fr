import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PoS_Tagger {

	private String model;
	
	public PoS_Tagger(String model) {
		this.model = model;
	}
	public String[] tag(String[] words) {

		try {
			InputStream modelIn = new FileInputStream(this.model);
			POSModel model = new POSModel(modelIn);
		
			POSTaggerME tagger = new POSTaggerME(model);
        
			String[] tags = tagger.tag(words);
        
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
			return tags;
		}
        catch (IOException ex) {
        	ex.printStackTrace();
        	return null;
        }
	}

}
