package testUnit.TypeCheck;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.ParserException;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _2_Tokenizer.TokenizerException;
import _3_Ast.Fst;
import _3_Ast.Length;
import _3_Ast.Pair;
import _3_Ast.Prog;
import _3_Ast.Snd;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class JUTypeCheck {

	@Test
	public void TestNewAtomCheckTypeRight()
	{
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/TypeCheck/TestNewAtomCheckTypeResult.txt")))
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
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestNewAtomCheckTypeRight.txt") ))
		{
			String result=null;
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				t.next();
				Object pp =  method.invoke(p);
				if (pp instanceof Pair)
					result=((Pair) pp).accept(new TypeCheck()).toString();
				else
					if (pp instanceof Length)
						result=((Length) pp).accept(new TypeCheck()).toString();
					else
						if (pp instanceof Fst)
							result=((Fst) pp).accept(new TypeCheck()).toString();
						else
							if (pp instanceof Snd)
								result=((Snd) pp).accept(new TypeCheck()).toString();
							else
								fail("error type");
				try
				{
					assertTrue(result.equals(relustList.get(i)));
				}catch(Throwable e)
				{
					fail("found "+ result + " expeted "+relustList.get(i));
				}
				i++;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
	@Test
	public void TestNewAtomCheckTypeWrong() {
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestNewAtomCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				t.next();
				
				try
				{
					Object pp =  method.invoke(p);
					if (pp instanceof Pair)
						((Pair) pp).accept(new TypeCheck());
					else
						if (pp instanceof Length)
							((Length) pp).accept(new TypeCheck());
						else
							if (pp instanceof Fst)
								((Fst) pp).accept(new TypeCheck());
							else
								if (pp instanceof Snd)
									((Snd) pp).accept(new TypeCheck());
								else
									fail("error type");
					fail("riconosciuto");
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

}
