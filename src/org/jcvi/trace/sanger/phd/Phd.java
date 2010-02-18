/*
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.List;
import java.util.Properties;

import org.jcvi.trace.sanger.SangerTrace;

public interface Phd extends SangerTrace {

    Properties getComments();
    List<PhdTag> getTags();
}
