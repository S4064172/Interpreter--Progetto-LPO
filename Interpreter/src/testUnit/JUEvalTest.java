package testUnit;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.ParserException;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _3_Ast.Add;
import _3_Ast.ConCat;
import _3_Ast.Div;
import _3_Ast.Fst;
import _3_Ast.IfStmt;
import _3_Ast.Length;
import _3_Ast.Mul;
import _3_Ast.Pair;
import _3_Ast.Prog;
import _3_Ast.Snd;
import _3_Ast.Sub;
import _3_Ast.SwitchStmt;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.evaluation.EvaluatorException;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class JUEvalTest {

	private static final Class<?>[] NoParams=null;
	@ParameterizedTest
	@CsvSource
	({ 
		"'length [5,2,3]' , 3",
		"'snd pair(pair(5,5),pair([3],[5]))' , '([3],[5])'", 
		"'snd pair([5],[5])' , '[5]'",
		"'snd pair (5,5)' , '5'",
		"'fst pair(pair(5,5),pair([3],[5]))' , '(5,5)'",
		"'fst pair([5],[5])' , '[5]'",
		"'fst pair (5,5)' , '5'",
		"'pair(pair(5,5),pair([3],[5]))' , '((5,5),([3],[5]))'",
		"'pair([5],[5])' , '([5],[5])'",
		"'pair (5,5)' , '(5,5)'"
	})
	public void TestNewAtomEvalRight(String input, String resultExpected) {
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader( new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name()))) ))
		{
				String resultCall = null;
				try
				{
					StreamParser parser = new StreamParser(tokenizer);
					Method method = parser.getClass().getDeclaredMethod("parseAtom", NoParams);
					method.setAccessible(true);
					tokenizer.next();
					Object resultInvoke =  method.invoke(parser);
					if (resultInvoke instanceof Pair)
					{
						((Pair) resultInvoke).accept(new TypeCheck());
						resultCall =((Pair) resultInvoke).accept(new Eval()).toString();
					}
					else
						if (resultInvoke instanceof Length)
						{
							((Length) resultInvoke).accept(new TypeCheck());
							resultCall =((Length) resultInvoke).accept(new Eval()).toString();
						}
						else
							if (resultInvoke instanceof Fst)
							{
								((Fst) resultInvoke).accept(new TypeCheck());
								resultCall = ((Fst) resultInvoke).accept(new Eval()).toString();
							}
							else
							{
								((Snd) resultInvoke).accept(new TypeCheck());
								resultCall = ((Snd) resultInvoke).accept(new Eval()).toString();
							}
							
					assertThat(resultCall, is(resultExpected));
				}catch(Exception e)
				{
					if(e.getClass().equals(TypecheckerException.class))
							fail(e.getMessage());
						else
							fail(e.getCause().getMessage());
				}		
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@ParameterizedTest
	@CsvSource
	({ 
		"'[5] @ [5]','[5, 5]'",
		"'[[5]] @ [[5]]','[[5], [5]]'",
		"'[pair(5,[5])] @ [pair(5,[5])]','[(5,[5]), (5,[5])]'",
		"'[true] @ [false]','[true, false]'"
	})
	public void TestConCatEvalRight(String input, String resultExpected) 
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			
			String resultCall = null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseConCat", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				ConCat resultInvoke = (ConCat) method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultCall =resultInvoke.accept(new Eval()).toString();
				assertThat(resultCall, is(resultExpected));
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@ParameterizedTest
	@CsvSource
	({ 
		"'5 + 5','10'",
		"'5 - 5','0'",
		"'5 - - 5','10'",
		"'5 + - 5','0'"
	})
	public void TestAddOrSubEvalRight(String input, String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			String resultCall = null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseAddOrSub", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Sub)
				{
					((Sub)resultInvoke).accept(new TypeCheck()).toString();
					resultCall =((Sub)resultInvoke).accept(new Eval()).toString();
				}
				else
				{
					((Add)resultInvoke).accept(new TypeCheck()).toString();
					resultCall =((Add)resultInvoke).accept(new Eval()).toString();
				}
			
				assertThat(resultCall, is(resultExpected));
				
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
		
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	
	@ParameterizedTest
	@CsvSource
	({ 
		"'5 * 5','25'",
		"'5 / 10','0'",
		"'5 * 10 / 5','10'"
	})
	public void TestTimesOrDivEvalRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			
			String resultCall = null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseTimesOrDiv", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Div)
				{
					((Div)resultInvoke).accept(new TypeCheck()).toString();
					resultCall =((Div)resultInvoke).accept(new Eval()).toString();
				}
				else
				{
					((Mul)resultInvoke).accept(new TypeCheck()).toString();
					resultCall =((Mul)resultInvoke).accept(new Eval()).toString();
				}
			
				assertThat(resultCall, is(resultExpected));
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
		
	@ParameterizedTest
	@CsvSource
	({ 
		"'if (3<05) {	var x = 5;	print x}else{	print 15}','5'",
		"'if (true) {	print 5}','5'",
		"'if (5<07){	print  10}','10'",
		"'if (5<1){	print  10}',''"
	})
	public void TestIfEvalRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseIfStmt", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				IfStmt resultInvoke =(IfStmt)  method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultInvoke.accept(new Eval());
				String resutlString =resultCall.toString();
				if(resutlString.length()>0)
					resutlString = resutlString.substring(0, resutlString.length()-2);
				assertThat(resutlString, is(resultExpected));
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
	}
	
	@ParameterizedTest
	@CsvSource
	({ 
		"'var i = 0;while (i<10){print i ; var qwe = 3 + i; print qwe; i=i+1}',"+
				"'0 3 1 4 2 5 3 6 4 7 5 8 6 9 7 10 8 11 9 12'",
		"'var x =10;while (x<10){print x ; var qwe = 3 + x; print qwe; x=x+1}',''"
	})
	public void TestWhileEvalRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseProg", NoParams);
				Prog resultInvoke =(Prog)  method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultInvoke.accept(new Eval());
				String resultString = resultCall.toString().replace("\r\n", " ");
				if(resultString.length()>0)
					resultString = resultString.substring(0, resultString.length()-1);
				assertThat(resultString, is(resultExpected));
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
	}
	
	@ParameterizedTest
	@CsvSource
	({
		"'switch(1){case 1{print 5 break}case 2{print 6 break}}','5'",
		"'switch(10){case 1{print 5 break}case 2{print 6 break}}',''",
		"'switch(-10){case 1{print 5 break}case 2{print 7 break}case -10{print 6 break}}','6'",
		"switch(1+5){case 5+1{print 5 break}case 2+5{print 5 break}},'5'"
		
	})
	public void TestSwitchEvalRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseSwitchStmt", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				SwitchStmt resultInvoke =(SwitchStmt) method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultInvoke.accept(new Eval());
				String resultString = resultCall.toString().replace("\r\n", " ");
				if(resultString.length()!=0)
					resultString = resultString.substring(0, resultString.length()-1);
				assertThat(resultString, is(resultExpected));
			}catch(Exception e)
			{
				e.printStackTrace();
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
	}
	
	@ParameterizedTest
	@CsvSource
	({ 
		"'switch(1+5){case 5+1{print 5 break}case 1+5{print 5 break}}'"
	})
	public void TestSwitchEvalWrong_ThrowsExeption(String input)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseSwitchStmt", NoParams);
				method.setAccessible(true);
				tokenizer.next();
				SwitchStmt resultInvoke =(SwitchStmt) method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultInvoke.accept(new Eval());
				fail("recognised ->"+input);
			}catch(Exception e)
			{
				if( !e.getClass().equals(TypecheckerException.class) &&
						!e.getClass().equals(EvaluatorException.class) &&
						!e.getCause().getClass().equals(ParserException.class) &&
						!e.getCause().getClass().equals(ScannerException.class) &&
						!e.getCause().getClass().equals(IOException.class))
								fail(e.getCause().getMessage());
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
	}
	
	@ParameterizedTest
	@CsvSource
	({ 
		"'var i = 0;do{print i ; var qwe = 3 + i; print qwe; i=i+1}while(i<10)',"+
				"'0 3 1 4 2 5 3 6 4 7 5 8 6 9 7 10 8 11 9 12'"
	})
	public void TestDoWhileEvalRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseProg", NoParams);
				Prog resultInvoke =(Prog)  method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
				resultInvoke.accept(new Eval());
				String resultString = resultCall.toString().replace("\r\n", " ");
				resultString = resultString.substring(0, resultString.length()-1);
				assertThat(resultString, is(resultExpected));
			}catch(Exception e)
			{
				if(e.getClass().equals(TypecheckerException.class))
					fail(e.getMessage());
				else
					fail(e.getCause().getMessage());
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
	}

}
