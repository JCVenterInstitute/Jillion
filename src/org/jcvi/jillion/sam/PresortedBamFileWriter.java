package org.jcvi.jillion.sam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.index.BamIndexer;
import org.jcvi.jillion.sam.index.IndexUtil;
import org.jcvi.jillion.sam.index.ReferenceIndex;
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
		//create parent dirs if needed
		IOUtil.mkdirs(bamFile.getParentFile());
		out = new BgzfOutputStream(new BufferedOutputStream(new FileOutputStream(bamFile)),optionalIndexer);
		
		writeHeader();
	}

	protected void writeHeader() throws IOException {
		StringBuilder headerAsStringBuilder = SamUtil.encodeHeader(header);
		int bytesOfReferences =4;
		for(ReferenceSequence ref: header.getReferenceSequences()){
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
		Collection<ReferenceSequence> refs =header.getReferenceSequences();
		buf.putInt(refs.size());
		for(ReferenceSequence ref : refs){
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
			List<ReferenceIndex> indexes =optionalIndexer.createReferenceIndexes();
			String baseName =FileUtil.getBaseName(bamFile);
			File indexFileOutFile = new File(bamFile.getParentFile(), baseName + ".bai");
			OutputStream indexOutStream =null;
			try{
				indexOutStream = new BufferedOutputStream(new FileOutputStream(indexFileOutFile));
				IndexUtil.writeIndex(indexOutStream, indexes);
			}finally{
				IOUtil.closeAndIgnoreErrors(indexOutStream);
			}
		}
	}

	@Override
	public void writeRecord(SamRecord record) throws IOException {
		try{
			header.validRecord(record, attributeValidator);
		}catch(SamValidationException e){
			throw new IOException("can not write record due to validation error(s)",e);
		}
		if(optionalIndexer !=null){
			optionalIndexer.setCurrentRecord(record);
		}
		
		SamUtil.writeAsBamRecord(out, header, record);
		
	}

}
