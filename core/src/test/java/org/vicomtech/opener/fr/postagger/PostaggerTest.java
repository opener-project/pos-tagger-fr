package org.vicomtech.opener.fr.postagger;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class PostaggerTest {

	@Test
	public void test() throws IOException {

		InputStream is=PostaggerTest.class.getResourceAsStream("/french_tokenized2.kaf");
		Kaf kaf=new Kaf();
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		kaf.execute(is, bos, new String[0]);
		is.close();
		System.out.println(bos.toString().substring(0,100));
		assertTrue(true);
	}

}
