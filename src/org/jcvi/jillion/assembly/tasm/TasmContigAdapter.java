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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
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
	private final Long celeraAssemblerId;
	private final String comment, commonName;
	private final Integer sampleId;
	private final Long asmblId;
	
	private final String editPerson;
	private final Long editDate;
	private final String assemblyMethod;
	private final double avgCoverage;
	private final boolean isCircular;
	
	/**
	 * Private constructor only called by the Builder.
	 * @param delegate
	 * @param optionalAttributes
	 * @see Builder
	 */
	private TasmContigAdapter(Contig<? extends AssembledRead> delegate,
			Builder builder
			) {
		this.delegate = delegate;
		StreamingIterator<? extends AssembledRead> iter = null;
		long totalNumberOfReadBases=0L;
		try{
			iter = delegate.getReadIterator();
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				totalNumberOfReadBases += read.getNucleotideSequence().getUngappedLength();
				adaptedReads.put(read.getId(), new TasmAssembledReadAdapter(read));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		avgCoverage = totalNumberOfReadBases/(double) delegate.getConsensusSequence().getUngappedLength();
		this.sampleId = builder.sampleId;
		this.celeraAssemblerId = builder.celeraAssemblerId;
		this.editDate = builder.editDate;
		this.editPerson = builder.editPerson;
		this.comment = builder.comment;
		this.commonName = builder.commonName;
		this.assemblyMethod = builder.assemblyMethod;
		this.isCircular = builder.isCircular;
		this.asmblId = builder.asmblId;
	}

	@Override
	public double getAvgCoverage() {
		return avgCoverage;
	}
	@Override
	public Integer getSampleId() {
		return sampleId;
	}

	@Override
	public Long getCeleraAssemblerId() {
		return celeraAssemblerId;
	}

	@Override
	public Long getTigrProjectAssemblyId() {
		return asmblId;
	}

	@Override
	public String getAssemblyMethod() {
		return assemblyMethod;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public String getCommonName() {
		return commonName;
	}

	@Override
	public String getEditPerson() {
		return editPerson;
	}


	@Override
	public Date getEditDate() {
		//create new Date everytime
		//since Date is mutable
		return new Date(editDate);
	}
	@Override
	public boolean isCircular() {
		return isCircular;
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

	
		private final Contig<? extends AssembledRead> contig;
		
		
		private Long celeraAssemblerId;
		private String comment, commonName;
		private Integer sampleId;
		private String editPerson;
		private Long editDate;
		private String assemblyMethod;
		private Long asmblId;
		private boolean isCircular=false;
		
		/**
		 * Adapts the given contig object into a TigrAssemblerContig.
		 * @param contig the contig to adapt.
		 */
		public Builder(Contig<? extends AssembledRead> contig) {
			this.contig = contig;
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
           this.comment=comment;
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
           this.commonName = commonName;
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
        	if(caId==null){
        		this.celeraAssemblerId = null;
        	}else{
        		this.celeraAssemblerId = Long.parseLong(caId);
        	}
            return this;
        }
        
       
		/**
		 * Sets the assembly method
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
			this.assemblyMethod = method;
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
				this.editPerson = null;
				this.editDate = null;
			}else{
				this.editDate = editDate.getTime();
				this.editPerson = editPerson;
			}
			return this;
		}

		public Builder isCircular(boolean isCircular){
			this.isCircular = isCircular;
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
		public Builder setTigrProjectAssemblyId(Long asmblId){
			this.asmblId = asmblId;
			return this;
		}
		
		/**
		 * Constructs a new TigrAssemblerContigAdapter with which
		 * will adapt the given contig with the given
		 * optional attributes set.
		 */
		@Override
		public TasmContigAdapter build() {
			return new TasmContigAdapter(contig, this);
		}
		
		 /**
         * Sets the {@link TasmContigAttribute#BAC_ID}
         * attribute for this adapted contig.  Calling this method
         * multiple times will overwrite previous entries with the current
         * entry. Setting the value to {@code null} will remove the current
         * entry (the type can later be re-added by calling this method 
         * again with a non-null value). 
         * @param sampleId the value of the sample id to set;
         * may be null if this contig does not have a 
         * sample id.
         * @return this.
         */
        public Builder setSampleId(String sampleId){
           if(sampleId==null){
        	   this.sampleId = null;
           }else{
        	   this.sampleId = Integer.parseInt(sampleId);
           }
            return this;
        }
	}
}
