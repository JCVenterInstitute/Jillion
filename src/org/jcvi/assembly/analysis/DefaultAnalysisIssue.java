/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

public class DefaultAnalysisIssue implements AnalysisIssue {
    private final Severity severity;
    private final String message;
    
    public DefaultAnalysisIssue(Severity severity, String message){
        this.severity = severity;
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }
    @Override
    public String toString() {
        return new StringBuilder("[ ")
                            .append(severity)
                            .append(" ] : ")
                            .append(message)
                            .toString();
    }

}
