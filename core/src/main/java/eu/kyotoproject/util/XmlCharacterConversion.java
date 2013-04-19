package eu.kyotoproject.util;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Piek Vossen
 * Date: 17-dec-2008
 * Time: 14:38:37
 * To change this template use File | Settings | File Templates.
 * This file is part of KafSaxParser.

    KafSaxParser is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    KafSaxParser is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with KafSaxParser.  If not, see <http://www.gnu.org/licenses/>.
 */
public class XmlCharacterConversion {

    static final String illegalChars = "<>&\"";

    static public void fixKafXml (String content, FileOutputStream fos) {
        try {
            int previousPos = 0;
            int idx_s = content.indexOf(" <text>");
            int idx_e = content.indexOf(" </text>");
            if ((idx_s>-1) && (idx_e>idx_s)) {
                String wordForms = content.substring(idx_s, idx_e);
                fos.write(content.substring(previousPos, idx_s).getBytes());
                cdataWordForms(wordForms, fos);
                //System.out.println("cleanContent = " + cleanContent);
                previousPos = idx_e;
            }
            idx_s = content.indexOf(" <terms>");
            idx_e = content.indexOf(" </terms>");
            if ((idx_s>-1) && (idx_e>idx_s)) {
                String terms = content.substring(idx_s, idx_e);
                fos.write(content.substring(previousPos, idx_s).getBytes());
                convertLemmas(terms, fos);
                //System.out.println("cleanContent = " + cleanContent);
                previousPos = idx_e;
            }
            fos.write(content.substring(previousPos, content.length()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static public void cdataWordForms (String content, FileOutputStream fos) throws IOException {
        //<wf wid="w812" sent="84" para="1">http://www.otevrena-veda.cz/ov/index.php?site=ov_en&p=index</wf>
      //  int wfCount = 0;
        int previousPos = 0;
        int idx_s = -1;
        int idx_e = content.indexOf("</wf>");
            while (idx_e>-1) {
                 idx_s = content.lastIndexOf(">", idx_e);
                 if ((idx_s>-1) && (idx_e>idx_s)) {

                     fos.write(content.substring(previousPos, idx_s+1).getBytes());
                     fos.write("<![CDATA[".getBytes());
                     fos.write(content.substring(idx_s+1, idx_e).getBytes());
                     fos.write("]]>".getBytes());
                     previousPos = idx_e;
                }
                idx_s = content.indexOf("<wf ", idx_e);
                if (idx_s>-1) {
                    idx_e = content.indexOf("</wf>", idx_s);
                }
                else {
                    idx_e = -1;
                }
            }
            fos.write(content.substring(previousPos, content.length()).getBytes());
    }

    static public void convertLemmas(String content, FileOutputStream fos) throws IOException {
        //    <term tid="t1940" type="open" lemma="EU-R & D-project FRAP" pos="n">
       // int lemmaCount = 0;
        int previousPos = 0;
        int idx_s = content.indexOf("lemma=\"");
        int idx_e = -1;
        String lemma = "";
        int attributeLength = 7;
        if (idx_s ==-1) {
            idx_s = content.indexOf("lemma =\"");
            attributeLength = 8;
        }
        if (idx_s ==-1) {
            idx_s = content.indexOf("lemma = \"");
            attributeLength = 9;
        }
        if (idx_s ==-1) {
            idx_s = content.indexOf("lemma= \"");
            attributeLength = 8;
        }
        while (idx_s>-1) {
             idx_e = content.indexOf("\" pos", idx_s+attributeLength);
             if (idx_e>-1) {

                lemma = content.substring(idx_s+attributeLength, idx_e);
                //System.out.println("lemma = " + lemma);
                if (hasIllegalChar(lemma)) {
                    lemma = replaceXmlChar(lemma);
                    fos.write(content.substring(previousPos, idx_s+attributeLength).getBytes());
                    fos.write(lemma.getBytes());
                    previousPos = idx_e;
                }
                // System.out.println("lemma = " + lemma);
            }
            idx_s = content.indexOf("lemma=\"", idx_e);
            attributeLength = 7;
            if (idx_s ==-1) {
                idx_s = content.indexOf("lemma =\"", idx_e);
                attributeLength = 8;
            }
            if (idx_s ==-1) {
                idx_s = content.indexOf("lemma = \"", idx_e);
                attributeLength = 9;
            }
            if (idx_s ==-1) {
                idx_s = content.indexOf("lemma= \"", idx_e);
                attributeLength = 8;
            }
        }
        fos.write(content.substring(previousPos, content.length()).getBytes());
    }

    static boolean hasIllegalChar (String htmlWord) {
        for (int i=0; i<htmlWord.length();i++) {
            char c = htmlWord.charAt(i);
            if (illegalChars.indexOf(c)!=-1) {
               return true;
            }
        }
        return false;
    }

    static public String replaceXmlChar (String word) {
            String htmlWord = word;
            htmlWord = htmlWord.replaceAll("<", "&lt;");
            htmlWord = htmlWord.replaceAll(">", "&gt;");
            htmlWord = htmlWord.replaceAll("&", "&amp;");
            htmlWord = htmlWord.replaceAll("\"", "&quot;");
        return htmlWord;
    }

    static public String replaceXmlCharKyoto (String word) {
            String htmlWord = word;
            //htmlWord = htmlWord.replaceAll("<", "&lt;");
            //htmlWord = htmlWord.replaceAll(">", "&gt;");
            //htmlWord = htmlWord.replaceAll("&", "&amp;");
            //htmlWord = htmlWord.replaceAll("\"", "&quot;");
            htmlWord = htmlWord.replaceAll("<", "[kyoto:lt]");
            htmlWord = htmlWord.replaceAll(">", "[kyoto:gt]");
            htmlWord = htmlWord.replaceAll("&", "[kyoto:amp]");
            htmlWord = htmlWord.replaceAll("\"", "[kyoto:qt]");

        return htmlWord;
    }

}
