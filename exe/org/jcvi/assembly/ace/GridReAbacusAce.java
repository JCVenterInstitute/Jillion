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

package org.jcvi.assembly.ace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.command.grid.GridJobBuilder;
import org.jcvi.common.command.grid.GridJobBuilders;
import org.jcvi.common.command.grid.GridJobExecutorService;
import org.jcvi.common.command.grid.PostExecutionHook;
import org.jcvi.common.command.grid.SimpleGridJob;
import org.jcvi.common.command.grid.GridJob.MemoryUnit;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AbstractAceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileUtil;
import org.jcvi.common.core.assembly.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultReadAceTag;
import org.jcvi.common.core.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.ReadAceTag;
import org.jcvi.common.core.assembly.ace.WholeAssemblyAceTag;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.internal.command.grid.JcviQueue;

/**
 * @author dkatzel
 *
 *
 */
public class GridReAbacusAce {

    private static final int DEFAULT_FLANK_LENGTH = 20;
    private static final int DEFAULT_MAX_JOBS = 300;
    
    private static final File ABACUS_WORKER_EXE;
    
    static{
        try {
            ABACUS_WORKER_EXE =getPathToAbacusWorker();
        } catch (IOException e) {
            throw new RuntimeException("error reading property file",e);
        }
    }
    
    
    public static void main(String[] args) throws IOException, DrmaaException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("nav", "consed navigation file input that says where the problems are to be fixed (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to ace output file (required)")
        .isRequired(true)
        .longName("out")
        .build());
        
        options.addOption(new CommandLineOptionBuilder("flank", "number of bases on each side of the problem regions to include in the reabacus.  " +
                "We add flanking bases in order to improve the alignments.  " +
                "Default flank if not specified is "+ DEFAULT_FLANK_LENGTH)
        .build());
        options.addOption(new CommandLineOptionBuilder("himem", "use the himem queue")
        .isFlag(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("P", "grid project code (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("max_submitted_jobs", "max number of jobs that are SUBMITTED to the grid " +
                "at any time.  If there are more jobs than this number to submit, then this program will queue them internally " +
                "and wait for the currently submitted jobs to finish.  NOTE: it is still up to the grid engine to schedule the jobs that have " +
                "been submitted.  If this option isn't set, then the default value is used : " + DEFAULT_MAX_JOBS)
        .build());
        
        
        options.addOption(CommandLineUtils.createHelpOption());     
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        SessionFactory factory = SessionFactory.getFactory();
        final Session session = factory.getSession();
        session.init("");
        GridJobExecutorService executor=null;
        OutputStream out=null;
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            String projectCode = commandLine.getOptionValue("P");
            File inputAceFile = new File(commandLine.getOptionValue("a"));
            File navigationFile = new File(commandLine.getOptionValue("nav"));
            File outputAceFile = new File(commandLine.getOptionValue("o"));
            out = new FileOutputStream(outputAceFile);
            int numberOfFlankingBases = commandLine.hasOption("flank")? 
                    Integer.parseInt(commandLine.getOptionValue("flank"))
                    : DEFAULT_FLANK_LENGTH;
                    
            int maxJobs = commandLine.hasOption("max_submitted_jobs")?
                    Integer.parseInt(commandLine.getOptionValue("max_submitted_jobs"))
                    : DEFAULT_MAX_JOBS;
                    
            boolean useHiMem = commandLine.hasOption("himem");
            File workDir = new File(outputAceFile.getAbsolutePath()).getParentFile();
            executor = new GridJobExecutorService(session,"abacusErrorDetector", maxJobs);
            
            MasterAceVisitor visitor = new MasterAceVisitor(out, inputAceFile,navigationFile, session, projectCode,workDir,executor, numberOfFlankingBases,outputAceFile, useHiMem);
      
            AceFileParser.parse(inputAceFile, visitor);
            //when we get here we are done all jobs
            executor.shutdown();
            for(String contigId : visitor.getContigIds()){
                File tempFile = new File(outputAceFile.getParentFile(), outputAceFile.getName()+".contig"+contigId);
                InputStream in = new FileInputStream(tempFile);
                IOUtil.copy(in, out);
                IOUtil.closeAndIgnoreErrors(in);
                tempFile.delete();
            }
            IOUtil.copy(new ByteArrayInputStream(visitor.getTagOutputStream().toByteArray()), out);
            
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }finally{
            IOUtil.closeAndIgnoreErrors(out);
            session.exit();
        }
        
    }
    
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "gridReAbacusAce -a <ace file> -nav <input nav file> -o <ace out file>", 
                
                "Parse an ace file and input nav file with abacus problem regions and write out a new ace file " +
                "with those regions realigned and consensus recalled to try to correct the problem regions.",
               
                options,
               "Created by Danny Katzel"
                  );
    }
    
    private static final File getPathToAbacusWorker() throws IOException{
       InputStream in = GridReAbacusAce.class.getResourceAsStream("/javacommon.config");
       Properties props =new Properties();
       try{
           props.load(in);
       }finally{
           IOUtil.closeAndIgnoreErrors(in);
       }
       if(!props.containsKey("re_abacus_worker")){
           throw new IllegalStateException("could not read property 're_abacus_worker'");
       }
       return new File(props.get("re_abacus_worker").toString());
    }
    private static final class MasterAceVisitor extends AbstractAceFileVisitor{
        private final OutputStream aceOut;
        
        private final ByteArrayOutputStream tagOutputStream = new ByteArrayOutputStream();
        private final File aceFile;
        private final File outputAceFile;
        private DefaultConsensusAceTag.Builder consensusTagBuilder;
        private final String projectCode;
        private final Session session;
        private final List<SimpleGridJob> jobs = new ArrayList<SimpleGridJob>();
        private final File navigationFile;
        private final File workDir;
        private final GridJobExecutorService executor;
        private final int numberOfFlankingBases;
        private final List<String> contigIds = new ArrayList<String>();
        private final boolean useHiMem;
        public MasterAceVisitor(OutputStream aceOut, File aceFile, File navigationFile, Session session, String projectCode,
                File workDir,GridJobExecutorService executor, int numberOfFlankingBases,File outputAceFile,boolean useHiMem) {
            this.aceOut = aceOut;
            this.outputAceFile = outputAceFile;
            this.aceFile = aceFile;
            this.session = session;
            this.projectCode = projectCode;
            this.navigationFile = navigationFile;
            this.workDir =workDir;
            this.executor = executor;
            this.numberOfFlankingBases = numberOfFlankingBases;
            this.useHiMem=useHiMem;
        }

        /**
         * @return the tagOut
         */
        public ByteArrayOutputStream getTagOutputStream() {
            return tagOutputStream;
        }

        /**
         * {@inheritDoc}
         */
         @Override
         public synchronized void visitHeader(int numberOfContigs,
                 int totalNumberOfReads) {
            //header will be the same because we aren't changing the
            //number of reads or contigs
             try {
                AceFileUtil.writeAceFileHeader(numberOfContigs, totalNumberOfReads, aceOut);
            } catch (IOException e) {
                throw new IllegalStateException("error writing out new ace header",e);
            }
             
         }
         @Override
         public void visitReadTag(String id, String type, String creator,
                 long gappedStart, long gappedEnd, Date creationDate,
                 boolean isTransient) {
             super.visitReadTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
             ReadAceTag tag =new DefaultReadAceTag(id, type, creator, creationDate, 
                     Range.create(gappedStart,gappedEnd), isTransient);
             
             try {
            	 AceFileUtil.writeReadTag(tag, tagOutputStream);
            } catch (IOException e) {
                throw new IllegalStateException("error writing out new ace read tag",e);
            }

         }
         
         @Override
         public void visitWholeAssemblyTag(String type, String creator,
                 Date creationDate, String data) {
             super.visitWholeAssemblyTag(type, creator, creationDate, data);
             WholeAssemblyAceTag tag = new DefaultWholeAssemblyAceTag(type, creator, creationDate, data);
             try {
            	 AceFileUtil.writeWholeAssemblyTag(tag, tagOutputStream);
             } catch (IOException e) {
                 throw new IllegalStateException("error writing out new ace whole assembly tag",e);
             }
         }
         
         @Override
         public synchronized void visitBeginConsensusTag(String id, String type, String creator,
                 long gappedStart, long gappedEnd, Date creationDate,
                 boolean isTransient) {
             super.visitBeginConsensusTag(id, type, creator, gappedStart, gappedEnd, creationDate, isTransient);
             consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                     type, creator, creationDate, Range.create(gappedStart, gappedEnd), isTransient);

         }
         @Override
         public void visitConsensusTagComment(String comment) {
             super.visitConsensusTagComment(comment);
             consensusTagBuilder.addComment(comment);

         }

         @Override
         public void visitConsensusTagData(String data) {
             super.visitConsensusTagData(data);
             consensusTagBuilder.appendData(data);

         }

        

         @Override
         public void visitEndConsensusTag() {
             super.visitEndConsensusTag();
             ConsensusAceTag tag = consensusTagBuilder.build();
             try {
            	 AceFileUtil.writeConsensusTag(tag, tagOutputStream);
             } catch (IOException e) {
                 throw new IllegalStateException("error writing out new ace consensus tag",e);
             }

         }
         
         /**
         * @return the contigIds
         */
        public List<String> getContigIds() {
            return contigIds;
        }

        @Override
         protected void visitNewContig(final String contigId, NucleotideSequence consensus, int numberOfBases, int numberOfReads, boolean complemented) {
             Command findAbacusErrorWorker = new Command(ABACUS_WORKER_EXE);
             contigIds.add(contigId);
             File tempOutputFile = new File(outputAceFile.getParentFile(), outputAceFile.getName()+".contig"+contigId);
             findAbacusErrorWorker.setOption("-ace", aceFile.getAbsolutePath());
             findAbacusErrorWorker.setOption("-c", contigId);
             findAbacusErrorWorker.setOption("-nav", navigationFile.getAbsolutePath());
             findAbacusErrorWorker.setOption("-o", tempOutputFile.getAbsolutePath());
             findAbacusErrorWorker.setOption("-flank", ""+numberOfFlankingBases);
             findAbacusErrorWorker.setWorkingDir(workDir);
             GridJobBuilder<SimpleGridJob> job = GridJobBuilders.createSimpleGridJobBuilder(
                                                 session,
                                                 findAbacusErrorWorker, 
                                                 projectCode);
             if(useHiMem){
                 job.setQueue(JcviQueue.HI_MEM.getQueueName());
             }
             job.postExecutionHook(new PostExecutionHook() {
                 
                 @Override
                 public int execute(Map<String, JobInfo> jobInfoMap) throws Exception {
                     for(Entry<String, JobInfo> entry : jobInfoMap.entrySet()){
                         JobInfo info = entry.getValue();
                         if(info.hasSignaled()){
                             System.err.printf("grid job %s for contig id %d was cancelled%n",
                                     entry.getKey(),contigId);
                             return 1;
                         }
                         if (info.hasExited() && info.getExitStatus() != 0){
                             System.err.printf("grid job %s for contig id %d FAILED%n",
                             entry.getKey(),contigId);
                             executor.shutdownNow();
                             return 1;
                         }
                             System.out.printf("grid job %s for contig id  %s finished%n", 
                                     entry.getKey(), contigId);
                     }
                     return 0;
                 }
             });
             job.setMemory(16, MemoryUnit.GB);
             job.setWorkingDirectory(workDir);
             jobs.add(job.build());
             
         }
         
         @Override
         protected void visitAceRead(String readId, NucleotideSequence validBasecalls,
                 int offset, Direction dir, Range validRange, PhdInfo phdInfo,
                 int ungappedFullLength) {
             // TODO Auto-generated method stub
             
         }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitEndOfFile() {
            try {
                for(Future<?> future : executor.invokeAll(jobs)){
                    try {
                    	future.get();
                    } catch (ExecutionException e) {
                     e.printStackTrace();
                     //TODO should we shutdown now and set some
                     //kind of flag saying we errored out so we don't
                     //delete temp files?
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
         
         
    }
}
