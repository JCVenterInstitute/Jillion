package org.jcvi.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public abstract class AbstractFileReader implements Closeable {

	protected BufferedReader reader;
	
	public AbstractFileReader() {
		super();
	}

	public AbstractFileReader(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AbstractFileReader(String fileName) {
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public AbstractFileReader(URL url) {
		this(url.getFile());
	}

	public AbstractFileReader(InputStream instream) {
		super();
		reader = new BufferedReader(new InputStreamReader(instream));
	}

	public BufferedReader getBufferedReader() {
		return reader;
	}

	public void setBufferedReader(BufferedReader reader) {
		this.reader = reader;
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
