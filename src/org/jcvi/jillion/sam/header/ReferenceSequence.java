/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.header;
/**
 * {@code ReferenceSequence} is an object
 * representation of the metadata for a 
 * reference used in a SAM or BAM file.
 * 
 * @author dkatzel
 *
 */
public class ReferenceSequence {

	private final String name;
	private final int length;
	
	private final String genomeAssemblyId;
	private final String species;
	private final String uri;
	private final String md5;
	
	
	private ReferenceSequence(Builder builder){
		this.name = builder.name;
		this.length = builder.length;
		
		this.genomeAssemblyId = builder.genomeAssemblyId;
		this.species = builder.species;
		this.uri = builder.uri;
		this.md5 = builder.md5;
	}
	
	
	
	
	@Override
	public String toString() {
		return "ReferenceSequence [name=" + name + ", length=" + length
				+ ", genomeAssemblyId=" + genomeAssemblyId + ", species="
				+ species + ", uri=" + uri + ", md5=" + md5 + "]";
	}



	/**
	 * Get the human readable name of this reference sequence.
	 * @return a String; will never be null.
	 */
	public String getName() {
		return name;
	}



	/**
	 * Get the number of bases in this reference sequence.
	 * @return the number of bases; will always be > 0.
	 */
	public int getLength() {
		return length;
	}



	
	public String getGenomeAssemblyId() {
		return genomeAssemblyId;
	}




	public String getSpecies() {
		return species;
	}




	public String getUri() {
		return uri;
	}




	public String getMd5() {
		return md5;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + length;
		result = prime
				* result
				+ ((genomeAssemblyId == null) ? 0 : genomeAssemblyId.hashCode());
		
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReferenceSequence)) {
			return false;
		}
		ReferenceSequence other = (ReferenceSequence) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		if (length != other.length) {
			return false;
		}
		if (genomeAssemblyId == null) {
			if (other.genomeAssemblyId != null) {
				return false;
			}
		} else if (!genomeAssemblyId.equals(other.genomeAssemblyId)) {
			return false;
		}
		
		if (md5 == null) {
			if (other.md5 != null) {
				return false;
			}
		} else if (!md5.equals(other.md5)) {
			return false;
		}
		
		if (species == null) {
			if (other.species != null) {
				return false;
			}
		} else if (!species.equals(other.species)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}




	public static final class Builder{
		private String name;
		private int length;
		
		private String genomeAssemblyId;
		private String species;
		private String uri;
		private String md5;
		
		/**
		 * Create a new Builder instance
		 * which is initialized to have 
		 * it's name and length set to the specified
		 * values and all other values set to {@code null}. 
		 * @param name the name of this ReferenceSequence;
		 * can not be null.
		 * @param length the length of this ReferenceSequence;
		 * can not be <1. 
		 * @throws NullPointerException if name is null.
		 * @throws IllegalArgumentException if length <1.
		 */
		public Builder(String name, int length){
			if(name ==null){
				throw new NullPointerException("name can not be null");
			}
			if(length<1){
				throw new IllegalArgumentException("length must >=1");
			}
			this.name = name;
			this.length = length;
		}

		/**
		 * Create a new Builder instance
		 * which is initialized to have 
		 * all the same values as the given 
		 * ReferenceSequence.
		 * @param copy the ReferenceSequence to copy 
		 * the values of; can not be null.
		 * @throws NullPointerException if copy is null.
		 */
		public Builder(ReferenceSequence copy) {
			name = copy.getName();
			length = copy.getLength();
			
			genomeAssemblyId = copy.getGenomeAssemblyId();
			species = copy.getSpecies();
			uri=copy.getUri();
			md5= copy.getMd5();
		}

		/**
		 * Change this reference's name.
		 * @param name the name to change it to;
		 * can not be null.
		 * @return this.
		 * @throws NullPointerException if name is null.
		 */
		public Builder setName(String name) {
			if(name ==null){
				throw new NullPointerException("name can not be null");
			}
			this.name = name;
			return this;
		}
		/**
		 * Change the length of this reference.
		 * @param length the length of this ReferenceSequence;
		 * can not be <1. 
		 * @throws IllegalArgumentException if length <1.
		 * @return this.
		 */
		public Builder setLength(int length) {
			if(length<1){
				throw new IllegalArgumentException("length must >=1");
			}
			this.length = length;
			return this;
		}

		public String getGenomeAssemblyId() {
			return genomeAssemblyId;
		}


		public Builder setGenomeAssemblyId(String genomeAssemblyId) {
			this.genomeAssemblyId = genomeAssemblyId;
			return this;
		}


		public String getSpecies() {
			return species;
		}


		public Builder setSpecies(String species) {
			this.species = species;
			return this;
		}


		public String getUri() {
			return uri;
		}


		public Builder setUri(String uri) {
			this.uri = uri;
			return this;
		}


		public String getMd5() {
			return md5;
		}


		public Builder setMd5(String md5) {
			this.md5 = md5;
			return this;
		}


		public String getName() {
			return name;
		}


		public int getLength() {
			return length;
		}
		/**
		 * Create a new {@link ReferenceSequence}
		 * instance using the current values in this Builder.
		 * @return a  new {@link ReferenceSequence},
		 * will never be null.
		 */
		public ReferenceSequence build(){
			return new ReferenceSequence(this);
		}
		
	}
}
