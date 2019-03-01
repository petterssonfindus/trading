package spielwiese;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
// import static org.mockito.Mockito.mock;
import org.mockito.Mockito;
import junit.framework.TestCase;

public class TestMyMathInject extends TestCase {
	
	@InjectMocks
	@Mock
	public MyMath myMath; 
	
	public void setUp() {
		
//		myMath = new MyMath();
	}
	
	@Test
	public void testMyMath() {
		MyMath myMathMock = Mockito.mock(MyMath.class);
		Mockito.when(myMathMock.rechneAddition(1d, 1d)).thenReturn(4d);
		// Ausführung der Methode normal 
		double test = myMath.rechneAddition(1, 1);
		// Ausführung der Methode über Mock 
		double testMock = myMathMock.rechneAddition(1, 1);
		
		Mockito.verify(myMathMock).rechneAddition(1d, 1d);
		assertEquals(3.0d, test);
		
		System.out.println("test = " + test);
		System.out.println("testMock = " + testMock);
	}

}
