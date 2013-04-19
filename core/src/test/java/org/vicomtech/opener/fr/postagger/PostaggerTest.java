package org.vicomtech.opener.fr.postagger;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class PostaggerTest {

	@Test
	public void test() throws IOException {

		InputStream is=PostaggerTest.class.getResourceAsStream("/french_tokenized.kaf");
		Kaf kaf=new Kaf();
		kaf.execute(is, System.out, new String[0]);
		is.close();
		assertTrue(true);
	}

}
