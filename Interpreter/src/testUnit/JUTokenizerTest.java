package testUnit;

import static org.junit.Assert.*;
import static _2_TokenType.TokenType.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import _2_Tokenizer.StreamTokenizer;
import _2_Tokenizer.Tokenizer;

public class JUTokenizerTest {

	@ParameterizedTest
	@ValueSource(strings = {
			"001",
			"010101",
			"080808",
			"0745628"
	})
	public void JUTokenizerTest_NUM(String input) 
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			tokenizer.next();
			assertThat(tokenizer.tokenType(), is(NUM));
		} catch (Exception e) {
			fail("Unexpected error. " + e.getMessage());
		}
	}
	
	@CsvSource
	({ 
		"'00','0'",
		"'01','1'",
		"'02','2'",
		"'03','3'",
		"'04','4'",
		"'05','5'",
		"'06','6'",
		"'07','7'",
		"'0','0'",
		"'1','1'",
		"'2','2'",
		"'3','3'",
		"'4','4'",
		"'5','5'",
		"'6','6'",
		"'7','7'",
		"'8','8'",
		"'9','9'",
		"'07564','3956'",
		"'026514','11596'",
		"'05555','2925'",
		"'0777','511'"
	})
	public void JUTokenizerTest_NUM(String input, String resultExpected)
	{
		try(Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8.name())))) ) 
		{
			tokenizer.next();
			assertThat(tokenizer.intValue(), is(Integer.parseInt(resultExpected)));	
		} catch (Exception e) {
			fail("Unexpected error. " + e.getMessage());
		}
	}
	

}
