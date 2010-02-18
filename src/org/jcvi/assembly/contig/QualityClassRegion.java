/*
 * Created on Feb 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.Range;
import org.jcvi.assembly.Placed;
import org.jcvi.glyph.qualClass.QualityClass;

public class QualityClassRegion implements Placed{

    private QualityClass qualityClass;
    private Placed placed;
    
    QualityClassRegion(QualityClass qualityClass, Range range){
        if(qualityClass ==null){
            throw new IllegalArgumentException("qualityClass can not be null");
        }
        if(range ==null){
            throw new IllegalArgumentException("range can not be null");
        }
        this.qualityClass = qualityClass;
        placed = range;
    }
    @Override
    public long getEnd() {
        return placed.getEnd();
    }

    @Override
    public long getLength() {
        return placed.getLength();
    }

    @Override
    public long getStart() {
        return placed.getStart();
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(placed)
            .append(" = quality class value ")
            .append(qualityClass.getValue());
        return builder.toString();
    }
    public QualityClass getQualityClass() {
        return qualityClass;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + placed.hashCode();
        result = prime * result + qualityClass.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof QualityClassRegion))
            return false;
        QualityClassRegion other = (QualityClassRegion) obj;
        return placed.equals(other.placed) && qualityClass.equals(other.qualityClass);
    }
    
    
}
