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
 * Created on Jul 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.ss;

import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.TextLineParser;


public class CSVReader extends AbstractSpreadSheetReader{

    public CSVReader(InputStream in) throws IOException{
        super(in);
    }
    public CSVReader(InputStream in, boolean skipFirstLine) throws IOException {
        super(in,skipFirstLine);
    }

    /**
     * @param in
     * @param hasHeaders
     * @throws IOException
     */
    public CSVReader(TextLineParser in, boolean hasHeaders) throws IOException {
        super(in, hasHeaders);
    }
    @Override
    protected String getColumnSeparator() {
        return ",";
    }

}
