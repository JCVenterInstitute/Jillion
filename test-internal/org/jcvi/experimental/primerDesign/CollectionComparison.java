package org.jcvi.experimental.primerDesign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: aresnick
* Date: Jan 22, 2010
* Time: 12:57:45 PM
* <p/>
* $HeadURL$
* $LastChangedRevision$
* $LastChangedBy$
* $LastChangedDate$
* <p/>
* Description:
*/
public class CollectionComparison {
    List<String> differences;

    public CollectionComparison(Collection a, Collection b) {
        differences = new ArrayList<String>();

        if ( a.size() != b.size() ) {
            differences.add("Collection a has " + a.size() + " elements, "
                + " while collection b has " + b.size() + " elements");
        }

        for ( Object item : a ) {
            if ( !b.contains(item) ) {
                differences.add("Item " + item + " in collection a, but not in collection b");
            }
        }

        for ( Object item : b ) {
            if ( !a.contains(item) ) {
                differences.add("Item " + item + " in collection b, but not in collection a");
            }
        }

    }

    public boolean areEquivalent() {
        return differences.isEmpty();
    }

    public String getDifferences() {
        StringBuilder builder = new StringBuilder();
        for ( String difference : differences ) {
            builder.append(difference);
            builder.append("\n");
        }
        return builder.toString();
    }

}