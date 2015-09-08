/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.header;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.sam.SamRecord;
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

    String toString();

    SortOrder getSortOrder();

    SamVersion getVersion();

    boolean hasReferenceSequence(String name);

    SamReferenceSequence getReferenceSequence(String name);

    Iterator<SamReferenceSequence> getReferenceSequencesIterator();

    boolean hasSamProgram(String id);

    SamProgram getSamProgram(String id);

    boolean hasReadGroup(String id);

    SamReadGroup getReadGroup(String id);

    Collection<SamReadGroup> getReadGroups();

    Collection<SamProgram> getPrograms();

    Collection<SamReferenceSequence> getReferenceSequences();

    List<String> getComments();

    int getReferenceIndexFor(String referenceName);

    SamReferenceSequence getReferenceSequence(int i);

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
    void validateRecord(SamRecord record,
            SamAttributeValidator attributeValidator)
            throws SamValidationException;

    Comparator<SamRecord> createRecordComparator();

}
