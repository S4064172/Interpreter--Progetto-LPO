package testUnit;

import static org.junit.Assert.*;

import org.junit.Test;

import testUnit.EvalTest.JUEvalTest;
import testUnit.ParsetTest.JUParsetTest;
import testUnit.TokenizerTest.JUTokenizerTest;
import testUnit.TypeCheck.JUTypeCheck;

public class AllTest {

	@Test
	public void TokenizerTest() 
	{
		JUTokenizerTest test = new JUTokenizerTest();
		test.JUTokenizerTest_CONV();
		test.JUTokenizerTest_NUM();
	}
	
	@Test
	public void ParserTest() 
	{
		JUParsetTest test = new JUParsetTest();
		test.testNewAtomRight();
		test.testNewAtomWrong();
	}
	
	@Test
	public void TypeCheckTest() 
	{
		JUTypeCheck test = new JUTypeCheck();
		test.TestNewAtomCheckTypeRight();
		test.TestNewAtomCheckTypeWrong();
	}
	
	@Test
	public void Eval() 
	{
		JUEvalTest test = new JUEvalTest();
		test.TestNewAtomCheckTypeRight();
		
	}

}
