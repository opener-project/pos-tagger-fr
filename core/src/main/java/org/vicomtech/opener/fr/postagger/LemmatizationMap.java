package org.vicomtech.opener.fr.postagger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Map;

public class LemmatizationMap {

	private static final String SERIALIZED_LEMMATIZATION_MAP = "/FrenchLemmatizationMap.ser";

	private static Map<String, String> lemmatizationMap;

	public LemmatizationMap() {
		if (lemmatizationMap == null || lemmatizationMap.isEmpty()) {
			readSerializedMap();
		}
	}

	@SuppressWarnings("unchecked")
	private void readSerializedMap() {
		try {
			InputStream is = LemmatizationMap.class
					.getResourceAsStream(SERIALIZED_LEMMATIZATION_MAP);
			// is = new FileInputStream(dir + "FrenchLemmatizationMap.ser");
			InputStream buffer = new BufferedInputStream(is);
			ObjectInput input = new ObjectInputStream(buffer);
			lemmatizationMap = (Map<String, String>) input.readObject();
			input.close();
			is.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLemma(String surfaceForm, String postag) {
		String lemmasAndTags = lemmatizationMap.get(surfaceForm.toLowerCase());
		if (lemmasAndTags == null) {
			return surfaceForm;
		}
		String[] splittedLemmasAndTags = lemmasAndTags.split(" ");
		for (int i = 0; i < splittedLemmasAndTags.length - 1; i += 2) {
			String currentLemma = splittedLemmasAndTags[i];
			String currentTag = TagsetMappings
					.convertFromLemmatagsToKaf(splittedLemmasAndTags[i + 1]);
			if (currentTag.equalsIgnoreCase(postag)) {
				return currentLemma;
			}
		}
		return surfaceForm;
	}
}
