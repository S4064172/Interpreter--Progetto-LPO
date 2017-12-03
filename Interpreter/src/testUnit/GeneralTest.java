package testUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _3_Ast.Prog;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

class GeneralTest {

	@ParameterizedTest
	@CsvSource
	({ 
//ConCatTest
		"'var varConcat = [5] @ [5];\r\n" + 
		"print varConcat;\r\n" + 
		"print [5] @ [6];\r\n" + 
		"print [7,9,8,7,8,9]@[4,5,6,5,4,5,5,6,04,04,05,06,04,07,01111]@[10];\r\n" + 
		"print length ([5]@[6])','[5, 5] [5, 6] [7, 9, 8, 7, 8, 9, 4, 5, 6, 5, 4, 5, 5, 6, 4, 4, 5, 6, 4, 7, 585, 10] 2'",
//LengthTest
		"'var xx = length [5,2,3];\r\n" + 
		"print xx;\r\n" + 
		"print length [5,5,5,3] + 1','3 5'",
//PairTest , FsdTest , SndTest
		"'var pair1 = pair([5],[5]);\r\n" + 
		"print pair1;\r\n" + 
		"var fst1 = fst pair1;\r\n" + 
		"print fst1;\r\n" + 
		"var snd1 = snd pair1;\r\n" + 
		"print snd1;\r\n" + 
		"\r\n" + 
		"var pair2 = pair(01,2);\r\n" + 
		"print pair2;\r\n" + 
		"var fst2 = fst pair2;\r\n" + 
		"print fst2;\r\n" + 
		"var snd2 = snd pair2;\r\n" + 
		"print snd2;\r\n" + 
		"\r\n" + 
		"var pair3 = pair(true,false);\r\n" + 
		"print pair3;\r\n" + 
		"var fst3 = fst pair3;\r\n" + 
		"print fst3;\r\n" + 
		"var snd3 = snd pair3;\r\n" + 
		"print snd3;\r\n" + 
		"\r\n" + 
		"var pair4 = pair(pair(5,5),pair([3],[5]));\r\n" + 
		"print pair4;\r\n" + 
		"print fst pair4;\r\n" + 
		"print snd pair4;\r\n" + 
		"\r\n" + 
		"var pair5 = pair (3,[5]);\r\n" + 
		"print pair5;\r\n" + 
		"\r\n" + 
		"var pair6 = pair (pair(05,5),5);\r\n" + 
		"print pair6;\r\n" + 
		"\r\n" + 
		"var pair7 = pair (pair(3,(true&&!false)&&(5<10)),pair([5]@[5,8,6,87,4,5,6,2,1,4,05,04,07745],true));\r\n" + 
		"print pair7;\r\n" + 
		"\r\n" + 
		"print pair(5,5)==pair(5,5)','([5],[5]) [5] [5] (1,2) 1 2 (true,false) true false ((5,5),([3],[5])) (5,5) ([3],[5]) (3,[5]) ((5,5),5) ((3,true),([5, 5, 8, 6, 87, 4, 5, 6, 2, 1, 4, 5, 4, 4069],true)) true'",		
//OpTest
		"'var x= 045;\r\n" + 
		"x = 552626/445425622;\r\n" + 
		"print x;\r\n" + 
		"print 10/2;\r\n" + 
		"print 10+5/5-1;\r\n" + 
		"print 10+5/5-1==10;\r\n" + 
		"var xx = 7 - 5;\r\n" + 
		"print xx;\r\n" + 
		"print 10-5+587-5422*0;\r\n" + 
		"print 6+054;\r\n" + 
		"print 10*5/10','0 5 10 true 2 592 50 5'"
		})
	public void GeneralTestInterpreterRight(String input, String resultExpected)
	{		
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			ByteArrayOutputStream resultCall = new ByteArrayOutputStream();
			System.setOut(new PrintStream(resultCall));
			
			try
			{
				StreamParser parser = new StreamParser(tokenizer);
				Method method = parser.getClass().getDeclaredMethod("parseProg", null);
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
