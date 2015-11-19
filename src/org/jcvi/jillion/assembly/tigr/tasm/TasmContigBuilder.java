/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AbstractContigBuilder;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.DefaultContig;
/**
 * Builder
 * for {@link TasmContig}s that allows
 * creating {@link TasmContig} objects read by read by adding assembled reads
 * and setting a consensus.  An {@link TasmContigBuilder}
 * can be used to create {@link TasmContig}s that 
 * have been created by an assembler or can be used
 * to create contigs from "scratch".
 * There are additional methods to allow
 * the contig consensus or underlying
 * reads to be modified before
 * the creation of the {@link TasmContig} instance
 * (which is immutable).
 * @author dkatzel
 *
 *
 */
public final class TasmContigBuilder extends AbstractContigBuilder<TasmAssembledRead, TasmContig>{
	private Long celeraAssemblerId;
	private String comment, commonName;
	private Integer sampleId;
	private Long asmblId;
	
	private String editPerson;
	private Long editDate;
	private String assemblyMethod;
	private boolean isCircular;
	
	private Double avgCoverage;
	private Integer numberOfReads;
	
    /**
     * Create a new TasmContigBuilder instance setting the
     * contig id and contig consensus.  If the contig id
     * is a positive numeric, then the TigrProjectAssemblyId
     * will also be set to the contig id.  This can
     * be changed by calling {@link #setTigrProjectAssemblyId(Long)}.
     * All other {@link TasmContig} optional specific attributes
     * will be set to the default values of {@code null}
     * or {@code false}.
     * @param id the contig id; can not be null.
     * @param consensus the contig consensus; can not be null.
     * @throws NullPointerException if either id or consensus
     * are null.
     */
    public TasmContigBuilder(String id, NucleotideSequence consensus) {
    	super(id,consensus);
    	try{
    		long asmblId =Long.parseLong(id);
    		if(asmblId >0){
    			setTigrProjectAssemblyId(asmblId);
    		}
    	}catch(Exception e){
    		//must not be an assemble id
    	}
    }
    /**
     * Create a new TasmContigBuilder instance setting the
     * initial contig state to have 
     * the same contig, id, and underlying reads
     * as the given contig. If the contig id
     * is a positive numeric, then the TigrProjectAssemblyId
     * will also be set to the contig id.  This can
     * be changed by calling {@link #setTigrProjectAssemblyId(Long)}.
     * All other {@link TasmContig} optional specific attributes
     * will be set to the default values of {@code null}
     * or {@code false}.
     * @param copy the contig to copy; can not be null.
     * @throws NullPointerException if copy is null.
     */
    public  TasmContigBuilder(Contig<? extends AssembledRead> copy){
    	this(copy.getId(), copy.getConsensusSequence());
        StreamingIterator<? extends AssembledRead> iter =null;
        try{
        	 iter = copy.getReadIterator();
        	 while(iter.hasNext()){
        		 AssembledRead read = iter.next();
        		 addRead(new TasmAssembledReadAdapter(read));
        	 }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }
    /**
     * Create a new Builder instance with
     * the initial state of a copy of the given {@link TasmContig}.
     * The contig id, consensus, underlying reads and all tasm
     * specific attributes will be identical to the values
     * of the input {@link TasmContig}.  These initial 
     * values may be changed by the mutator methods
     * of this class.
     * 
     * @param copy the TasmContig to copy; can not be null.
     * @throws NullPointerException if copy is null.
     */
    public  TasmContigBuilder(TasmContig copy){
        this(copy.getId(), copy.getConsensusSequence());
        StreamingIterator<TasmAssembledRead> iter =null;
        try{
        	 iter = copy.getReadIterator();
        	 while(iter.hasNext()){
        		 TasmAssembledRead read = iter.next();
        		 addRead(read);
        	 }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        this.celeraAssemblerId = copy.getCeleraAssemblerId();
        this.sampleId = copy.getSampleId();
        this.asmblId = copy.getTigrProjectAssemblyId();
        
        this.assemblyMethod = copy.getAssemblyMethod();
        this.comment = copy.getComment();
        this.commonName = copy.getCommonName();
        this.editDate = Long.valueOf(copy.getEditDate().getTime());
        this.editPerson = copy.getEditPerson();
        this.isCircular = copy.isCircular();
        if(copy.getNumberOfReads()==0D){
        	this.avgCoverage = copy.getAvgCoverage();
        }
     }
   
   
    @Override
    public TasmContigBuilder addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){            
        if(offset <0){
            throw new IllegalArgumentException("circular reads not supported");
            
          }
        super.addRead(id, offset, validRange, basecalls, dir,fullUngappedLength);
        return this;            
    }
    
   
   
   
    @SuppressWarnings("unchecked")
	@Override
	public Collection<TasmAssembledReadBuilder> getAllAssembledReadBuilders() {
		return (Collection<TasmAssembledReadBuilder>) super.getAllAssembledReadBuilders();
	}
	@Override
	public TasmAssembledReadBuilder getAssembledReadBuilder(
			String readId) {
		return (TasmAssembledReadBuilder)super.getAssembledReadBuilder(readId);
	}
	/**
     * Sets the comment
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
    public TasmContigBuilder withComment(String comment){
       this.comment=comment;
        return this;
    }
    /**
     * Sets the com_name
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
    public TasmContigBuilder withCommonName(String commonName){
       this.commonName = commonName;
        return this;
    }
    
    /**
     * Sets the number of reads and avg coverage
     * attributes for this contig.  If these values are not set,
     * or set to {@code null}, then the values
     * will be computed by counting the bases in the
     * underlying reads.  Calling this method
     * multiple times will overwrite previous entries with the current
     * entry. Setting the values to {@code null} will remove the current
     * entries (and can later be re-added by calling this method 
     * again with a non-null values).
     * @param numberOfReads the value to set as the number of
     * reads
     * <strong>regardless</strong> of what the actual
     * underlying read count is.
     * @param avgCoverage the value to set as the average coverage
     * <strong>regardless</strong> of what the actual
     * underlying reads are.
     * @return this.
     * @throws IllegalArgumentException if avgCoverage &lt; 0
     */
    public TasmContigBuilder setCoverageInfo(Integer numberOfReads, Double avgCoverage){
    	if(numberOfReads !=null && numberOfReads <0){
    		throw new IllegalArgumentException("number of reads must be >= 0");
    	}
    	if(avgCoverage !=null && avgCoverage <0D){
    		throw new IllegalArgumentException("avg coverage must be >= 0");
    	}
       this.avgCoverage = avgCoverage;
       this.numberOfReads = numberOfReads;
        return this;
    }
    /**
     * Sets the Celeara Assembler UID
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
    public TasmContigBuilder withCeleraAssemblerContigId(Long caId){
    	if(caId==null){
    		this.celeraAssemblerId = null;
    	}else{
    		this.celeraAssemblerId = caId;
    	}
        return this;
    }
    
    /**
     * Sets the bac_id
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
    public TasmContigBuilder setSampleId(String sampleId){
       if(sampleId==null){
    	   this.sampleId = null;
       }else{
    	   this.sampleId = Integer.parseInt(sampleId);
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
	public TasmContigBuilder withMethod(String method){
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
	public TasmContigBuilder withEditInfo(String editPerson, Date editDate){
		if(editPerson ==null || editDate ==null){
			this.editPerson = null;
			this.editDate = null;
		}else{
			this.editDate = Long.valueOf(editDate.getTime());
			this.editPerson = editPerson;
		}
		return this;
	}

	public TasmContigBuilder isCircular(boolean isCircular){
		this.isCircular = isCircular;
		return this;
	}
	
	public TasmContigBuilder setTigrProjectAssemblyId(Long asmblId){
		this.asmblId = asmblId;
		return this;
	}
    @Override
    public DefaultTasmContig build() {
    	 if(consensusCaller !=null){
 			recallConsensusNow();
         }
    	 int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfReads());
        Map<String,TasmAssembledRead> reads = new LinkedHashMap<String, TasmAssembledRead>(capacity);
        NucleotideSequence consensus = getConsensusBuilder().build();
        for(AssembledReadBuilder<TasmAssembledRead> builder : getAllAssembledReadBuilders()){
            reads.put(builder.getId(),builder.build(consensus));
        }
        return new DefaultTasmContig(this, consensus,reads, avgCoverage, numberOfReads);
    }

    
    /**
    * {@inheritDoc}
    */
    @Override
    protected TasmAssembledReadBuilder createPlacedReadBuilder(
            TasmAssembledRead read) {
        TasmAssembledReadBuilder builder =DefaultTasmAssembledRead.createBuilder(
                read.getId(), 
                read.getNucleotideSequence().toString(), 
                (int)read.getGappedStartOffset(), 
                read.getDirection(), 
                read.getReadInfo().getValidRange(), 
                read.getReadInfo().getUngappedFullLength());

        return builder;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    protected TasmAssembledReadBuilder createPlacedReadBuilder(
            String id, int offset, Range validRange, String basecalls,
            Direction dir, int fullUngappedLength) {
        return DefaultTasmAssembledRead.createBuilder(
                id, 
                basecalls, 
                offset, 
                dir, 
                validRange, 
                fullUngappedLength);
    }
    /**
     * {@code DefaultTasmContig} is a {@link Contig}
     * implementation for TIGR Assembler contig data.
     * @author dkatzel
     *
     *
     */
    private static final class DefaultTasmContig implements TasmContig{
    	private final Long celeraAssemblerId;
    	private final String comment, commonName;
    	private final Integer sampleId;
    	private final Long asmblId;
    	
    	private final String editPerson;
    	private final Long editDate;
    	private final String assemblyMethod;
    	private final double avgCoverage;
    	private final boolean isCircular;
    	private final int numberOfReads;
        private final Contig<TasmAssembledRead> contig;
        /**
         * @param id
         * @param consensus
         * @param placedReads
         * @param circular
         */
        private DefaultTasmContig(TasmContigBuilder builder, NucleotideSequence consensus, Map<String,TasmAssembledRead> reads,
        		Double userProvidedAvgCoverage, Integer userProvidedNumberOfReads) {
            contig = new DefaultContig<TasmAssembledRead>(builder.getContigId(),
            		consensus,reads);
            this.numberOfReads = userProvidedNumberOfReads ==null ? (int) contig.getNumberOfReads() : userProvidedNumberOfReads;
            if(userProvidedAvgCoverage ==null){
	            if(numberOfReads >0){
	    	        long totalNumberOfReadBases=0L;
	    	        
	    	        for(TasmAssembledRead read : reads.values()){
	    	        	totalNumberOfReadBases+=read.getNucleotideSequence().getUngappedLength();
	    	        }
	    	       
	    			avgCoverage = totalNumberOfReadBases/(double) contig.getConsensusSequence().getUngappedLength();
	            }else{
	            	avgCoverage=0D;
	            }
            }else{
            	this.avgCoverage = userProvidedAvgCoverage;
            }
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
		public boolean isAnnotationContig() {
			return contig.getNumberOfReads()==0;
		}

		@Override
    	public String getId() {
    		return contig.getId();
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
    		return new Date(editDate);
    	}
    	@Override
    	public boolean isCircular() {
    		return isCircular;
    	}


    	@Override
    	public long getNumberOfReads() {
    		return numberOfReads;
    	}



    	@Override
    	public NucleotideSequence getConsensusSequence() {
    		return contig.getConsensusSequence();
    	}



    	@Override
    	public TasmAssembledRead getRead(String id) {
    		return contig.getRead(id);
    	}



    	@Override
    	public boolean containsRead(String readId) {
    		return contig.containsRead(readId);
    	}



    	@Override
    	public StreamingIterator<TasmAssembledRead> getReadIterator() {
    		return contig.getReadIterator();
    	}


        


    	


    	@Override
    	public int hashCode() {
    		final int prime = 31;
    		int result = 1;
    		result = prime * result + ((asmblId == null) ? 0 : asmblId.hashCode());
    		result = prime * result
    				+ ((assemblyMethod == null) ? 0 : assemblyMethod.hashCode());
    		long temp;
    		temp = Double.doubleToLongBits(avgCoverage);
    		result = prime * result + (int) (temp ^ (temp >>> 32));
    		result = prime
    				* result
    				+ ((celeraAssemblerId == null) ? 0 : celeraAssemblerId
    						.hashCode());
    		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
    		result = prime * result
    				+ ((commonName == null) ? 0 : commonName.hashCode());
    		result = prime * result + ((contig == null) ? 0 : contig.hashCode());
    		result = prime * result
    				+ ((editDate == null) ? 0 : editDate.hashCode());
    		result = prime * result
    				+ ((editPerson == null) ? 0 : editPerson.hashCode());
    		result = prime * result + (isCircular ? 1231 : 1237);
    		result = prime * result
    				+ ((sampleId == null) ? 0 : sampleId.hashCode());
    		return result;
    	}

    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if (obj == null) {
    			return false;
    		}
    		if (!(obj instanceof TasmContig)) {
    			return false;
    		}
    		TasmContig other = (TasmContig) obj;
    		if (!contig.getId().equals(other.getId())) {
    			return false;
    		}
    		if (!contig.getConsensusSequence().equals(other.getConsensusSequence())) {
    			return false;
    		}
    		if (contig.getNumberOfReads()!=other.getNumberOfReads()) {
    			return false;
    		}
    		if(!readsMatch(other)){
    			return false;
    		}
    		if (asmblId == null) {
    			if (other.getTigrProjectAssemblyId() != null) {
    				return false;
    			}
    		} else if (!asmblId.equals(other.getTigrProjectAssemblyId())) {
    			return false;
    		}
    		if (assemblyMethod == null) {
    			if (other.getAssemblyMethod() != null) {
    				return false;
    			}
    		} else if (!assemblyMethod.equals(other.getAssemblyMethod())) {
    			return false;
    		}
    		if (Double.doubleToLongBits(avgCoverage) != Double
    				.doubleToLongBits(other.getAvgCoverage())) {
    			return false;
    		}
    		if (celeraAssemblerId == null) {
    			if (other.getCeleraAssemblerId() != null) {
    				return false;
    			}
    		} else if (!celeraAssemblerId.equals(other.getCeleraAssemblerId())) {
    			return false;
    		}
    		if (comment == null) {
    			if (other.getComment() != null) {
    				return false;
    			}
    		} else if (!comment.equals(other.getComment())) {
    			return false;
    		}
    		if (commonName == null) {
    			if (other.getCommonName() != null) {
    				return false;
    			}
    		} else if (!commonName.equals(other.getCommonName())) {
    			return false;
    		}
    		
    		if (editDate == null) {
    			if (other.getEditDate() != null) {
    				return false;
    			}
    		} else if (editDate.longValue()!=other.getEditDate().getTime()) {
    			return false;
    		}
    		if (editPerson == null) {
    			if (other.getEditPerson() != null) {
    				return false;
    			}
    		} else if (!editPerson.equals(other.getEditPerson())) {
    			return false;
    		}
    		if (isCircular != other.isCircular()) {
    			return false;
    		}
    		if (sampleId == null) {
    			if (other.getSampleId() != null) {
    				return false;
    			}
    		} else if (!sampleId.equals(other.getSampleId())) {
    			return false;
    		}
    		return true;
    	}

        private boolean readsMatch(TasmContig other) {

            try (StreamingIterator<TasmAssembledRead> readIter = contig
                    .getReadIterator()) {
                while (readIter.hasNext()) {
                    TasmAssembledRead read = readIter.next();
                    String readId = read.getId();
                    if (!other.containsRead(readId)) {
                        return false;
                    }
                    if (!read.equals(other.getRead(readId))) {
                        return false;
                    }
                }
                return true;
            }
        }

    }

}
