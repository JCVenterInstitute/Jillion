package org.jcvi.jillion.sam;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jcvi.jillion.core.io.IOUtil;

public class SamRecordFlags {

    private static final ConcurrentHashMap<Integer, SamRecordFlags> CACHE = new ConcurrentHashMap<>();
    
    public static SamRecordFlags valueOf(int bits){
        return CACHE.computeIfAbsent(bits, i -> new SamRecordFlags(i));
    }
    public static SamRecordFlags valueOf(BitSet bits){
        return valueOf((int)bits.toLongArray()[0]);
    }
    public static SamRecordFlags valueOf(Collection<SamRecordFlag> flags){
        int v =0;
        for(SamRecordFlag f : flags){
            v |=f.getBitFlags();
        }
        return valueOf(v);
    }
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
    
    public int asInt(){
        return setBits;
    }
    
    public BitSet asBitSet(){
        return IOUtil.toBitSet(setBits);
    }
    
    public boolean contains(SamRecordFlag flag){
        return flag.matches(setBits);
    }
    
    public SamRecordFlags add(SamRecordFlag flag){
        int newValue = setBits;
        newValue |= flag.getBitFlags();
        return valueOf(newValue);
    }
    public SamRecordFlags add(SamRecordFlag flag, SamRecordFlag...additionalFlags){
        int newValue = setBits;
        newValue |= flag.getBitFlags();
        for(SamRecordFlag f : additionalFlags){
            newValue |= f.getBitFlags();
        }
        return valueOf(newValue);
    }
    
    public SamRecordFlags remove(SamRecordFlag flag){
        int newValue = setBits;
        newValue ^= flag.getBitFlags();
        return valueOf(newValue);
    }
    public SamRecordFlags remove(SamRecordFlag flag, SamRecordFlag...additionalFlags){
        int newValue = setBits;
        newValue ^= flag.getBitFlags();
        for(SamRecordFlag f : additionalFlags){
            newValue ^= f.getBitFlags();
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
    
    Set<SamRecordFlag> getFlags(){
        return SamRecordFlag.parseFlags(setBits);
    }
    
    @Override
    public String toString() {
        return "SamRecordFlags [setBits=" + setBits + " flags = " + getFlags() + " ]";
    }
    
    public boolean isReferenceSequence(){
        return setBits == 516;
    }
    /**
     * Is this record a dummy sequence where only the 
     * property annotations are used.
     * @return
     */
    public boolean isAnnotation(){
        
        return setBits == 768;
    }
    
    
}
