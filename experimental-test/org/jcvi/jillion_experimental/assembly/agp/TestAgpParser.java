/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.assembly.agp;

import java.io.IOException;

import org.jcvi.jillion.assembly.DefaultScaffold;
import org.jcvi.jillion.assembly.Scaffold;
import org.jcvi.jillion.assembly.ScaffoldDataStore;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion_experimental.assembly.agp.AgpParser;
import org.jcvi.jillion_experimental.assembly.agp.DefaultAgpScaffoldDataStore;
import org.jcvi.jillion_experimental.assembly.agp.ScaffoldDataStoreBuilderAgpVisitor;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAgpParser {

    ResourceHelper resourceFS = new ResourceHelper(TestAgpParser.class);
    @Test
    public void parseScaffold() throws IOException, DataStoreException{
        ScaffoldDataStoreBuilderAgpVisitor builderVisitor = DefaultAgpScaffoldDataStore.createBuilder();
        AgpParser.parseAgpFile(resourceFS.getFileAsStream("files/example.agp"),builderVisitor);
        ScaffoldDataStore datastore = builderVisitor.build();
        Scaffold actualScaffold = datastore.get("chrY");
        Scaffold expectedScaffold = DefaultScaffold.createBuilder("chrY")
        .add("AADB02037640.1",Range.of(10503427,10507045),Direction.REVERSE)
                         .add("AADB02037624.1",Range.of(9332432,9358222),Direction.FORWARD)
                         .add("AADB02037629.1",Range.of(9424834,9431854),Direction.FORWARD)
                         .add("AADB02037618.1",Range.of(8614679,8619858),Direction.FORWARD)
                         .add("AADB02037563.1",Range.of(285516,286717),Direction.FORWARD)
                         .add("AADB02037617.1",Range.of(8551789,8608338),Direction.REVERSE)
                         .add("AADB02037559.1",Range.of(225619,266271),Direction.FORWARD)
                         .add("AADB02037600.1",Range.of(6984572,6989696),Direction.FORWARD)
                         .add("AADB02037564.1",Range.of(286757,287967),Direction.FORWARD)
                         .add("AADB02037639.1",Range.of(10386243,10487039),Direction.FORWARD)
                         .add("AADB02037636.1",Range.of(10178960,10194982),Direction.FORWARD)
                         .add("AADB02037562.1",Range.of(283615,284790),Direction.REVERSE)
                         .add("AADB02037645.1",Range.of(11005351,11009780),Direction.FORWARD)
                         .add("AADB02037571.1",Range.of(2386281,2590019),Direction.FORWARD)
                         .add("AADB02037551.1",Range.of(0,3042),Direction.FORWARD)
                         .add("AADB02037616.1",Range.of(8537565,8541319),Direction.REVERSE)
                         .add("AADB02037584.1",Range.of(3728682,3733271),Direction.FORWARD)
                         .add("AADB02037578.1",Range.of(3302894,3345190),Direction.FORWARD)
                         .add("AADB02037642.1",Range.of(10517020,10587380),Direction.FORWARD)
                         .add("AADB02037608.1",Range.of(8336153,8409081),Direction.FORWARD)
                         .add("AADB02037560.1",Range.of(266372,279508),Direction.FORWARD)
                         .add("AADB02037609.1",Range.of(8451472,8469373),Direction.FORWARD)
                         .add("AADB02037603.1",Range.of(7103381,7107649),Direction.FORWARD)
                         .add("AADB02037611.1",Range.of(8520990,8527211),Direction.REVERSE)
                         .add("AADB02037634.1",Range.of(9766128,9771184),Direction.FORWARD)
                         .add("AADB02037619.1",Range.of(8669859,9049333),Direction.FORWARD)
                         .add("AADB02037555.1",Range.of(198441,201392),Direction.FORWARD)
                         .add("AADB02037574.1",Range.of(2797354,2816907),Direction.FORWARD)
                         .add("AADB02037632.1",Range.of(9528723,9636534),Direction.FORWARD)
                         .add("AADB02037623.1",Range.of(9325178,9328202),Direction.FORWARD)
                         .add("AADB02037568.1",Range.of(1743966,1763615),Direction.FORWARD)
                         .add("AADB02037589.1",Range.of(5330109,5335415),Direction.FORWARD)
                         .add("AADB02037569.1",Range.of(1772611,1852121),Direction.FORWARD)
                         .add("AADB02037601.1",Range.of(6997597,7000630),Direction.FORWARD)
                         .add("AADB02037572.1",Range.of(2614058,2793941),Direction.FORWARD)
                         .add("AADB02037622.1",Range.of(9322053,9325077),Direction.FORWARD)
                         .add("AADB02037599.1",Range.of(6579454,6934571),Direction.FORWARD)
                         .add("AADB02037643.1",Range.of(10588677,10901299),Direction.FORWARD)
                         .add("AADB02037596.1",Range.of(5506404,5508328),Direction.REVERSE)
                         .add("AADB02037556.1",Range.of(201493,205641),Direction.REVERSE)
                         .add("AADB02037627.1",Range.of(9385297,9389146),Direction.FORWARD)
                         .add("AADB02037597.1",Range.of(5515916,5791800),Direction.FORWARD)
                         .add("AADB02037581.1",Range.of(3473627,3565953),Direction.FORWARD)
                         .add("AADB02037582.1",Range.of(3615954,3623103),Direction.FORWARD)
                         .add("AADB02037579.1",Range.of(3345359,3351683),Direction.FORWARD)
                         .add("AADB02037595.1",Range.of(5453312,5456403),Direction.FORWARD)
                         .add("AADB02037606.1",Range.of(7831715,7862604),Direction.REVERSE)
                         .add("AADB02037554.1",Range.of(170672,198340),Direction.FORWARD)
                         .add("AADB02037598.1",Range.of(5791967,6579353),Direction.FORWARD)
                         .add("AADB02037615.1",Range.of(8535938,8537257),Direction.REVERSE)
                         .add("AADB02037613.1",Range.of(8532429,8534241),Direction.FORWARD)
                         .add("AADB02037583.1",Range.of(3673104,3678681),Direction.FORWARD)
                         .add("AADB02037604.1",Range.of(7157650,7281419),Direction.FORWARD)
                         .add("AADB02037586.1",Range.of(3856628,4878164),Direction.FORWARD)
                         .add("AADB02037620.1",Range.of(9099334,9302371),Direction.FORWARD)
                         .add("AADB02037588.1",Range.of(5284455,5300533),Direction.FORWARD)
                         .add("AADB02037591.1",Range.of(5375839,5379688),Direction.REVERSE)
                         .add("AADB02037565.1",Range.of(301116,1712589),Direction.FORWARD)
                         .add("AADB02037575.1",Range.of(2817008,2823925),Direction.FORWARD)
                         .add("AADB02037553.1",Range.of(93591,170571),Direction.FORWARD)
                         .add("AADB02037633.1",Range.of(9686535,9716127),Direction.FORWARD)
                         .add("AADB02037593.1",Range.of(5399398,5405245),Direction.FORWARD)
                         .add("AADB02037602.1",Range.of(7050631,7053380),Direction.FORWARD)
                         .add("AADB02037570.1",Range.of(1852421,2386126),Direction.FORWARD)
                         .add("AADB02037610.1",Range.of(8519374,8520889),Direction.FORWARD)
                         .add("AADB02037552.1",Range.of(53043,93490),Direction.FORWARD)
                         .add("AADB02037612.1",Range.of(8529765,8532292),Direction.REVERSE)
                         .add("AADB02037567.1",Range.of(1736309,1741392),Direction.FORWARD)
                         .add("AADB02037638.1",Range.of(10350772,10384883),Direction.FORWARD)
                         .add("AADB02037635.1",Range.of(9821185,10128959),Direction.FORWARD)
                         .add("AADB02037566.1",Range.of(1732109,1736208),Direction.FORWARD)
                         .add("AADB02037585.1",Range.of(3733463,3806627),Direction.FORWARD)
                         .add("AADB02037594.1",Range.of(5431630,5453002),Direction.FORWARD)
                         .add("AADB02037637.1",Range.of(10244983,10339652),Direction.FORWARD)
                         .add("AADB02037641.1",Range.of(10507146,10509938),Direction.REVERSE)
                         .add("AADB02037561.1",Range.of(279908,282909),Direction.FORWARD)
                         .add("AADB02037576.1",Range.of(2826770,2973278),Direction.FORWARD)
                         .add("AADB02037580.1",Range.of(3352953,3423626),Direction.FORWARD)
                         .add("AADB02037626.1",Range.of(9364586,9383557),Direction.FORWARD)
                         .add("AADB02037621.1",Range.of(9307070,9315044),Direction.REVERSE)
                         .add("AADB02037631.1",Range.of(9466902,9528369),Direction.FORWARD)
                         .add("AADB02037592.1",Range.of(5379789,5398722),Direction.FORWARD)
                         .add("AADB02037625.1",Range.of(9358323,9364422),Direction.FORWARD)
                         .add("AADB02037630.1",Range.of(9434769,9442743),Direction.FORWARD)
                         .add("AADB02037557.1",Range.of(205742,211649),Direction.FORWARD)
                         .add("AADB02037577.1",Range.of(2973379,3302640),Direction.FORWARD)
                         .add("AADB02037628.1",Range.of(9389888,9422612),Direction.FORWARD)
                         .add("AADB02037644.1",Range.of(10951300,10955350),Direction.FORWARD)
                         .add("AADB02037573.1",Range.of(2794042,2797033),Direction.FORWARD)
                         .add("AADB02037614.1",Range.of(8534387,8535697),Direction.FORWARD)
                         .add("AADB02037587.1",Range.of(4884628,5284354),Direction.FORWARD)
                         .add("AADB02037590.1",Range.of(5342520,5346938),Direction.FORWARD)
                         .add("AADB02037607.1",Range.of(7862705,8286152),Direction.REVERSE)
                         .add("AADB02037558.1",Range.of(216001,225518),Direction.FORWARD)
                         .add("AADB02037605.1",Range.of(7283563,7831614),Direction.FORWARD)       
        
        .build();
        
        assertEquals(expectedScaffold, actualScaffold);
        
    }
}
