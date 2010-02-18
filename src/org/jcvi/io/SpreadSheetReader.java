/*
 * Created on Jul 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;

public interface SpreadSheetReader extends Closeable{
    String[] getColumnNames();
    Iterator<Map<String, String>> getRowIterator();
}
