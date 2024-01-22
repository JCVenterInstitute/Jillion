# Jillion

Jillion 6 requires Java 11+

Jillion 5 requires Java 8 and uses new Java 8 language features such as default methods, lambda expressions and the new `Stream` and `Collector` API.

# How to install

##Maven Central
The easiest way to use Jillion is including it as a dependency 
in your package manager.  Jillion is available on Maven Central
```
<dependency>
  <groupId>org.jcvi.jillion</groupId>
  <artifactId>jillion</artifactId>
  <version>6.0</version>
</dependency>
```

## Building from source
Jillion 5+ uses Maven to build and package a jar file.  From the root folder where the `pom.xml` file is type on the command line
```
% mvn clean install
```

This will build Jillion, run all the unit and integration tests and install it in your local repository.

Jillion is now ready to use.

### Building Without Running Tests
Some of the integration tests take several minutes.  To Build without re-running those tests, use

```
 %mvn clean install -DskipTests
```




 

# Bug Reports:
 
 Please report any bugs to the Issue section Jillion's github page:
 
 https://github.com/JCVenterInstitute/Jillion/issues
 
 Please include the version and git hash revision number for SNAPSHOTs if you know it in any bug reports.
 
 Thank you,
 
 Danny Katzel
 
 
 
# 6.0 Release Notes
There are too many to list here, see `change_log.txt` for the complete set of changes

## API Changes
1.  Jillion requires Java 11+
1.  fasta and fastq parsers will now seamlessly work on tar.gz files
1. Performance improvements

## Bug Fixes
1. Bug fixes for seeking and parsing BAM files


# 5.3.3 Release Notes

## API Changes
1. `AbstractNucleotideFastaRecordVisitor` and `AbstractProteinFastaRecordVisitor` now have 
   an new constructor with a boolean to turn off sequence compression for runtime performance improvements.
2. added default method `DataStore#isEmpty()`
3. `Residue#isAmbiguity()` has been added. previously this method was only on `Nucleotide` 
    it has been added to `AminoAcid` also.

# 5.3.2 Release Notes
## Performance improvements
1. New ProteinSequence implementation when the Builder's `turnOffCompression()` method is set to true.
this version takes up more memory per sequence but will be much faster to iterate over
or randomly access.
1. Some computations in Sequences are now lazily executed and cached which should improve performance
for certain sequence operations especially things like computations involving gaps.

1. Pairwise alignments runtime have been greatly improved.  Combined with the sequence improvements mentioned above
Protein sequence alignments are now 4x faster than in 5.3.1.

1. Fasta Writers implementations have been modified to be threadsafe so that writers
can be written to by multiple threads at the same time. (FastqWriters should already be threadsafe)

## API Changes
1. AminoAcid ambiguity code 'J' (Leucine or Isoleucine) is now supported.
1. added `ResidueSequence#ungappedIterator()` and `ResidueSequence#ungappedIteratable`
to more easily iterate over all the bases that aren't gaps.
1. added `Sequence#trim(Range)` which returns a new Sequence object that 
contains only the subsequence of the range.
1. added `FastaWriter#trim(FastaRecord, Range)` which only writes out the part of the fasta 
within the given Range.
1. Added new `FastaCollectors` and `FastqCollectors` classes that have several Java 8 Collector
factory methods to do things like write from stream of records to formatted `Writers` or collect
the records to `DataStore`s.
1. added `trim(Range)` methods to `FastqRecord` and `FastaRecord`.
1. Exonerate and Vulgar packages have been deprecated and will be removed in the next version.

## 5.3.1 Release Notes
Jillion is now in Maven Central.  The first version to be deployed is 5.3.1.
## API Changes
1. Internal API method names have had some typos fixed.
1. Added `Nucleotide#cleanSequence()` and `AminoAcid#cleanSequence()` methods that remove
whitespace and invalid  characters.  There are multiple overloads with different parameters to specify
what to put in the place of the removed characters (ex: N).

# 5.3 Release Notes
## Bug Fixes
1. Fix for differently ordered original vs current data records in Ab1 Sanger traces files. ([#7][i7])

## API Changes 
1. made `Range#RangeAndCoordinateSystemToStringFunction` interface public so it can be used
as a lambda expression in client code. (was mistakenly package private before)

1. Added Support for Uracil. 
1. `NucleotideSequence` now has new method `isDna()`
1. Added new `SliceMapCollector` class 
1. Added new methods `NucleotideSequence#findMatches(regex)` that return the ranges
of regions in the sequence that match the given regular expression.
# 5.2 Release Notes

## New Features
1. Added new method to fastq writer to automatically trim given a Range. 
This saves users the trouble of creating SequenceBuilders and trimming themselves.
    
2.  Added new method to FastqRecord to get the average Quality of the quality sequence.
    The default implementation calls getQualitySequence().getAvgQuality() but some implementations
    use a more efficient version. 
    
3.  Added new QualityTrimmer SlidingWindowQualityTrimmer which acts like Trimmomatic's SLIDINGWINDOW option.

4. Added new convenience methods to NucleotideTrimmer and QualityTrimmer that take Builders.  This is really useful
   when performing multiple trimming operations in serial since some trimmers may be able to save CPU cycles
   and work directly from the builders.
   
5. Added new TrimmerPipeline and TrimmerPipelineBuilder classes which can take multiple NucleotideTrimmers
   and QualityTrimmers and combine the trimming results for you.
   
6. Added SamFileDataStore and SamFileDataStoreBuilder to finally provide a higher level API for
   working with sam and bam files without needing to use a low level Visitor. 
   
7. Added Optional<File> getFile() to FastqParser and refactored CasParser
   implementations to make it easier to extend cas file parsing.
   
8. Add lambda hook to CasFileTransformationService to override how FastqDataStore is generated so
   users could provide their own implementation. 
   
9.  Added new ConsensusCollectors class that can take Streams of various sequence inputs and compute a consensus.

10.  Added new TraceDirPhdDataStoreBuilder class that can make a PhdDataStore implementation from a folder of sanger trace files.

11. AbiChromatogramParser - Added support for ABI 3500 abi files.

## API Changes
1. Added Trace.getLength() 

2. Added default methods to Rangeable for getLength() getBegin(), getEnd() and isEmpty() since 
   that is used the most don't have to always build a new Range object.

3. Added Range.Builder intersect methods

4.  Changed TrimmerPipeline methods to be faster by making fewer Range objects and working off of Range.Builders instead.

5.  Added new Range.toString() methods that take lambda expressions so users can make their 
    own toString implementations.  There are several overloaded versions:
    * toString(RangeToStringFunction)
    * toString(RangeToStringFunction, CoordinateSystem)
    
    * toString(RangeAndCoordinateSystemToStringFunction)
    * toString(RangeAndCoordinateSystemToStringFunction, CoordinateSystem)
    
    to let users convert to different coordinate systems and to
    include that coordinate system in the lambda expression or not.

6.  Added `toGappedRange( Range)` and `toUngappedRange( Range)` to ResidueSequence
   with default implementations and more efficient implementation when the codec 
   knows it doesn't have gaps.  Changed AssemblyUtil to use that instead of its own implementation.

7.  Added toUngappedRange( Range) to NucleotideSequenceBuilder

8.  DataStoreException now extends IOException - This is a *Breaking Change* if you had code that
    caught only DataStoreException and not IOException of had code that used a multi-catch to catch
    both an IOException and a DataStore Exception will now cause a compiler error if left unchanged. 

9.  Added new StreamingIterator.empty() method

## Bug Fixes
1.  BlastParser - fixed bug in XML Blast Parser when it sometimes accidentally set percent identity to be (1 - percent identity).
 
---

# 5.1 Release Notes

## New Features
1. Added new methods to `FastaDataStore` getSequence( id) which gets just the sequence
   and is equivalent to get(id).getSequence().
2. Added new methods `FastaDataStore.getSubSequence( id, offset)` which gets just the sequence
   starting from the given offset.
3. Added new methods to `FastaDataStore.getSubSequence( id, range)` which gets just the sequence
   that intersects the given range.
4.  Added support for Fasta Index Files (.fai) files to NucleotideFastaDataStore.
    The `NucleotideFastaFileDataStoreBuilder` object can now be given an fai file
    or auto-detect one and use that to make a more efficient implementation
    to be used with the new getSequence() or getSubSequence() methods.    
5.  Added support for writing Fasta Index Files (.fai) files to `NucleotideFastaWriter` using 
    the createIndex(true) method.  This will make an additional file named `$outputFasta.fai`.
    Supports normal, zipped and non-redundant fasta files.    
6.  Added new class `FaiNucleotideWriterBuilder` that can create new Fasta Index Files (.fai) for 
    existing fasta files.  The builder object supports fully configuration of the fai to be written
    including the output path, the end of line character, and the Charset.
7. Improved JavaDoc
8. `BlosumMatrices` class added support for Blosum30 and 40.

## API Changes
1. `FastqFileParser.canAccept()` renamed to `canParse()` to match the other parsers.
2. Created new abstract class AbstractReadCasVisitor which is now the parent class of AbstractAlignedReadCasVisitor.  The new class handles iterating over the input read files to link cas alignments to their read names, sequences and qualities.
   Now you can extend that class if you want that extra information without realigning to gapped references.
3. To Fix OSGi issues, Some classes that were in jillion.internal were moved to jillion.shared since all internal classes can't be exported by OSGI.  These classes should not be considered part of the public API and should only be for internal use.
4. Moved FastaUtil to internal package since it should not be used outside of Jillion classes.  Heavily refactored it.

## Bug Fixes
1.  PositionSequence - sanger `PositionSequence.iterator(Range)`
    had off by 1 bug that did not include the last base in the range.    
2.  StreamingIterator - abstract class that many StreamingIterators extend to use background thread
    to populate iterator has been improved to fix occasional dead lock issues if the background thread throws exceptions.

---

# 5.0 Release Notes

## Jillion 5 License change. 
Jillion 5 is now LGPL 2.1.  Previous versions of Jillion were GPL 3.  
This change follows similar bioinformatics libraries such as BioJava which should allow 
users to switch their code to use Jillion instead without any worries about license issues.

## Jillion 5 is now OSGI compliant module. 
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
 
 
 [i7]: https://github.com/JCVenterInstitute/Jillion/issues/7