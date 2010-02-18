/*
 * Created on Sep 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

public enum AssemblyArchiveAttribute {
    /**
     * Submitter's free text reference attribute, 
     * submitter's internal reference id.
     */
    SUBMITTER_REFERENCE("submitter_reference"),
    /**
     * Attribute of the submission type.
     */
    TYPE("type"),
    /**
     * Assembly archive identifier.
     */
    ASSEMBLY_ARCHIVE_ID("ai");
    
    
     private final String elementName;
        
     AssemblyArchiveAttribute(String name){
        this.elementName = name;
    }

    @Override
    public String toString() {
        return elementName;
    }
}
