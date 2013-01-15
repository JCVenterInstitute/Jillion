package org.jcvi.jillion.assembly.util.slice;

public interface IdedSliceElement extends SliceElement{

	/**
     * Get the ID of this element.  Each element in a Slice must
     * have a different ID, although there SliceElements from 
     * different Slices can have the same ID.  This ID is usually the 
     * read ID.
     * @return the ID of this slice element.
     */
    String getId();
}
