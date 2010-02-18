/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.util.Properties;

import org.jcvi.trace.sanger.SangerTrace;

public interface Chromatogram extends SangerTrace{
   
    
    ChannelGroup getChannelGroup();
    Properties getProperties();

}
