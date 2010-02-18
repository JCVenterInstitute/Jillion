/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import org.jcvi.glyph.nuc.NucleotideGlyph;

public interface ChannelGroup {

    /**
     * @return the aChannel
     */
    Channel getAChannel();

    /**
     * @return the cChannel
     */
    Channel getCChannel();

    /**
     * @return the gChannel
     */
    Channel getGChannel();

    /**
     * @return the tChannel
     */
    Channel getTChannel();
    /**
     * 
     * @param nucleotide the nucleotide channel to get.
     * @return the {@link Channel} for that {@link NucleotideGlyph}.
     */
    Channel getChannel(NucleotideGlyph nucleotide);

}