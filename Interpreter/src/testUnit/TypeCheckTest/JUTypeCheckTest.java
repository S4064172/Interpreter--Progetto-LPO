package testUnit.TypeCheckTest;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
	
	@ParameterizedTest
	@ValueSource(strings = {
			"5 * 5",
			"5 / 10",
			"5 * 10 / 5"
	})
	public void TestTimesOrDivCheckTypeRight(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			String resutlCall=null;
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseTimesOrDiv", null);
				method.setAccessible(true);
				tokenizer.next();
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Div)
					resutlCall=((Div)resultInvoke).accept(new TypeCheck()).toString();
				else
					resutlCall=((Mul)resultInvoke).accept(new TypeCheck()).toString();
				assertThat(resutlCall, is("INT"));
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
			"5 * [5]",
			"5 / [5]",
			"true * 5"
	})
	public void TestTimesOrDivCheckTypeWrong_ThrowExecption(String input) 
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseTimesOrDiv", null);
			method.setAccessible(true);
			tokenizer.next();
			String resultCall;
			try
			{
				Object resultInvoke =  method.invoke(parser);
				if(resultInvoke instanceof Div)
					resultCall=((Div)resultInvoke).accept(new TypeCheck()).toString();
				else
					resultCall=((Mul)resultInvoke).accept(new TypeCheck()).toString();
				fail("recognised--> "+input);
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
	@ValueSource(strings = {
			"while (5<10){print 5 }",
			"while (true){print true}"
	})
	public void TestWhileCheckTypeRight(String input)
	{
		
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseWhileStmt", null);
				method.setAccessible(true);
				tokenizer.next();
				WhileStmt resultInvoke =  (WhileStmt)method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
			} catch (Exception e) {
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
			"while([5]){print 5}", 
			"while(50){print 5}",
			"while(pair(5,5)){print 5}"
	})
	public void TestWhileCheckTypeWrong_ThrowExecption(String input) 
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseWhileStmt", null);
				method.setAccessible(true);
				tokenizer.next();
				WhileStmt resultInvoke =  (WhileStmt)method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
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
	@ValueSource(strings = {
			"if (3<05){ var x = 5;	print x }else{ print 15}",
			"if (true){ print 5 }", 
			"if (5<07){	print  10 }"
	})
	public void TestIfCheckTypeRight(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseIfStmt", null);
				method.setAccessible(true);
				tokenizer.next();
				IfStmt resultInvoke =  (IfStmt)method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
			} catch (Exception e) {
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
			"if([5]){print 5}", 
			"if(50){print 5}",
			"if(pair(5,5)){print 5}"
	})
	public void TestIfCheckTypeWrong_ThrowExecption(String input) 
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseIfStmt", null);
				method.setAccessible(true);
				tokenizer.next();
				IfStmt resultInvoke =  (IfStmt)method.invoke(parser);
				resultInvoke.accept(new TypeCheck());
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
	
}
