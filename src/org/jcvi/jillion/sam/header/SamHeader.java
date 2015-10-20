/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.header;

import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.sam.SamRecordI;
import org.jcvi.jillion.sam.SamValidationException;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
/**
 * {@code SamHeader}
 * is an object representation of
 * the header of a SAM or BAM file.
 * @author dkatzel
 *
 */
public interface SamHeader {
    /**
     * Get the {@link SortOrder} for the
     * {@link SamRecord}s in the SAM File with
     * this header.
     * @return a {@link SortOrder}; will never be null.
     */
    SortOrder getSortOrder();
    /**
     * Get the {@link SamVersion} of this SAM File
     * with this header if known.
     * @return the {@link SamVersion} if known
     * or {@code null} if unknown.
     */
    SamVersion getVersion();
    /**
     * Get the {@link SamReferenceSequence}
     * object with the given name.
     * @param name the name to look up.
     * @return a {@link SamReferenceSequence}
     * which will be non-null if the reference is known to the header;
     * and {@code null} if the reference is not present.
     */
    SamReferenceSequence getReferenceSequence(String name);
    /**
     * Get the {@link SamProgram} with the given id
     * if known.
     * @param id the id of the {@link SamProgram}.
     * @return a {@link SamProgram} or {@code null}
     * if unknown.
     */
    SamProgram getSamProgram(String id);
    /**
     * Get the {@link SamReadGroup} with the given id
     * if known.
     * @param id the id of the {@link SamReadGroup}.
     * @return a {@link SamReadGroup} or {@code null}
     * if unknown.
     */
    SamReadGroup getReadGroup(String id);
    /**
     * Get the {@link SamReadGroup}s used in this SAM File.
     * 
     * @return a Collection of {@link SamReadGroup}s
     * will never be null but may be empty if there are no read groups are mentioned.
     */
    Collection<SamReadGroup> getReadGroups();
    /**
     * Get the {@link SamProgram}s used in this SAM File.
     * 
     * @return a Collection of {@link SamProgram}s
     * will never be null but may be empty if there are no programs are mentioned.
     */
    Collection<SamProgram> getPrograms();
    /**
     * Get the {@link SamReferenceSequence}s used in this SAM File
     * in the order they were encountered.  This will be the index 
     * order if the SAM file is an indexed BAM.
     * 
     * @return a Collection of {@link SamReferenceSequence}s
     * will never be null but may be empty if there are no references used.
     */
    Collection<SamReferenceSequence> getReferenceSequences();
    /**
     * Get the String comments if any.  Each Comment line
     * is usually a single element in the returned list.
     * @return a List of Strings; will never be null.
     * If there are no comments, then an empty list will be returned.
     */
    List<String> getComments();
    /**
     * Validate the given {@link SamRecord} using the given
     * {@link SamAttributeValidator}.
     * <br/>
     * A {@link SamRecord} is invalid if:
     * <ul>
     * <li>The {@link SamRecord#getReferenceName()} is not null
     * and this SamHeader does not have the {@link ReferenceSequence} via 
     * {@link #hasReferenceSequence(String)}.</li>
     * <li>The {@link SamRecord#getNextName()} is not null
     * and this SamHeader does not have the {@link ReferenceSequence} via 
     * {@link #hasReferenceSequence(String)}.</li>
     * <li>Any of the {@link SamAttribute}s in the {@link SamRecord}
     * fail the validation of the given {@link SamAttributeValidator}</li>
     * </ul>
     * @param record the {@link SamRecord} to validate;
     * can not be null.
     * @param attributeValidator the {@link SamAttributeValidator};
     * can not be null.
     * @throws SamValidationException if there is a validation problem.
     * @throws NullPointerException if either parameter is null.
     */
    void validateRecord(SamRecordI record,
            SamAttributeValidator attributeValidator)
            throws SamValidationException;

}
