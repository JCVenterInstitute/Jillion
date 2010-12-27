package org.jcvi.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;



/*
 * An {@code AbstractObjectIO} is used to read and write objects from and to a 
 * character stream.  The no argument constructor writes to System.out by default.
 * 
 * In the abstract implementation of the writer, we simply
 * iterate the objects and call the toString method on the object
 * to write to the output Writer. 
 * 
 *  To extend the AbstractObjectIO, requires writing the reader, which 
 *  should use the input BufferedReader and return an iterator of objects.
 *  of the appropriate type.
 *  
 *  @author naxelrod
 */


public abstract class AbstractObjectIO<T extends Object> implements ObjectIO<T> {

	protected BufferedReader input;
	protected PrintWriter output;
	
	public AbstractObjectIO() {
		super();
		// setInput(System.in);
		setOutput(System.out);
	}
	
	public AbstractObjectIO(BufferedReader input) {
		this();
		this.input = input;
	}
	public AbstractObjectIO(BufferedReader input, PrintWriter output) {
		this(input);
		this.output = output;
	}

	// Read and write from and to file
	public AbstractObjectIO(String file) {
		this();
		try {
			setInput(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public AbstractObjectIO(URL url) {
		this(url.getFile());
	}
	public AbstractObjectIO(String inFile, String outFile) {
		super();
		try {
			setInput(inFile);
			setOutput(outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public AbstractObjectIO(URL inUrl, URL outUrl) {
		this(inUrl.getFile(), outUrl.getFile());
	}
	
	public BufferedReader getInput() {
		return input;
	}
	public void setInput(String file) throws FileNotFoundException {
		this.input = new BufferedReader(new FileReader(file));
	}
	public void setInput(BufferedReader input) {
		this.input = input;
	}
	public void setInput(InputStream in) {
		this.input = new BufferedReader(new InputStreamReader(in));
	}
	
	public PrintWriter getOutput() {
		return output;
	}
	public void setOutput(PrintWriter output) {
		this.output = output;
	}
	public void setOutput(OutputStream output) {
		this.output = new PrintWriter(output, true);
	}
	public void setOutput(String output) throws IOException {
		setOutput(new PrintWriter(new FileWriter(output, true), true));
	}
	public void setOutput(URL output) throws IOException {
		setOutput(output.getFile());
	}

	@Override
	public abstract Iterator<T> iterator();
	
	@Override
	public boolean write(T sequence) {
		output.println(sequence.toString());
		return true;
	}
	
	@Override
	public int write(Iterable<T> sequences) {
		int i = 0;
		for (T sequence : sequences) {
			write(sequence);
			i++;
		}
		return i;
	}
	@Override
	public int write() {
		return write(this);
	}
	@Override
	public void close() {
		try {
			input.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
