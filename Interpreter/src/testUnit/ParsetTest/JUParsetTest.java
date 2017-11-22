package testUnit.ParsetTest;

import static _2_TokenType.TokenType.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

import javax.naming.ldap.ManageReferralControl;

import org.junit.Test;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.ParserException;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;

public class JUParsetTest {

	@Test
	public void testNewAtomRight()
	{
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/ParsetTest/testNewAtomResult.txt")))
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
		String result=null;
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/ParsetTest/testNewAtomRight.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				t.next();
				try
				{
					result= method.invoke(p).toString();
					assertTrue(result.equals(relustList.get(i)));
					i++;
				}catch (Exception e) {
					fail(e.getCause().getMessage());
				} 
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}
	
	@Test
	public void testNewAtomWrong()
	{
		try(Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/ParsetTest/testNewAtomWrong.txt") ))
		{
			while (t.hasNext()) 
			{
				StreamParser p = new StreamParser(t);
				Method method = p.getClass().getDeclaredMethod("parseAtom", null);
				method.setAccessible(true);
				t.next();
				try
				{
					String res =method.invoke(p).toString();
					fail("correst -->"+res);
				}catch (Exception e) 
				{
				
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
						fail("found "+e.getCause().getClass()+" expeted "+ParserException.class+" OR" 
								+ScannerException.class+"OR"
								+ IOException.class);
					
				} 
			}
		}
		catch (Exception e) {
			fail(e.getMessage());
		} 
		
	}

}
