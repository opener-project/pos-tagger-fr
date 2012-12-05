package org.vicomtech.opener.postagging.training;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelType;

public class TrainModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		POSModel model = null;

		InputStream dataIn = null;
		try {
			//POSDictionary posdic=POSDictionary.create(new FileInputStream("tagDictES_fromFreeling.tagdict"));
			
			
		  dataIn = new FileInputStream("french-pos-treetagger_lemmas.train");
//		  dataIn = new FileInputStream("OpenNLP_training_french_20120510.txt");
		  ObjectStream<String> lineStream =
				new PlainTextByLineStream(dataIn, "UTF-8");
		  ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
		
		  
		  URL url=Class.class.getResource("/tagdict.xml");
		  POSDictionary posdict=POSDictionary.create(url.openStream());
		  
		  TrainingParameters trainingParameters=new TrainingParameters();
		  trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, "10");
		  trainingParameters.put(TrainingParameters.CUTOFF_PARAM, "5");
		  
		 // model = POSTaggerME.train("fr", sampleStream, ModelType.MAXENT, null, null, 10, 5);
		  model = POSTaggerME.train("fr", sampleStream, trainingParameters, posdict, null);
		  
		  BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream("french-pos-treetagger_lemmas.bin"));
		  model.serialize(modelOut);

		}
		catch (IOException e) {
		  // Failed to read or parse training data, training failed
		  e.printStackTrace();
		}
		finally {
		  if (dataIn != null) {
		    try {
		      dataIn.close();
		    }
		    catch (IOException e) {
		      // Not an issue, training already finished.
		      // The exception should be logged and investigated
		      // if part of a production system.
		      e.printStackTrace();
		    }
		  }
		}

	}

}
