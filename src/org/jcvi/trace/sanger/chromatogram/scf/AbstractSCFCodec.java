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
/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.DefaultSectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoder;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionEncoder;

import static org.jcvi.trace.sanger.chromatogram.scf.SCFUtils.*;
/**
 * <code>AbstractSCFCodec</code> is an abstract implementation
 * of {@link SCFCodec} that contains all the common steps in the
 * encoding and decoding algorithms of different SCF versions.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractSCFCodec implements SCFCodec{
    private SectionCodecFactory sectionCodecFactory;
    private SCFHeaderCodec headerCodec;
    
    /**
     * 
     */
    public AbstractSCFCodec(){
        this(new DefaultSCFHeaderCodec(), new DefaultSectionCodecFactory());
    }
    /**
     * Constructor.
     * @param headerCodec instance of {@link SCFHeaderCodec} used
     * to encode/ decode the {@link SCFHeader}.
     * @param sectionCodecFactory instance of {@link SectionCodecFactory}
     * which returns the version specific {@link SectionCodec}s needed
     * to encode and decode an {@link SCFChromatogram}.
     */
    public AbstractSCFCodec(SCFHeaderCodec headerCodec,SectionCodecFactory sectionCodecFactory){
        this.headerCodec = headerCodec;
        this.sectionCodecFactory = sectionCodecFactory;
    }

    /**
     *
    * {@inheritDoc}
     */
    public SCFChromatogram decode(InputStream in) throws SCFDecoderException{
           DataInputStream dataIn = new DataInputStream(in);
           SCFHeader header= headerCodec.decode(dataIn);
           SCFChromatogramBuilder chromoStruct = createSCFChromatogramStruct();
           SortedMap<Integer, Section> sectionsByOffset = createSectionsByOffsetMap(header);
           long currentOffset =HEADER_SIZE;
           for(Entry<Integer, Section> entry: sectionsByOffset.entrySet()){
              SectionDecoder sp=sectionCodecFactory.getSectionParserFor(entry.getValue(), header);
              currentOffset = sp.decode(dataIn, currentOffset, header, chromoStruct);
           }
           return chromoStruct.getChromatogram();

       
    }
    /**
     * Creates a new {@link SCFChromatogramBuilder} may be overridden
     * by subclasses to return a different implementation (or mock).
     * @return a {@link SCFChromatogramBuilder} (not null).
     */
    protected SCFChromatogramBuilder createSCFChromatogramStruct() {
        return new SCFChromatogramBuilder();
    }
    /**
     * Since each section does not depend on the other, parsing is made
     * easier if the sections are parsed in offset order.
     * This prevents the parser from having to go back into the stream
     * which may not be possible depending on the source.  This method
     * creates a Map of the sections which is ordered by the offset.
     * @param header the {@link SCFHeader} which contains the offsets
     * for each Section.
     * @return a {@link Map} of {@link Section}s ordered by offset.
     */
    private static SortedMap<Integer, Section> createSectionsByOffsetMap(SCFHeader header) {
        SortedMap<Integer, Section> sectionsByOffset = new TreeMap<Integer, Section>();
        sectionsByOffset.put(header.getBasesOffset(), Section.BASES);
        sectionsByOffset.put(header.getSampleOffset(), Section.SAMPLES);
        sectionsByOffset.put(header.getCommentOffset(), Section.COMMENTS);
        sectionsByOffset.put(header.getPrivateDataOffset(), Section.PRIVATE_DATA);
        return sectionsByOffset;
    }

    /**
     * Encodes the given {@link SCFChromatogram} into SCF version specific
     * format.
     * @param out the OutputStream to write the encoded {@link SCFChromatogram}.
     * @param c the {@link SCFChromatogram} to write.
     * @param version which SCF format version spec to encode.
     * @throws IOException f there are any problems encoding the chromatogram
     * or any problems writing to the {@link OutputStream}.
     */
    protected final void encode(OutputStream out, SCFChromatogram c, int version) throws IOException{
        SCFHeader header = new DefaultSCFHeader();
        header.setVersion(version);
        int currentOffset = HEADER_SIZE;
        Map<Section, EncodedSection> encodedSectionMap = new EnumMap<Section, EncodedSection>(Section.class);
        for(Section s : ORDER_OF_SECTIONS){
            setOffsetFor(s, currentOffset,header);
            SectionEncoder encoder =sectionCodecFactory.getSectionEncoderFor(s, version);
            final EncodedSection encodedSection = encoder.encode(c, header);
            encodedSectionMap.put(s, encodedSection);
            currentOffset+=encodedSection.getData().limit();
        }
        ByteBuffer result = ByteBuffer.allocate(currentOffset);
        result.put(headerCodec.encode(header));
        for(Section s : ORDER_OF_SECTIONS){
            result.put(encodedSectionMap.get(s).getData());
        }
        result.rewind();
        out.write(result.array());

    }

    private static void setOffsetFor(Section s, int currentOffset,SCFHeader header) {
        if(s==Section.SAMPLES){
            header.setSampleOffset(currentOffset);
        }
        else if(s == Section.BASES){
            header.setBasesOffset(currentOffset);
        }
        else if(s== Section.COMMENTS){
            header.setCommentOffset(currentOffset);
        }
        else if(s== Section.PRIVATE_DATA){
            header.setPrivateDataOffset(currentOffset);
        }

    }
}
