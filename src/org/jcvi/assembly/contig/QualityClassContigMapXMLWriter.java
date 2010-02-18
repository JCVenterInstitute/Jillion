/*
 * Created on May 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassContigMapXMLWriter {

    private static final String QUALITY_CLASS_REGION_FORMAT = 
        "<coverageregion start =\"%d\" end=\"%d\" value= \"%d\"/>\n";

    public void write(OutputStream out, List<QualityClassRegion> qualityClassRegions) throws IOException{
        out.write("<qualityclassmap>\n".getBytes());
        for(QualityClassRegion region : qualityClassRegions){
            final QualityClass qualityClass = region.getQualityClass();
            out.write(String.format(QUALITY_CLASS_REGION_FORMAT, region.getStart(), region.getEnd(), 
                    qualityClass.getValue()).getBytes());
        }
        out.write("</qualityclassmap>\n".getBytes());
        out.flush();
    }
}
