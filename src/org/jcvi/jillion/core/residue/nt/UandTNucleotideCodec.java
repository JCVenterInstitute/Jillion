package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Special codec to handle the weird sequences used in some therapeutics that
 * have both Ts and Us.  This codec stores everything as bytes no bit packing
 * since U is ordinal 17.
 *
 * @since 5.3
 */
final class UandTNucleotideCodec  extends AbstractNucleotideCodec{

    public static final UandTNucleotideCodec INSTANCE = new UandTNucleotideCodec();

    private UandTNucleotideCodec(){
        super(Nucleotide.Gap);
    }

    @Override
    protected int getNucleotidesPerGroup() {
        return 1;
    }

    @Override
    protected Nucleotide getNucleotide(byte encodedByte, int index){
        return getGlyphFor(encodedByte);
    }

    @Override
    protected byte getByteFor(Nucleotide nuc) {
        return nuc.getOrdinalAsByte();
    }

    @Override
    protected Nucleotide getGlyphFor(byte b) {
        return Nucleotide.getByOrdinal(b);
    }

    @Override
    protected void encodeCompleteGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
        result.put( getByteFor(glyphs.next()));
    }

    @Override
    protected void encodeLastGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
       result.put( glyphs.hasNext() ? getByteFor(glyphs.next()) : 0);
    }

    @Override
    public List<Range> getNRanges(byte[] encodedData){
        List<Range> ranges = new ArrayList<>();
        Iterator<Nucleotide> iter = iterator(encodedData);
        int offset=0;
        Range.Builder currentBuilder = null;
        while(iter.hasNext()){
            Nucleotide n = iter.next();
            if(n == Nucleotide.Unknown){
                if(currentBuilder == null){
                    currentBuilder = new Range.Builder(1)
                            .shift(offset);
                }else{
                    //do we grow or make a new one?
                    if(currentBuilder.getEnd() == offset -1){
                        //grow
                        currentBuilder.expandEnd(1);
                    }else{
                        //new range
                        ranges.add(currentBuilder.build());

                        currentBuilder = new Range.Builder(1)
                                .shift(offset);
                    }
                }
            }
            offset++;
        }
        if(currentBuilder !=null){
            ranges.add(currentBuilder.build());

        }
        return ranges;
    }

    @Override
    public List<Integer> getGapOffsets(byte[] encodedData) {
        GrowableIntArray array = this.getSentinelOffsets(encodedData);

        return array.toBoxedList();
    }
}
