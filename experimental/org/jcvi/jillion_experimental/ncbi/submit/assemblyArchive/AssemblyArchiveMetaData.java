package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

import java.util.Date;
import java.util.List;

public interface AssemblyArchiveMetaData {

	/**
	 * 
	 * @return
	 */
	String getSubmitterReference();
	
	String getCenterName();

	String getDescription();
	
	String getOrganismName();

	String getStructure();

	Date getSubmissionDate();
	
	List<String> getSegmentsToSubmit();

}
