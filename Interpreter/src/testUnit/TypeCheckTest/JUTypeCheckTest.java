package testUnit.TypeCheckTest;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static org.hamcrest.CoreMatchers.*;
import javax.sound.midi.SysexMessage;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.ParserException;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _2_Tokenizer.TokenizerException;
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
import _3_Ast.WhileStmt;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class JUTypeCheckTest {

	@ParameterizedTest
	@CsvSource
	({ 
		"'length [5,2,3]' , INT",
		"'snd pair(pair(5,5),pair([3],[5]))' , '(INT LIST,INT LIST)'", 
		"'snd pair([5],[5])' , 'INT LIST'",
		"'snd pair (5,5)' , 'INT'",
		"'fst pair(pair(5,5),pair([3],[5]))' , '(INT,INT)'",
		"'fst pair([5],[5])' , 'INT LIST'",
		"'fst pair (5,5)' , 'INT'",
		"'pair(pair(5,5),pair([3],[5]))' , '((INT,INT),(INT LIST,INT LIST))'",
		"'pair([5],[5])' , '(INT LIST,INT LIST)'",
		"'pair (5,5)' , '(INT,INT)'"
	})
	public void TestNewAtomCheckTypeRight(String input, String resultExpected)
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader( new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name()))) ))
		{
			String resultCall=null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				tokenizer.next();
				Object resultInvoke =  method.invoke(parser);
				if (resultInvoke instanceof Pair)
					resultCall=((Pair) resultInvoke).accept(new TypeCheck()).toString();
				else
					if (resultInvoke instanceof Length)
						resultCall=((Length) resultInvoke).accept(new TypeCheck()).toString();
					else
						if (resultInvoke instanceof Fst)
							resultCall=((Fst) resultInvoke).accept(new TypeCheck()).toString();
						else
							resultCall=((Snd) resultInvoke).accept(new TypeCheck()).toString();
				
				assertThat(resultCall, is(resultExpected));
			}catch(IOException e)
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
	@ValueSource(strings = {
			"length [[pair(5,6)],[5,5,5]]",
			"length [[pair(5,6)],5]",
			"length [[5],5]",
			"length 5",
			"fst 5",
			"fst (5)",
			"snd 5",
			"fst [5,5]",
			"snd ([5,5])"
			
	})
	public void TestNewAtomCheckTypeWrong_ThrowExecption(String input) 
	{
		try (Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader( new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name()))) ))
		{
			
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				tokenizer.next();
				
				try
				{
					Object resultInvoke =  method.invoke(parser);
					if (resultInvoke instanceof Pair)
						((Pair) resultInvoke).accept(new TypeCheck());
					else
						if (resultInvoke instanceof Length)
							((Length) resultInvoke).accept(new TypeCheck());
						else
							if (resultInvoke instanceof Fst)
								((Fst) resultInvoke).accept(new TypeCheck());
							else
								((Snd) resultInvoke).accept(new TypeCheck());
								
					fail("recognised : "+input);
				}catch(Exception e )
				{
					if( !e.getClass().equals(TypecheckerException.class) &&
						!e.getCause().getClass().equals(ParserException.class) &&
						!e.getCause().getClass().equals(ScannerException.class) &&
						!e.getCause().getClass().equals(IOException.class))
								fail(e.getCause().getMessage());
				}
				
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}

	@ParameterizedTest
	@CsvSource
	({ 
		"'[5] @ [5]' , 	INT LIST",
		"'[[5]] @ [[5]]' , 'INT LIST LIST'", 
		"'[pair(5,[5])] @ [pair(5,[5])]' , '(INT,INT LIST) LIST'",
		"'[true] @ [false]' , 'BOOL LIST'"
	})
	public void TestConCatCheckTypeRight(String input, String resultExpected)
	{	
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			String resultCall=null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseConCat", null);
				method.setAccessible(true);
				tokenizer.next();
				ConCat resultInvoke =  (ConCat)method.invoke(parser);
				resultCall=resultInvoke.accept(new TypeCheck()).toString();
				assertThat(resultCall, is(resultExpected));
			}catch(Throwable e)
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
	@ValueSource(strings = {
			"[5] @ [[5]]",
			"[pair(5,5)] @ [true]",
			"[5] @ [true]"				
	})
	public void TestConCatCheckTypeWrong_ThrowExecption(String input) 
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseConCat", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				ConCat pp =  (ConCat) method.invoke(parser);
				pp.accept(new TypeCheck());
				fail("recognised-->"+input);
			}catch(Exception e )
			{
				if(	!e.getClass().equals(TypecheckerException.class) &&
					!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))					
							fail(e.getCause().getMessage());
			}
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"5 + 5",
			"5 - 5",
			"5 - - 5",
			"5 + - 5"	
	})
	public void TestAddOrSubCheckTypeRight(String input)
	{

		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			try
			{
				String resultCall;
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseAddOrSub", null);
				method.setAccessible(true);
				tokenizer.next();
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Sub)
					resultCall=((Sub)resultInvoke).accept(new TypeCheck()).toString();
				else
					resultCall=((Add)resultInvoke).accept(new TypeCheck()).toString();
				assertThat(resultCall, is("INT"));
			}catch(Throwable e)
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

	@ParameterizedTest()
	@ValueSource(strings = {
			"[5] + [5]",
			"true + true",
			"pair(5,5) + true",
			"[5] - pair (5,5)",
			"[5] - [5]",
			"true - true",
			"pair(5,5) - true",
			"[5] + pair (5,5)"
	})
	public void TestAddOrSubCheckTypeWrong_ThrowExecption(String input) 
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseAddOrSub", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Sub)
					((Sub)resultInvoke).accept(new TypeCheck()).toString();
				else
					((Add)resultInvoke).accept(new TypeCheck()).toString();
				fail("recognised --> "+input);
			}catch(Exception e )
			{
				if( !e.getClass().equals(TypecheckerException.class) &&
					!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
						fail(e.getCause().getMessage());
			}
			
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
/*	
	@Test
	public void TestTimesOrDivCheckTypeRight()
	{
		
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestTimesOrDivCheckTypeRight.txt") ))
		{
			String result=null;
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseTimesOrDiv", null);
					method.setAccessible(true);
					t.next();
					Object pp =  method.invoke(p);
					if(pp instanceof Div)
						result=((Div)pp).accept(new TypeCheck()).toString();
					else
						result=((Mul)pp).accept(new TypeCheck()).toString();
				
					assertTrue(result.equals("INT"));
				}catch(Throwable e)
				{
					if(result!=null)
						fail("found "+ result + " expeted "+"INT");
					else
						if(e.getClass().equals(TypecheckerException.class))
							fail(e.getMessage());
						else
							fail(e.getCause().getMessage());
				}
				result=null;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@Test
	public void TestTimesOrDivCheckTypeWrong() 
	{
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestTimesOrDivCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseTimesOrDiv", null);
				method.setAccessible(true);
				t.next();
				String result;
				try
				{
					Object pp =  method.invoke(p);
					if(pp instanceof Div)
						result=((Div)pp).accept(new TypeCheck()).toString();
					else
						result=((Mul)pp).accept(new TypeCheck()).toString();
					fail("riconosciuto-->"+result);
				}catch(Exception e )
				{
					if(!e.getClass().equals(TypecheckerException.class))
						if(	e.getCause().getClass().equals(ParserException.class) ||
							e.getCause().getClass().equals(ScannerException.class) ||
							e.getCause().getClass().equals(IOException.class))
						{
								while(!t.tokenString().equals(";") && t.hasNext())
								{
									t.next();
								}
						}
						else
								fail(e.getCause().getMessage());
				}
				
			}
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@Test
	public void TestWhileCheckTypeRight()
	{
		
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestWhileCheckTypeRight.txt") ))
		{
			
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseWhileStmt", null);
					method.setAccessible(true);
					t.next();
					WhileStmt pp =  (WhileStmt)method.invoke(p);
					pp.accept(new TypeCheck());
				} catch (Exception e) {
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
	}

	@Test
	public void TestWhileCheckTypeWrong() 
	{
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestWhileCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseWhileStmt", null);
					method.setAccessible(true);
					t.next();
					WhileStmt pp =  (WhileStmt)method.invoke(p);
					pp.accept(new TypeCheck());
				}catch(Exception e )
				{
					if(!e.getClass().equals(TypecheckerException.class))
						if(	e.getCause().getClass().equals(ParserException.class) ||
							e.getCause().getClass().equals(ScannerException.class) ||
							e.getCause().getClass().equals(IOException.class))
						{
								while(!t.tokenString().equals(";") && t.hasNext())
								{
									t.next();
								}
						}
						else
								fail(e.getCause().getMessage());
				}
				
			}
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@Test
	public void TestIfCheckTypeRight()
	{
		
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestIfCheckTypeRight.txt") ))
		{
			
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseIfStmt", null);
					method.setAccessible(true);
					t.next();
					IfStmt pp =  (IfStmt)method.invoke(p);
					pp.accept(new TypeCheck());
				} catch (Exception e) {
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
	}


	public void TestIfCheckTypeWrong() 
	{
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestIfCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseIfStmt", null);
					method.setAccessible(true);
					t.next();
					IfStmt pp =  (IfStmt)method.invoke(p);
					pp.accept(new TypeCheck());
				}catch(Exception e )
				{
					if(!e.getClass().equals(TypecheckerException.class))
						if(	e.getCause().getClass().equals(ParserException.class) ||
							e.getCause().getClass().equals(ScannerException.class) ||
							e.getCause().getClass().equals(IOException.class))
						{
								while(!t.tokenString().equals(";") && t.hasNext())
								{
									t.next();
								}
						}
						else
								fail(e.getCause().getMessage());
				}
				
			}
		} catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	*/
}
