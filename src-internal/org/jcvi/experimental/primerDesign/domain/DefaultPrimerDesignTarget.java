package org.jcvi.experimental.primerDesign.domain;

import org.jcvi.common.core.Range;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Sep 1, 2010
 * Time: 2:27:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPrimerDesignTarget implements PrimerDesignTarget {

    private String ID;
    private Range range;

    public DefaultPrimerDesignTarget(String ID, Range range) {
        this.ID = ID;
        this.range = range;

        if ( this.ID == null ) {
            throw new IllegalStateException("DefaultPrimerDesignTarget id cannot be null");
        }

        if ( this.range == null ) {
            throw new IllegalStateException("DefaultPrimerDesignTarget range cannot be null");
        }
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "DefaultPrimerDesignTarget{" +
                "ID='" + ID + '\'' +
                ", range=" + range +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultPrimerDesignTarget that = (DefaultPrimerDesignTarget) o;

        if ( !ID.equals(that.ID) ) return false;
        return range.equals(that.range);
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }
}
