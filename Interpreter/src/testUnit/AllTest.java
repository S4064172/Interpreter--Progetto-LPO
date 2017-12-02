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
/*	
	@Test
	public void ParserTest() 
	{
		JUParsetTest test = new JUParsetTest();
		test.testNewAtomRight();
		test.testNewAtomWrong();
		test.testAddOrSubRight();
		test.testAddOrSubWrong();
		test.testConCatRight();
		test.testConCatWrong();
		test.testTimesOrDivRight();
		test.testTimesOrDivWrong();
		test.testWhileRight();
		test.testWhileWrong();
		test.testIfRight();
		test.testIfWrong();
	}
	
	@Test
	public void TypeCheckTest() 
	{
		JUTypeCheck test = new JUTypeCheck();
		test.TestNewAtomCheckTypeRight();
		test.TestNewAtomCheckTypeWrong();
		test.TestAddOrSubCheckTypeRight();
		test.TestAddOrSubCheckTypeWrong();
		test.TestConCatCheckTypeRight();
		test.TestConCatCheckTypeWrong();
		test.TestTimesOrDivCheckTypeRight();
		test.TestTimesOrDivCheckTypeWrong();
		test.TestWhileCheckTypeRight();
		test.TestWhileCheckTypeWrong();
		test.TestIfCheckTypeRight();
		test.TestIfCheckTypeWrong();
	}
	
	@Test
	public void Eval() 
	{
		JUEvalTest test = new JUEvalTest();
		test.TestNewAtomEvalRight();
		test.TestAddOrSubEvalRight();
		test.TestConCatEvalRight();
		test.TestTimesOrDivEvalRight();
		test.TestIfEvalRight();
		
		
	}
*/
}
