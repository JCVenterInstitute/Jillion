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
 * Created on Jun 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.idReader;
/**
 * @{code StringIdParser} is an implementation of
 * {@link IdParser} for Strings.
 * @author dkatzel
 *
 *
 */
public class StringIdParser implements IdParser<String> {
    /**
     * Any non-null string is valid.
     * @return {@code true} if given string is not null;
     * {@code false} otherwise.
     */
    @Override
    public boolean isValidId(String string) {
        return string !=null;
    }
    /**
     * Returns the given non-null value as the Id.
     * @return the given value.
     * @throws NullPointerException if given string is null.
     */
    @Override
    public String parseIdFrom(String string) {
        if(string ==null){
            throw new NullPointerException("string can not be null");
        }
        return string;
    }

}
