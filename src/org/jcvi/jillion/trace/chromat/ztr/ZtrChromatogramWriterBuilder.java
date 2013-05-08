package org.jcvi.jillion.trace.chromat.ztr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.chromat.ztr.IOLibLikeZtrChromatogramWriter;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter;
/**
 * {@code ZtrChromatogramWriterBuilder} builds a {@link ChromatogramWriter}
 * implementation that performs the same encoding operations in the same order
 * as the Staden IO_Lib C module.  Experiments have shown that 
 *  this implementation
 * will encode valid ZTR files that have about a 5% larger file size
 * than the Staden package.
 * This is probably due to the standard Java implementation of zip does not allow
 * changing the "windowbits" size which could result in better
 * compression.  Adding a 3rd party library that allows more configuration
 * of encoding zipped data might enable smaller output file sizes
 * but that would cause an unnecessary dependency.
 * @author dkatzel
 * @see <a href ="http://staden.sourceforge.net/"> Staden Package Website</a>
 *
 */
public final class ZtrChromatogramWriterBuilder implements Builder<ChromatogramWriter>{

	private final File ztrFile; 
	private final OutputStream out;
	/**
	 * Create a new {@link ZtrChromatogramWriterBuilder}
	 * that will write the ztr file to the given
	 * output file.  The writer will overwrite
	 * any previously existing contents
	 * for the file.
	 * @param ztrFile the {@link File} to write
	 * the encoded ztr data into.
	 * @throws NullPointerException if ztrFile is null.
	 */
	public ZtrChromatogramWriterBuilder(File ztrFile){
		if(ztrFile ==null){
			throw new NullPointerException("output file can not be null");
		}
		this.ztrFile = ztrFile;
		this.out =null;
	}
	/**
	 * Create a new {@link ZtrChromatogramWriterBuilder}
	 * that will write the ztr file to the given
	 * output file.  The writer will overwrite
	 * any previously existing contents
	 * for the file.
	 * @param ztrFile the {@link File} to write
	 * the encoded ztr data into.
	 * @throws NullPointerException if ztrFile is null.
	 */
	public ZtrChromatogramWriterBuilder(OutputStream out){
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		this.ztrFile = null;
		this.out =out;
	}

	@Override
	public ChromatogramWriter build() {
		try {
			if(out==null){
				return new ZtrChromatogramWriterImpl(ztrFile);
			}
			return new ZtrChromatogramWriterImpl(out);
		} catch (IOException e) {
			throw new IllegalStateException("error creating ztr output file");
		}
	}
	
	private static class ZtrChromatogramWriterImpl implements ChromatogramWriter{
		private final OutputStream out;
		private volatile boolean closed=false;
		private final boolean ownOutputStream;
		
		public ZtrChromatogramWriterImpl(File ztrFile) throws IOException{
			IOUtil.mkdirs(ztrFile.getParentFile());
			out = new BufferedOutputStream(new FileOutputStream(ztrFile));
			ownOutputStream=true;
		}
		public ZtrChromatogramWriterImpl(OutputStream out) throws IOException{
			this.out = out;
			ownOutputStream=false;
		}
		@Override
		public void close() throws IOException {
			if(ownOutputStream){
				out.close();
			}
			closed=false;
			
		}
		@Override
		public void write(Chromatogram c) throws IOException {
			if(closed){
				throw new IllegalStateException("can only write one ztr");
			}
			IOLibLikeZtrChromatogramWriter.INSTANCE.write(c, out);
			close();
		}
		
		
	}
}
