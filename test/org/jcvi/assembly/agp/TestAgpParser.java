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
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.agp;

import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.datastore.DefaultAgpScaffoldDataStore;
import org.jcvi.assembly.DefaultScaffold;
import org.jcvi.assembly.Scaffold;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAgpParser {

    ResourceFileServer resourceFS = new ResourceFileServer(TestAgpParser.class);
    @Test
    public void parseScaffold() throws IOException{
        DefaultAgpScaffoldDataStore scaffolds = new DefaultAgpScaffoldDataStore();
        AgpParser.parseAgpFile(resourceFS.getFileAsStream("files/example.agp"),scaffolds);
        Scaffold actualScaffold = scaffolds.getScaffold("chrY");
        Scaffold expectedScaffold = new DefaultScaffold.Builder("chrY")
        .add("AADB02037640.1",Range.buildRange(10503427,10507045),SequenceDirection.REVERSE)
                         .add("AADB02037624.1",Range.buildRange(9332432,9358222),SequenceDirection.FORWARD)
                         .add("AADB02037629.1",Range.buildRange(9424834,9431854),SequenceDirection.FORWARD)
                         .add("AADB02037618.1",Range.buildRange(8614679,8619858),SequenceDirection.FORWARD)
                         .add("AADB02037563.1",Range.buildRange(285516,286717),SequenceDirection.FORWARD)
                         .add("AADB02037617.1",Range.buildRange(8551789,8608338),SequenceDirection.REVERSE)
                         .add("AADB02037559.1",Range.buildRange(225619,266271),SequenceDirection.FORWARD)
                         .add("AADB02037600.1",Range.buildRange(6984572,6989696),SequenceDirection.FORWARD)
                         .add("AADB02037564.1",Range.buildRange(286757,287967),SequenceDirection.FORWARD)
                         .add("AADB02037639.1",Range.buildRange(10386243,10487039),SequenceDirection.FORWARD)
                         .add("AADB02037636.1",Range.buildRange(10178960,10194982),SequenceDirection.FORWARD)
                         .add("AADB02037562.1",Range.buildRange(283615,284790),SequenceDirection.REVERSE)
                         .add("AADB02037645.1",Range.buildRange(11005351,11009780),SequenceDirection.FORWARD)
                         .add("AADB02037571.1",Range.buildRange(2386281,2590019),SequenceDirection.FORWARD)
                         .add("AADB02037551.1",Range.buildRange(0,3042),SequenceDirection.FORWARD)
                         .add("AADB02037616.1",Range.buildRange(8537565,8541319),SequenceDirection.REVERSE)
                         .add("AADB02037584.1",Range.buildRange(3728682,3733271),SequenceDirection.FORWARD)
                         .add("AADB02037578.1",Range.buildRange(3302894,3345190),SequenceDirection.FORWARD)
                         .add("AADB02037642.1",Range.buildRange(10517020,10587380),SequenceDirection.FORWARD)
                         .add("AADB02037608.1",Range.buildRange(8336153,8409081),SequenceDirection.FORWARD)
                         .add("AADB02037560.1",Range.buildRange(266372,279508),SequenceDirection.FORWARD)
                         .add("AADB02037609.1",Range.buildRange(8451472,8469373),SequenceDirection.FORWARD)
                         .add("AADB02037603.1",Range.buildRange(7103381,7107649),SequenceDirection.FORWARD)
                         .add("AADB02037611.1",Range.buildRange(8520990,8527211),SequenceDirection.REVERSE)
                         .add("AADB02037634.1",Range.buildRange(9766128,9771184),SequenceDirection.FORWARD)
                         .add("AADB02037619.1",Range.buildRange(8669859,9049333),SequenceDirection.FORWARD)
                         .add("AADB02037555.1",Range.buildRange(198441,201392),SequenceDirection.FORWARD)
                         .add("AADB02037574.1",Range.buildRange(2797354,2816907),SequenceDirection.FORWARD)
                         .add("AADB02037632.1",Range.buildRange(9528723,9636534),SequenceDirection.FORWARD)
                         .add("AADB02037623.1",Range.buildRange(9325178,9328202),SequenceDirection.FORWARD)
                         .add("AADB02037568.1",Range.buildRange(1743966,1763615),SequenceDirection.FORWARD)
                         .add("AADB02037589.1",Range.buildRange(5330109,5335415),SequenceDirection.FORWARD)
                         .add("AADB02037569.1",Range.buildRange(1772611,1852121),SequenceDirection.FORWARD)
                         .add("AADB02037601.1",Range.buildRange(6997597,7000630),SequenceDirection.FORWARD)
                         .add("AADB02037572.1",Range.buildRange(2614058,2793941),SequenceDirection.FORWARD)
                         .add("AADB02037622.1",Range.buildRange(9322053,9325077),SequenceDirection.FORWARD)
                         .add("AADB02037599.1",Range.buildRange(6579454,6934571),SequenceDirection.FORWARD)
                         .add("AADB02037643.1",Range.buildRange(10588677,10901299),SequenceDirection.FORWARD)
                         .add("AADB02037596.1",Range.buildRange(5506404,5508328),SequenceDirection.REVERSE)
                         .add("AADB02037556.1",Range.buildRange(201493,205641),SequenceDirection.REVERSE)
                         .add("AADB02037627.1",Range.buildRange(9385297,9389146),SequenceDirection.FORWARD)
                         .add("AADB02037597.1",Range.buildRange(5515916,5791800),SequenceDirection.FORWARD)
                         .add("AADB02037581.1",Range.buildRange(3473627,3565953),SequenceDirection.FORWARD)
                         .add("AADB02037582.1",Range.buildRange(3615954,3623103),SequenceDirection.FORWARD)
                         .add("AADB02037579.1",Range.buildRange(3345359,3351683),SequenceDirection.FORWARD)
                         .add("AADB02037595.1",Range.buildRange(5453312,5456403),SequenceDirection.FORWARD)
                         .add("AADB02037606.1",Range.buildRange(7831715,7862604),SequenceDirection.REVERSE)
                         .add("AADB02037554.1",Range.buildRange(170672,198340),SequenceDirection.FORWARD)
                         .add("AADB02037598.1",Range.buildRange(5791967,6579353),SequenceDirection.FORWARD)
                         .add("AADB02037615.1",Range.buildRange(8535938,8537257),SequenceDirection.REVERSE)
                         .add("AADB02037613.1",Range.buildRange(8532429,8534241),SequenceDirection.FORWARD)
                         .add("AADB02037583.1",Range.buildRange(3673104,3678681),SequenceDirection.FORWARD)
                         .add("AADB02037604.1",Range.buildRange(7157650,7281419),SequenceDirection.FORWARD)
                         .add("AADB02037586.1",Range.buildRange(3856628,4878164),SequenceDirection.FORWARD)
                         .add("AADB02037620.1",Range.buildRange(9099334,9302371),SequenceDirection.FORWARD)
                         .add("AADB02037588.1",Range.buildRange(5284455,5300533),SequenceDirection.FORWARD)
                         .add("AADB02037591.1",Range.buildRange(5375839,5379688),SequenceDirection.REVERSE)
                         .add("AADB02037565.1",Range.buildRange(301116,1712589),SequenceDirection.FORWARD)
                         .add("AADB02037575.1",Range.buildRange(2817008,2823925),SequenceDirection.FORWARD)
                         .add("AADB02037553.1",Range.buildRange(93591,170571),SequenceDirection.FORWARD)
                         .add("AADB02037633.1",Range.buildRange(9686535,9716127),SequenceDirection.FORWARD)
                         .add("AADB02037593.1",Range.buildRange(5399398,5405245),SequenceDirection.FORWARD)
                         .add("AADB02037602.1",Range.buildRange(7050631,7053380),SequenceDirection.FORWARD)
                         .add("AADB02037570.1",Range.buildRange(1852421,2386126),SequenceDirection.FORWARD)
                         .add("AADB02037610.1",Range.buildRange(8519374,8520889),SequenceDirection.FORWARD)
                         .add("AADB02037552.1",Range.buildRange(53043,93490),SequenceDirection.FORWARD)
                         .add("AADB02037612.1",Range.buildRange(8529765,8532292),SequenceDirection.REVERSE)
                         .add("AADB02037567.1",Range.buildRange(1736309,1741392),SequenceDirection.FORWARD)
                         .add("AADB02037638.1",Range.buildRange(10350772,10384883),SequenceDirection.FORWARD)
                         .add("AADB02037635.1",Range.buildRange(9821185,10128959),SequenceDirection.FORWARD)
                         .add("AADB02037566.1",Range.buildRange(1732109,1736208),SequenceDirection.FORWARD)
                         .add("AADB02037585.1",Range.buildRange(3733463,3806627),SequenceDirection.FORWARD)
                         .add("AADB02037594.1",Range.buildRange(5431630,5453002),SequenceDirection.FORWARD)
                         .add("AADB02037637.1",Range.buildRange(10244983,10339652),SequenceDirection.FORWARD)
                         .add("AADB02037641.1",Range.buildRange(10507146,10509938),SequenceDirection.REVERSE)
                         .add("AADB02037561.1",Range.buildRange(279908,282909),SequenceDirection.FORWARD)
                         .add("AADB02037576.1",Range.buildRange(2826770,2973278),SequenceDirection.FORWARD)
                         .add("AADB02037580.1",Range.buildRange(3352953,3423626),SequenceDirection.FORWARD)
                         .add("AADB02037626.1",Range.buildRange(9364586,9383557),SequenceDirection.FORWARD)
                         .add("AADB02037621.1",Range.buildRange(9307070,9315044),SequenceDirection.REVERSE)
                         .add("AADB02037631.1",Range.buildRange(9466902,9528369),SequenceDirection.FORWARD)
                         .add("AADB02037592.1",Range.buildRange(5379789,5398722),SequenceDirection.FORWARD)
                         .add("AADB02037625.1",Range.buildRange(9358323,9364422),SequenceDirection.FORWARD)
                         .add("AADB02037630.1",Range.buildRange(9434769,9442743),SequenceDirection.FORWARD)
                         .add("AADB02037557.1",Range.buildRange(205742,211649),SequenceDirection.FORWARD)
                         .add("AADB02037577.1",Range.buildRange(2973379,3302640),SequenceDirection.FORWARD)
                         .add("AADB02037628.1",Range.buildRange(9389888,9422612),SequenceDirection.FORWARD)
                         .add("AADB02037644.1",Range.buildRange(10951300,10955350),SequenceDirection.FORWARD)
                         .add("AADB02037573.1",Range.buildRange(2794042,2797033),SequenceDirection.FORWARD)
                         .add("AADB02037614.1",Range.buildRange(8534387,8535697),SequenceDirection.FORWARD)
                         .add("AADB02037587.1",Range.buildRange(4884628,5284354),SequenceDirection.FORWARD)
                         .add("AADB02037590.1",Range.buildRange(5342520,5346938),SequenceDirection.FORWARD)
                         .add("AADB02037607.1",Range.buildRange(7862705,8286152),SequenceDirection.REVERSE)
                         .add("AADB02037558.1",Range.buildRange(216001,225518),SequenceDirection.FORWARD)
                         .add("AADB02037605.1",Range.buildRange(7283563,7831614),SequenceDirection.FORWARD)       
        
        .build();
        
        assertEquals(expectedScaffold, actualScaffold);
        
    }
}
