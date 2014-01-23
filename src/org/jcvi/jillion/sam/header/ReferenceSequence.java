package org.jcvi.jillion.sam.header;

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
	
	
	
	
	public String getName() {
		return name;
	}




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
		private final String name;
		private final int length;
		
		private String genomeAssemblyId;
		private String species;
		private String uri;
		private String md5;
		
		
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
		
		public ReferenceSequence build(){
			return new ReferenceSequence(this);
		}
		
	}
}
