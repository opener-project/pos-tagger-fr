package org.vicomtech.opener.fr.postagger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.openerproject.kaf.layers.KafTarget;
import eu.openerproject.kaf.layers.KafTerm;
import eu.openerproject.kaf.layers.KafWordForm;
import eu.openerproject.kaf.reader.KafSaxParser;

public class Main {

	private PosTagger postagger;
	private LemmatizationMap lemmatizationMap;
	private boolean staticTimestamp;
	private boolean helpParameter;

	public Main() {
		postagger = new PosTagger();
		lemmatizationMap = new LemmatizationMap();
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.execute(System.in, System.out,args);
	}

	public void execute(InputStream is, OutputStream os, String[]args) {
		checkParametersAndTimestamp(args);
		//if helpParameter flag activated, then just return
		if(helpParameter){
			System.out.println("Usage: \n[-t] for static timestamp (for test purposes)");
			return;
		}
		KafSaxParser kafParser = new KafSaxParser();
		kafParser.parseFile(is);
		addLinguisticProcessorInfo(kafParser, args);
		List<KafWordForm> wordForms = kafParser.getWordList();
		String[] tokens = new String[wordForms.size()];
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = wordForms.get(i).getWordform();
		}
		String[] tags = postagger.postag(tokens);
		String[] lemmas = new String[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			String kafPosTag = TagsetMappings.convertFromFtbToKaf(tags[i]);
			lemmas[i] = lemmatizationMap.getLemma(tokens[i], kafPosTag);
		}
		for (int i = 0; i < tokens.length; i++) {
			KafTerm kafTerm = new KafTerm();
			List<KafWordForm> targetWordForms = new ArrayList<KafWordForm>();
			// only one wordform per term, no multiwords
			KafWordForm kafWordForm = kafParser.getWordList().get(i);
			targetWordForms.add(kafWordForm);
			kafTerm.setSpan(getTermTokenSpan(targetWordForms));
			kafTerm.setLemma(lemmas[i]);
			// dirty trick to leverage wid to obtain the same numbering for tid
			kafTerm.setTid(kafWordForm.getWid().replace("w", "t"));
			String kafPosTag = TagsetMappings.convertFromFtbToKaf(tags[i]);
			kafTerm.setPos(kafPosTag);
			kafTerm.setMorphofeat(tags[i]);
			kafTerm.setType(getKafType(kafPosTag));
			kafParser.getTermList().add(kafTerm);
		}
		kafParser.writeKafToStream(os, false);

	}

	protected List<KafTarget> getTermTokenSpan(List<KafWordForm> targetWordForms) {
		List<KafTarget> result = new ArrayList<KafTarget>();
		for (KafWordForm kafWordForm : targetWordForms) {
			KafTarget kafTarget = new KafTarget();
			kafTarget.setId(kafWordForm.getWid());
			// kafTarget.setHead(kafWordForm.getWid());
			//kafTarget.setTokenString(kafWordForm.getWordform());
			result.add(kafTarget);
		}
		return result;
	}

	protected String getKafType(String pos) {
		if (pos.equalsIgnoreCase("N") || pos.equalsIgnoreCase("V")
				|| pos.equalsIgnoreCase("A") || pos.equalsIgnoreCase("G")) {
			return "open";
		} else {
			return "close";
		}
	}
	
	/**
	 * Checks if input parameter is --help, or if it is -t, and updates the flags
	 * @return
	 */
	protected void checkParametersAndTimestamp(String[]args){
		staticTimestamp=false;
		helpParameter=false;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase("--help")){
					helpParameter=true;
				}else if(args[i].equalsIgnoreCase("-t")){
					staticTimestamp = true;
				}
			}
		}
	}
	
	protected void addLinguisticProcessorInfo(KafSaxParser kafParser, String[]args){
		if (!staticTimestamp) {
			kafParser.getMetadata().addLayer("terms", "opennlp-pos-treetagger-fr", "1.0");
			kafParser.getMetadata().addLayer("terms", "opennlp-multiword-fr", "1.0");
		} else {
			kafParser.getMetadata().addLayer("terms", "opennlp-pos-treetagger-fr", "1.0", "2013-02-11T11:07:17Z");
			kafParser.getMetadata().addLayer("terms", "opennlp-multiword-fr", "1.0", "2013-02-11T11:07:17Z");
		}
	}
}
