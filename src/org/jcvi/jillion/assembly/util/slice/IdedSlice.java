package org.jcvi.jillion.assembly.util.slice;

public interface IdedSlice extends Slice<IdedSliceElement>{

	/**
     * Does this {@link Slice} contain a 
     * {@link IdedSliceElement} with the given id.
     * @param elementId the id of the {@link IdedSliceElement} being queried.
     * @return {@code true} if this Slice does contain
     * a {@link IdedSliceElement} with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link IdedSliceElement} if exists; or {@code null}
     * if there is no {@link IdedSliceElement} for this Slice with that id.
     */
    IdedSliceElement getSliceElement(String elementId);
}
