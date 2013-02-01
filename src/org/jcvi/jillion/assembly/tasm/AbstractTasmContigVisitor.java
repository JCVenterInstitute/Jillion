package org.jcvi.jillion.assembly.tasm;

import java.util.Date;
import java.util.EnumMap;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class  AbstractTasmContigVisitor implements TasmContigVisitor{

	private DefaultTasmContig.Builder builder;
	private final String contigId;
	private final DataStore<Long> fullRangeLengthDataStore;
	
	public AbstractTasmContigVisitor(String contigId, DataStore<Long> fullRangeLengthDataStore){
		this.contigId = contigId;
		this.fullRangeLengthDataStore = fullRangeLengthDataStore;
	}
	
	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		builder = new DefaultTasmContig.Builder(contigId, consensus);
		
	}

	@Override
	public void visitCeleraId(long id) {
		builder.addAttribute(TasmContigAttribute.CA_CONTIG_ID, Long.toString(id));
		
	}

	@Override
	public void visitComments(int bacId, String comment, String commonName,
			String assemblyMethod, boolean isCircular) {
		builder.addAttribute(TasmContigAttribute.BAC_ID, Integer.toString(bacId));
		builder.addAttribute(TasmContigAttribute.COM_NAME, commonName);
		builder.addAttribute(TasmContigAttribute.METHOD, assemblyMethod);
		builder.addAttribute(TasmContigAttribute.IS_CIRCULAR, isCircular?"1":"0");
	}

	@Override
	public void visitCoverageData(int numberOfReads, float avgCoverage) {
		builder.addAttribute(TasmContigAttribute.NUMBER_OF_READS, Integer.toString(numberOfReads));
		builder.addAttribute(TasmContigAttribute.AVG_COVERAGE, String.format("%.2f",avgCoverage));
		
	}

	@Override
	public void visitLastEdited(String username, Date editDate) {
		builder.addAttribute(TasmContigAttribute.EDIT_PERSON, username);
		builder.addAttribute(TasmContigAttribute.EDIT_DATE, TasmUtil.EDIT_DATE_FORMAT.format(editDate));
	}

	@Override
	public TasmContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir, final Range validRange) {
		return new TasmContigReadVisitor() {
			private NucleotideSequence gappedBasecalls;
			
			
			@Override
			public void visitEnd() {
				final Long fullLength;
				try {
					fullLength = fullRangeLengthDataStore.get(readId);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error reading from full length sequence datastore for read "+ readId, e);
				}
				if(fullLength ==null){
					throw new IllegalStateException("full length sequence datastore did not contain read "+ readId);
				}
				String gappedSequence = gappedBasecalls.toString();
				builder.addRead(readId, (int)gappedStartOffset, validRange, 
						gappedSequence, dir, fullLength.intValue());
				//compute read attributes
				EnumMap<TasmReadAttribute, String> readAttributes = new EnumMap<TasmReadAttribute, String>(TasmReadAttribute.class);
				readAttributes.put(TasmReadAttribute.NAME, readId);
				readAttributes.put(TasmReadAttribute.CONTIG_START_OFFSET, Long.toString(gappedStartOffset));
				readAttributes.put(TasmReadAttribute.GAPPED_SEQUENCE, gappedSequence);
				final long seqleft,seqRight, asmLeft,asmRight;
				if(dir == Direction.FORWARD){
					seqleft = validRange.getBegin(CoordinateSystem.RESIDUE_BASED);
					seqRight = validRange.getEnd(CoordinateSystem.RESIDUE_BASED);
				}else{
					seqRight = validRange.getBegin(CoordinateSystem.RESIDUE_BASED);
					seqleft = validRange.getEnd(CoordinateSystem.RESIDUE_BASED);
				}
				
				readAttributes.put(TasmReadAttribute.SEQUENCE_LEFT, Long.toString(seqleft));
				readAttributes.put(TasmReadAttribute.SEQUENCE_RIGHT, Long.toString(seqRight));
				readAttributes.put(TasmReadAttribute.CONTIG_LEFT, Long.toString(seqRight));
				
				builder.addReadAttributes(readId, readAttributes);
				
			}
			
			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				this.gappedBasecalls = gappedBasecalls;				
			}
		};
	}

	@Override
	public void visitIncompleteEnd() {
		//no-op
		
	}

	@Override
	public void visitEnd() {
		populateAsmLeftAndRight();
		visitRecord(builder);
		builder=null;
		
	}
	
	private void populateAsmLeftAndRight() {
		NucleotideSequence consensus =builder.getConsensusBuilder().build();
		for(TasmAssembledReadBuilder readBuilder : builder.getAllAssembledReadBuilders()){
			int asmLend= consensus.getUngappedOffsetFor((int)readBuilder.getBegin());
			int asmRend= consensus.getUngappedOffsetFor((int)readBuilder.getEnd());
			EnumMap<TasmReadAttribute, String> readAttributes = new EnumMap<TasmReadAttribute, String>(TasmReadAttribute.class);
			//add 1 to make it residue based
			readAttributes.put(TasmReadAttribute.CONTIG_LEFT, Long.toString(asmLend+1));
			readAttributes.put(TasmReadAttribute.CONTIG_RIGHT, Long.toString(asmRend +1));
			readBuilder.addAllAttributes(readAttributes);
		}
		
	}

	protected abstract void visitRecord(DefaultTasmContig.Builder builder);

}
