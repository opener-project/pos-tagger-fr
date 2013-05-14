package org.vicomtech.opener.fr.postagger;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class PosTagger {

	private static final String POSTAGGING_MODEL_PATH = "/fr-pos-ftb-morpho.bin";
	private static final String MULTIWORD_MODEL_PATH = "/fr-multiword-maxent.bin";
	private static POSModel posModel;
	private static POSModel multiwordModel;
	private POSTaggerME postagger;
	private POSTaggerME multiwordTagger;

	/**
	 * The PosTagger class uses an internal instance of OpenNLP POSTaggerME. The
	 * model is only loaded once, when the first instance is created, and is
	 * shared among all instances.
	 */
	public PosTagger() {
		try {
			//Load pos model only if it has not been loaded before
			if (posModel == null) {
				InputStream modelIn = PosTagger.class
						.getResourceAsStream(POSTAGGING_MODEL_PATH);
				posModel = new POSModel(modelIn);
				modelIn.close();
			}
			//Load multiword model only if it has not been loaded before
			if (multiwordModel == null) {
				InputStream modelIn = PosTagger.class
						.getResourceAsStream(MULTIWORD_MODEL_PATH);
				multiwordModel = new POSModel(modelIn);
				modelIn.close();
			}
			//set the instances for both tagging tools
			postagger = new POSTaggerME(posModel);
			multiwordTagger = new POSTaggerME(multiwordModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] postag(String[] words) {
		String[] tags = postagger.tag(words);
		return tags;
	}
	
	public String[] multiwordTag(String[] words){
		String[] multiwordtags = multiwordTagger.tag(words);
		return multiwordtags;
	}

}
