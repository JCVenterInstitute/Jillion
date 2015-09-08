/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.util.Builder;


abstract class AbstractCoverageMapBuilder<P extends Rangeable> implements Builder<CoverageMap<P>> {

    private static enum MinCoverageSelectorComparator implements Comparator<Rangeable> {
    	
    	INSTANCE;
    	
		@Override
		public int compare(Rangeable o1, Rangeable o2) {
			return Range.Comparators.LONGEST_TO_SHORTEST.compare(
					o1.asRange(), o2.asRange());
		}
	}

	private P enteringObject;
    private P leavingObject;
    
    private Range enteringObjectRange, leavingObjectRange;
    
    private final Queue<P> coveringObjects;
    private Iterator<P> enteringIterator;
    private Iterator<P> leavingIterator;
    private List<CoverageRegionBuilder<P>> coverageRegionBuilders;
    
    private final Integer maxAllowedCoverage, minRequiredCoverage;
    
    
    protected abstract Iterator<P> createEnteringIterator();
    protected abstract Iterator<P> createLeavingIterator();
    
    private final List<P> sortedOverMaxCoverageObjects = new LinkedList<P>();
    
    protected abstract CoverageMap<P> build(List<CoverageRegionBuilder<P>> coverageRegionBuilders);
    
    public AbstractCoverageMapBuilder(){
        coveringObjects =  new ArrayDeque<P>();
        this.maxAllowedCoverage =null;
        this.minRequiredCoverage = null;
    }
    public AbstractCoverageMapBuilder(int maxAllowedCoverage) {
        coveringObjects =  new ArrayBlockingQueue<P>(maxAllowedCoverage);
        this.maxAllowedCoverage = maxAllowedCoverage;
        this.minRequiredCoverage = null;
    }
    public AbstractCoverageMapBuilder(int maxAllowedCoverage, int minRequiredCoverage) {
        coveringObjects =  new ArrayBlockingQueue<P>(maxAllowedCoverage);
        this.maxAllowedCoverage = maxAllowedCoverage;
        this.minRequiredCoverage = minRequiredCoverage;
    }
    @Override
    public CoverageMap<P> build() {
        initialize();
        createListOfRegionBuilders();
        return build(coverageRegionBuilders);
    }
    private void initialize() {
        enteringIterator = createEnteringIterator();
        leavingIterator = createLeavingIterator();

        enteringObject = getNextObject(enteringIterator);
        enteringObjectRange = enteringObject ==null? null :enteringObject.asRange();
        
        leavingObject = getNextObject(leavingIterator);
        leavingObjectRange = leavingObject ==null? null :leavingObject.asRange();
        coverageRegionBuilders = new ArrayList<CoverageRegionBuilder<P>>();
    }

    private void createListOfRegionBuilders() {
        createAllRegionBuilders();
        if (anyRegionBuildersCreated()) {
            removeLastRegionBuilder();            
            removeAnyBuildersWithEmptyRanges();
            addSkippedReadsIfPossible();
            combineConsecutiveRegionsWithSameCoveringObjects();
        }
    }
    
    private void addSkippedReadsIfPossible(){
    	if(minRequiredCoverage == null || sortedOverMaxCoverageObjects.isEmpty()){
    		//nothing to add
    		return;
    	}
		Collections.sort(sortedOverMaxCoverageObjects, MinCoverageSelectorComparator.INSTANCE);
		int minCoverageLevel = minRequiredCoverage.intValue();
		if (sortedOverMaxCoverageObjects.isEmpty()) {
			return;
		}
		for (P e : sortedOverMaxCoverageObjects) {
			Range range = e.asRange();
			List<CoverageRegionBuilder<P>> intersectingRegions = getIntersectionRegionBuilders(range.getBegin(), range.getEnd());
			
			if (readProvidesMinRequiredCoverage(minCoverageLevel, intersectingRegions)) {
				addReadToCoverageRegionBuilders(e, range, intersectingRegions);
			}
		}
        
    }
	private void addReadToCoverageRegionBuilders(P e, Range range, List<CoverageRegionBuilder<P>> intersectingRegions) {
		for (CoverageRegionBuilder<P> builder : intersectingRegions) {
			// might need to split builder into multiple
			// if the read doesn't cover entire range.
			long builderStart = builder.start();
			long builderEnd = builder.end();
			if (range.getBegin() <= builderStart && range.getEnd() >= builderEnd) {
				// full span builder we can just add
				builder.forceAdd(e);
			} else {
				// need to split builder into 2
				Collection<P> oldElements = builder.getElements();
				if (range.getBegin() > builderStart) {
					// add new builder on 3' side
					CoverageRegionBuilder<P> rightBuilder = createNewCoverageRegionBuilder(
							oldElements, builderStart, null).end(
							range.getBegin() - 1);

					CoverageRegionBuilder<P> leftBuilder = createNewCoverageRegionBuilder(
							oldElements, range.getBegin(), null).add(e)
							.end(builderEnd);

					int i = getBuilderOffsetFor(builderStart);

					this.coverageRegionBuilders.remove(i);
					this.coverageRegionBuilders.add(i, leftBuilder);
					this.coverageRegionBuilders.add(i, rightBuilder);
				} else {
					// add new builder to 5' side
					CoverageRegionBuilder<P> leftBuilder = createNewCoverageRegionBuilder(
							oldElements, builderStart, null);
					leftBuilder.offer(e);
					leftBuilder.end(range.getEnd());

					CoverageRegionBuilder<P> rightBuilder = createNewCoverageRegionBuilder(
							oldElements, range.getEnd() + 1, null);
					rightBuilder.end(builderEnd);

					int i = getBuilderOffsetFor(builderStart);

					this.coverageRegionBuilders.remove(i);
					this.coverageRegionBuilders.add(i, leftBuilder);
					this.coverageRegionBuilders.add(i, rightBuilder);
				}

			}
		}
	}
	private boolean readProvidesMinRequiredCoverage(int minCoverageLevel,
			List<CoverageRegionBuilder<P>> intersectingRegions) {
		boolean shouldAdd = false;
		for (CoverageRegionBuilder<P> builder : intersectingRegions) {
			if (builder.getCurrentCoverageDepth() < minCoverageLevel) {
				shouldAdd = true;
				break;
			}
		}
		return shouldAdd;
	}
    /**
     * If we restrict the max coverage
     * then we could have adjacent coverageRegions
     * that actually have the same covering objects
     * but different start and end coordinates.
     * (These would have different coverage depths
     * but we didn't add the missing read since it would
     * put us over the limit)
     */
    private void combineConsecutiveRegionsWithSameCoveringObjects() {
    	//iterate backwards to avoid concurrent modification errors
    	CoverageRegionBuilder<P> previousBuilder=null;
        for (int i = coverageRegionBuilders.size() - 1; i >= 0; i--) {
            CoverageRegionBuilder<P> builder = coverageRegionBuilders.get(i);
            
            if(builder.hasSameElementsAs(previousBuilder)){
            	//merge region
            	builder.end(previousBuilder.end());
            	//remove previous
            	coverageRegionBuilders.remove(i+1);
            }   
            previousBuilder=builder;
        }
		
	}
	private void removeAnyBuildersWithEmptyRanges() {
        //iterate backwards to avoid concurrent modification errors
        for (int i = coverageRegionBuilders.size() - 1; i >= 0; i--) {
            if (coverageRegionBuilders.get(i).rangeIsEmpty()) {
                coverageRegionBuilders.remove(i);
            }
        }
    }

    private boolean anyRegionBuildersCreated() {
        return !coverageRegionBuilders.isEmpty();
    }

    private void removeLastRegionBuilder() {
        // last is invalid, not only should it be empty,
        // but it doesn't have an end set so just chop it off.
        coverageRegionBuilders.remove(coverageRegionBuilders.size() - 1);
    }

    private void createAllRegionBuilders() {
        computeRegionsForAllEnteringObjects();
        computeRemainingRegions();
    }

    private void computeRegionsForAllEnteringObjects() {
        while (stillHaveEnteringObjects()) {
            if (isEntering()) {
                handleEnteringObject();
            } else if (isAbutment()) {
                removeAndAdvanceLeavingObject();
            } else {
                handleLeavingObject();
            }
        }
        
    }

    private int getBuilderOffsetFor(long startCoord){
    	for(int i=0; i<coverageRegionBuilders.size(); i++){
    		if(startCoord ==coverageRegionBuilders.get(i).start()){
    			return i;
    		}
    	}
    	throw new IllegalStateException("no coverage region builder with start coord " + startCoord);
    }
    
    private List<CoverageRegionBuilder<P>> getIntersectionRegionBuilders(long start, long end){

    	List<CoverageRegionBuilder<P>> intersectingRegions = new ArrayList<CoverageRegionBuilder<P>>();
    	for(int i=0; i< coverageRegionBuilders.size(); i++ ){
    		CoverageRegionBuilder<P> builder = coverageRegionBuilders.get(i);
			long regionStart = builder.start();
    		//since we are iterating in order,
    		//we only need to check the start boundary
    		//to see when we start intersecting
			//if the region boundary is beyond our read
    		//then we can stop looking.
    		if(regionStart > end){
    			break;
    		}
    		
    		if(regionStart>=start){
    			intersectingRegions.add(builder);    			
    		}
    		
    	}
    	return intersectingRegions;
    }
    
    private boolean stillHaveEnteringObjects() {
        return enteringObject != null;
    }

    private void computeRemainingRegions() {
        while (stillHaveLeavingObjects()) {
            createNewRegionWithoutCurrentLeavingObject();
            skipAllLeavingObjectsWithSameEndCoordinate();
        }
    }

    private boolean stillHaveLeavingObjects() {
        return leavingObject != null;
    }

    private void skipAllLeavingObjectsWithSameEndCoordinate() {
        long endCoord = leavingObject.asRange().getEnd();
        leavingObject = getNextObject(leavingIterator);
        leavingObjectRange = leavingObject ==null? null :leavingObject.asRange();
        while (stillHaveLeavingObjects()
                && currentLeavingObjectHasEndCoordinate(endCoord)) {
            removeLeavingObjectFromPreviousRegionBuilder();
            removeAndAdvanceLeavingObject();
        }
    }

    private void removeLeavingObjectFromPreviousRegionBuilder() {
        getPreviousRegion().remove(leavingObject);
    }

    private boolean currentLeavingObjectHasEndCoordinate(long endCoord) {
        return leavingObject.asRange().getEnd() == endCoord;
    }

    private void handleEnteringObject() {
        long startCoord = enteringObjectRange.getBegin();
        createNewRegionWithEnteringAmplicon();
        enteringObject = getNextObject(enteringIterator);
        enteringObjectRange = enteringObject ==null? null :enteringObject.asRange();
        
        handleAmpliconsWithSameStartCoord(startCoord);
    }

    
    
    private void handleLeavingObject() {
        createNewRegionWithoutCurrentLeavingObject();
        skipAllLeavingObjectsWithSameEndCoordinate();
        
        
        
    }

    private void removeAndAdvanceLeavingObject() {
        coveringObjects.remove(leavingObject);
        leavingObject = getNextObject(leavingIterator);
        leavingObjectRange = leavingObject ==null? null :leavingObject.asRange();
    }

    private boolean isAbutment() {
        return leavingObjectRange.getEnd() == enteringObjectRange.getBegin() - 1;

    }

    private void handleAmpliconsWithSameStartCoord(long regionStart) {
        while (stillHaveEnteringObjects()
                && enteringObjectRange.getBegin() == regionStart) {
            // next amplicon also starts here, add this to current region
            addEnteringObjectToPreviousRegionBuilder();
            addAndAdvanceEnteringObject();
        }
    }

    private void addEnteringObjectToPreviousRegionBuilder() {
        if(!getPreviousRegion().offer(enteringObject) && minRequiredCoverage !=null){        
            	sortedOverMaxCoverageObjects.add(enteringObject);
        }
        
    }

    private void addAndAdvanceEnteringObject() {
        coveringObjects.offer(enteringObject);
        enteringObject = getNextObject(enteringIterator);
        enteringObjectRange = enteringObject ==null? null :enteringObject.asRange();
    }

    private boolean isEntering() {
        return enteringObjectRange.getBegin() <= leavingObjectRange.getEnd() ;
    }

    private void createNewRegionWithoutCurrentLeavingObject() {
        coveringObjects.remove(leavingObject);
        final long endCoordinate = leavingObjectRange.getEnd();

        setEndCoordinateOfPreviousRegion(endCoordinate);
        coverageRegionBuilders.add(createNewCoverageRegionBuilder(coveringObjects, leavingObjectRange.getEnd() + 1, maxAllowedCoverage ));

    }

    private void setEndCoordinateOfPreviousRegion(final long endCoordinate) {
        getPreviousRegion().end(endCoordinate);
    }

    private void createNewRegionWithEnteringAmplicon() {
        if (!coverageRegionBuilders.isEmpty()) {
            final long endCoordinate = enteringObjectRange.getBegin() - 1;
            setEndCoordinateOfPreviousRegion(endCoordinate);
        }
        boolean added =coveringObjects.offer(enteringObject);
        if(!added && minRequiredCoverage !=null){
        	this.sortedOverMaxCoverageObjects.add(enteringObject);
        }
        coverageRegionBuilders.add(createNewCoverageRegionBuilder(coveringObjects, enteringObjectRange.getBegin(), maxAllowedCoverage ));
        

    }

    protected abstract CoverageRegionBuilder<P> createNewCoverageRegionBuilder(
            Collection<P> elements, long start, Integer maxAllowedCoverage);
    
    private CoverageRegionBuilder<P> getPreviousRegion() {
        return coverageRegionBuilders.get(coverageRegionBuilders.size() - 1);
    }

    private P getNextObject(Iterator<P> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }

    
}
