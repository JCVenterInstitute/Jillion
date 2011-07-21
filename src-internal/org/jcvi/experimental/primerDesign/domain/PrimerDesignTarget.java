package org.jcvi.experimental.primerDesign.domain;

import org.jcvi.common.core.Range;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Sep 1, 2010
 * Time: 2:25:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PrimerDesignTarget {

    String getID();
    Range getRange();
    
}
