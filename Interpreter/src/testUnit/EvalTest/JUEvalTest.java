package testUnit.EvalTest;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.ParserException;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _3_Ast.Add;
import _3_Ast.ConCat;
import _3_Ast.Div;
import _3_Ast.Fst;
import _3_Ast.IntLiteral;
import _3_Ast.Length;
import _3_Ast.Mul;
import _3_Ast.Pair;
import _3_Ast.ProgClass;
import _3_Ast.Snd;
import _3_Ast.Sub;
import _3_Ast.WhileStmt;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class JUEvalTest {

	@Test
	public void TestNewAtomEvalRight() {
		
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestNewAtomEvalResult.txt")))
		{
			while (s.hasNext())
			{
				relustList.add(s.next());
			}
		s.close();
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
		int i = 0;
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/EvalTest/TestNewAtomEvalRight.txt") ))
		{
			while (t.hasNext()) 
			{
				String result = null;
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseAtom", null);
					method.setAccessible(true);
					t.next();
					Object pp =  method.invoke(p);
					if (pp instanceof Pair)
					{
						((Pair) pp).accept(new TypeCheck());
						result =((Pair) pp).accept(new Eval()).toString();
					}
					else
						if (pp instanceof Length)
						{
							((Length) pp).accept(new TypeCheck());
							result =((Length) pp).accept(new Eval()).toString();
						}
						else
							if (pp instanceof Fst)
							{
								((Fst) pp).accept(new TypeCheck());
								result = ((Fst) pp).accept(new Eval()).toString();
							}
							else
								if (pp instanceof Snd)
								{
									((Snd) pp).accept(new TypeCheck());
									result = ((Snd) pp).accept(new Eval()).toString();
								}
								else
									fail("error type");
					
					assertTrue(result.equals(relustList.get(i)));
				}catch(Throwable e)
				{
					if(result!=null)
						fail("found "+ result + " expeted "+relustList.get(i));
					else
						fail(e.getCause().getMessage());
				}
				i++;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@Test
	public void TestConCatEvalRight() 
	{
		
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestConCatEvalResult.txt")))
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
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/EvalTest/TestConCatEvalRight.txt") ))
		{
			while (t.hasNext()) 
			{
				String result = null;
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseConCat", null);
					method.setAccessible(true);
					t.next();
					ConCat pp = (ConCat) method.invoke(p);
					pp.accept(new TypeCheck());
					result =pp.accept(new Eval()).toString();
					assertTrue(result.equals(relustList.get(i)));
				}catch(Throwable e)
				{
					if(result!=null)
						fail("found "+ result + " expeted "+relustList.get(i));
					else
						fail(e.getCause().getMessage());
				}
				i++;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}

	@Test
	public void TestAddOrSubEvalRight() 
	{
		
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/EvalTest/TestAddOrSubEvalResult.txt")))
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
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/EvalTest/TestAddOrSubEvalRight.txt") ))
		{
			while (t.hasNext()) 
			{
				String result = null;
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseAddOrSub", null);
					method.setAccessible(true);
					t.next();
					Object pp =  method.invoke(p);
					if(pp instanceof Sub)
					{
						((Sub)pp).accept(new TypeCheck()).toString();
						result =((Sub)pp).accept(new Eval()).toString();
					}
					else
					{
						((Add)pp).accept(new TypeCheck()).toString();
						result =((Add)pp).accept(new Eval()).toString();
					}
				
				
					assertTrue(result.equals(relustList.get(i)));
				}catch(Throwable e)
				{
					if(result!=null)
						fail("found "+ result + " expeted "+relustList.get(i));
					else
						fail(e.getCause().getMessage());
				}
				i++;
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
						fail(e.getCause().getMessage());
				}
				i++;
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
	}
}
