package org.jcvi.jillion.sam.index;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public final class BamIndexWriter implements Closeable{

	private final OutputStream out;

	
	private BamIndexWriter(Builder builder) {
		this.out = builder.out;
	}


	@Override
	public void close() throws IOException {
		out.close();
		
	}
	
	
	public static class Builder{
		
		private final OutputStream out;

		public Builder(OutputStream out){
			if(out ==null){
				throw new NullPointerException("output stream can not be null");
			}
			this.out = out;
		}
		
		public BamIndexWriter build(){
			return new BamIndexWriter(this);
		}
	}
	
}
