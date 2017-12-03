package test;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.in;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.Parser;
import _2_StreamParser.StreamParser;
import _2_StreamParser.ParserException;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _2_Tokenizer.TokenizerException;
import _3_Ast.Prog;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.evaluation.EvaluatorException;
import _4_Visitors.typechecking.TypeCheck;
import _4_Visitors.typechecking.TypecheckerException;

public class Main {
	public static void main(String[] args) {
		int stdIn=0;
		int stdOut=0;
		
		switch (args.length) {
			case 0:
				break;
			case 1:
				if (args[0].equals("-o"))
					throw new IllegalArgumentException("(-o NomeFile)? (NomeFile)?");
				stdIn=0;
				break;
			case 3:
				stdIn=2;
			case 2:
				if (!args[0].equals("-o"))
					throw new IllegalArgumentException("(-o NomeFile)? (NomeFile)?");
				stdOut=1;
				break;
					
			default:
				throw new IllegalArgumentException("(-o NomeFile)? (NomeFile)?");

	}
		try (Tokenizer tokenizer = new StreamTokenizer(
				args.length %2 != 0 ?  new FileReader(args[stdIn]) :  new InputStreamReader(in)); 
			PrintStream std = (args.length > 1 ? new PrintStream(args[stdOut]) : new PrintStream(out))) 
		{
			System.setOut(std);
			
			Parser parser = new StreamParser(tokenizer);
			Prog prog = parser.parseProg();
			//out.println("Program correctly parsed: " + prog);
			prog.accept(new TypeCheck());
			//out.println("Program statically correct");
			prog.accept(new Eval());
		} catch (ScannerException e) {
			String skipped = e.getSkipped();
			if (skipped != null)
				err.println(e.getMessage() + e.getSkipped());
			else
				err.println(e.getMessage());
		}catch (TokenizerException e) {
			err.println( "TokenizerException "+e.getMessage());
		} 
		catch (ParserException e) {
			err.println("ParserException " +e.getMessage());
		} 
		catch (TypecheckerException e) {
			err.println("TypecheckerException " +e.getMessage());
		} 
		catch (EvaluatorException e) {
			err.println("EvaluatorException " +e.getMessage());
		} 
		catch (FileNotFoundException e) {
			err.println("FileNotFoundException " +e.getMessage());
		}
		catch (Exception e) {
			err.println("OtherException "+e.getMessage());
		} catch (Throwable e) {
			err.println("Unexpected error. " + e.getMessage());
		}
	}
}
