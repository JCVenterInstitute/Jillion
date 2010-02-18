/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

public interface AnalysisIssue {

    public enum Severity{
        LOW,
        MEDIUM,
        HIGH
    }
    
    Severity getSeverity();
    String getMessage();
    
}
