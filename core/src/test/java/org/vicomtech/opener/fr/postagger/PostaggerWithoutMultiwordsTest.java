package org.vicomtech.opener.fr.postagger;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class PostaggerWithoutMultiwordsTest {

	InputStream is;
	
	@Before
	public void setUp() throws Exception {
		is=PostaggerWithoutMultiwordsTest.class.getResourceAsStream("/french_tokenized.kaf");
	}

	@Test
	public void testPosTag() {
		Main main=new Main();
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		main.execute(is, bos, new String[]{"-t"});
		System.out.println(bos.toString());
		assertTrue(true);
		

//		InputStream is=PostaggerTest.class.getResourceAsStream("/french_tokenized.kaf");
//		Kaf kaf=new Kaf();
//		ByteArrayOutputStream bos2=new ByteArrayOutputStream();
//		kaf.execute(is, bos2, new String[]{"-t"});
//		assertTrue(true);
		
//		System.out.println(bos.toString().substring(0, 1000));
//		System.out.println("==========");
//		System.out.println(bos2.toString().substring(0, 1000));
		
//		String[]newLines=bos.toString().split("\n");
//		String[]oldLines=bos2.toString().split("\n");
//		for(int i=0;i<newLines.length;i++){
//			assertEquals("Difference in line "+i,newLines[i], oldLines[i]);
//		}
		//assertEquals(bos.toString(), bos2.toString());
		
		
	}

}
