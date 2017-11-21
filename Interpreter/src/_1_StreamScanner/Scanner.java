package _1_StreamScanner;

import java.io.IOException;

public interface Scanner extends AutoCloseable {
	void next() throws IOException, ScannerException;

	boolean hasNext() throws IOException;

	String group();

	String group(int group);

	public void close() throws IOException;
}