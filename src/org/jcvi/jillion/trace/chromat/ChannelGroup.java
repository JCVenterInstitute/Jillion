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
/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code ChannelGroup} is a composite of all 4 {@link Channel}s
 * of a Sanger sequenced trace.
 * @author dkatzel
 *
 *
 */
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
     * Get the {@link Channel} for a particular {@link Nucleotide}.
     * For example {@link #getChannel(Nucleotide) getChannel(NucleotideGlyph.Adenine)}
     * is the same as {@link #getAChannel()}.
     * @param channelToGet the nucleotide channel to get; can not be null.
     * To follow some Chromatogram format requirements, 
     * passing in an ambiguous nucleotide such as an N 
     * will return the T channel since some formats
     * put non A,C,G data in the T channel.
     * @return the {@link DefaultChannel} for that {@link Nucleotide}.
     * @throws NullPointerException if channelToGet is null.
     */
    Channel getChannel(Nucleotide channelToGet);

}
