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
 * Created on May 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.assembly.Placed;

public class XMLCoverageWriter<T extends Placed>{

    private static final String BEGIN_COVERAGE_REGION_FORMAT = 
        "<coverageregion %s/>\n";

    private static final String END_COVERAGE_MAP_FORMAT = 
        "</%s>\n";
    private static final String BEGIN_COVERAGE_MAP_FORMAT = 
        "<%s>\n";
    

    public void write(OutputStream out, CoverageMap<CoverageRegion<T>> coverageMap) throws IOException {
        out.write(String.format(BEGIN_COVERAGE_MAP_FORMAT, getCoverageMapTagName()).getBytes());
        for(CoverageRegion<T> region : coverageMap){
            writeCoverageRegion(out,region);
        }
        out.write(String.format(END_COVERAGE_MAP_FORMAT, getCoverageMapTagName()).getBytes());
        out.flush();
    }
    protected String getCoverageMapTagName(){
        return "coveragemap";
    }
    protected void writeCoverageRegion(OutputStream out,CoverageRegion<T> region) throws IOException{
        Properties attriubtes = createBasicAttributes(region);
        addAdditionalAttributes(region, attriubtes);
        StringBuilder attributeBuilder = new StringBuilder();
        for(Entry<Object, Object> entry : attriubtes.entrySet()){
            attributeBuilder.append(String.format("%s = \"%s\" ", entry.getKey(), entry.getValue()));
        }
        out.write(String.format(BEGIN_COVERAGE_REGION_FORMAT, attributeBuilder.toString()).getBytes());
    }
    @SuppressWarnings("unused")
    protected void addAdditionalAttributes( CoverageRegion<T> region,
            Properties attriubtes) {
        // no-op
        
    }
    private Properties createBasicAttributes(CoverageRegion<T> region){
        Properties properties = new Properties();
        properties.put("start", region.getStart());
        properties.put("end", region.getEnd());
        properties.put("depth", region.getCoverage());
        return properties;
    }
    
}
