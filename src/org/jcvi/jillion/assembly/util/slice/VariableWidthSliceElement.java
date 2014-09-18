package org.jcvi.jillion.assembly.util.slice;

import java.util.List;

public interface VariableWidthSliceElement<T> extends Comparable<VariableWidthSliceElement<T>>{

	List<T> get();
	
	int getCount();
	
	int getLength();
	
	@Override
	default int compareTo(VariableWidthSliceElement<T> other)	{
		//highest count sorted first
		int countCmp = Integer.compare(other.getCount(), getCount());
		if(countCmp !=0){
			return countCmp;
		}
		int lengthCmp = Integer.compare(getLength(), other.getLength());
		if(lengthCmp !=0){
			return lengthCmp;
		}
		//same length same count sort by T?
		//convert to String?
		//can't factor out stringBuilding to method
		//because we are in interface and would have to make the method public
		//and pollute API
		StringBuilder myString = new StringBuilder(getLength());
		for(T t : get()){
			myString.append(t.toString());
		}
		
		StringBuilder otherString = new StringBuilder(other.getLength());
		for(T t : other.get()){
			otherString.append(t.toString());
		}
		return myString.toString().compareTo(otherString.toString());
		
	}
	
}
