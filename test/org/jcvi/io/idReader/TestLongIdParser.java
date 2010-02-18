/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public class TestLongIdParser extends AbstractIdParser<Long>{

    private Long value = Long.valueOf(123456789L);
    @Override
    protected IdParser<Long> createNewIdParser() {
        return new LongIdParser();
    }

    @Override
    protected String getInvalidId() {
        return "not an Id";
    }

    @Override
    protected Long getValidIdAsCorrectType() {
        return value;
    }

    @Override
    protected String getValidIdAsString() {
        return value.toString();
    }

}
