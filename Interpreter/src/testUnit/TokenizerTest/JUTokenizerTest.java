package testUnit.TokenizerTest;

import static java.lang.System.err;
import static java.lang.System.out;
import static org.junit.Assert.*;
import static _2_TokenType.TokenType.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;
import org.omg.PortableInterceptor.SUCCESSFUL;

import _1_StreamScanner.ScannerException;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;

public class JUTokenizerTest {

	@Test
	public void JUTokenizerTest_NUM() 
	{
		try (Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TokenizerTest/testNum.txt") )) 
		{
			while (t.hasNext()) 
			{
				
				try {
					t.next();
					if(t.tokenType()==EOF && t.tokenString()=="")
						return;
					assertTrue(t.tokenType()==NUM);
				} catch (ScannerException e) {
					String skipped = e.getSkipped();
					err.println(e.getMessage() + (skipped != null ? skipped : ""));
				} catch (Throwable e) {
					fail("found "+t.tokenType()+" expeted "+NUM);
				}
			}
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
	}
	
	@Test
	public void JUTokenizerTest_CONV() 
	{
		ArrayList<String> relustList = new ArrayList<String>();
		try(Scanner s = new Scanner(new File("src/testUnit/TokenizerTest/testConvResult.txt")))
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
		try (Tokenizer t = new StreamTokenizer(new FileReader("src/testUnit/TokenizerTest/testConv.txt") )) 
		{
			while (t.hasNext()) 
			{
				
				try {
					t.next();
					if(t.tokenType()==EOF && t.tokenString()=="")
						return;
					assertTrue(t.intValue()==Integer.parseInt(relustList.get(i)));
					i++;
				} catch (ScannerException e) {
					String skipped = e.getSkipped();
					err.println(e.getMessage() + (skipped != null ? skipped : ""));
				} catch (Throwable e) {
					fail("found "+t.intValue()+" expeted "+relustList.get(i));
				}
			}
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail("Unexpected error. " + e.getMessage());
		}
	}
	

}
