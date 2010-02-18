/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.glyph.GlyphCodec;
/**
 * <code>DefaultNucleotideGlyphCodec</code> is the implementation
 * of {@link GlyphCodec} that can converts {@link NucleotideGlyph}s
 * into a 4 bit representation.  This allows 2 {@link NucleotideGlyph}s
 * to be packed into a single byte.
 * @author dkatzel
 *
 *
 */
public final class DefaultNucleotideGlyphCodec implements GlyphCodec<NucleotideGlyph>{

    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complimenting.
     */
    private static final Map<Byte, NucleotideGlyph> BYTE_TO_GLYPH_MAP = new HashMap<Byte, NucleotideGlyph>();
    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complimenting.
     */
    private static final Map<NucleotideGlyph, Byte> GLYPH_TO_BYTE_MAP = new EnumMap<NucleotideGlyph, Byte>(NucleotideGlyph.class);
    /**
     * The header will contain an int value specifying how many glyphs are encoded.
     */
    private static final int HEADER_LENGTH = 4;
    /**
     * populate the maps.
     * Each byte key has been specially assigned so
     * that flipping the bits (then bit masking)
     * returns the byte value for the key of the reverse compliment
     * glyph where applicable.
     */
    static{
        //special case for
        //Gap and Unknown since they compliment to themselves
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x00), NucleotideGlyph.Gap);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0F), NucleotideGlyph.Unknown);
        //everything else has a compliment
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x01), NucleotideGlyph.Adenine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0E), NucleotideGlyph.Thymine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x02), NucleotideGlyph.Guanine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0D), NucleotideGlyph.Cytosine);

        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x03), NucleotideGlyph.Pyrimidine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0C), NucleotideGlyph.Purine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x04), NucleotideGlyph.Weak);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0B), NucleotideGlyph.Strong);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x05), NucleotideGlyph.Keto);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0A), NucleotideGlyph.Amino);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x06), NucleotideGlyph.NotCytosine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x09), NucleotideGlyph.NotGuanine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x07), NucleotideGlyph.NotThymine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x08), NucleotideGlyph.NotAdenine);
        //populate the reverse mapping
        for(Entry<Byte, NucleotideGlyph> entry : BYTE_TO_GLYPH_MAP.entrySet()){
            GLYPH_TO_BYTE_MAP.put(entry.getValue(), entry.getKey());           
        }
    }
    private static DefaultNucleotideGlyphCodec INSTANCE = new DefaultNucleotideGlyphCodec();
    private DefaultNucleotideGlyphCodec(){}
    public static DefaultNucleotideGlyphCodec getInstance(){
        return INSTANCE;
    }
    @Override
    public List<NucleotideGlyph> decode(byte[] encodedGlyphs) {
        int length = decodedLengthOf(encodedGlyphs);
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>(length);
        
        for(int i=HEADER_LENGTH; i<encodedGlyphs.length-1; i++){
            result.addAll(decodeNext2Values(encodedGlyphs[i]));
        }
        if(length>0){
            if(isEven(length)){
                result.addAll(decodeNext2Values(encodedGlyphs[encodedGlyphs.length-1]));
            }
            else{
                result.add(decodeLastValues(encodedGlyphs[encodedGlyphs.length-1]));
            }
        }
        return result;
    }
    @Override
    public NucleotideGlyph decode(byte[] encodedGlyphs, int index){
        final byte getByteForGlyph = getEncodedByteForGlyph(encodedGlyphs,index);
        return decode(getByteForGlyph, isEven(index));
    }
    private NucleotideGlyph decode(final byte getByteForGlyph, boolean isFirstNibble) {
        List<NucleotideGlyph> values = decodeNext2Values(getByteForGlyph);
        if(isFirstNibble){
            return values.get(0);
        }
        return values.get(1);
    }
    private byte getEncodedByteForGlyph(byte[] encodedGlyphs, int index) {
        final int encodedIndex = computeEncodedIndexForGlyph(index);
        if(encodedIndex >= encodedGlyphs.length){
            throw new ArrayIndexOutOfBoundsException("index "+index + " corresponds to encodedIndex "+encodedIndex + "  encodedglyph length is "+encodedGlyphs.length);
        }
        final byte getByteForGlyph = encodedGlyphs[encodedIndex];
        return getByteForGlyph;
    }
    private int computeEncodedIndexForGlyph(int index) {
        final int encodedIndexForGlyph = HEADER_LENGTH+index/2;
        return encodedIndexForGlyph;
    }

    @Override
    public byte[] encode(List<NucleotideGlyph> glyphs) {
        final int unEncodedSize = glyphs.size();
        
        int encodedSize = computeEncodedSize(unEncodedSize);
        return encodeGlyphs(glyphs, unEncodedSize, encodedSize);
        
    }
    private byte[] encodeGlyphs(List<NucleotideGlyph> glyphs,
            final int unEncodedSize, int encodedSize) {
        ByteBuffer result = ByteBuffer.allocate(encodedSize);
        result.putInt(unEncodedSize);
        encodeAllButTheLastByte(glyphs, unEncodedSize, result);
        encodeFinalByte(glyphs, unEncodedSize, result);
        return result.array();
    }
    /**
     * pack every 2 glyphs into a single byte.  this method
     * encodes all glyphs upto but not including the final byte
     * since the final byte is a special case.
     * @param glyphs
     * @param unEncodedSize
     * @param result
     */
    private void encodeAllButTheLastByte(List<NucleotideGlyph> glyphs,
            final int unEncodedSize, ByteBuffer result) {
        for(int i=0; i<unEncodedSize-2; i+=2){
            encodeNext2Values(glyphs, i, result);
        }
    }
    /**
     * the final encoded byte is a special case because
     * there may only be a single glyph inside if
     * the unencoded size is odd.
     * @param glyphs
     * @param unEncodedSize
     * @param result
     */
    private void encodeFinalByte(List<NucleotideGlyph> glyphs,
            final int unEncodedSize, ByteBuffer result) {
        if(unEncodedSize>0){
            final boolean even = isEven(unEncodedSize);
            if(even){
                encodeNext2Values(glyphs, unEncodedSize-2, result);
            }
            else{
                encodeLastValue(glyphs, result);
            }
        }
    }
    private int computeEncodedSize(final int size) {
        int encodedSize = 4 + size/2 + (isEven(size)?0:1);
        return encodedSize;
    }
    private boolean isEven(final int size) {
        return size%2==0;
    }
    private void encodeLastValue(List<NucleotideGlyph> glyphs, ByteBuffer result) {
        byte hi = GLYPH_TO_BYTE_MAP.get(glyphs.get(glyphs.size()-1));
        result.put((byte) ((hi<<4) &0xFF));
    }
    private void encodeNext2Values(List<NucleotideGlyph> glyphs, int i, ByteBuffer result) {
        byte hi = GLYPH_TO_BYTE_MAP.get(glyphs.get(i));
        byte low = GLYPH_TO_BYTE_MAP.get(glyphs.get(i+1));
        result.put((byte) ((hi<<4 | low) &0xFF));
    }
    private List<NucleotideGlyph> decodeNext2Values(byte b) {
        byte hi = (byte)(b>>>4 &0x0F);
        byte low = (byte)(b & 0x0F);
       return Arrays.asList(BYTE_TO_GLYPH_MAP.get(hi),BYTE_TO_GLYPH_MAP.get(low));
    }
    private NucleotideGlyph decodeLastValues(byte b) {
        byte hi = (byte)(b>>>4 &0x0F);
       return BYTE_TO_GLYPH_MAP.get(hi);
    }
    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        return buf.getInt();
    }
}
