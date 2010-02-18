/*
 * Created on Jul 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.InputStream;


public class CSVReader extends AbstractSpreadSheetReader{

    public CSVReader(InputStream in){
        super(in);
    }
    public CSVReader(InputStream in, boolean skipFirstLine) {
        super(in,skipFirstLine);
    }

    @Override
    protected String getColumnSeparator() {
        return ",";
    }

}
