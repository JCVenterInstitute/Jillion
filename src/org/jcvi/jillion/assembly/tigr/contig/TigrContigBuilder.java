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
package org.jcvi.jillion.assembly.tigr.contig;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.AbstractContigBuilder;
import org.jcvi.jillion.internal.assembly.DefaultAssembledRead;
import org.jcvi.jillion.internal.assembly.DefaultContig;

public class TigrContigBuilder extends AbstractContigBuilder<TigrContigRead, TigrContig>{


	public TigrContigBuilder(String id, NucleotideSequence consensus) {
		super(id, consensus);
	}

	@Override
	protected TigrContigReadBuilder createPlacedReadBuilder(
			TigrContigRead read) {
		return createPlacedReadBuilder(read.getId(),
				(int)read.getGappedStartOffset(),
				read.getReadInfo().getValidRange(),
				read.getNucleotideSequence().toString(),
				read.getDirection(),
				read.getReadInfo().getUngappedFullLength()
				);
	}

	@Override
	protected TigrContigReadBuilder createPlacedReadBuilder(
			String id, int offset, Range validRange, String basecalls,
			Direction dir, int fullUngappedLength) {
		return new DefaultTigrContigReadBuilder(getConsensusBuilder().build(),
				id, offset, validRange, basecalls, dir, fullUngappedLength);
	}

	@Override
	public TigrContig build() {
		 if(consensusCaller !=null){
				recallConsensusNow();
	        }
		Set<TigrContigRead> reads = new LinkedHashSet<TigrContigRead>();
        for(AssembledReadBuilder<TigrContigRead> builder : getAllAssembledReadBuilders()){          
            reads.add(builder.build());
        }
        return new DefaultTigrContig(getContigId(),
        		getConsensusBuilder().build(),
                reads);
	}
	

	private static class DefaultTigrContig implements TigrContig{
		private final Contig<TigrContigRead> delegate;

		

		public DefaultTigrContig(String id, NucleotideSequence consensus,
				Set<TigrContigRead> reads) {
			delegate = new DefaultContig<TigrContigRead>(id,consensus,reads);
		}

		@Override
		public String getId() {
			return delegate.getId();
		}

		@Override
		public long getNumberOfReads() {
			return delegate.getNumberOfReads();
		}

		@Override
		public StreamingIterator<TigrContigRead> getReadIterator() {
			return delegate.getReadIterator();
		}

		@Override
		public NucleotideSequence getConsensusSequence() {
			return delegate.getConsensusSequence();
		}

		@Override
		public TigrContigRead getRead(String id) {
			return delegate.getRead(id);
		}

		@Override
		public boolean containsRead(String readId) {
			return delegate.containsRead(readId);
		}
		
	}
	
	private static class DefaultTigrContigRead implements TigrContigRead{
		private final AssembledRead delegate;

		public DefaultTigrContigRead(AssembledRead delegate) {
			this.delegate = delegate;
		}

		@Override
		public long getGappedStartOffset() {
			return delegate.getGappedStartOffset();
		}

		@Override
		public long getGappedEndOffset() {
			return delegate.getGappedEndOffset();
		}

		@Override
		public long getGappedLength() {
			return delegate.getGappedLength();
		}

		@Override
		public Direction getDirection() {
			return delegate.getDirection();
		}

		@Override
		public long toGappedValidRangeOffset(long referenceOffset) {
			return delegate.toGappedValidRangeOffset(referenceOffset);
		}

		@Override
		public long toReferenceOffset(long gappedValidRangeOffset) {
			return delegate.toReferenceOffset(gappedValidRangeOffset);
		}

		@Override
		public Range getGappedContigRange() {
			return delegate.getGappedContigRange();
		}

		@Override
		public Range asRange() {
			return delegate.asRange();
		}

		@Override
		public String getId() {
			return delegate.getId();
		}

		@Override
		public ReferenceMappedNucleotideSequence getNucleotideSequence() {
			return delegate.getNucleotideSequence();
		}

		@Override
		public ReadInfo getReadInfo() {
			return delegate.getReadInfo();
		}
		
	}
	
	private static class DefaultTigrContigReadBuilder implements TigrContigReadBuilder{
		private final AssembledReadBuilder<AssembledRead> builder;
		
		private DefaultTigrContigReadBuilder(AssembledReadBuilder<AssembledRead> copy){
			this.builder = copy.copy();
		}
		public DefaultTigrContigReadBuilder(NucleotideSequence currentConsensus,String id, int offset, Range validRange, String basecalls,
			Direction dir, int fullUngappedLength){
			builder = DefaultAssembledRead.createBuilder(
					currentConsensus, 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    fullUngappedLength);
		}
		@Override
		public TigrContigReadBuilder reference(
				NucleotideSequence reference, int newOffset) {
			builder.reference(reference, newOffset);
			return this;
		}

		@Override
		public long getBegin() {
			return builder.getBegin();
		}

		@Override
		public String getId() {
			return builder.getId();
		}

		@Override
		public TigrContigReadBuilder setStartOffset(int newOffset) {
			builder.setStartOffset(newOffset);
			return this;
		}

		@Override
		public TigrContigReadBuilder shift(int numberOfBases) {
			builder.shift(numberOfBases);
			return this;
		}

		@Override
		public Range getClearRange() {			
			return builder.getClearRange();
		}

		@Override
		public Direction getDirection() {
			return builder.getDirection();
		}

		@Override
		public int getUngappedFullLength() {
			return builder.getUngappedFullLength();
		}

		@Override
		public TigrContigRead build() {
			return new DefaultTigrContigRead(builder.build());
		}

		@Override
		public TigrContigReadBuilder reAbacus(
				Range gappedValidRangeToChange, NucleotideSequence newBasecalls) {
			builder.reAbacus(gappedValidRangeToChange, newBasecalls);
			return this;
		}

		@Override
		public long getLength() {
			return builder.getLength();
		}

		@Override
		public long getEnd() {
			return builder.getEnd();
		}

		@Override
		public Range asRange() {
			return builder.asRange();
		}

		@Override
		public NucleotideSequenceBuilder getNucleotideSequenceBuilder() {
			return builder.getNucleotideSequenceBuilder();
		}

		@Override
		public NucleotideSequence getCurrentNucleotideSequence() {
			return builder.getCurrentNucleotideSequence();
		}

		@Override
		public TigrContigReadBuilder append(Nucleotide base) {
			builder.append(base);
			return this;
		}

		@Override
		public TigrContigReadBuilder append(
				Iterable<Nucleotide> sequence) {
			builder.append(sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder append(String sequence) {
			builder.append(sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder insert(int offset,
				String sequence) {
			builder.insert(offset, sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder replace(int offset,
				Nucleotide replacement) {
			builder.replace(offset, replacement);
			return this;
		}

		@Override
		public TigrContigReadBuilder delete(Range range) {
			builder.delete(range);
			return this;
		}

		@Override
		public int getNumGaps() {
			return builder.getNumGaps();
		}

		@Override
		public int getNumNs() {
			return builder.getNumNs();
		}

		@Override
		public int getNumAmbiguities() {
			return builder.getNumAmbiguities();
		}

		@Override
		public TigrContigReadBuilder prepend(String sequence) {
			builder.prepend(sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder insert(int offset,
				Iterable<Nucleotide> sequence) {
			builder.insert(offset, sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder insert(int offset,
				Nucleotide base) {
			builder.insert(offset, base);
			return this;
		}

		@Override
		public TigrContigReadBuilder prepend(
				Iterable<Nucleotide> sequence) {
			builder.prepend(sequence);
			return this;
		}

		@Override
		public TigrContigReadBuilder trim(Range trimRange) {
			builder.trim(trimRange);
			return this;
		}

		@Override
		public TigrContigReadBuilder copy() {
			return new DefaultTigrContigReadBuilder(builder);
		}
		
	}
}
