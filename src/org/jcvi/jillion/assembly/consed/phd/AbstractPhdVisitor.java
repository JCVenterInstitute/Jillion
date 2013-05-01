package org.jcvi.jillion.assembly.consed.phd;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code AbstractPhdVisitor} is an abstract
 * implementation of {@link PhdVisitor}
 * that collects all the information about a single
 * phd record.
 * Subclasses are required to implement the abstract
 * method {@link #visitPhd(String, Integer, NucleotideSequence, QualitySequence, PositionSequence, Map, List, List)}
 * which will be called when all the information has been collected
 * (when {@link #visitEnd()} is called).
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractPhdVisitor implements PhdVisitor{

	private final String id;
	private final Integer version;
	
	private final Map<String, String> comments = new LinkedHashMap<String, String>();
	
	private final NucleotideSequenceBuilder sequenceBuilder = new NucleotideSequenceBuilder(1024);
	private final QualitySequenceBuilder qualityBuilder = new QualitySequenceBuilder(1024);
	private final PositionSequenceBuilder positionBuilder = new PositionSequenceBuilder(1024);
	
	private final List<PhdWholeReadItem> wholeReadItems = new ArrayList<PhdWholeReadItem>();
	
	private final List<PhdReadTag> readTags = new ArrayList<PhdReadTag>();
	
	
	
	public AbstractPhdVisitor(String id, Integer version){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(version !=null && version.intValue() < 1){
			throw new IllegalArgumentException("version must be >=1 : " + version);
		}
		this.id = id;
		this.version = version;
	}

	@Override
	public final void visitComments(Map<String, String> comments) {
		this.comments.putAll(comments);		
	}

	

	@Override
	public final void visitBasecall(Nucleotide base, PhredQuality quality,
			Integer tracePosition) {
		sequenceBuilder.append(base);
		qualityBuilder.append(quality);
		if(tracePosition !=null){
			positionBuilder.append(tracePosition.intValue());
		}
	}

	@Override
	public final PhdReadTagVisitor visitReadTag() {
		return new AbstractPhdReadTagVisitor(){

			@Override
			protected void visitPhdReadTag(String type, String source,
					Range ungappedRange, Date date, String comment,
					String freeFormData) {
				readTags.add(new DefaultPhdReadTag(type, source, ungappedRange, date, comment, freeFormData));
				
			}
			
		};
	}

	@Override
	public final PhdWholeReadItemVisitor visitWholeReadItem() {
		return new PhdWholeReadItemVisitor() {
			List<String> lines = new ArrayList<String>();
			@Override
			public void visitLine(String line) {
				lines.add(line);				
			}
			
			@Override
			public void visitEnd() {
				wholeReadItems.add(new DefaultPhdWholeReadItem(lines));
				
			}
			
			@Override
			public void halted() {
				//no-op				
			}
		};
	}

	@Override
	public final void visitEnd() {
		final PositionSequence peaks;
		//no peaks
		if(positionBuilder.getLength() ==0){
			peaks =null;
		}else{
			if( positionBuilder.getLength() != sequenceBuilder.getLength()){
				throw new IllegalStateException("not all basecalls have positions set");
			}
			peaks = positionBuilder.build();
		}
		visitPhd(id, version, 
				sequenceBuilder.build(), qualityBuilder.build(), peaks,
				comments, wholeReadItems,readTags);
		
	}

	protected abstract void visitPhd(String id, Integer version,
			NucleotideSequence basescalls, QualitySequence qualities, PositionSequence positions,
			Map<String,String> comments,
			List<PhdWholeReadItem> wholeReadItems,
			List<PhdReadTag> readTags);
	
	/**
	 * Ignored by default, please
	 * override to get halted notification.
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op		
	}

}
