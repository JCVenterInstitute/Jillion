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

package org.jcvi.common.core.seq.trim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.trim.TrimDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
import org.jcvi.io.TextLineParser;

/**
 * {@code TrimFileUtil} is a utility class for parsing and writing
 * trim files.  the Trim File format is the same format as used
 * by sfffile.
 * @author dkatzel
 *
 *
 */
public final class TrimFileUtil {
    /**
     * The Trim format used by sfffile is {@code <read_id>\t<clear_left>\t<clear_right>\n}
     * the trim points are 1-based (residue).
     */
    private static final Pattern TRIM_PATTERN = Pattern.compile("^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s*$");
    
    private TrimFileUtil(){}
    /**
     * Parse the given trim file and visit
     * the appropriate methods of the given visitor.
     * @param trimFile a file containing trim data.
     * @param visitor the visitor to visit with the parsed trim data
     * @throws FileNotFoundException if the trim file does not exist.
     */
    public static void parseTrimFile(File trimFile, TrimFileVisitor visitor) throws FileNotFoundException{
        FileInputStream in = new FileInputStream(trimFile);
        try{
            parseTrimFile(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given InputStream which contains trim data and visit
     * the appropriate methods of the given visitor.  InputStream will
     * NOT be closed when parsing is complete.
     * @param trimData the trim data to parse.
     * @param visitor the visitor to visit with the parsed trim data.
     */
    public static void parseTrimFile(InputStream trimData, TrimFileVisitor visitor){
        TextLineParser scanner = null;
        try{
            try {
                scanner = new TextLineParser(trimData);           
                visitor.visitFile();
                boolean keepParsing=true;
                while(keepParsing && scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    visitor.visitLine(line);
                    Matcher matcher = TRIM_PATTERN.matcher(line);
                    if(matcher.matches()){
                        String id = matcher.group(1);
                        Range validRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, 
                                    Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
                        keepParsing =visitor.visitTrim(id, validRange);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("error reading file",e);
             }
        }
        finally{
            visitor.visitEndOfFile();
            IOUtil.closeAndIgnoreErrors(scanner);
        }
        
    }
    /**
     * Write the trim data contained in the given {@link TrimDataStore} to the given
     * trim file, overwriting any data previously contained in the file.
     * <p>
     * This is the same as {@link #writeTrimFile(TrimDataStore, File, boolean)
     * writeTrimFile(datastore, trimFile,false)}.
     * @param datastore the {@link TrimDataStore} containing the data to write.
     * @param trimFile the {@link File} to write the data to.
     * @throws DataStoreException if there is a problem getting the trim 
     * data from the datastore.
     * @throws IOException if there is a problem writing trim information 
     * to the File.
     * @throws NullPointerException if datastore or out are null.
     * @see #writeTrimFile(TrimDataStore, File, boolean)
     */
    public static void writeTrimFile(TrimDataStore datastore, File trimFile) throws DataStoreException, IOException{
        writeTrimFile(datastore, trimFile,false);
    }
    /**
     * Write the trim data contained in the given {@link TrimDataStore} to the given
     * trim file.
     * @param datastore the {@link TrimDataStore} containing the data to write.
     * @param trimFile the {@link File} to write the data to.
     * @param append {@code true} if the data should be appended to the file;
     * {@code false} if the data should overwrite the given file.
     * @throws DataStoreException if there is a problem getting the trim 
     * data from the datastore.
     * @throws IOException if there is a problem writing trim information 
     * to the File.
     * @throws NullPointerException if datastore or out are null.
     */
    public static void writeTrimFile(TrimDataStore datastore, File trimFile, boolean append) throws DataStoreException, IOException{
        OutputStream out = new FileOutputStream(trimFile, append);
        try{
            writeTrimFile(datastore, out);
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
        }
        
    }
    /**
     * Write the trim data contained in the given {@link TrimDataStore} to the given
     * {@link OutputStream}.
     * @param datastore the {@link TrimDataStore} containing the data to write.
     * @param out the OutputStream to write the trim data to.
     * @throws DataStoreException if there is a problem getting the trim 
     * data from the datastore.
     * @throws IOException if there is a problem writing trim information 
     * to the {@link OutputStream}.
     * @throws NullPointerException if datastore or out are null.
     */
    public static void writeTrimFile(TrimDataStore datastore, OutputStream out) throws DataStoreException, IOException{
        Iterator<String> iter = datastore.getIds();
        while(iter.hasNext()){
            final String id = iter.next();
            //force residue based
            Range trimRange = datastore.get(id)
                        .convertRange(CoordinateSystem.RESIDUE_BASED);
            out.write(String.format("%s\t%d\t%d%n",id,trimRange.getLocalStart(), trimRange.getLocalEnd()).getBytes());
        }
    }
}
