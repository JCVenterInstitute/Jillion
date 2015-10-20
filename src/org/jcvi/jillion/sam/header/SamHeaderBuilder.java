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
package org.jcvi.jillion.sam.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.sam.SamRecordI;
import org.jcvi.jillion.sam.SamValidationException;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;

/**
 * {@code Builder}
 * is the builder class used to construct
 * a {@link SamHeader}
 * @author dkatzel
 *
 */
public final class SamHeaderBuilder{
	//default to unknown if not specified
	private SortOrder sortOrder = SortOrder.UNKNOWN;
	private SamVersion version;
	
	private final Map<String, SamReferenceSequence> referenceSequences = new LinkedHashMap<String, SamReferenceSequence>();
	
	private final Map<String, SamReadGroup> readGroups = new HashMap<String, SamReadGroup>();
	
	private final Map<String, SamProgram> programs = new HashMap<String, SamProgram>();
	
	private final List<String> comments = new ArrayList<String>();
	
	/**
	 * Create a new Builder instance with 
	 * the default values
	 * of "unknown" sort order
	 * and nothing or empty for everything else.
	 * 
	 * Use the methods on the builder
	 * to add/set values for the header.
	 */
	public SamHeaderBuilder(){
		//use defaults
	}
	
	public SamHeaderBuilder(SamHeader copy) {
		this.sortOrder = copy.getSortOrder();
		this.version = copy.getVersion();
		for(SamReferenceSequence ref :copy.getReferenceSequences()){
			referenceSequences.put(ref.getName(), ref);
		}
		for(SamReadGroup readGroup :copy.getReadGroups()){
			readGroups.put(readGroup.getId(), readGroup);
		}
		for(SamProgram prog :copy.getPrograms()){
			programs.put(prog.getId(), prog);
		}
		comments.addAll(copy.getComments());
	}

	public SamHeaderBuilder setVersion(SamVersion version){
		this.version = version;
		return this;
	}
	/**
	 * Add a {@link ReferenceSequence}
	 * to this header.  The order that the references are added
	 * defines the alignment sorting order
	 * for some {@link SortOrder} implementations.
	 * @param refSeq the {@link ReferenceSequence} to add;
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if refSeq is null.
	 * @throws IllegalArgumentException if a {@link ReferenceSequence}
	 * with the same name has already been added to this builder.
	 */
	public SamHeaderBuilder addReferenceSequence(SamReferenceSequence refSeq){
		if(refSeq ==null){
			throw new NullPointerException("reference sequence can not be null");
		}
		if(referenceSequences.containsKey(refSeq.getName())){
			throw new IllegalArgumentException("reference sequence with same name already in header : "+ refSeq.getName());
		}
		referenceSequences.put(refSeq.getName(), refSeq);
		
		return this;
	}
	
	/**
	 * Add a {@link ReadGroup}
	 * to this header. Each record added
	 * must have a unique Id.
	 * @param readGroup the {@link ReadGroup} to add;
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if readGroup is null.
	 * @throws IllegalArgumentException if a {@link ReadGroup}
	 * with the same id has already been added to this builder.
	 */
	public SamHeaderBuilder addReadGroup(SamReadGroup readGroup){
		if(readGroup ==null){
			throw new NullPointerException("read group can not be null");
		}
		if(readGroups.containsKey(readGroup.getId())){
			throw new IllegalArgumentException("read group with same id already in header : "+ readGroup.getId());
		}
		readGroups.put(readGroup.getId(), readGroup);
		
		return this;
	}
	/**
	 * Add a {@link SamProgram}
	 * to this header. Each record added
	 * must have a unique Id.
	 * @param program the {@link SamProgram} to add;
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if program is null.
	 * @throws IllegalArgumentException if a {@link SamProgram}
	 * with the same id has already been added to this builder.
	 */
	public SamHeaderBuilder addProgram(SamProgram program){
		if(program ==null){
			throw new NullPointerException("program can not be null");
		}
		if(programs.containsKey(program.getId())){
			throw new IllegalArgumentException("program with same id already in header : "+ program.getId());
		}
		programs.put(program.getId(), program);
		
		return this;
	}
	/**
	 * Add a comment line to the list of comments.
	 * @param commentLine the comment line to add;
	 * can not be null.
	 * @return this.
	 * @throws NullPointerException if commentLine is null.
	 */
	public SamHeaderBuilder addComment(String commentLine){
		if(commentLine ==null){
			throw new NullPointerException("commentLine can not be null");
		}
		comments.add(commentLine);
		return this;
	}
	/**
	 * Set the {@link SortOrder}.
	 * @param order the SortOrder;
	 * if this value is set to {@code null}
	 * then the value will be changed to
	 * {@link SortOrder#UNKNOWN}.
	 * @return this.
	 */
	public SamHeaderBuilder setSortOrder(SortOrder order){
		if(order ==null){
			this.sortOrder = SortOrder.UNKNOWN;
		}else{
			this.sortOrder = order;
		}
		return this;
		
	}
	/**
	 * Build a new SamHeader.
	 * @return
	 * @throws IllegalStateException if a {@link SamProgram}'s
	 * previous program id is not also included in this header.
	 */
	public SamHeader build(){
		//confirm all program's previous are known?
		for(SamProgram program : programs.values()){
			String prevId = program.getPreviousProgramId();
			if(prevId !=null && !programs.containsKey(prevId)){						
				throw new IllegalStateException("known previous program " + prevId + "referenced in " + program.getId());
			}
		}
		return new SamHeaderImpl(this);
	}

	public boolean hasReferenceSequence(String name) {
		return referenceSequences.containsKey(name);
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}
	
	private static final class SamHeaderImpl implements SamHeader {

	        private final SortOrder sortOrder;
	        private final SamVersion version;
	        
	        private final Map<String, SamReferenceSequence> referenceSequences;
	        private final Map<String, Integer> referenceIndexMap;
	        
	        private final SamReferenceSequence[] indexedReferenceSequences;
	        
	        private final Map<String, SamReadGroup> readGroups;
	        
	        private final Map<String, SamProgram> programs;
	        
	        private final List<String> comments;
	        
	        
	        SamHeaderImpl(SamHeaderBuilder builder){
	                this.sortOrder = builder.sortOrder;
	                this.version =  builder.version;
	                this.referenceSequences =  Collections.unmodifiableMap(new LinkedHashMap<String, SamReferenceSequence>(builder.referenceSequences));
	        
	                this.readGroups =  Collections.unmodifiableMap(new HashMap<String, SamReadGroup>(builder.readGroups));
	                this.programs =  Collections.unmodifiableMap(new HashMap<String, SamProgram>(builder.programs));
	                
	                this.comments = Collections.unmodifiableList(new ArrayList<String>(builder.comments));
	                referenceIndexMap = new LinkedHashMap<String, Integer>();
	               
	                int i=0;
	                Collection<SamReferenceSequence> refSeqs = referenceSequences.values();
	                this.indexedReferenceSequences = new SamReferenceSequence[refSeqs.size()];
	                        
                        for(SamReferenceSequence refSeq : refSeqs){
                            indexedReferenceSequences[i] = refSeq;
	                    referenceIndexMap.put(refSeq.getName(), Integer.valueOf(i));	                       
	                    i++;
	                }
	        }
	        
	    @Override
	        public String toString() {
	                return "SamHeader [sortOrder=" + sortOrder + ", version=" + version
	                                + ", referenceSequences=" + referenceSequences
	                                + ", referenceIndexMap=" + referenceIndexMap + ", readGroups="
	                                + readGroups + ", programs=" + programs + ", comments="
	                                + comments + "]";
	        }



	        @Override
	    public SortOrder getSortOrder() {
	                return sortOrder;
	        }



	        @Override
	    public SamVersion getVersion() {
	                return version;
	        }

	     

	        @Override
	    public SamReferenceSequence getReferenceSequence(String name){
	                return referenceSequences.get(name);
	        }
	     

	        @Override
	    public SamProgram getSamProgram(String id){
	                return programs.get(id);
	        }


	        @Override
	    public SamReadGroup getReadGroup(String id){
	                return readGroups.get(id);
	        }
	        @Override
	    public Collection<SamReadGroup> getReadGroups(){
	                return readGroups.values();
	        }
	        @Override
	    public Collection<SamProgram> getPrograms(){
	                return programs.values();
	        }
	        @Override
	    public Collection<SamReferenceSequence> getReferenceSequences(){
	                return referenceSequences.values();
	        }

	        
	    @Override
	        public int hashCode() {
	                final int prime = 31;
	                int result = 1;
	                result = prime * result
	                                + comments.hashCode();
	                result = prime * result
	                                +  programs.hashCode();
	                result = prime * result
	                                +  readGroups.hashCode();
	                result = prime
	                                * result
	                                + referenceSequences
	                                                .hashCode();
	                result = prime * result
	                                +  sortOrder.hashCode();
	                result = prime * result + (version==null? 0:version.hashCode());
	                return result;
	        }

	    
	    private <T> boolean collectionMatch(Iterable<T> expected, Iterable<T> actual){
	        Iterator<T> expectedIter = expected.iterator();
	        Iterator<T> actualIter = actual.iterator();
	        
	        while(expectedIter.hasNext()){
	           if(!actualIter.hasNext()){
	               return false;
	           }
	           if(!expectedIter.next().equals(actualIter.next())){
	               return false;
	           }
	        }
	        
	        if(actualIter.hasNext()){
	            return false;
	        }
	        return true;
	    }
	    @Override
	        public boolean equals(Object obj) {
	                if (this == obj) {
	                        return true;
	                }
	                
	                if (!(obj instanceof SamHeader)) {
	                        return false;
	                }
	                //all this collectionMatch() checks 
	                //because the getters return unmodifiableCollections
	                //which don't delegate equals() checks
	                //so we have to iterate through the whole thing
	                //to see if the same elements are present
	                //in the same order...
	                SamHeader other = (SamHeader) obj;
	                 if (!comments.equals(other.getComments())) {
	                        return false;
	                }
	                 if (!collectionMatch(programs.values(),other.getPrograms())) {
	                        return false;
	                }
	                if (!collectionMatch(readGroups.values(),other.getReadGroups())) {
	                        return false;
	                }
	                 if (!collectionMatch(referenceSequences.values(),other.getReferenceSequences())) {
	                        return false;
	                }
	                if (sortOrder != other.getSortOrder()) {
	                        return false;
	                }
	                if(version==null){
	                    if(other.getVersion() !=null){
	                        return false;
	                    }
	                }else if (!version.equals(other.getVersion())) {
	                        return false;
	                }
	                return true;
	        }



	        @Override
	    public List<String> getComments() {
	                return comments;
	        }


	        /**
	         * Validate the given {@link SamRecord} using the given
	         * {@link SamAttributeValidator}.
	         * <br/>
	         * A {@link SamRecord} is invalid if:
	         * <ul>
	         * <li>The {@link SamRecord#getReferenceName()} is not null
	         * and this SamHeader does not have the {@link ReferenceSequence} via 
	         * {@link #hasReferenceSequence(String)}.</li>
	         * <li>The {@link SamRecord#getNextName()} is not null
	         * and this SamHeader does not have the {@link ReferenceSequence} via 
	         * {@link #hasReferenceSequence(String)}.</li>
	         * <li>Any of the {@link SamAttribute}s in the {@link SamRecord}
	         * fail the validation of the given {@link SamAttributeValidator}</li>
	         * </ul>
	         * @param record the {@link SamRecord} to validate;
	         * can not be null.
	         * @param attributeValidator the {@link SamAttributeValidator};
	         * can not be null.
	         * @throws SamValidationException if there is a validation problem.
	         * @throws NullPointerException if either parameter is null.
	         */
	        @Override
	    public void validateRecord(SamRecordI record, SamAttributeValidator attributeValidator) throws SamValidationException{
	                //reference names must be present in a SQ-SN tag
	                String refName =record.getReferenceName();
	                if(refName !=null && this.getReferenceSequence(refName) ==null){
	                        throw new SamValidationException("unknown reference "+ refName);
	                }
	                String nextRefName = record.getNextName();
	                if(nextRefName !=null &&  !"=".equals(nextRefName) && this.getReferenceSequence(nextRefName)==null){
	                        throw new SamValidationException("unknown next reference "+ nextRefName);
	                }
	                for(SamAttribute attribute : record.getAttributes()){                   
	                                attributeValidator.validate(this, attribute);
	                        
	                }
	                
	        }
	    

	}
}
