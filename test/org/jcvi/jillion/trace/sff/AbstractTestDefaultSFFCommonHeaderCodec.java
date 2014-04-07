/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 13, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sff.DefaultSFFCommonHeaderDecoder;
import org.jcvi.jillion.trace.sff.DefaultSffCommonHeader;
import org.jcvi.jillion.trace.sff.SffUtil;

public class AbstractTestDefaultSFFCommonHeaderCodec {

    protected BigInteger indexOffset=BigInteger.valueOf(100000L);
    protected int indexLength = 2000;
    protected int numberOfReads = 5;
    protected short numberOfFlowsPerRead = 12;
    protected NucleotideSequence flow = new NucleotideSequenceBuilder("TCAGTCAGTCAG").build();
    protected NucleotideSequence keySequence = new NucleotideSequenceBuilder("TCAG").build();
    protected short headerLength = (short)(31+numberOfFlowsPerRead+SffUtil.caclulatePaddedBytes(31+numberOfFlowsPerRead));

    protected DefaultSffCommonHeader expectedHeader = new DefaultSffCommonHeader(indexOffset,  indexLength,
             numberOfReads,  numberOfFlowsPerRead,  flow,
             keySequence);

    protected DefaultSFFCommonHeaderDecoder sut = DefaultSFFCommonHeaderDecoder.INSTANCE;
}
