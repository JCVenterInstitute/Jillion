package org.jcvi.jillion.sam;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * Immutable Wrapper around bit flags set in a SAM/BAM file to show which
 * {@link SamRecordFlag} values are present in this SamRecord.
 * This class uses the flyweight pattern to save memory
 * since most of the millions of reads in BAM file will have the same
 * flag combinations.
 * 
 * Several mutator methods in this class such as {@link #add(SamRecordFlag)} and {@link #remove(SamRecordFlag)}
 * return new/different instances since these objects are immutable.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
public class SamRecordFlags implements Serializable{

    
	private static final long serialVersionUID = 8586093977275081582L;
	private static final ConcurrentHashMap<Integer, SamRecordFlags> CACHE = new ConcurrentHashMap<>();
    /**
     * Get the {@link SamRecordFlags} object for the given set bits as an int (as it is stored in BAM).
     * @param bits the bit values for the set flag; will always be >=0.
     * 
     * @return a {@link SamRecordFlags} object which may be the same reference as
     *         previously returned objects. Will never be null.
     */
    public static SamRecordFlags valueOf(int bits){
        return CACHE.computeIfAbsent(bits, i -> new SamRecordFlags(i));
    }
    /**
     * Get the {@link SamRecordFlags} object for the given set bits stored in a BitSet.
     * @param bits the bit values for the set flag; can not be null.
     * 
     * @return a {@link SamRecordFlags} object which may be the same reference as
     *         previously returned objects. Will never be null.
     *         
     * @throws NullPointerException if bits is null.
     */
    public static SamRecordFlags valueOf(BitSet bits){
        return valueOf((int)bits.toLongArray()[0]);
    }
    /**
     * Get the {@link SamRecordFlags} object for the given collection of SamRecordFlag objects.
     * @param flags the {@link SamRecordFlag} objects to turn into a SamRecordFlags object.
     * 
     * @return a {@link SamRecordFlags} object which may be the same reference as
     *         previously returned objects. Will never be null.
     *         
     * @throws NullPointerException if the collection  is null or any value inside the collection is null.
     */
    public static SamRecordFlags valueOf(Collection<SamRecordFlag> flags){
        int v =0;
        for(SamRecordFlag f : flags){
            v |=f.getBitFlags();
        }
        return valueOf(v);
    }
    /**
     * Get the {@link SamRecordFlags} object for the given array/varargs of SamRecordFlag objects.
     * @param flags the {@link SamRecordFlag} objects to turn into a SamRecordFlags object.
     * 
     * @return a {@link SamRecordFlags} object which may be the same reference as
     *         previously returned objects. Will never be null.
     *         
     * @throws NullPointerException if any value inside the collection is null.
     */
    public static SamRecordFlags valueOf(SamRecordFlag... flags){
        int v =0;
        for(SamRecordFlag f : flags){
            v |=f.getBitFlags();
        }
        return valueOf(v);
    }
    
    private int setBits;
    
    private SamRecordFlags(int setBits){
        this.setBits = setBits;
    }
    /**
     * Get the flags as an int like the one used to encode SAM/BAM files.
     * @return the set bits as an int.
     */
    public int asInt(){
        return setBits;
    }
    /**
     * Get the flags as an BitSet where the values present will be switched to "on".
     * @return a new BitSet with the values for the present flags set.
     */
    public BitSet asBitSet(){
        return IOUtil.toBitSet(setBits);
    }
    /**
     * Does this flags object contain the given flag.
     * @param flag the flag to check can not be null.
     * @return true if it contains; false otherwise.
     * 
     * @throws NullPointerException if flag is null.
     */
    public boolean contains(SamRecordFlag flag){
        return flag.matches(setBits);
    }
    /**
     * Add the given flag to the current set AND RETURN A DIFFERENT OBJECT.
     * @param flag the flag to add can not be null.
     * @return A different SamRecordFlags object than this since SamRecordFlags is immutable.
     * 
     * @throws NullPointerException if flag is null.
     */
    public SamRecordFlags add(SamRecordFlag flag){
        int newValue = setBits;
        newValue |= flag.getBitFlags();
        return valueOf(newValue);
    }
    /**
     * Add the given flags to the current set AND RETURN A DIFFERENT OBJECT.
     * @param flag the flag to add can not be null.
     * @param additionalFlags more  flags to add can not be null nor contain any null values.
     * @return A different SamRecordFlags object than this since SamRecordFlags is immutable.
     * 
     * @throws NullPointerException if any flag is null.
     */
    public SamRecordFlags add(SamRecordFlag flag, SamRecordFlag...additionalFlags){
        int newValue = setBits;
        newValue |= flag.getBitFlags();
        for(SamRecordFlag f : additionalFlags){
            newValue |= f.getBitFlags();
        }
        return valueOf(newValue);
    }
    /**
     * Removes the given flag to the current set AND RETURN A DIFFERENT OBJECT.
     * @param flag the flag to remove can not be null.
     * @param additionalFlags more  flags to remove can not be null nor contain any null values.
     * @return A different SamRecordFlags object than this since SamRecordFlags is immutable.
     * 
     * @throws NullPointerException any flag is null.
     */
    public SamRecordFlags remove(SamRecordFlag flag){
        int newValue = setBits;
        if((newValue | flag.getBitFlags()) == newValue) {
        	newValue ^= flag.getBitFlags();
        }
        return valueOf(newValue);
    }
    /**
     * Removes the given flag to the current set AND RETURN A DIFFERENT OBJECT.
     * @param flag the flag to remove can not be null.
     * @return A different SamRecordFlags object than this since SamRecordFlags is immutable.
     * 
     * @throws NullPointerException if flag is null.
     */
    public SamRecordFlags remove(SamRecordFlag flag, SamRecordFlag...additionalFlags){
        int newValue = setBits;
        if((newValue | flag.getBitFlags()) == newValue) {
        	newValue ^= flag.getBitFlags();
        }
        for(SamRecordFlag f : additionalFlags){
        	if((newValue | f.getBitFlags()) == newValue) {
        		newValue ^= f.getBitFlags();
        	}
        }
        return valueOf(newValue);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + setBits;
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
        if (!(obj instanceof SamRecordFlags)) {
            return false;
        }
        SamRecordFlags other = (SamRecordFlags) obj;
        if (setBits != other.setBits) {
            return false;
        }
        return true;
    }
    /**
     * Get the flags as a Set of {@link SamRecordFlag} objects.
     * @return a new Set.
     */
    public Set<SamRecordFlag> getFlags(){
        return SamRecordFlag.parseFlags(setBits);
    }
    
    @Override
    public String toString() {
        return "SamRecordFlags [setBits=" + setBits + " flags = " + getFlags() + " ]";
    }
    /**
     * Is this potentially a reference sequence.
     * @return true if the bits == 516 as per the SAM spec.
     */
    public boolean maybeReferenceSequence(){
        return setBits == 516;
    }
    /**
     * Is this record a dummy sequence where only the 
     * property annotations are used.
     * @return true if flags is set to 768 as per the SAM spec.
     */
    public boolean isAnnotation(){
        
        return setBits == 768;
    }
    
    private Object writeReplace(){
		return new FlagProxy(this);
	}
	
	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}
	
	private static class FlagProxy implements Serializable{
		
		private static final long serialVersionUID = -8170063982187142849L;
		private int flags;
		public FlagProxy(SamRecordFlags flags) {
			this.flags = flags.asInt();
		}
		
		private Object readResolve(){
			return SamRecordFlags.valueOf(flags);
		}
	}
}
