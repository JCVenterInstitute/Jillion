#5.0 Release Notes

##Jillion 5 License change. 
Jillion 5 is now LGPL 2.1.  Previous versions of Jillion were GPL 3.  
This change follows similar bioinformatics libraries such as BioJava which should allow 
users to switch their code to use Jillion instead without any worries about license issues.

##Jillion 5 is now OSGI compliant module. 
All classes except for those under org.jcvi.jillion.internal.* are exported.

This release notes do not cover all changes, there are too many to list.
Only some of the most important changes are listed.  

For a complete list, please consult the change_log.txt file.

API changes:
============

1. Java 8 Support - Java 8 Lambda Support to many APIs. Most notably in 
                     many of the filter() methods on various Builder objects.  
    
  * Various Jillion Filter interfaces such as DataStoreFilter, ReadFilter and SliceElementFilter 
       now extend the new Java 8 Predicate interface which allows client code to use simple Java 8 
       Lambda expressions to filter their data.

       For example, to make a CoverageMap object for only forward reads of a contig you can now do this:

            new ContigCoverageMapBuilder<>(contig)
        			.filter(read -> read.getDirection() == Direction.FORWARD)
        			.build()

  * Many DataStoreBuilder objects have a new filterRecords( Prediate<T> ) method to only include
       only records that match the Java 8 Predicate.  DataStoreFilters and Java 8 Lambda Expressions are valid input.
       
For example, to make a NucleotideFastaDataStore where all the sequences are > 1000bp :

             new NucleotideFastaFileDataStoreBuilder(fastaFile)
						.filterRecords(record-> record.getLength() >1000)
						.build();

2.  FastqFileParser and FastaFileParser will now auto-detect zip and gzipped Files and handle the
    decompression for you.  Previously you had to provide a decompressed InputStream which could only
    be parsed once.  Now the Files can be parsed multiple times.

3.  Sam / BAM API changes - a lot of work was done to improve SAM and BAM support, some improvements
                            required API changes.

  * Changed SamVisitor API to remove visitRecord(Callback, SamRecord) which was only
        called when visiting SAM files.  All records for both SAM and BAM files now use 
    
        visitRecord(Callback callback, SamRecord record, VirtualFileOffset start, VirtualFileOffset end)
    
  where the offset values are either the offsets into the BAM for bam encoded files or null for sam files.
        This removes a lot of confusion and duplicated code when dealing with parsing both formats.

  * SamParserFactory.create(File) will now check to see if there is an indexed bam file
       using the samtools naming conventions, and if so, uses an optimized
       SamParser object that can use the index to randomly access reads by reference or alignment region.

  * SamRecord.Builder is now pulled out into its own class SamRecordBuilder

  * SamRecord from a class to an interface.  The old SamRecord class is now package private. 
       All API methods in sam package now use the new SamRecord interface instead of the old class.

  *  Created new SamAttributed interface which has the methods hasAttribute(...) and getAttribute(...)
       SamRecord and SamRecordBuilder now both implement this interface.
     
  * Added additional parameter to SamAttributeValidator to add a SamAttributed instance.  This will be
       the source that the attribute is from.  This allows new validators to be written to check other attributes
       from the same source.


New Methods Added:
==================

1. Java 8 Support  - added new methods to several classes that return Java 8 Streams. Including:

  *  Contig#reads() which returns a Stream<AssembledRead>
	
  * StreamingIterator#toStream() which converts a Jillion StreamingIterator into a Stream<T>.  
       Please remember to close the Stream when done.
       
  * Added new default method SamAttributeValidator#thenComparing(SamAttributeValidator other)
       which returns a new SamAttributeValidator that checks both validators in a chain and only
       passes if both validators pass the attribute.  Uses a similar construction to the new
       Java 8 Comparator.thenComparing(...) methods.
      
2. FastaRecord and FastqRecord - Added new method getLength() to both FastaRecord and FastqRecord
   which returns the length of the sequence.  Some implementations may use an optimized way
   to compute the length instead of querying the wrapped sequence object.

3. Sorting Fasta and Fastq Writers -  Nucleotide, Protein, Quality and Position FastqWriterBuilder 
                                      and FastaWriterBuilders can now sort records using a Comparator. 
                                      
  * Both in-memory only and using temp files to sort all the records are supported. Using the temp 
   files to help with sorting allows the writing very large sorted output files that would
   not have been able to all fit in memory.
    
  *  An additional overloaded sort() method takes a File object that is the directory to create the temp files in 
   (default directory is System temp).  

4. Sam/Bam Parser - Added new methods that only parse alignments for specific reference names and regions.

  * Added SamParser.parse(String referenceName, SamVisitor visitor) which will only visit the SamRecords
               in the file that map to the given reference.  Some implementations may use the bam index to quickly
               seek to the part of the bam file where the alignments for those references are stored.

  *  Added SamParser.parse(String referenceName, Range alignmentRange, SamVisitor visitor) which 
               will only visit the SamRecords in the file that map to the given reference.  Some 
               implementations may use the bam index to quickly seek to the part of the bam file
               where the alignments for those references are stored.

5. Added new helper method SamRecord.getAlignmentRange() which returns a Range that the record aligned to the read.


New Classes Added:
==================
1. SplitFastaWriter and SplitFastqWriter - Added new SplitFastaWriter and SplitFastqWriter classes
                which have 3 factory methods to make different Writer implementations that split up
                writing records to different files using different strategies : 
                roundRobin(), rollover() and deconvolve() each method takes a lambda function to create 
                the new individual writers and deconvolve() takes a second lambda which determines which
                output file the record will go to.

2. GenomeStatistics - New utility class for computing different  statistical measurements about genomes
                    (for example N50).  It uses the new Java 8 Collector interface.  
                    
For example to compute the N50 of all the records in a Fasta file:
    
    try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
														.hint(DataStoreProviderHint.ITERATION_ONLY)
														.build();
			
		Stream<NucleotideFastaRecord> stream = datastore.iterator().toStream();
		){
			OptionalInt n50Value = stream
										.map(fasta -> fasta.getLength())
										.collect(GenomeStatistics.n50Collector());
			
			//return value is optional because there might not be any records!
			if(n50Value.isPresent()){
				System.out.println("N50 = " + n50Value.getAsInt());
			}
		}

3.  CoverageMapCollectors - New utility class for creating Java 8 Collector objects that create
                           CoverageMap objects.  
                           
 For example,  if you had a contig and wanted a coverage map of the alignment locations of 
              just the forward reads capped  to a max of 200x coverage the code would look like this:
    
         CoverageMap<Range> forwardCoverageMap200x = contig.reads()
	 					.filter(read -> read.getDirection() == Direction.FORWARD)
	 					.map(AssembledRead::asRange)
	 					.collect(CoverageMapCollectors.toCoverageMap(200));


4.  LucyVectorSpliceTrimmer  - Performs vector splice trimming using  a simplified version 
     of the algorithm that the TIGR program Lucy used.  Takes a NucleotideSequence object as input
     and returns the Range that is vector free.


Performance Improvements
=========================

1. Fastq File Parsing and Writing  - Previous versions of Jillion had terribly slow fastq parsing and writing
                         that was 3-5x slower than other libraries.  A lot of effort was put into Jillion 5
                         to make it at least as fast as similar libraries.  The end result is Jillion 5 is 
                         now just as fast or faster than other libraries such as BioJava and Picard
                         when parsing fastq data for the most common use cases.

  * When not using Mementos	or DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY (which uses mementos)
        A new faster parsing implementation is used that doesn't need to keep track of file offsets. This improves
	    parsing time by 400 %
	    
  * Improved FastqWriting - The most common use case of parsing a fastq file and writing out the FastqRecord instances as is
	    to a different writer.  New internal classes are now used which don't convert the encoded quality strings
	    into QualitySequence objects unless getQualitySequence() is called.  This takes up slightly more memory
	    per record.  This usually isn't an issue because most of the time the files are streamed as ITERATION_ONLY
        so the records will be GC'ed as soon as they are out of scope in the iterator. 
         
When tested on large 25 million read fastq files from 1000genomes project, throughput improved by more than 25%.
        
  * ITERATION_ONLY data processing improvements - when using ITERATION_ONLY,
        certain expensive memory optimizations are turned off.  This improves runtime performance but up slightly more memory
	    per record.


Bug Fixes
===========
1. Generate 454 Universal Accession number did not
   generate valid id if the location x,y coordinates were very small.
   
2. Bug Fix in SAM and BAM header writer which incorrectly wrote out the MD5 values of the references as "MD5" 
   instead of the actual md5 hash value.
   
3. Bug Fix in SAM and BAM header writer which incorrectly wrote out the URI path to the reference file to be the md5 value
   instead of the actual path.

4. Bug Fix in BAM writer which incorrectly computed BAM bin.

5. Bug Fixes in BAM index writer which incorrectly computed BAM bin and intervals.

6. AceFileParser - more lenient Consensus Tag timestamp parsers to support CLC Workbench ace output
    which doesn't follow the ace file spec regarding timestamp resolution.
 
 
#Jillion installation instructions


Use Download Jar:
=================
Down load the latest Jillion jar file and then put it in your classpath.


##To build from Source:

 Jillion has both a Maven POM file as well as an Apache ANT file that can both be used
 to build source and test files. So use which ever is easier for you to (Maven is recommended).
 
 Jillion 5 requires Java 8 or higher to run.

To Build with Maven
-------------------
 Once Java  and Maven are installed on your system,
 from the root directory of a Jillion check-out type:
 
 %mvn clean install
 
 This will build jillion, run all the unit and integration tests and installs it in your local repository.
 Jillion is now ready to use.
 
To Build with Ant
-----------------
 Once Java  and Ant are installed on your system,
 from the root directory of a Jillion check-out type:
 
 %ant release
 
 This will compile all source files and create a new file in the root directory 
 named "Jillion-${version}.jar"
 
 Then put the build jar in your classpath.

# Bug Reports:
 
 Please report any bugs to the Bug Tracker on Jillion's sourceforge page:
 
 https://sourceforge.net/p/jillion/bugs/
 
 Please include the version and SVN revision number if you know it in any bug reports.
 
 Thank you,
 
 Danny Katzel
 
 