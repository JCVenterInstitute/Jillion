package org.jcvi.jillion.core.residue;

import lombok.*;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.spi.InvalidCharacterHandler;

/**
 * Options for how to decode Nucleotide's from Strings/characters into {@link Nucleotide}
 * objects.  This object handles invalid characters via and
 * additional options such as converting ambigious bases all to Ns.
 * Create using the {@link #builder()} and {@link #toBuilder()}
 * methods.
 *
 * @author dkatzel
 * @since 6.0
 * <p>
 * see {@link DecodingOptionsBuilder}
 */
@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DecodingOptions {

    public static DecodingOptions DEFAULT = DecodingOptions.builder().build();
    @Getter
    @Builder.Default
    InvalidCharacterHandler invalidCharacterHandler = InvalidCharacterHandlers.ERROR_OUT;

    /**
     * Replace all ambiguous bases with Ns.  This only affects
     * downstream calls to append/prepend/insert, previously
     * added bases are not changed.
     *
     * @param replaceAllAmbigutiesWithNs {@code true} if any future encountered
     * ambiguities should be changed to Ns, {@code false} otherwise.
     * @return this
     */
    @Getter(value = AccessLevel.PRIVATE)
    boolean replaceAllAmbiguitiesWithNs;

    /**
     * Change the {@link InvalidCharacterHandler}
     * to use {@link InvalidCharacterHandlers#REPLACE_WITH_UNKNOWN}
     * @return this
     *
     * @since 6.1
     */
    @Deprecated
    public boolean replaceAllAmbiguitiesWithNs() {
        return replaceAllAmbiguitiesWithNs;
    }

    public boolean replaceAllAmbiguitiesWithUnknown() {
        return replaceAllAmbiguitiesWithNs;
    }



    public static class DecodingOptionsBuilder{

        public DecodingOptions build(){
            if (this.invalidCharacterHandler$value ==null) {
                invalidCharacterHandler$value = InvalidCharacterHandlers.ERROR_OUT;
            }
            return new DecodingOptions(invalidCharacterHandler$value, this.replaceAllAmbiguitiesWithNs);

        }
    }

}
