package spielwiese;

import org.junit.Test;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;

public class TestKlassen {

	@Test
	public void test() {
		Object o = new IndikatorGD();
		String tst = o.getClass().getName();
		System.out.println("Klaasenname: " + tst);
		Class class1 = null; 
		try {
			class1 = Class.forName(tst);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IndikatorAlgorithmus neuesObject = null; 
		try {
			neuesObject = (IndikatorAlgorithmus) class1.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Klaasenname neu: " + neuesObject.getClass().getName());
		
	}

	
	
	
}
