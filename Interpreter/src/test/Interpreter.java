package test;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.FileReader;
import java.io.InputStreamReader;

import _1_StreamScanner.ScannerException;
import _2_StreamParser.Parser;
import _2_StreamParser.StreamParser;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;
import _3_Ast.Prog;
import _4_Visitors.evaluation.Eval;
import _4_Visitors.typechecking.TypeCheck;

public class Interpreter {
	public static void main(String[] args) {
		try (Tokenizer tokenizer = new StreamTokenizer(
				args.length > 0 ? new FileReader(args[0]) : new InputStreamReader(System.in))) {
			Parser parser = new StreamParser(tokenizer);
			Prog prog = parser.parseProg();
			out.println("Program correctly parsed: " + prog);
			prog.accept(new TypeCheck());
			out.println("Program statically correct");
			prog.accept(new Eval());
		} catch (ScannerException e) {
			String skipped = e.getSkipped();
			if (skipped != null)
				err.println(e.getMessage() + e.getSkipped());
			else
				err.println(e.getMessage());
		} catch (Exception e) {
			err.println(e.getMessage());
		} catch (Throwable e) {
			err.println("Unexpected error. " + e.getMessage());
		}
	}
}
