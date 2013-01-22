/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.tasm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * {@code TasmContigAdapter} is an adapter to convert a 
 * {@link Contig} into a {@link TasmContig} with all
 * the appropriate {@link TasmContigAttribute} 
 * and {@link TasmReadAttribute} attributes set.
 * @author dkatzel
 *
 */
public final class TasmContigAdapter implements TasmContig{

	private final Contig<? extends AssembledRead> delegate;
	private final Map<String, TasmAssembledRead> adaptedReads = new LinkedHashMap<String, TasmAssembledRead>();
	private final Map<TasmContigAttribute,String> attributes;
	/**
	 * Private constructor only called by the Builder.
	 * @param delegate
	 * @param optionalAttributes
	 * @see Builder
	 */
	private TasmContigAdapter(Contig<? extends AssembledRead> delegate, Map<TasmContigAttribute, String> optionalAttributes) {
		this.delegate = delegate;
		StreamingIterator<? extends AssembledRead> iter = null;
		try{
			iter = delegate.getReadIterator();
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				adaptedReads.put(read.getId(), new TasmAssembledReadAdapter(read));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		attributes = createNonConsensusAttributes(delegate,optionalAttributes);
	}
	/**
	 * It's silly to store the gapped and ungapped consensus as
	 * Strings when we have encoded glyph representations
	 * so we will store all the other attributes
	 * but generate the consensus attributes 
	 * only when needed.
	 * @param delegate
	 * @param optionalAttributes
	 * @return a Map containing all the adapted
	 * attributes except for gapped and ungapped consensus.
	 */
	private Map<TasmContigAttribute,String> createNonConsensusAttributes(
			Contig<? extends AssembledRead> delegate, Map<TasmContigAttribute, String> optionalAttributes) {
		Map<TasmContigAttribute,String> map = new EnumMap<TasmContigAttribute,String>(TasmContigAttribute.class);
		map.put(TasmContigAttribute.ASMBL_ID, delegate.getId());
		map.put(TasmContigAttribute.NUMBER_OF_READS, ""+delegate.getNumberOfReads());
		
		map.put(TasmContigAttribute.IS_CIRCULAR, "0");
		
		double averageCoverage = computeAvgCoverageFor(delegate);
		map.put(TasmContigAttribute.AVG_COVERAGE,String.format("%.2f",averageCoverage));
		double percentN = computePercentNFor(delegate);
		map.put(TasmContigAttribute.PERCENT_N,String.format("%.2f",percentN));
		
		map.putAll(optionalAttributes);
		return map;
	}
	
	private Map<TasmContigAttribute,String> generateConsensusAttributes(){
		Map<TasmContigAttribute,String> map = new EnumMap<TasmContigAttribute,String>(TasmContigAttribute.class);
		map.put(TasmContigAttribute.GAPPED_CONSENSUS, delegate.getConsensusSequence().toString());
	
		map.put(TasmContigAttribute.UNGAPPED_CONSENSUS, new NucleotideSequenceBuilder(delegate.getConsensusSequence())
																	.ungap()
																	.toString());
		
		return map;
	}

	private double computePercentNFor(Contig<? extends AssembledRead> delegate) {
		// TODO is this supposed to be %N in reads or %N in consensus or both?
		//going with consensus for now since its the assembly table
		int numberOfNs =0;
		for(Nucleotide g: delegate.getConsensusSequence()){
			if(g == Nucleotide.Unknown){
				numberOfNs++;
			}
		}
		return numberOfNs/(double)delegate.getConsensusSequence().getLength();
	}

	private double computeAvgCoverageFor(Contig<? extends AssembledRead> delegate) {
		long ungappedConsensusLength=delegate.getConsensusSequence().getUngappedLength();
		long bases=0;
		StreamingIterator<? extends AssembledRead> iter =null;
		try{
			iter = delegate.getReadIterator();
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				bases+=read.getNucleotideSequence().getUngappedLength();
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}		
		return ((double)bases/ungappedConsensusLength);
	}

	@Override
	public String getAttributeValue(TasmContigAttribute attribute) {
		switch(attribute){
		case UNGAPPED_CONSENSUS:
		case GAPPED_CONSENSUS:
			return generateConsensusAttributes().get(attribute);
		default : 
			return attributes.get(attribute);
		}
	}

	private Map<TasmContigAttribute, String> createAllAttributes(){
		Map<TasmContigAttribute,String> map = new EnumMap<TasmContigAttribute,String>(TasmContigAttribute.class);
		map.putAll(attributes);
		map.putAll(generateConsensusAttributes());
		return map;
		
	}
	@Override
	public Map<TasmContigAttribute, String> getAttributes() {
		return Collections.unmodifiableMap(createAllAttributes());
	}

	
	@Override
	public boolean hasAttribute(TasmContigAttribute attribute) {
		switch(attribute){
		case UNGAPPED_CONSENSUS:
		case GAPPED_CONSENSUS:
			return true;
		default : 
			return attributes.containsKey(attribute);
		}
	}

	@Override
	public boolean containsRead(String placedReadId) {
		return delegate.containsRead(placedReadId);
	}

	@Override
	public NucleotideSequence getConsensusSequence() {
		return delegate.getConsensusSequence();
	}

	@Override
	public String getId() {
		return delegate.getId();
	}

	@Override
	public long getNumberOfReads() {
		return delegate.getNumberOfReads();
	}

	@Override
	public TasmAssembledRead getRead(
			String id) {
		return adaptedReads.get(id);
	}

	@Override
	public StreamingIterator<TasmAssembledRead> getReadIterator() {

		return IteratorUtil.createStreamingIterator(adaptedReads.values().iterator());
	}

	
	/**
	 * {@code Builder} is a Builder object for making 
	 * TigrAssemblerContigAdapter instances.  Some {@link TasmContigAttribute}s
	 * can be 
	 * automatically computed based on the contig to adapt,
	 * however, some other optional contig attributes can not be inferred
	 * and must be set manually
	 * with the withXXX methods.
	 * @author dkatzel
	 *
	 */
	public static class Builder implements org.jcvi.jillion.core.util.Builder<TasmContigAdapter>{

		//03/05/10 01:52:31 PM
		/**
		 * This is the format that dates should be set as Strings
		 * in the TIGR Project DB database.
		 */
		private static final DateFormat EDIT_DATE_FORMATTER =
			new SimpleDateFormat("MM/dd/yy hh:mm:ss aa", Locale.US);
		
		
		
		private final Contig<? extends AssembledRead> contig;
		private final Map<TasmContigAttribute, String> optionalAttributes = new EnumMap<TasmContigAttribute, String>(TasmContigAttribute.class);
		/**
		 * Adapts the given contig object into a TigrAssemblerContig.
		 * @param contig the contig to adapt.
		 */
		public Builder(Contig<? extends AssembledRead> contig) {
			this.contig = contig;
		}
		
		private synchronized String formatEditDate(Date editDate){
			return EDIT_DATE_FORMATTER.format(editDate);
		}
		/**
		 * Sets the {@link TasmContigAttribute#TYPE}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the type can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param type the value of the type to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withType(String type){
			if(type ==null){
				optionalAttributes.remove(TasmContigAttribute.TYPE);
			}
			optionalAttributes.put(TasmContigAttribute.TYPE,type);
			return this;
		}
		/**
         * Sets the {@link TasmContigAttribute#COMMENT}
         * attribute for this adapted contig.  Calling this method
         * multiple times will overwrite previous entries with the current
         * entry. Setting the value to {@code null} will remove the current
         * entry (the type can later be re-added by calling this method 
         * again with a non-null value).
         * @param comment the value of the comment to set;
         * if this value is null, then the contig should not
         * have this attribute.
         * @return this.
         */
        public Builder withComment(String comment){
            if(comment ==null){
                optionalAttributes.remove(TasmContigAttribute.COMMENT);
            }
            optionalAttributes.put(TasmContigAttribute.COMMENT,comment);
            return this;
        }
        /**
         * Sets the {@link TasmContigAttribute#COM_NAME}
         * attribute for this adapted contig.  Calling this method
         * multiple times will overwrite previous entries with the current
         * entry. Setting the value to {@code null} will remove the current
         * entry (the type can later be re-added by calling this method 
         * again with a non-null value).
         * @param commonName the value of the com-name to set;
         * if this value is null, then the contig should not
         * have this attribute.
         * @return this.
         */
        public Builder withCommonName(String commonName){
            if(commonName ==null){
                optionalAttributes.remove(TasmContigAttribute.COM_NAME);
            }
            optionalAttributes.put(TasmContigAttribute.COM_NAME,commonName);
            return this;
        }
        /**
         * Sets the {@link TasmContigAttribute#CA_CONTIG_ID}
         * attribute for this adapted contig.  Calling this method
         * multiple times will overwrite previous entries with the current
         * entry. Setting the value to {@code null} will remove the current
         * entry (the type can later be re-added by calling this method 
         * again with a non-null value). As of 2006, All contigs
         * should get a CA Contig Id set to a UID.
         * @param caId the value of the Celera Assembler Contig Id to set;
         * if this value is null, then the contig should not
         * have this attribute.
         * @return this.
         */
        public Builder withCeleraAssemblerContigId(String caId){
            if(caId ==null){
                optionalAttributes.remove(TasmContigAttribute.CA_CONTIG_ID);
            }
            optionalAttributes.put(TasmContigAttribute.CA_CONTIG_ID,caId);
            return this;
        }
        
        /**
         * Sets the {@link TasmContigAttribute#ASMBL_ID}
         * attribute for this adapted contig.  Calling this method
         * multiple times will overwrite previous entries with the current
         * entry. Setting the value to {@code null} will remove the current
         * entry (the type can later be re-added by calling this method 
         * again with a non-null value). 
         * @param asmblId the value of the asmbl_id to set;
         * if this value is null, then the contig will default
         * to the value returned by the adapted {@link Contig#getId()}
         * @return this.
         */
        public Builder withAssembleId(String asmblId){
            if(asmblId ==null){
                optionalAttributes.remove(TasmContigAttribute.ASMBL_ID);
            }
            optionalAttributes.put(TasmContigAttribute.ASMBL_ID,asmblId);
            return this;
        }
		/**
		 * Sets the {@link TasmContigAttribute#METHOD}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the method can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param method the value of the method to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withMethod(String method){
			if(method ==null){
				optionalAttributes.remove(TasmContigAttribute.METHOD);
			}
			optionalAttributes.put(TasmContigAttribute.METHOD,method);
			return this;
		}
		/**
		 * Sets the {@link TasmContigAttribute#EDIT_STATUS}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the edit status can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param editStatus the value of the edit status to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withEditStatus(String editStatus){
			if(editStatus ==null){
				optionalAttributes.remove(TasmContigAttribute.EDIT_STATUS);
			}
			optionalAttributes.put(TasmContigAttribute.EDIT_STATUS,editStatus);
			return this;
		}
		/**
		 * Sets the {@link TasmContigAttribute#FULL_CDS}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the attribute can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param fullCDS the value of the fullCDS to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withFullCDS(String fullCDS){
			if(fullCDS ==null){
				optionalAttributes.remove(TasmContigAttribute.FULL_CDS);
			}
			optionalAttributes.put(TasmContigAttribute.FULL_CDS,fullCDS);
			return this;
		}
		/**
		 * Sets the {@link TasmContigAttribute#CDS_START}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the attribute can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param cdsStart the value of the cds start to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withCDSStart(Integer cdsStart){
			if(cdsStart ==null){
				optionalAttributes.remove(TasmContigAttribute.CDS_START);
			}else{
			    optionalAttributes.put(TasmContigAttribute.CDS_START,cdsStart.toString());
			}
			return this;
		}
		/**
		 * Sets the {@link TasmContigAttribute#CDS_END}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the attribute can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param cdsEnd the value of the cds end to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withCDSEnd(Integer cdsEnd){
			if(cdsEnd ==null){
				optionalAttributes.remove(TasmContigAttribute.CDS_END);
			}else{
			    optionalAttributes.put(TasmContigAttribute.CDS_END,cdsEnd.toString());
			}
			return this;
		}
		/**
		 * Sets the edit person and edit date attributes for this contig.
		 * Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entries. Setting <strong>EITHER</strong> value to {@code null} will remove the current
		 * entries (the attributes can later be re-added by calling this method 
		 * again with a non-null values).
		 * @param editPerson the person who last made an edit
		 * to this contig. 
		 * @param editDate the date that this contig was last edited
		 * (by the editPerson)
		 * @return this.
		 */
		public Builder withEditInfo(String editPerson, Date editDate){
			if(editPerson ==null || editDate ==null){
				optionalAttributes.remove(TasmContigAttribute.EDIT_PERSON);
				optionalAttributes.remove(TasmContigAttribute.EDIT_DATE);
			}
			optionalAttributes.put(TasmContigAttribute.EDIT_PERSON,editPerson);
			optionalAttributes.put(TasmContigAttribute.EDIT_DATE, formatEditDate(editDate));
			return this;
		}
		/**
		 * Sets the {@link TasmContigAttribute#FRAME_SHIFT}
		 * attribute for this adapted contig.  Calling this method
		 * multiple times will overwrite previous entries with the current
		 * entry. Setting the value to {@code null} will remove the current
		 * entry (the attribute can later be re-added by calling this method 
		 * again with a non-null value).
		 * @param frameShift the value of the frameShift to set;
		 * if this value is null, then the contig should not
		 * have this attribute.
		 * @return this.
		 */
		public Builder withFrameShift(String frameShift){
			if(frameShift ==null){
				optionalAttributes.remove(TasmContigAttribute.FRAME_SHIFT);
			}
			optionalAttributes.put(TasmContigAttribute.FRAME_SHIFT,frameShift);
			return this;
		}
		/**
		 * Constructs a new TigrAssemblerContigAdapter with which
		 * will adapt the given contig with the given
		 * optional attributes set.
		 */
		@Override
		public TasmContigAdapter build() {
			return new TasmContigAdapter(contig, optionalAttributes);
		}
		
	}
}
