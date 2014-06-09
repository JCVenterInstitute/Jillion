package org.jcvi.jillion_experimental.align.blast;

import java.util.List;

public interface BlastHit {

	/**
	 * Get the Id of the Query sequence in this Hsp.
	 * @return a String; will never be null.
	 */
	String getQueryId();
	/**
	 * Get the Id of the Subject sequence in this Hsp.
	 * @return a String; will never be null.
	 */
    String getSubjectId();
    /**
	 * Get the defline of the Subject in this Hsp.
	 * @return a String; may be null if not specified.
	 */
    String getSubjectDefinition();
    
    Integer getQueryLength();
    
    Integer getSubjectLength();
    
    List<Hsp> getHsps();
    
    String getBlastDbName();
    
    String getBlastProgramName();
}
