/*
 * Created on Jun 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.InputStream;


public class TSVReader extends AbstractSpreadSheetReader{
    
    /**
     * @param in
     */
    public TSVReader(InputStream in) {
        super(in);
    }

    public TSVReader(InputStream in, boolean skipFirstLine) {
        super(in,skipFirstLine);
    }

    @Override
    protected String getColumnSeparator() {
        return "\t";
    }

    
    
}
