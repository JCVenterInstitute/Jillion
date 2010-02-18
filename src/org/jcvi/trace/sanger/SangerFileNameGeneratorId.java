/*
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.IOException;


import org.jcvi.trace.TraceFileNameIdGenerator;
import org.jcvi.trace.TraceIdGenerator;

public class SangerFileNameGeneratorId<T extends FileSangerTrace> implements TraceIdGenerator<T,String>{

    private final TraceFileNameIdGenerator fileNameIdGenerator;
    
    SangerFileNameGeneratorId(TraceFileNameIdGenerator fileNameIdGenerator){
        this.fileNameIdGenerator= fileNameIdGenerator;
    }
    @Override
    public String generateIdFor(T input) {
        try {
            return fileNameIdGenerator.generateIdFor(input.getFile().getName());
        } catch (IOException e) {
            throw new IllegalStateException("could not get SangerFile", e);
        }
    }

}
