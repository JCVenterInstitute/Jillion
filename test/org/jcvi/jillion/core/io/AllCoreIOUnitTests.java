/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestIOUtil_closeAndIgnore.class,
        TestIOUtil_blockingSkip.class,
        TestIOUtil_readByteArray.class,
        TestIOUtil_readShortArray.class,
        TestIOUtil_convertSignedToUnsigned.class,
        TestIOUtil_UnsignedByteArray.class,
        TestIOUtil_deleteDir.class,
        TestIOUtil_convertToUnsignedByteArray.class,
        TestIOUtil_whenMakingDirectories.class,
        TestIOUtil_unsignedByteToSignedByte.class,
        TestIOUtil_unsignedShortToSignedShort.class,
        TestIOUtil_unsignedIntToSignedInt.class,
        TestIOUtil_getUnsignedBitAndByteCount.class,
        TestIOUtil_bitSet.class,
        TestIOUtil_copy.class,
        TestIOUtil_toByteArray.class,
        TestIOUtil_toString.class,
        
        
        TestFileUtil.class,
        TestMagicNumberInputStream.class,
        TestOpenAwareInputStream.class
    }
    )
public class AllCoreIOUnitTests {

}
