package org.jcvi.primerDesign.results;

import org.jcvi.sequence.SequenceDirection;
import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

import java.io.File;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 11:30:12 AM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignResult {

    private DesignGroupKey designGroupKey;

    // id of the parent sequence target for which this primer was designed
    private String parentID;

    // primer location on parent sequence
    private Range range;

    // primer sequencing direction relative to its parent sequence target
    private SequenceDirection orientation;

    // primer sequence
    private NucleotideEncodedGlyphs primerSequence;

    private PrimerDesignResult(DesignGroupKey designGroupKey,
                               String parentID,
                               Range range,
                               SequenceDirection orientation,
                               NucleotideEncodedGlyphs primerSequence) {
        this.designGroupKey = designGroupKey;
        this.parentID = parentID;
        this.range = range;
        this.orientation = orientation;
        this.primerSequence = primerSequence;
    }

    // todo: make sure this is legit
    public String getUniqueIdentifier() {
        StringBuilder builder = new StringBuilder();
        builder.append(parentID);
        builder.append(".");
        builder.append(designGroupKey.getDesignGroupID());
        builder.append(".");
        builder.append(orientation);
        return builder.toString();
    }

    public DesignGroupKey getDesignGroupKey() {
        return designGroupKey;
    }

    public String getDesignGroupID() {
        return designGroupKey.getDesignGroupID();
    }

    public String getParentID() {
        return parentID;
    }

    public Range getRange() {
        return range;
    }

    public SequenceDirection getOrientation() {
        return orientation;
    }

    public NucleotideEncodedGlyphs getPrimerSequence() {
        return primerSequence;
    }

    /**
     * equals GUARANTEED to work when two PrimerDesignResult objects
     * have the same Design Group Key; equals will probably work on
     * two PrimerDesignResult objects with different Design Group Keys
     * but this may not occur if the objects are from successive primer design runs
     * for the same parent sequence and same target sequence range
     * @param o PrimerDesignResult object to compare
     * @return return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimerDesignResult that = (PrimerDesignResult) o;

        if (!designGroupKey.getDesignGroupID().equals(that.designGroupKey.getDesignGroupID())) return false;
        if (orientation != that.orientation) return false;
        if (!parentID.equals(that.parentID)) return false;
        if (!primerSequence.equals(that.primerSequence)) return false;
        if (!range.equals(that.range)) return false;

        return true;
    }

    /**
     * hashCode GUARANTEED to return unique value when PrimerDesignResult objects
     * have the same Design Group Key; hashCode will probably return a unique value
     * when PrimerDesignResult objects have different Design Group Keys
     * but this may not occur if the objects are from successive primer design runs
     * for the same parent sequence and same target sequence range
     * @return return true if objects are equal, false otherwise
     */
    @Override
    public int hashCode() {
        int result = designGroupKey.getDesignGroupID().hashCode();
        result = 31 * result + parentID.hashCode();
        result = 31 * result + range.hashCode();
        result = 31 * result + orientation.hashCode();
        result = 31 * result + primerSequence.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PrimerDesignerResult");
        sb.append("{designGroupID='").append(designGroupKey.getDesignGroupID()).append('\'');
        sb.append(", parentID='").append(parentID).append('\'');
        sb.append(", range=").append(range);
        sb.append(", orientation=").append(orientation);
        sb.append(", primerSequence=").append(primerSequence.toString().replaceAll(",\\s*",""));
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private DesignGroupKey designGroupKey;
        private Integer designGroupLocationHash;
        private String designGroupID;

        private String parentID;
        private Range range;
        private SequenceDirection orientation;
        private NucleotideEncodedGlyphs primerSequence;

        public Builder() {
        }

        public Builder(File primerDesignFile) {
            this.designGroupLocationHash = primerDesignFile.hashCode();
        }

        public Builder(DesignGroupKey designGroupKey) {
            this.designGroupKey = designGroupKey;
        }

        public Builder setDesignGroupLocationHash(Integer designGroupLocationHash) {
            this.designGroupLocationHash = designGroupLocationHash;
            return this;
        }

        public Builder setDesignGroupID(String designGroupID) {
            this.designGroupID = designGroupID;
            return this;
        }

        public Builder setParentID(String parentID) {
            this.parentID = parentID;
            return this;
        }

        public Builder setRange(Range range) {
            this.range = range;
            return this;
        }

        public Builder setOrientation(SequenceDirection orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setPrimerSequence(NucleotideEncodedGlyphs primerSequence) {
            this.primerSequence = primerSequence;
            return this;
        }

        public PrimerDesignResult build() {
            DesignGroupKey key = getDesignGroupKey();
            return new PrimerDesignResult(key, parentID, range, orientation, primerSequence);
        }

        private DesignGroupKey getDesignGroupKey() {
            if ( designGroupKey != null ) {
                if ( designGroupLocationHash != null || designGroupID != null ) {
                    throw new IllegalStateException(
                        "Can't build a PrimerDesignResult using both a designGroupKey and" +
                            " designGroupID|designGroupLocationHash values"
                    );
                }
                return designGroupKey;
            } 
            if ( designGroupLocationHash == null || designGroupID == null ) {
                throw new IllegalStateException(
                    "designGroupID|designGroupLocationHash values must be non-null" +
                        " when building a PrimerDesignResult object using these values"
                );
            }
            return new DesignGroupKey(designGroupID,designGroupLocationHash);
        }
    }

    public static class DesignGroupKey {

        // hash value used to guarantee uniqueness between design group ids
        // since kelvin's primer designer may/will re-use design group ids across
        // multiple primer designer runs
        private int designGroupLocationHash;

        // (kelvin) primer designer id assigned a group of related primers
        // (loosely) links a set of primers together into a primer group
        // unique across all primers in a single primer designer run, but
        // may not be unique across primers in multiple primer designer runs
        private String designGroupID;

        DesignGroupKey(String designGroupID, int designGroupLocationHash) {
            this.designGroupID = designGroupID;
            this.designGroupLocationHash = designGroupLocationHash;
        }

        public String getDesignGroupID() {
            return designGroupID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DesignGroupKey that = (DesignGroupKey) o;

            if (designGroupLocationHash != that.designGroupLocationHash) return false;
            if (!designGroupID.equals(that.designGroupID)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = designGroupLocationHash;
            result = 31 * result + designGroupID.hashCode();
            return result;
        }
    }
}
