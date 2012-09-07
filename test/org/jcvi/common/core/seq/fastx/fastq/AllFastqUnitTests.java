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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestSangerFastQQualityCodec.class,
        TestIlluminaFastQQualityCodec.class,
        TestSangerFastQQualityCodecActual.class,
        TestParseSangerEncodedFastQFile.class,        
        TestFormattingFastqRecords.class,
        TestDefaultFastqRecordWriter.class,
        
        TestDefaultFastQFileDataStore.class,
        TestDefaultMultiLineFastqRecordsInDataStore.class,
        TestDefaultFastqFileDataStoreGuessCodec.class, 
        
        TestIndexedFastQFileDataStore.class,
        TestDefaultFastqFileDataStoreMultilineGuessCodec.class,
        TestIndexedFastqFileDataStoreGuessCodec.class,
        TestIndexedMultilineFastqDataStore.class,
        
        TestLargeFastQFileDataStore.class,
        TestLargeMultilineFastqDataStore.class,
        TestLargeFastqFileDataStoreGuessCodec.class,
        TestLargeMultilineFastqFileDataStoreGuessCodec.class,
        
       
        
        
        
        AllIlluminaUnitTests.class,
        AllSolexaUnitTests.class,
        AllFastqUtilUnitTests.class
    }
    )
public class AllFastqUnitTests {

}
