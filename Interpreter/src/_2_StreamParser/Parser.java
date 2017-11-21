package _2_StreamParser;

import java.io.IOException;

import _1_StreamScanner.ScannerException;
import _3_Ast.Prog;

public interface Parser {

	Prog parseProg() throws IOException, ScannerException, ParserException;

}