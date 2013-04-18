package org.jcvi.jillion.trace.sanger.phd;

import java.util.LinkedHashMap;
import java.util.Map;

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
		return null;
	}

	@Override
	public void visitEnd() {
		visitPhd(id, version, 
				sequenceBuilder.build(), qualityBuilder.build(), positionBuilder==null?null : positionBuilder.build(),
				comments);
		
	}

	protected abstract void visitPhd(String id, Integer version,
			NucleotideSequence basescalls, QualitySequence qualities, PositionSequence positions,
			Map<String,String> comments);
	
	@Override
	public void halted() {
		//no-op		
	}

}
