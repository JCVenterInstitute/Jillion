package org.jcvi.jillion.trace.sanger.phd;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.jcvi.jillion.trace.sanger.PositionSequenceBuilder;

public abstract class AbstractPhdVisitor2 implements PhdVisitor2{

	private final String id;
	private final Integer version;
	
	private final Map<String, String> comments = new LinkedHashMap<String, String>();
	
	private final NucleotideSequenceBuilder sequenceBuilder = new NucleotideSequenceBuilder(1024);
	private final QualitySequenceBuilder qualityBuilder = new QualitySequenceBuilder(1024);
	private PositionSequenceBuilder positionBuilder = new PositionSequenceBuilder(1024);
	private boolean hasPositions=true;
	
	private final List<PhdWholeReadItem> wholeReadItems = new ArrayList<PhdWholeReadItem>();
	
	private final List<PhdReadTag> readTags = new ArrayList<PhdReadTag>();
	
	public AbstractPhdVisitor2(String id) {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		this.id = id;
		version = null;
	}
	
	public AbstractPhdVisitor2(String id, int version){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		this.id = id;
		this.version = version;
	}

	@Override
	public void visitComment(Map<String, String> comments) {
		this.comments.putAll(comments);		
	}

	@Override
	public void visitBasecall(Nucleotide base, PhredQuality quality) {
		sequenceBuilder.append(base);
		qualityBuilder.append(quality);
		hasPositions=false;
	}

	@Override
	public void visitBasecall(Nucleotide base, PhredQuality quality,
			int tracePosition) {
		if(!hasPositions){
			throw new IllegalStateException("this phd has some basecalls with and without positions");
		}
		sequenceBuilder.append(base);
		qualityBuilder.append(quality);
		positionBuilder.append(tracePosition);		
	}
	/**
	 * Ignores readTags, override this
	 * method if you want to deal with
	 * readTags;
	 * @return null.
	 */
	@Override
	public PhdReadTagVisitor2 visitReadTag() {
		//ignores read tag 
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
	public PhdWholeReadItemVisitor visitWholeReadItem() {
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
	public void visitEnd() {
		visitPhd(id, version, 
				sequenceBuilder.build(), qualityBuilder.build(), positionBuilder==null?null : positionBuilder.build(),
				comments, wholeReadItems,readTags);
		
	}

	protected abstract void visitPhd(String id, Integer version,
			NucleotideSequence basescalls, QualitySequence qualities, PositionSequence positions,
			Map<String,String> comments,
			List<PhdWholeReadItem> wholeReadItems,
			List<PhdReadTag> readTags);
	
	@Override
	public void halted() {
		//no-op		
	}

}
