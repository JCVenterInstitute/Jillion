/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
 * Created on Dec 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.auth.BasicEncodedJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizerUtils;
import org.jcvi.auth.TigrAuthorizerUtils;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.net.http.HttpUtil;
import org.jcvi.datastore.zip.DefaultZipDataStore;
import org.jcvi.datastore.zip.ZipDataStore;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.StringIdParser;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.FileType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.RequestType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.ReturnFormat;

public class DownloadTraces {

    private static final String DEFAULT_OUTPUT_DIR =".";
    private static final int BATCH_SIZE =1000;
    private static final FileType DEFAULT_FORMAT = FileType.ZTR;
    private static final RequestType DEFAULT_REQUEST_TYPE = RequestType.SEQ_NAME;
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        TigrAuthorizerUtils.addProjectDbLoginOptionsTo(options, false);
        options.addOption(new CommandLineOptionBuilder("o", "output directory")
                        .longName("outdir")
                        .build());
       
        options.addOption(new CommandLineOptionBuilder("list", "file from which to read list")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("format", 
                String.format("format traces files should be downloaded as (default =%s)",DEFAULT_FORMAT.toString().toLowerCase()))
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("id_type", 
                String.format("id type that the given list of ids is in.  default is %s supported values = %s",DEFAULT_REQUEST_TYPE,Arrays.asList(RequestType.values())))
                        .build());
        System.setProperty(HttpUtil.SSL_TRUSTSTORE_PROPERTY_KEY,
        TraceFileServerUtil.TRACE_FILE_SERVER_TRUSTSTORE_UNIX_PATH);
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            JCVIAuthorizer authorizer;
            String baseUrl;
            Console console = System.console();
            if(commandLine.hasOption("D")){
                authorizer = TigrAuthorizerUtils.getProjectDbAuthorizerFrom(commandLine, console);
                baseUrl = TraceFileServerUtil.TIGR_URL;
            }
            else{
                authorizer = JCVIAuthorizerUtils.parseAuthorizerFrom(commandLine, console);
                baseUrl = TraceFileServerUtil.JCVI_FETCH_URL;
            }
            FileType fileType = commandLine.hasOption("format")?
                                FileType.valueOf(commandLine.getOptionValue("format").toUpperCase())
                                :
                                DEFAULT_FORMAT;
            IdReader<String> ids = new DefaultFileIdReader<String>(
                    new File(commandLine.getOptionValue("list")), new StringIdParser());
            
            String outputDir = commandLine.hasOption("out")? commandLine.getOptionValue("out"):DEFAULT_OUTPUT_DIR;
            
            RequestType requestType = commandLine.hasOption("id_type")? 
                    RequestType.parseRequestType(commandLine.getOptionValue("id_type")):
                        DEFAULT_REQUEST_TYPE;
            ReadWriteFileServer output = DirectoryFileServer.createReadWriteDirectoryFileServer(new File(outputDir));
            JcviTraceFileServer traceFileServer = JcviTraceFileServer.createJcviTraceFileServer(baseUrl, new BasicEncodedJCVIAuthorizer(authorizer));
            Iterator<String> idIterator = ids.iterator();
            while(idIterator.hasNext()){
                List<String> list = new ArrayList<String>();
                while(list.size() < BATCH_SIZE && idIterator.hasNext()){
                    list.add(idIterator.next());
                }
                if(list.size()>0){
                    ReadWriteFileServer tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
                    tempDir.putStream("traces.zip",
                            traceFileServer.getMultipleFilesAsStream(list, requestType, fileType, ReturnFormat.ZIP));
                    ZipDataStore zipStore = new DefaultZipDataStore(new ZipFile(tempDir.getFile("traces.zip")));
                    Iterator<String> zipIterator = zipStore.getIds();
                    while(zipIterator.hasNext()){
                        String id = zipIterator.next();
                        String externalId =JTraceFilenameUtil.getLIMSParentIDFrom(id);
                        output.putStream(externalId, zipStore.get(id));
                    }
                }
            }
            
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "downloadTraces", options );
            System.exit(1);
        }
        

    }

}

