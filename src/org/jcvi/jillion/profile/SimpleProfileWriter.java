package org.jcvi.jillion.profile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

class SimpleProfileWriter implements ProfileWriter {

	
	
	private final MostFrequentTieBreakerRule tieBreakerRule;
	
	private final PrintWriter writer;
	private final DisplayCountStrategy lineWriterStrategy;
	//private final NucleotideSequence referenceOrConsensus;
	private final double[][] counts;
	
	private final NucleotideSequence reference;
	private final boolean include0xEdges;
	
	private volatile boolean isOpen=true;
	
	public SimpleProfileWriter(File outputFile, DisplayCountStrategy displayStrategy, MostFrequentTieBreakerRule tieBreakerRule,
			NucleotideSequence referenceOfConsensus,boolean include0xEdges) throws IOException {
		IOUtil.mkdirs(outputFile.getParentFile());
		this.writer = new PrintWriter(outputFile);
		this.lineWriterStrategy = displayStrategy;
		this.counts = new double[(int)referenceOfConsensus.getLength()][5];
		this.tieBreakerRule = tieBreakerRule;
		this.reference = referenceOfConsensus;
		this.include0xEdges = include0xEdges;

	}

	public SimpleProfileWriter(OutputStream out, DisplayCountStrategy displayStrategy,MostFrequentTieBreakerRule tieBreakerRule, NucleotideSequence referenceOfConsensus,boolean include0xEdges){
		this.writer = new PrintWriter(out);
		this.lineWriterStrategy = displayStrategy;
		this.counts = new double[(int)referenceOfConsensus.getLength()][5];
		this.tieBreakerRule = tieBreakerRule;
		this.reference = referenceOfConsensus;
		this.include0xEdges = include0xEdges;
	}
	
	@Override
	public void close() throws IOException {
		//no-op is already closed
		if(!isOpen){
			return;
		}
		try{
			isOpen=false;
		writer.printf("#Major\t-\tA\tC\tG\tT%n");
		int start = getStartOffset();
		int end = getEndOffset();
		for(int i=start; i<= end; i++){
			
			
			Nucleotide mostFreq = getMostFrequentBase(i,
												counts[i][0],
												counts[i][1],
												counts[i][2],
												counts[i][3]);
			lineWriterStrategy.write(writer, mostFreq, counts[i][4],
						counts[i][0],
						counts[i][1],
						counts[i][2],
						counts[i][3]
						);
		}
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	
	protected double getAs(int offset){
		return counts[offset][0];
	}
	protected double getCs(int offset){
		return counts[offset][1];
	}
	protected double getGs(int offset){
		return counts[offset][2];
	}
	protected double getTs(int offset){
		return counts[offset][3];
	}
	protected double getGaps(int offset){
		return counts[offset][4];
	}

	private boolean is0x(int i) {
		return getAs(i) ==0D && getCs(i) ==0D && getGs(i)==0D && getTs(i) ==0D && getGaps(i) ==0D;
	}

	
	protected int getStartOffset() {
		if(include0xEdges){
			return 0;
		}
		for(int i=0; i< getLength(); i++){
			if(!is0x(i)){
				return i;
			}
		}
		return -1;
	}
	
	protected int getEndOffset() {
		if(include0xEdges){
			return getLength()-1;
		}
		for(int i=getLength()-1; i>0; i--){
			if(!is0x(i)){
				return i;
			}
		}
		return -1;
	}

	
	protected int getLength(){
		return counts.length;
	}
	protected Nucleotide getMostFrequentBase(int i,double a,double c,double g,double t) {
		if(a==0D && c==0D && g ==0D && t==0D && getGaps(i) ==0D){
			//0x use reference ?
			return reference.get(i);
		}
		Nucleotide mostFreq = getMostFrequentNonGapBase(a,c,g,t);
		return mostFreq;
	}

	@Override
	public void addSequence(int startOffset, NucleotideSequence sequence) {
		int currentOffset = startOffset;
		for (Nucleotide n : sequence) {
			Set<Nucleotide> bases = n.getBasesFor();
			double fraction = 1D / bases.size();
			for (Nucleotide base : bases) {
				switch (base) {
					case Adenine:
						counts[currentOffset][0] += fraction;
						break;
					case Cytosine:
						counts[currentOffset][1] += fraction;
						break;
					case Guanine:
						counts[currentOffset][2] += fraction;
						break;
					case Thymine:
						counts[currentOffset][3] += fraction;						
						break;
					case Gap:
						counts[currentOffset][4] += fraction;
						break;
					default:
						throw new IllegalStateException("not ACGT- : " + base);
				}
			}
			currentOffset++;
		}

	}
	
	
	private Nucleotide getMostFrequentNonGapBase(double a, double c, double g, double t){
		List<BaseCount> list = new ArrayList<BaseCount>(4);
		list.add(new BaseCount(Nucleotide.Adenine, a));
		list.add( new BaseCount(Nucleotide.Cytosine, c));
		list.add( new BaseCount(Nucleotide.Guanine, g));
		list.add( new BaseCount(Nucleotide.Thymine, t));
		Collections.sort(list);
		
		List<Nucleotide> mostFrequent = new ArrayList<>(4);
		Iterator<BaseCount> iter = list.iterator();
		BaseCount first = iter.next();
		mostFrequent.add(first.getBase());
		
		double value = first.getCount();
		boolean done = false;
		do{
			BaseCount next = iter.next();
			done = next.getCount() < value;
			if(!done){
				mostFrequent.add(next.getBase());
			}
		}while(!done && iter.hasNext());
		
		if(mostFrequent.size()>1){
			return tieBreakerRule.getMostFrequent(mostFrequent);
		}
		
		return mostFrequent.get(0);
	}

}
