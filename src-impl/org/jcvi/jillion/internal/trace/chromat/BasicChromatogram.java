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
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat;


import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.Chromatogram;



/**
 * <code>BasicChromatogram</code> is an implementation
 * of {@link Chromatogram} that contains all the common base attributes
 * of a Chroamtogram object.  Subclasses may extend this class to add additional
 * format specific methods.
 * @author dkatzel
 *
 *
 */
public class BasicChromatogram implements Chromatogram {
   
    private final ChannelGroup channelGroup;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    private final PositionSequence positions;
    
    private final String id;
    
    /**
     * Used to store the TEXT properties of a ZTR file.
     */
    private Map<String,String> properties;


    public BasicChromatogram(Chromatogram c){
        this(c.getId(),
        		c.getNucleotideSequence(),
                c.getQualitySequence(),
                c.getPositionSequence(),
               c.getChannelGroup(),
                c.getComments());
    }
    public BasicChromatogram(String id, NucleotideSequence basecalls,QualitySequence qualities, PositionSequence peaks,
            ChannelGroup channelGroup){
        this(id,basecalls, qualities, peaks, channelGroup, new HashMap<String,String>());
    }
    public BasicChromatogram(String id, String basecalls, byte[] qualities,PositionSequence peaks,
            ChannelGroup channelGroup,
            Map<String,String> comments){
        this(id,new NucleotideSequenceBuilder(basecalls).build(),
        		 new QualitySequenceBuilder(qualities).build(),
                peaks,
                     channelGroup, comments);
    }
    public BasicChromatogram(String id, NucleotideSequence basecalls, 
    		QualitySequence qualities,PositionSequence peaks,
           ChannelGroup channelGroup,
           Map<String,String> comments){
        canNotBeNull(id,basecalls, peaks, channelGroup, comments);
        this.id=id;
        this.positions = peaks;        
        this.properties = comments;
        this.channelGroup =channelGroup;
        this.basecalls = basecalls;
        this.qualities = qualities;
    }
   
    private void canNotBeNull(Object...objects ) {
        for(Object obj : objects){
            if(obj == null){
                throw new IllegalArgumentException("null parameter");
            }
        }
        
    }

    @Override
	public String getId() {
		return id;
	}
	public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
	public PositionSequence getPositionSequence() {
		return positions;
	}
	public Map<String,String> getComments() {
        return properties;
    }

    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }





    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getChannelGroup().hashCode();
        result = prime * result +  basecalls.hashCode();
        result = prime * result +  positions.hashCode();
        result = prime * result +  properties.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Chromatogram)){
            return false;
        }
        final Chromatogram other = (Chromatogram) obj;

        return ObjectsUtil.nullSafeEquals(getNucleotideSequence(), other.getNucleotideSequence())
        && ObjectsUtil.nullSafeEquals(getPositionSequence(), other.getPositionSequence())
        && ObjectsUtil.nullSafeEquals(getChannelGroup(), other.getChannelGroup())
        && ObjectsUtil.nullSafeEquals(getComments(), other.getComments());
    }


    /**
     * @return the channelGroup
     */
    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }
    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }
    @Override
    public int getNumberOfTracePositions() {
		return (int)getChannelGroup().getAChannel().getPositions().getLength();
    }



}
