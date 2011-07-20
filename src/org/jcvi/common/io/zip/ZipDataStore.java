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
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.zip;

import java.io.InputStream;
import java.util.zip.ZipEntry;

import org.jcvi.common.core.datastore.DataStore;
/**
 * A {@code ZipDataStore} is a {@link DataStore} implementation
 * of a ZIP file.  The ids in this DataStore are the {@link ZipEntry}s
 * and the objects returned are {@link InputStream}s of the Files
 * contained in the zip.  NOTE: Since JAR files are actually ZIP
 * files, {@link ZipDataStore} can be used to read JARS as well.
 * @author dkatzel
 *
 *
 */
public interface ZipDataStore extends DataStore<InputStream>{

}
