package org.vicomtech.opener.fr.postagger;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import eu.openerproject.kaf.layers.KafTarget;
import eu.openerproject.kaf.layers.KafTerm;
import eu.openerproject.kaf.layers.KafTermComponent;
import eu.openerproject.kaf.reader.KafSaxParser;

//import eu.kyotoproject.kaf.KafSaxParser;
//import eu.kyotoproject.kaf.KafTerm;
//import eu.kyotoproject.kaf.TermComponent;

public class Kaf {

	private KafTextLayer ktl;

	static public void main(String[] args) {
		Kaf kaf=new Kaf();
		kaf.execute(System.in, System.out, args);
	}
	
	public void execute(InputStream in, OutputStream out, String[]args){
		
		Boolean staticTimestamp = false;
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if(args[i].equalsIgnoreCase("--help")){
					System.out.println("Usage: \n[-t] for static timestamp (for test purposes)");
					return;
				}else if(args[i].equalsIgnoreCase("-t")){
					staticTimestamp = true;
				}
			}
		}
		LemmatizationMap lemmatizationMap = new LemmatizationMap();
		KafSaxParser parser = new KafSaxParser();

		parser.parseFile(in);
		if (!staticTimestamp) {
			parser.getMetadata().addLayer("terms", "opennlp-pos-treetagger-fr", "1.0");
			parser.getMetadata().addLayer("terms", "opennlp-multiword-fr", "1.0");
		} else {
			parser.getMetadata().addLayer("terms", "opennlp-pos-treetagger-fr", "1.0", "2013-02-11T11:07:17Z");
			parser.getMetadata().addLayer("terms", "opennlp-multiword-fr", "1.0", "2013-02-11T11:07:17Z");
		}

		ktl = new KafTextLayer(parser);

		// get words
		ktl.getKafWordsWids();

		// compounds
		ktl.getKafCompounds();

		// tags
		ktl.getKafPosTags();

		// multiwords
		ktl.getKafMultiwordTags();

		// Kaf construction
		KafTerm kt;
		KafTermComponent tc;
		ArrayList<String> termPoSTags = new ArrayList<String>(); // <PoS#word_id>
		int c = 0;
		String head_word;
		String multiword_lemma;
		String compound_lemma;
		String lemma;
		int mw = 1;
		int tid = 1;
		Iterator<String> itr;

		for (int i = 0; i < ktl.getKafWordsLength(); i++) {
			kt = new KafTerm();
			kt.setTid("t" + (tid));

			// word is a multiword part
			if (ktl.isMW(i)) {
				mw = 1;
				multiword_lemma = "";
				kt.setPos(ktl.getKafPoS(i));
				kt.setMorphofeat(ktl.getMorphofeat()[i]);
				kt.setType(ktl.getKafType(kt.getPos()));
				kt.setHead("t" + (tid) + ".1");
				head_word = "";
				termPoSTags = new ArrayList<String>();

				// for all multiword components do
				while (i < ktl.getKafMWLength() && ktl.isMW(i)) {
					termPoSTags.add(ktl.getKafPoS(i) + "#" + "t" + (tid) + "."
							+ mw);
					lemma = lemmatizationMap.getLemma(ktl.getKafWord(i),
							ktl.getKafPoS(i));

					kt.getSpan().add(new KafTarget(ktl.getKafWid(i)));
					// if multiword component is a compound
					if (ktl.isCompound(i)) {
						compound_lemma = "";
						for (int z = 0; z < ktl.getKafCompoundSize(i); z++) {
							// find compound's head
							if (ktl.getKafPoS(i).compareTo(
									ktl.getKafCompoundPoS(c)) == 0
									&& ktl.getKafCompundComponent(i, z)
											.length() > head_word.length()) {
								termPoSTags.remove(termPoSTags.size() - 1);
								termPoSTags.add(ktl.getKafPoS(i) + "#" + "t"
										+ (tid) + "." + (z + 1));
								head_word = ktl.getKafCompundComponent(i, z);
							}
							// build TermComponent object
							if (ktl.getKafCompundComponent(i, z).matches(
									"[A-Z].*"))
								tc = ktl.builKafTermComponent("t" + (tid) + "."
										+ (mw), ktl.getKafCompoundPoS(c),
										ktl.getKafCompundComponent(i, z));
							else
								tc = ktl.builKafTermComponent("t" + (tid) + "."
										+ (mw), ktl.getKafCompoundPoS(c),
										lemmatizationMap.getLemma(ktl
												.getKafCompundComponent(i, z),
												ktl.getKafCompoundPoS(c)));
							compound_lemma += tc.getLemma();
							kt.addComponents(tc);
							c++;
							mw++;
						}
						multiword_lemma += compound_lemma + "_";
					}
					// word is not a compound
					else {

						if (ktl.getKafWord(i).matches("[A-Z].*"))
							tc = ktl.builKafTermComponent("t" + (tid) + "."
									+ (mw), ktl.getKafPoS(i), ktl.getKafWord(i));
						else
							tc = ktl.builKafTermComponent("t" + (tid) + "."
									+ (mw), ktl.getKafPoS(i), lemma);
						multiword_lemma += tc.getLemma() + "_";
						kt.addComponents(tc);
						mw++;
						c++;
					}
					i++;
				}
				// word was not multiword fix
				if (mw == 2) {
					kt.setComponents(new ArrayList<KafTermComponent>());
					kt.setHead("");
				}
				// set term's head and PoS. The candidates are at the
				// termPoSTags array
				else {
					itr = termPoSTags.iterator();
					ktl.setTermHeadPoS(kt, itr);
					kt.setType(ktl.getKafType(kt.getPos()));
				}
				kt.setLemma(multiword_lemma.substring(0,
						multiword_lemma.length() - 1));
				i--;
			}
			// word is not multiword component
			else {
				kt.setPos(ktl.getKafPoS(i));
				kt.setMorphofeat(ktl.getMorphofeat()[i]);
				kt.setType(ktl.getKafType(kt.getPos()));
				kt.setLemma(lemmatizationMap.getLemma(ktl.getKafWord(i),
						ktl.getKafPoS(i)));
				kt.getSpan().add(new KafTarget(ktl.getKafWid(i)));
				// word is a compound
				if (ktl.isCompound(i)) {
					compound_lemma = "";
					kt.setHead("t" + (tid) + ".1");
					head_word = "";
					for (int z = 0; z < ktl.getKafCompoundSize(i); z++) {
						// find compound's head
						if (ktl.getKafPoS(i)
								.compareTo(ktl.getKafCompoundPoS(c)) == 0
								&& ktl.getKafCompundComponent(i, z).length() > head_word
										.length()) {
							kt.setHead("t" + (tid) + "." + (z + 1));
							head_word = ktl.getKafCompundComponent(i, z);
						}
						// build TermComponent object
						if (ktl.getKafCompundComponent(i, z).matches("[A-Z].*"))
							tc = ktl.builKafTermComponent("t" + (tid) + "."
									+ (z + 1), ktl.getKafCompoundPoS(c),
									ktl.getKafCompundComponent(i, z));
						else
							tc = ktl.builKafTermComponent("t" + (tid) + "."
									+ (z + 1), ktl.getKafCompoundPoS(c),
									lemmatizationMap.getLemma(
											ktl.getKafCompundComponent(i, z),
											ktl.getKafCompoundPoS(c)));
						compound_lemma += tc.getLemma();
						kt.addComponents(tc);
						c++;
					}
					kt.setLemma(compound_lemma);
				} else {
					c++;
				}
			}
			tid++;
			parser.getTermList().add(kt);
		}

		// try {
		parser.writeKafToStream(out,false);

	}

}
