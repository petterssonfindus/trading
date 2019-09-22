package com.algotrading.util;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;

public class FileUtilTest extends AbstractTest {

/*
	public void testWriteFile() {
		String dateiname = "testIt002";
		ArrayList<String> test = new ArrayList<String>();
		test.add("Zeile1");
		test.add("Zeile2");
		test.add("Zeile3");
		File file = FileUtil.writeFile(test, dateiname, "txt");
		assertNotNull(file);
		
	}
*/
	public void testReadFile() {
		ArrayList<String> test = new FileUtil().readContent("testIt002");
		assertNotNull(test);
		assertEquals(3, test.size());
		
	}
	

}
