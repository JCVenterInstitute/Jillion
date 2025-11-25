package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Marker interface to denote that this function
 * convert a Fasta defline into a new defline.
 *
 * @since 6.1
 */
@FunctionalInterface
public interface DeflineConverter extends BiFunction<String, String, Defline> {
    /**
     * Convert the given id and optional comment into a Defline.
     * @param id the id; will never be null.
     * @param optionalComment the optional comment; which MAY be null.
     * @return a new Defline object; May be null
     * which some processes might use to denote to skip the record.
     */
    @Override
    Defline apply(String id, String optionalComment);

    default Defline apply(Defline defline){
        if(defline==null){
            return null;
        }
        return apply(defline.getId(), defline.getComment());
    }
    /**
     * Apply this conversion and then the following DeflineConverter.
     *
     * @param nextConverter the next converter to apply; can not be null.
     * @return a new DeflineConverter that performs both conversions serially.
     * @throws NullPointerException if nextConverter is null.
     *
     * @implNote this is the same as
     * {@code
     * return (id,comment)->{
     *             Defline initial = apply(id, comment);
     *             if(initial==null){
     *                 return null;
     *             }
     *             return nextConverter.apply(initial.getId(), initial.getComment());
     *         };
     * }
     */
    default DeflineConverter andThen(DeflineConverter nextConverter){
        Objects.requireNonNull(nextConverter);
        return (id,comment)->{
            Defline initial = apply(id, comment);
            if(initial==null){
                return null;
            }
            return nextConverter.apply(initial);
        };
    }
}
