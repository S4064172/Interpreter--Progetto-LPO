package testUnit.EvalTest;


import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
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
import _3_Ast.IntLiteral;
import _3_Ast.Length;
import _3_Ast.Mul;
import _3_Ast.Pair;
import _3_Ast.Prog;
import _3_Ast.ProgClass;
import _3_Ast.Snd;
import _3_Ast.Sub;
import _3_Ast.WhileStmt;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class JUEvalTest {

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
					Method method = parser.getClass().getDeclaredMethod("parseAtom", null);
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
				Method method = parser.getClass().getDeclaredMethod("parseConCat", null);
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
				Method method = parser.getClass().getDeclaredMethod("parseAddOrSub", null);
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
	
	@Test
	public void TestTimesOrDivEvalRight() 
	{
		
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestTimesOrDivEvalResult.txt")))
		{
			while (s.hasNext())
			{
				relustList.add(s.nextLine());
			}
		s.close();
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
		int i = 0;
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/EvalTest/TestTimesOrDivEvalRight.txt") ))
		{
			while (t.hasNext()) 
			{
				String result = null;
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseTimesOrDiv", null);
					method.setAccessible(true);
					t.next();
					Object pp =  method.invoke(p);
					if(pp instanceof Div)
					{
						((Div)pp).accept(new TypeCheck()).toString();
						result =((Div)pp).accept(new Eval()).toString();
					}
					else
					{
						((Mul)pp).accept(new TypeCheck()).toString();
						result =((Mul)pp).accept(new Eval()).toString();
					}
				
				
					assertTrue(result.equals(relustList.get(i)));
				}catch(Throwable e)
				{
					if(result!=null)
						fail("found "+ result + " expeted "+relustList.get(i));
					else
						if(e.getClass().equals(TypecheckerException.class))
							fail(e.getMessage());
						else
							fail(e.getCause().getMessage());
				}
				i++;
				result=null;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
		
	@Test
	public void TestIfEvalRight() 
	{
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/EvalTest/TestIfEvalRight.txt") ))
		{
			System.setOut(new PrintStream("src/testUnit/EvalTest/TestIfResult.txt"));
			while (t.hasNext()) 
			{
				try
				{
					
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseIfStmt", null);
					method.setAccessible(true);
					t.next();
					IfStmt pp =(IfStmt)  method.invoke(p);
					pp.accept(new TypeCheck());
					pp.accept(new Eval());
					
				}catch(Throwable e)
				{
					if(e.getClass().equals(TypecheckerException.class))
						fail(e.getMessage());
					else
						fail(e.getCause().getMessage());
				}
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		System.setOut(System.out);
		
		
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestIfResult.txt")))
		{
			while (s.hasNext())
			{
				relustList.add(s.nextLine());
			}
		s.close();
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
		
		ArrayList<String> outPutList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestIfOutPut.txt")))
		{
			while (s.hasNext())
			{
				outPutList.add(s.nextLine());
			}
		s.close();
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
		
		int index=0;
		for (String result : relustList) 
		{	
			if(outPutList.size()<index)
				fail("found "+ "nothing" + " expeted "+result);
			
			if(!result.equals(outPutList.get(index)))
				fail("found "+ outPutList.get(index) + " expeted "+result);		
			index++;
		}
	}
}
