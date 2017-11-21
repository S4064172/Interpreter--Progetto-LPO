package testUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import testUnit.TokenizerTest.JUTokenizerTest;




public class AllTest {

	@Test
	public void TokenizerTest() 
	{
		JUTokenizerTest test = new JUTokenizerTest();
		test.JUTokenizerTest_CONV();
		test.JUTokenizerTest_NUM();
	}
	
	

}
