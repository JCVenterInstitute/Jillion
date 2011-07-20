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

package org.jcvi.common.io.idReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code FirstWordStringIdParser} parses the first non whitespace
 * word from a String.  For example "an_id comment" would return "an_id"
 * as the id.
 * @author dkatzel
 *
 *
 */
public class FirstWordStringIdParser extends StringIdParser{

    private static final Pattern FIRST_WORD_PATTERN = Pattern.compile("(\\S+)");
    @Override
    public boolean isValidId(String string) {        
        if( super.isValidId(string)){
            return FIRST_WORD_PATTERN.matcher(string).find();
        }
        return false;
    }

    @Override
    public String parseIdFrom(String string) {
        Matcher matcher =FIRST_WORD_PATTERN.matcher(string);
        if(matcher.find()){
            return matcher.group(1);
        }
        throw new IllegalStateException("could not parse first word from "+ string);
    }

}
