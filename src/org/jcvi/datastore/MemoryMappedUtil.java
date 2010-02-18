/*
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jcvi.Range;

public class MemoryMappedUtil {

    public static InputStream createInputStreamFromFile(File file,Range range)throws IOException {
        FileChannel fastaFileChannel =new FileInputStream(file).getChannel();
        ByteBuffer buf= ByteBuffer.allocate((int)range.size());
        fastaFileChannel.position((int)range.getStart());
        fastaFileChannel.read(buf);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(buf.array());
        return inputStream;
    }
}
