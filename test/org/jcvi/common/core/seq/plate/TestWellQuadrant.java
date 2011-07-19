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

package org.jcvi.common.core.seq.plate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.jcvi.common.core.seq.plate.PlateFormat;
import org.jcvi.common.core.seq.plate.Well;
import org.jcvi.common.core.seq.plate.Well.IndexOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestWellQuadrant {

    private Well actual;
    private int expectedQuadIndex;
    private int actualQualIndex;
    private PlateFormat type;
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.addAll(add96WellQuadrantData());
        data.addAll(add384WellQuadrantData());
        return data;
    }

    private static List<Object[]> add96WellQuadrantData() {
        List<Object[]> data = new ArrayList<Object[]>();
        int wellIndex=0;
        for(int quadrant=0; quadrant<4; quadrant++){
            for(int i=0; i<24; i++){
                
                Well well = Well.compute96Well(wellIndex, IndexOrder.ROW_MAJOR);
                data.add(new Object[]{well,quadrant,well.get96WellQuadrantIndex(IndexOrder.ROW_MAJOR), PlateFormat._96});
                wellIndex++;
            }
        }
        return data;
    }
    private static List<Object[]> add384WellQuadrantData() {
        List<Object[]> data = new ArrayList<Object[]>();
        
        EnumSet<IndexOrder> all384IndexOrders = EnumSet.complementOf(EnumSet.of(IndexOrder.ABI_3130_16_CAPILLARIES));
        for(IndexOrder order : all384IndexOrders){
            int wellIndex=0;
            for(int quadrant=0; quadrant<4; quadrant++){
                for(int i=0; i<96; i++){
                    
                    Well well = Well.compute384Well(wellIndex, order);
                    data.add(new Object[]{well,quadrant,well.get384WellQuadrantIndex(order), PlateFormat._384});
                    wellIndex++;
                }
            }
        }
        return data;
    }

    public TestWellQuadrant(Well actual, int expectedQuadIndex,
            int actualQualIndex,PlateFormat type) {
        this.actual = actual;
        this.expectedQuadIndex = expectedQuadIndex;
        this.actualQualIndex = actualQualIndex;
        this.type=type;
    }
    
    @Test
    public void quadrantComputedCorrectly(){
        assertEquals(actual + "  " + type,expectedQuadIndex, actualQualIndex);
    }
    
}
