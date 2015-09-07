/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.internal.sam.index.BamIndexer;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.jcvi.jillion.sam.index.BamIndex;
/**
 * {@code PresortedBamFileWriter} is a {@link SamWriter}
 * that writes out BAM files whose {@link SamRecord}s
 * are written out in the order that they are given.
 * Only use this class if the records are either not sorted
 * or if the records are already sorted when they are given to
 * this writer.  The {@link SamHeader#getSortOrder()}
 * is also written out to the BAM so make sure it is correct.
 * @author dkatzel
 *
 */
class PresortedBamFileWriter implements SamWriter{

	
	private final SamHeader header;
	
	private final File bamFile;
	private final OutputStream out;
	private final SamAttributeValidator attributeValidator;
	
	private final BamIndexer optionalIndexer;
	private boolean closed =false;
	
	public PresortedBamFileWriter(SamHeader header, File outputFile, BamIndexer optionalIndexer) throws IOException{
		this(header, outputFile, optionalIndexer, ReservedAttributeValidator.INSTANCE);
	}
	
	public PresortedBamFileWriter(SamHeader header, File outputFile, BamIndexer optionalIndexer, SamAttributeValidator attributeValidator) throws IOException {
		this.header = header;
		this.bamFile = outputFile;
		this.attributeValidator = attributeValidator;
		this.optionalIndexer = optionalIndexer;
		out = new BgzfOutputStream(bamFile,optionalIndexer);
		
		writeHeader();
	}

	private void writeHeader() throws IOException {
		StringBuilder headerAsStringBuilder = SamUtil.encodeHeader(header);
		int bytesOfReferences =4;
		for(SamReferenceSequence ref: header.getReferenceSequences()){
			bytesOfReferences += ref.getName().length() +1 +8;
		}
		ByteBuffer buf = ByteBuffer.allocate(8 + headerAsStringBuilder.length() + bytesOfReferences );
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		buf.put(SamUtil.getBamMagicNumber());
		buf.putInt(headerAsStringBuilder.length());
		char[] chars = new char[headerAsStringBuilder.length()];
		headerAsStringBuilder.getChars(0, chars.length, chars, 0);
		
		for(int i=0; i< chars.length; i++){
			buf.put((byte)chars[i]);
		}
		Collection<SamReferenceSequence> refs =header.getReferenceSequences();
		buf.putInt(refs.size());
		for(SamReferenceSequence ref : refs){
			//length of ref name + null terminal
			buf.putInt(ref.getName().length() +1);
			buf.put(ref.getName().getBytes(IOUtil.UTF_8));
			buf.put((byte)0);	//null terminated
			buf.putInt(ref.getLength());
		}
		buf.flip();
		out.write(buf.array());
	}

	@Override
	public void close() throws IOException {
		if(closed){
			//no-op so we don't write BAM and index twice
			return;
		}
		closed= true;
		out.close();
		if(optionalIndexer !=null){
			BamIndex bamIndex =optionalIndexer.createBamIndex();
			String baseName =FileUtil.getBaseName(bamFile);
			File indexFileOutFile = new File(bamFile.getParentFile(), baseName + ".bai");
			OutputStream indexOutStream =null;
			try{
				indexOutStream = new BufferedOutputStream(new FileOutputStream(indexFileOutFile));
				IndexUtil.writeIndex(indexOutStream, bamIndex);
			}finally{
				IOUtil.closeAndIgnoreErrors(indexOutStream);
			}
		}
	}

	@Override
	public void writeRecord(SamRecord record) throws IOException {
		try{
			header.validateRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		if(optionalIndexer !=null){
			optionalIndexer.setCurrentRecord(record);
		}
		
		SamUtil.writeAsBamRecord(out, header, record);
		
	}

}
