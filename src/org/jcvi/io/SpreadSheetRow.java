/*
 * Created on Oct 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

public interface SpreadSheetRow {

    String getColumn(int index);
    String getColumn(String columnName);
}
