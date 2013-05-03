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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
