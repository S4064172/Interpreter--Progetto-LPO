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

import javax.sound.midi.SysexMessage;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

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
				try
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
	public void TestNewAtomCheckTypeWrong() 
	{
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

	@Test
	public void TestConCatCheckTypeRight()
	{
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/TypeCheck/TestConCatCheckTypeResult.txt")))
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
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestConCatCheckTypeRight.txt") ))
		{
			String result=null;
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseConCat", null);
					method.setAccessible(true);
					t.next();
					ConCat pp =  (ConCat)method.invoke(p);
					result=pp.accept(new TypeCheck()).toString();
				
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
	public void TestConCatCheckTypeWrong() 
	{
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestConCatCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseConCat", null);
				method.setAccessible(true);
				t.next();
				String result;
				try
				{
					ConCat pp =  (ConCat) method.invoke(p);
					result=pp.accept(new TypeCheck()).toString();
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
	public void TestAddOrSubCheckTypeRight()
	{
		
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestAddOrSubCheckTypeRight.txt") ))
		{
			String result=null;
			while (t.hasNext()) 
			{
				try
				{
					StreamParser p = new StreamParser(t);
					Method method = p.getClass().getDeclaredMethod("parseAddOrSub", null);
					method.setAccessible(true);
					t.next();
					Object pp =  method.invoke(p);
					if(pp instanceof Sub)
						result=((Sub)pp).accept(new TypeCheck()).toString();
					else
						result=((Add)pp).accept(new TypeCheck()).toString();
				
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
	public void TestAddOrSubCheckTypeWrong() 
	{
		
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TypeCheck/TestAddOrSubCheckTypeWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseAddOrSub", null);
				method.setAccessible(true);
				t.next();
				String result;
				try
				{
					Object pp =  method.invoke(p);
					if(pp instanceof Sub)
						result=((Sub)pp).accept(new TypeCheck()).toString();
					else
						result=((Add)pp).accept(new TypeCheck()).toString();
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
}
