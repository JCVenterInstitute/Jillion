/*
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;


import java.io.IOException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRUtil;
import org.jcvi.trace.sanger.chromatogram.ztr.data.Data;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DataFactory;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RawData;

/**
 * The Chunk is the basic unit of the ZTR Structure.
 * Each Chunk consists of a type, some metadata and then the data.
 * @author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public abstract class Chunk {


    public void parseChunk(ZTRChromatogramBuilder builder, InputStream inputStream) throws TraceDecoderException{
        if(inputStream ==null){
            throw new TraceDecoderException("inputStream can not be null");
        }
        if(builder ==null){
            throw new TraceDecoderException("chromoStruct can not be null");
        }
        readMetaData(inputStream);
        readData(builder,inputStream);
    }

    /**
     * Read the meta data portion of the chunk
     * Some implementations of chunk may not have
     * metaData.
     *
     */
    protected void readMetaData(InputStream inputStream)throws TraceDecoderException{

       try{
        long length = readLength(inputStream);

        //skip over meta data here
        IOUtil.blockingSkip(inputStream, length);
       }
        catch(IOException ioEx){
            throw new TraceDecoderException("error reading chunk meta data",ioEx);
        }
    }

    /**
     * @return
     * @throws IOException
     * @throws TraceDecoderException
     * @throws NumberFormatException
     */
    protected int readLength(InputStream inputStream) throws TraceDecoderException{
       try{

        //get metaDataLength
        byte[] lengthArray = readLengthFromInputStream(inputStream);
        long length = ZTRUtil.readInt(lengthArray);
        if(length <0 || length > Integer.MAX_VALUE){
            //uhh... try other endian
            length = ZTRUtil.readInt(IOUtil.switchEndian(lengthArray));
        }

        return  (int)length;
       }
       catch(Exception e){
           throw new TraceDecoderException("error reading chunk length", e);
       }

    }

    private byte[] readLengthFromInputStream(InputStream inputStream)
            throws IOException, TraceDecoderException {
        byte[] lengthArray = new byte[4];
        int bytesRead = inputStream.read(lengthArray);
        if(bytesRead <4){
            String message ="invalid metaData length record only has " +bytesRead + " bytes";
            throw new TraceDecoderException(message);
        }
        return lengthArray;
    }


    /**
     * Read the data portion of the chunk.
     * This method calls parseData to interpret
     * the data and returns the result.
     */
    private void readData(ZTRChromatogramBuilder builder,InputStream inputStream)throws TraceDecoderException{
        int length = readLength(inputStream);

        //the data may be encoded
        //call dataFactory to get the data implementation

        parseData(decodeChunk(inputStream,length), builder);
    }

    /**
     * Performs the actual conversion from the data stored in the chunk
     * into the appropriate format and SETS the data to the given
     * {@link ZTRChromatogramBuilder} object.
     * @param unEncodedData the actual bytes to parse
     * (including any formating bytes).
     * @param builder the {@link ZTRChromatogramBuilder} to set the data to.
     * @throws TraceDecoderException if there are any problems parsing the data.
     */
    protected abstract void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder) throws TraceDecoderException;


    protected byte[] decodeChunk(InputStream inputStream, int datalength) throws TraceDecoderException{
        try{
           
           //first we have to read the data encoding
//          data can be encoded, get data type.
            // the data can be encoded multiple times
            //ex: runlength encoded, then zipped
            //we will know when we are done, when the first byte is 0.
            boolean stillEncoded = true;
            byte[] data = readData(inputStream,datalength);
            while(stillEncoded){
                final Data dataImplementation = DataFactory.getDataImplementation(data);
                if(dataImplementation instanceof RawData){
                    stillEncoded = false;
                }            
                else{
                    //not done yet
                    data = dataImplementation.parseData(data);
                }
            }
            return data;
        }
        catch(IOException e){
            throw new TraceDecoderException("error decoding chunk",e);
        }
    }

    private byte[] readData(InputStream inputStream, int datalength) throws IOException,
            TraceDecoderException {
        byte[] data = new byte[datalength];
        int indexIntoData=0;
        int bytesRead;
        //have to keep looping
        //because inputStream won't block on reads
        while((bytesRead = inputStream.read(data,indexIntoData,datalength-indexIntoData)) >0){
            indexIntoData +=bytesRead;
        }

        if(indexIntoData < datalength){
            String message = "invalid datalength field, length specified was " + datalength + " only " + indexIntoData;
            throw new TraceDecoderException(message);
        }
        return data;
    }





}
