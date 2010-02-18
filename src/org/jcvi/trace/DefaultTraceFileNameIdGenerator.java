/*
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

public class DefaultTraceFileNameIdGenerator implements TraceFileNameIdGenerator{

    private final boolean stripExtension;
    public DefaultTraceFileNameIdGenerator(boolean stripExtension){
        this.stripExtension = stripExtension;
    }
    @Override
    public String generateIdFor(String filePath) {
        String fileName = getFileNameFrom(filePath);
        if(stripExtension){
            return stripExtension(fileName);
        }
        return fileName;
    }

    private String getFileNameFrom(String filePath) {
        String[] brokendownPath =filePath.split("/");
        return  brokendownPath[brokendownPath.length-1];
    }
    private String stripExtension(String fileName){
        int index =fileName.lastIndexOf('.');
        if(index !=-1){
            if(index ==0){
                throw new IllegalArgumentException("fileName can not only have 1 '.' which is at the beginning");
            }
            return fileName.substring(0, index);
        }
        return fileName;
    }

}
