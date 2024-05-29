package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
import org.jcvi.jillion.trace.fastq.PairedFastqVisitor.PairedFastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.PairedFastqVisitor.PairedFastqVisitorCallback.PairedFastqVisitorMemento;

import lombok.Value;
/**
 * Parser for a pair of fastq files that represent
 * read 1 and read 2 of paired end sequencing.
 * this parser allows the user to visit both files simultaneously.
 * 
 * @author dkatzel
 * 
 * @since 6.0.2
 * @see PairedFastqVisitor
 */
public class PairedFastqFileParser{

	private final FastqFileParser read1Parser, read2Parser;
	
	/**
     * Create a new {@link PairedFastqFileParser} instance
     * that will parse the given pair of fastq encoded
     * Files which may be zipped or gzipped, but does not contain defline comments and is
     * only 4 lines per fastq record.
     * <p>
     * If you need to parse Fastq files that have
     * comments or have more than 4 lines per record
     * or you want to use {@link FastqVisitorMemento}s
     * please use {@link FastqFileParserBuilder}
     * which has additional configuration options.
     *
     * @param fastq the fastq encoded file to parse; must
     * exist and be readable; may be a "normal" fastq file, or zipped or gzipped.
     * 
     * @throws NullPointerException if fastq is null.
     * @throws IOException if fastq does not exist or is not readable.
     * 
     * @return a new {@link FastqParser} instance; will never be null.
     * 
     * @see FastqFileParserBuilder#FastqFileParserBuilder(File)
     */
    public static PairedFastqFileParser create(File read1Fastq, File read2Fastq) throws IOException{
            return new PairedFastqFileParser((FastqFileParser) FastqFileParser.create(read1Fastq),
            		(FastqFileParser) FastqFileParser.create(read2Fastq));
    }
    
    

    public void parse(PairedFastqVisitor visitor) throws IOException {
    	try(FastqSingleVisitIterator read1Iterator = read1Parser.iterator();
    		FastqSingleVisitIterator read2Iterator = read2Parser.iterator();
    			){
    		FastqVisitor read1Visitor = new PairOfFastqVisitors(visitor, read2Iterator);
    		while(read1Iterator.hasNext()) {
    			read1Iterator.next(read1Visitor);
    		}
    	}
    }
	



	public PairedFastqFileParser(FastqFileParser read1Parser, FastqFileParser read2Parser) {
		this.read1Parser = Objects.requireNonNull(read1Parser);
		this.read2Parser = Objects.requireNonNull(read2Parser);
	}

	private static class PairOfFastqVisitors implements FastqVisitor{
		private final PairedFastqVisitor delegate;
		private final FastqSingleVisitIterator  read2Iterator;
		

		private final SwitchingPairedVisitor currentRecordVisitor[] = new SwitchingPairedVisitor[1];
		public PairOfFastqVisitors(PairedFastqVisitor delegate, FastqSingleVisitIterator read2Iterator) throws IOException {
			this.delegate = delegate;
			this.read2Iterator = read2Iterator;
		}

		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback read1Callback, String read1Id, String read1OptionalComment) {
			
			read2Iterator.next(new AbstractFastqVisitor() {

				@Override
				public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id,
						String optionalComment) {
					PairedVisitorCallback pairedCallback = new PairedVisitorCallback(read1Callback, callback);
					
					PairedFastqRecordVisitor pairedRecordVisitor = delegate.visitDefline(pairedCallback, read1Id, read1OptionalComment,
							id, optionalComment);
					
					currentRecordVisitor[0]=pairedRecordVisitor==null?null : new SwitchingPairedVisitor(pairedRecordVisitor); 
					return currentRecordVisitor[0];
					
				}
				
			});
			SwitchingPairedVisitor visitor = currentRecordVisitor[0];
			if(visitor !=null) {
				visitor.switchRead();
			}
			return visitor;
		}

		@Override
		public void visitEnd() {
			delegate.visitEnd();
			
		}

		@Override
		public void halted() {
			delegate.halted();
			
		}
		
	}
	
	
	private static class PairedVisitorCallback implements PairedFastqVisitorCallback{

		private FastqVisitorCallback read1Callback, read2Callback;
		
		
		
		public PairedVisitorCallback(FastqVisitorCallback read1Callback, FastqVisitorCallback read2Callback) {
			this.read1Callback = read1Callback;
			this.read2Callback = read2Callback;
		}

		@Override
		public boolean canCreateMemento() {
			return read1Callback.canCreateMemento() && read2Callback.canCreateMemento();
		}

		@Override
		public PairedFastqVisitorMemento createMemento() {
			return new PairedFastqVisitorMementoImpl(read1Callback.createMemento(), read2Callback.createMemento());
		}

		@Override
		public void haltParsing() {
			read1Callback.haltParsing();
			read2Callback.haltParsing();
			
		}
		
	}
	
	@Value
	private static class PairedFastqVisitorMementoImpl implements PairedFastqVisitorMemento{
		private final FastqVisitorMemento memento1, memento2;
		
	}
	private static class SwitchingPairedVisitor implements FastqRecordVisitor{
		private final PairedFastqRecordVisitor delegate;
		boolean inRead2=true;
		
		

		public SwitchingPairedVisitor(PairedFastqRecordVisitor delegate) {
			this.delegate = delegate;
		}

		@Override
		public void visitNucleotides(String nucleotides) {
			if(inRead2) {
				
				delegate.visitRead2Nucleotides(nucleotides);
			}else {
				delegate.visitRead1Nucleotides(nucleotides);
			}
			
		}

		@Override
		public void visitEncodedQualities(String encodedQualities) {
			if(inRead2) {
				delegate.visitEncodedRead2Qualities(encodedQualities);
			}else {
				
				delegate.visitEncodedRead1Qualities(encodedQualities);
			}
			
		}

		@Override
		public void visitQualities(QualitySequence qualities) {
			if(inRead2) {
				delegate.visitRead2Qualities(qualities);
			}else {
				
				delegate.visitRead1Qualities(qualities);
			}
			
		}
		
		
		public void switchRead() {
			inRead2 = !inRead2;
		}
		@Override
		public void visitEnd() {
			if(!inRead2) {				
				delegate.visitEnd();
			}
			
		}

		@Override
		public void halted() {
			delegate.halted();
			
		}
		
	}
}
