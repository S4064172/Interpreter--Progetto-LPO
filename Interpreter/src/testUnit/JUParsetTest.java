package testUnit;


import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
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

public class JUParsetTest {

	@ParameterizedTest
	@CsvSource
	({ 
		"'length [5,2,3]' , 'Length(ListLiteral(MoreExp(IntLiteral(5),MoreExp(IntLiteral(2),SingleExp(IntLiteral(3))))))'",
		"'length [[pair(5,6)],[5,5,5]]' , 'Length(ListLiteral(MoreExp(ListLiteral(SingleExp(Pair(IntLiteral(5),IntLiteral(6)))),SingleExp(ListLiteral(MoreExp(IntLiteral(5),MoreExp(IntLiteral(5),SingleExp(IntLiteral(5)))))))))'", 
		"'snd pair(pair(5,5),pair([3],[5]))' , 'Snd(Pair(Pair(IntLiteral(5),IntLiteral(5)),Pair(ListLiteral(SingleExp(IntLiteral(3))),ListLiteral(SingleExp(IntLiteral(5))))))'", 
		"'snd pair([5],[5])' , 'Snd(Pair(ListLiteral(SingleExp(IntLiteral(5))),ListLiteral(SingleExp(IntLiteral(5)))))'",
		"'snd pair (5,5)' , 'Snd(Pair(IntLiteral(5),IntLiteral(5)))'",
		"'fst pair(pair(5,5),pair([3],[5]))' , 'Fst(Pair(Pair(IntLiteral(5),IntLiteral(5)),Pair(ListLiteral(SingleExp(IntLiteral(3))),ListLiteral(SingleExp(IntLiteral(5))))))'",
		"'fst pair([5],[5])' , 'Fst(Pair(ListLiteral(SingleExp(IntLiteral(5))),ListLiteral(SingleExp(IntLiteral(5)))))'",
		"'fst pair (5,5)' , 'Fst(Pair(IntLiteral(5),IntLiteral(5)))'",
		"'pair(pair(5,5),pair([3],[5]))' , 'Pair(Pair(IntLiteral(5),IntLiteral(5)),Pair(ListLiteral(SingleExp(IntLiteral(3))),ListLiteral(SingleExp(IntLiteral(5)))))'",
		"'pair([5],[5])' , 'Pair(ListLiteral(SingleExp(IntLiteral(5))),ListLiteral(SingleExp(IntLiteral(5))))'",
		"'pair (5,5)' , 'Pair(IntLiteral(5),IntLiteral(5))'"
	})
	public void testNewAtomRight(String input, String resultExpected)
	{
		
		String resultCall=null;
																	//to convert string to inputStream
		try(Tokenizer tokenizer = new StreamTokenizer( new InputStreamReader( new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name()))) ))
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseAtom", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				resultCall= method.invoke(parser).toString();
				assertThat(resultCall, is(resultExpected));
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"length [5,5,5,]",
			"length []",
			"snd [5,]",
			"fst [5,5",
			"pair(5],[5])",
			"pair[5],[5])",
			"pair([5],[5)",
			"pair([5],[5]"
			
	})
	public void testNewAtomWrong_ThrowExecption(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseAtom", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(parser).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
			
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
					fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest
	@CsvSource
	({ 
		"'[1] @ [1]','ConCat(ListLiteral(SingleExp(IntLiteral(1))),ListLiteral(SingleExp(IntLiteral(1))))'",
		"'[pair(1,2)] @ [1]','ConCat(ListLiteral(SingleExp(Pair(IntLiteral(1),IntLiteral(2)))),ListLiteral(SingleExp(IntLiteral(1))))'",
		"'[true] @ [1]','ConCat(ListLiteral(SingleExp(BoolLiteral(true))),ListLiteral(SingleExp(IntLiteral(1))))'",
		"'[pair(1,2)] @ [true]','ConCat(ListLiteral(SingleExp(Pair(IntLiteral(1),IntLiteral(2)))),ListLiteral(SingleExp(BoolLiteral(true))))'"
	})
	public void testConCatRight(String input, String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseConCat", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				String resultInvoke = method.invoke(parser).toString();
				assertThat(resultInvoke, is(resultExpected));
				
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"[] @ [1]",
			"[] @",
			"[1] @ []",
			"@ []",
			"@"	
	})
	public void testConCatWrong_ThrowExecption(String input)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseConCat", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(parser);
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
						fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
		
	@ParameterizedTest()
	@CsvSource({
		"'5 + 4','Add(IntLiteral(5),IntLiteral(4))'",
		"'5 - 4','Sub(IntLiteral(5),IntLiteral(4))'",
		"'- 5 + - 5','Add(Sign(IntLiteral(5)),Sign(IntLiteral(5)))'",
		"'5 - - - 5','Sub(IntLiteral(5),Sign(Sign(IntLiteral(5))))'",
		"'- 5 - 5','Sub(Sign(IntLiteral(5)),IntLiteral(5))'"	
	})
	public void testAddOrSubRight(String input, String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name()))) ))
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseAddOrSub", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				String reresultInvoke= method.invoke(parser).toString();
				assertThat(reresultInvoke, is(resultExpected));
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"5 +",
			"+ 5",
			"5 -",
			"5-+6"	
	})
	public void testAddOrSubWrong_ThrowExecption(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser parset = new StreamParser(tokenizer);
			Method method = parset.getClass().getDeclaredMethod("parseAddOrSub", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(parset).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
						fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);	
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}

	@ParameterizedTest()
	@CsvSource({
		"'5 * 5','Mul(IntLiteral(5),IntLiteral(5))'",
		"'5 / 10','Div(IntLiteral(5),IntLiteral(10))'",
		"'5 * 10 / 5','Div(Mul(IntLiteral(5),IntLiteral(10)),IntLiteral(5))'"	
	})
	public void testTimesOrDivRight(String input, String resultExpected)
	{
		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader( new ByteArrayInputStream( input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseTimesOrDiv", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				String resultIvoke= method.invoke(parser).toString();
				assertThat(resultIvoke, is(resultExpected));
			
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"5 *",
			"* 5",
			"5 /",
			"/ 6"	
	})
	public void testTimesOrDivWrong_ThrowExecption(String input)
	{
		try(Tokenizer t = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser p = new StreamParser(t);
			Method method = p.getClass().getDeclaredMethod("parseTimesOrDiv", null);
			method.setAccessible(true);
			t.next();
			try
			{
				method.invoke(p).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
						fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}

	@ParameterizedTest()
	@CsvSource({
		"'while (i<10){print i ; var qwe = 3 + i}','WhileStmt(Lth(SimpleIdent(i),IntLiteral(10)),MoreStmt(PrintStmt(SimpleIdent(i)),SingleStmt(VarStmt(SimpleIdent(qwe),Add(IntLiteral(3),SimpleIdent(i))))))'",
		"'while (true){ print 5 }','WhileStmt(BoolLiteral(true),SingleStmt(PrintStmt(IntLiteral(5))))'",
		"'while ([5]){ print 5 }','WhileStmt(ListLiteral(SingleExp(IntLiteral(5))),SingleStmt(PrintStmt(IntLiteral(5))))'"
	})
	public void testWhileRight(String input, String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseWhileStmt", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				String result= method.invoke(parser).toString();
				assertThat(result, is(resultExpected));
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"while(5){}",
			"while(){}"
	})
	public void testWhileWrong_ThrowExecption(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseWhileStmt", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(parser).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
							fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);
			} 
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest()
	@CsvSource({
		"'if (3<05){ print x }else{ print 15 }','IfStmt(Lth(IntLiteral(3),IntLiteral(5)),SingleStmt(PrintStmt(SimpleIdent(x))),SingleStmt(PrintStmt(IntLiteral(15))))'",
		"'if (x<5){ print x }','IfStmt(Lth(SimpleIdent(x),IntLiteral(5)),SingleStmt(PrintStmt(SimpleIdent(x)))))'",
		"'if (true){ print x }','IfStmt(BoolLiteral(true),SingleStmt(PrintStmt(SimpleIdent(x)))))'",
		"'if (x==5){ print x }','IfStmt(Eq(SimpleIdent(x),IntLiteral(5)),SingleStmt(PrintStmt(SimpleIdent(x)))))'",
		"'if (5<07){ print  10 }','IfStmt(Lth(IntLiteral(5),IntLiteral(7)),SingleStmt(PrintStmt(IntLiteral(10)))))'"
	})
	public void testIfRight(String input,String resultExpected)
	{
		try(Tokenizer tokenizr = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizr);
			Method method = parser.getClass().getDeclaredMethod("parseIfStmt", null);
			method.setAccessible(true);
			tokenizr.next();
			try
			{
				String resultInvoke= method.invoke(parser).toString();
				assertThat(resultInvoke, is(resultExpected));
			}catch (Exception e) {
				fail(e.getCause().getMessage());
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"if(){}",
			"if(){print x}",
			"if(true){print x}else{}"
	})
	public void testIfWrong_ThrowExecption(String input)
	{
		/*try(Tokenizer tokenizer = new StreamTokenizer(new FileReader("src/testUnit/ParsetTest/testIfWrong.txt") ))
		{
			
			StreamParser p = new StreamParser(tokenizer);
			Method method = p.getClass().getDeclaredMethod("parseIfStmt", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(p).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
			
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class))
							fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
							+ScannerException.class+"OR"
							+ IOException.class);
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		*/
	}
	
	
	@ParameterizedTest()
	@CsvSource({
		"'switch(1){case 1{print 5 break}}','SwitchStmt(IntLiteral(1),CaseStmt(IntLiteral(1),SingleStmt(PrintStmt(IntLiteral(5)))))'",
		"'switch(1){case 1{print 5 break}case 2{print 6 break}}','SwitchStmt(IntLiteral(1),CaseStmt(IntLiteral(1),SingleStmt(PrintStmt(IntLiteral(5))))CaseStmt(IntLiteral(2),SingleStmt(PrintStmt(IntLiteral(6)))))'",
		"'switch(1){case 1{print 5 break}case 1{print 6 break}}','SwitchStmt(IntLiteral(1),CaseStmt(IntLiteral(1),SingleStmt(PrintStmt(IntLiteral(5))))CaseStmt(IntLiteral(1),SingleStmt(PrintStmt(IntLiteral(6)))))'"
	})
	public void testSwitchRight(String input,String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser parser = new StreamParser(tokenizer);
			Method method = parser.getClass().getDeclaredMethod("parseSwitchStmt", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				String resultInvoke= method.invoke(parser).toString();
				assertThat(resultInvoke, is(resultExpected));
			}catch (Exception e) {
				e.printStackTrace();
				fail(e.getCause().getMessage());
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
		
		
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"switch(){}",
			"switch(1){}",
			"switch(1){case [5]{}}",
			"switch(1){case 1 {print 5}}",
			"switch(1){case 1 print 5}"
	})
	public void testSwitchWrong_ThrowExecption(String input)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) )
		{
			StreamParser p = new StreamParser(tokenizer);
			Method method = p.getClass().getDeclaredMethod("parseSwitchStmt", null);
			method.setAccessible(true);
			tokenizer.next();
			try
			{
				method.invoke(p).toString();
				fail("recognised -->"+input);
			}catch (Exception e) 
			{
			
				if(	!e.getCause().getClass().equals(ParserException.class) &&
					!e.getCause().getClass().equals(ScannerException.class) &&
					!e.getCause().getClass().equals(IOException.class) &&
					!e.getCause().getClass().equals(IllegalStateException.class))
							fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR " 
							+ScannerException.class+" OR"
							+ IOException.class+ " OR "+IllegalStateException.class);	
			} 
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
}
