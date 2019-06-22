package com.algotrading.indikator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;

import junit.framework.TestCase;

public class TestMinMax extends TestCase {
	
	public void testMinMax() {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktie("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorMinMax());
		iA.addParameter("dauer", 10);
		aktie.rechneIndikatoren();
		
		Kurs kurs = aktie.getKurse().get(20);
		float test = kurs.getIndikatorWert(iA);
		System.out.println("test" + test);
		assertEquals(0.43922076f, test);
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("OutputTestFile.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ObjectOutputStream oos = null; 
		try {
			oos = new ObjectOutputStream(fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			oos.writeObject(aktie);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		aktie.writeFileIndikatoren();
		
	}

}
