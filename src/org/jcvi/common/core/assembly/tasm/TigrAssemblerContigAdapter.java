/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.tasm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;
/**
 * {@code TigrAssemblerContigAdapter} is an adapter to convert a 
 * {@link Contig} into a {@link TigrAssemblerContig} with all
 * the appropriate {@link TigrAssemblerContigAttribute} 
 * and {@link TigrAssemblerReadAttribute} attributes set.
 * @author dkatzel
 *
 */
public final class TigrAssemblerContigAdapter implements TigrAssemblerContig{

	private final Contig<? extends AssembledRead> delegate;
	private final Map<String, TigrAssemblerPlacedRead> adaptedReads = new LinkedHashMap<String, TigrAssemblerPlacedRead>();
	private final Map<TigrAssemblerContigAttribute,String> attributes;
	/**
	 * Private constructor only called by the Builder.
	 * @param delegate
	 * @param optionalAttributes
	 * @see Builder
	 */
	private TigrAssemblerContigAdapter(Contig<? extends AssembledRead> delegate, Map<TigrAssemblerContigAttribute, String> optionalAttributes) {
		this.delegate = delegate;
		CloseableIterator<? extends AssembledRead> iter = null;
		try{
			iter = delegate.getReadIterator();
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				adaptedReads.put(read.getId(), new TigrAssemblerPlacedReadAdapter(read));
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
	private Map<TigrAssemblerContigAttribute,String> createNonConsensusAttributes(
			Contig<? extends AssembledRead> delegate, Map<TigrAssemblerContigAttribute, String> optionalAttributes) {
		Map<TigrAssemblerContigAttribute,String> map = new EnumMap<TigrAssemblerContigAttribute,String>(TigrAssemblerContigAttribute.class);
		map.put(TigrAssemblerContigAttribute.ASMBL_ID, delegate.getId());
		map.put(TigrAssemblerContigAttribute.NUMBER_OF_READS, ""+delegate.getNumberOfReads());
		
		map.put(TigrAssemblerContigAttribute.IS_CIRCULAR, "0");
		
		double averageCoverage = computeAvgCoverageFor(delegate);
		map.put(TigrAssemblerContigAttribute.AVG_COVERAGE,String.format("%.2f",averageCoverage));
		double percentN = computePercentNFor(delegate);
		map.put(TigrAssemblerContigAttribute.PERCENT_N,String.format("%.2f",percentN));
		
		map.putAll(optionalAttributes);
		return map;
	}
	
	private Map<TigrAssemblerContigAttribute,String> generateConsensusAttributes(){
		Map<TigrAssemblerContigAttribute,String> map = new EnumMap<TigrAssemblerContigAttribute,String>(TigrAssemblerContigAttribute.class);
		map.put(TigrAssemblerContigAttribute.UNGAPPED_CONSENSUS, Nucleotides.asString(delegate.getConsensus().asUngappedList()));
		map.put(TigrAssemblerContigAttribute.GAPPED_CONSENSUS, Nucleotides.asString(delegate.getConsensus().asList()));
	
		return map;
	}

	private double computePercentNFor(Contig<? extends AssembledRead> delegate) {
		// TODO is this supposed to be %N in reads or %N in consensus or both?
		//going with consensus for now since its the assembly table
		int numberOfNs =0;
		for(Nucleotide g: delegate.getConsensus().asList()){
			if(g == Nucleotide.Unknown){
				numberOfNs++;
			}
		}
		return numberOfNs/(double)delegate.getConsensus().getLength();
	}

	private double computeAvgCoverageFor(Contig<? extends AssembledRead> delegate) {
		double averageCoverage = DefaultCoverageMap.buildCoverageMap(delegate).getAverageCoverage();
		return averageCoverage;
	}

	@Override
	public String getAttributeValue(TigrAssemblerContigAttribute attribute) {
		switch(attribute){
		case UNGAPPED_CONSENSUS:
		case GAPPED_CONSENSUS:
			return generateConsensusAttributes().get(attribute);
		default : 
			return attributes.get(attribute);
		}
	}

	private Map<TigrAssemblerContigAttribute, String> createAllAttributes(){
		Map<TigrAssemblerContigAttribute,String> map = new EnumMap<TigrAssemblerContigAttribute,String>(TigrAssemblerContigAttribute.class);
		map.putAll(attributes);
		map.putAll(generateConsensusAttributes());
		return map;
		
	}
	@Override
	public Map<TigrAssemblerContigAttribute, String> getAttributes() {
		return Collections.unmodifiableMap(createAllAttributes());
	}

	
	@Override
	public boolean hasAttribute(TigrAssemblerContigAttribute attribute) {
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
	public NucleotideSequence getConsensus() {
		return delegate.getConsensus();
	}

	@Override
	public String getId() {
		return delegate.getId();
	}

	@Override
	public int getNumberOfReads() {
		return delegate.getNumberOfReads();
	}

	@Override
	public TigrAssemblerPlacedRead getRead(
			String id) {
		return adaptedReads.get(id);
	}

	@Override
	public CloseableIterator<TigrAssemblerPlacedRead> getReadIterator() {

		return CloseableIteratorAdapter.adapt(adaptedReads.values().iterator());
	}

	
	/**
	 * {@code Builder} is a Builder object for making 
	 * TigrAssemblerContigAdapter instances.  Some {@link TigrAssemblerContigAttribute}s
	 * can be 
	 * automatically computed based on the contig to adapt,
	 * however, some other optional contig attributes can not be inferred
	 * and must be set manually
	 * with the withXXX methods.
	 * @author dkatzel
	 *
	 */
	public static class Builder implements org.jcvi.common.core.util.Builder<TigrAssemblerContigAdapter>{

		//03/05/10 01:52:31 PM
		/**
		 * This is the format that dates should be set as Strings
		 * in the TIGR Project DB database.
		 */
		private static final DateFormat EDIT_DATE_FORMATTER =
			new SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
		private final Contig<? extends AssembledRead> contig;
		private final Map<TigrAssemblerContigAttribute, String> optionalAttributes = new EnumMap<TigrAssemblerContigAttribute, String>(TigrAssemblerContigAttribute.class);
		/**
		 * Adapts the given contig object into a TigrAssemblerContig.
		 * @param contig the contig to adapt.
		 */
		public Builder(Contig<? extends AssembledRead> contig) {
			this.contig = contig;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#TYPE}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.TYPE);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.TYPE,type);
			return this;
		}
		/**
         * Sets the {@link TigrAssemblerContigAttribute#COMMENT}
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
                optionalAttributes.remove(TigrAssemblerContigAttribute.COMMENT);
            }
            optionalAttributes.put(TigrAssemblerContigAttribute.COMMENT,comment);
            return this;
        }
        /**
         * Sets the {@link TigrAssemblerContigAttribute#COM_NAME}
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
                optionalAttributes.remove(TigrAssemblerContigAttribute.COM_NAME);
            }
            optionalAttributes.put(TigrAssemblerContigAttribute.COM_NAME,commonName);
            return this;
        }
        /**
         * Sets the {@link TigrAssemblerContigAttribute#CA_CONTIG_ID}
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
                optionalAttributes.remove(TigrAssemblerContigAttribute.CA_CONTIG_ID);
            }
            optionalAttributes.put(TigrAssemblerContigAttribute.CA_CONTIG_ID,caId);
            return this;
        }
        
        /**
         * Sets the {@link TigrAssemblerContigAttribute#ASMBL_ID}
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
                optionalAttributes.remove(TigrAssemblerContigAttribute.ASMBL_ID);
            }
            optionalAttributes.put(TigrAssemblerContigAttribute.ASMBL_ID,asmblId);
            return this;
        }
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#METHOD}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.METHOD);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.METHOD,method);
			return this;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#EDIT_STATUS}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.EDIT_STATUS);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.EDIT_STATUS,editStatus);
			return this;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#FULL_CDS}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.FULL_CDS);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.FULL_CDS,fullCDS);
			return this;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#CDS_START}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.CDS_START);
			}else{
			    optionalAttributes.put(TigrAssemblerContigAttribute.CDS_START,cdsStart.toString());
			}
			return this;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#CDS_END}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.CDS_END);
			}else{
			    optionalAttributes.put(TigrAssemblerContigAttribute.CDS_END,cdsEnd.toString());
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.EDIT_PERSON);
				optionalAttributes.remove(TigrAssemblerContigAttribute.EDIT_DATE);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.EDIT_PERSON,editPerson);
			optionalAttributes.put(TigrAssemblerContigAttribute.EDIT_DATE,EDIT_DATE_FORMATTER.format(editDate));
			return this;
		}
		/**
		 * Sets the {@link TigrAssemblerContigAttribute#FRAME_SHIFT}
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
				optionalAttributes.remove(TigrAssemblerContigAttribute.FRAME_SHIFT);
			}
			optionalAttributes.put(TigrAssemblerContigAttribute.FRAME_SHIFT,frameShift);
			return this;
		}
		/**
		 * Constructs a new TigrAssemblerContigAdapter with which
		 * will adapt the given contig with the given
		 * optional attributes set.
		 */
		@Override
		public TigrAssemblerContigAdapter build() {
			return new TigrAssemblerContigAdapter(contig, optionalAttributes);
		}
		
	}
}
