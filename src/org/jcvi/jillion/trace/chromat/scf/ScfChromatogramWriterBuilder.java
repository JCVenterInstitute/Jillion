/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.chromat.scf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodecs;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter;

public final class ScfChromatogramWriterBuilder implements Builder<ChromatogramWriter>{
	private final File scfFile; 
	private final OutputStream out;
	private SCFCodec codec = SCFCodecs.VERSION_3;
	
	public ScfChromatogramWriterBuilder(File scfFile){
		if(scfFile ==null){
			throw new NullPointerException("output file can not be null");
		}
		this.scfFile = scfFile;
		this.out =null;
	}
	
	public ScfChromatogramWriterBuilder(OutputStream out){
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		this.scfFile = null;
		this.out =out;
	}
	
	public ScfChromatogramWriterBuilder useVersion2Encoding(){
		this.codec = SCFCodecs.VERSION_2;
		return this;
	}

	@Override
	public ChromatogramWriter build() {
		try {
			if(out==null){
				return new ScfChromatogramWriterImpl(scfFile, codec);
			}
			return new ScfChromatogramWriterImpl(out,codec);
		} catch (IOException e) {
			throw new IllegalStateException("error creating scf output file",e);
		}
	}
	
	private static class ScfChromatogramWriterImpl implements ChromatogramWriter{
		private final OutputStream out;
		private volatile boolean closed=false;
		private final SCFCodec codec;
		public ScfChromatogramWriterImpl(File scfFile, SCFCodec codec) throws IOException{
			IOUtil.mkdirs(scfFile.getParentFile());
			this.codec= codec;
			out = new BufferedOutputStream(new FileOutputStream(scfFile));
		}
		public ScfChromatogramWriterImpl(OutputStream out, SCFCodec codec) throws IOException{
			this.out = out;
			this.codec = codec;
		}
		@Override
		public void close() throws IOException {
			out.close();
			closed=true;
			
		}
		@Override
		public void write(Chromatogram c) throws IOException {
			if(closed){
				throw new IllegalStateException("can only write one scf");
			}
			codec.write(c, out);
			close();
		}
		
		
	}
}
