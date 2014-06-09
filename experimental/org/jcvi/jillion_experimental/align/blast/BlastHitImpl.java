package org.jcvi.jillion_experimental.align.blast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class BlastHitImpl implements BlastHit{

	private final String queryId, subjectId, subjectDeflineComment, programName, dbName;;

	private final Integer queryLength, subjectLength;
	//initialize to empty list
	private final List<Hsp> hsps;
	
	public BlastHitImpl(Builder builder) {
		this.queryId = builder.queryId;
		this.subjectId = builder.subjectId;
		this.queryLength = builder.queryLength;
		this.subjectLength = builder.subjectLength;
		this.subjectDeflineComment = builder.subjectDeflineComment;
		this.programName = builder.programName;
		this.dbName = builder.dbName;
		
		//defensive copy
		ArrayList<Hsp> sortedList = new ArrayList<Hsp>(builder.hsps);
		Collections.sort(sortedList, HspComparator.INSTANCE);
		this.hsps = Collections.unmodifiableList(sortedList);
	}

	@Override
	public String getBlastDbName() {
		return dbName;
	}

	@Override
	public String getBlastProgramName() {
		return programName;
	}

	@Override
	public String getQueryId() {
		return queryId;
	}

	@Override
	public String getSubjectId() {
		return subjectId;
	}

	@Override
	public String getSubjectDefinition() {
		return subjectDeflineComment;
	}

	@Override
	public Integer getQueryLength() {
		return queryLength;
	}

	@Override
	public Integer getSubjectLength() {
		return subjectLength;
	}

	@Override
	public List<Hsp> getHsps() {
		return hsps;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
		result = prime * result + ((hsps == null) ? 0 : hsps.hashCode());
		result = prime * result
				+ ((programName == null) ? 0 : programName.hashCode());
		result = prime * result + ((queryId == null) ? 0 : queryId.hashCode());
		result = prime * result
				+ ((queryLength == null) ? 0 : queryLength.hashCode());
		result = prime
				* result
				+ ((subjectDeflineComment == null) ? 0 : subjectDeflineComment
						.hashCode());
		result = prime * result
				+ ((subjectId == null) ? 0 : subjectId.hashCode());
		result = prime * result
				+ ((subjectLength == null) ? 0 : subjectLength.hashCode());
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
		if (!(obj instanceof BlastHitImpl)) {
			return false;
		}
		BlastHitImpl other = (BlastHitImpl) obj;
		if (dbName == null) {
			if (other.dbName != null) {
				return false;
			}
		} else if (!dbName.equals(other.dbName)) {
			return false;
		}
		if (hsps == null) {
			if (other.hsps != null) {
				return false;
			}
		} else if (!hsps.equals(other.hsps)) {
			return false;
		}
		if (programName == null) {
			if (other.programName != null) {
				return false;
			}
		} else if (!programName.equals(other.programName)) {
			return false;
		}
		if (queryId == null) {
			if (other.queryId != null) {
				return false;
			}
		} else if (!queryId.equals(other.queryId)) {
			return false;
		}
		if (queryLength == null) {
			if (other.queryLength != null) {
				return false;
			}
		} else if (!queryLength.equals(other.queryLength)) {
			return false;
		}
		if (subjectDeflineComment == null) {
			if (other.subjectDeflineComment != null) {
				return false;
			}
		} else if (!subjectDeflineComment.equals(other.subjectDeflineComment)) {
			return false;
		}
		if (subjectId == null) {
			if (other.subjectId != null) {
				return false;
			}
		} else if (!subjectId.equals(other.subjectId)) {
			return false;
		}
		if (subjectLength == null) {
			if (other.subjectLength != null) {
				return false;
			}
		} else if (!subjectLength.equals(other.subjectLength)) {
			return false;
		}
		return true;
	}



	private static enum HspComparator implements Comparator<Hsp> {
		INSTANCE;
		
		@Override
		public int compare(Hsp o1, Hsp o2) {
			//the lower the evalue the better
			int eCmp =o1.getEvalue().compareTo(o2.getEvalue());
			if(eCmp !=0){
				return eCmp;
			}
			//the higher the value the better
			
			//NOTE the order of the comparison is switched
			//so the higher values get a lower comparison			
			
			int bitScoreCmp= o2.getBitScore().compareTo(o1.getBitScore());
			if(bitScoreCmp !=0){
				return bitScoreCmp;
			}
			return o2.getHspScore().compareTo(o1.getHspScore());
		}
	}

	public static class Builder{
		private final String queryId, subjectId;

		private String subjectDeflineComment;
		
		private Integer queryLength, subjectLength;
		
		private String programName, dbName;
		//initialize to empty list
		private List<Hsp> hsps = new ArrayList<Hsp>();
		
		public Builder(String queryId, String subjectId) {
			this.queryId = queryId;
			this.subjectId = subjectId;
		}
		
		public Builder setQueryLength(int queryLength){
			if(queryLength <0){
				throw new IllegalArgumentException("query length can not be negative");
			}
			this.queryLength = queryLength;
			return this;
		}
		
		public Builder setSubjectDeflineComment(String subjectDeflineComment){
			this.subjectDeflineComment = subjectDeflineComment;
			return this;
		}
		
		public Builder setBlastProgramName(String programName){
			this.programName = programName;
			return this;
		}
		
		public Builder setBlastDbName(String dbName){
			this.dbName = dbName;
			return this;
		}
		
		
		
		
		public Builder setSubjectLength(int subjectLength){
			if(subjectLength <0){
				throw new IllegalArgumentException("subject length can not be negative");
			}
			this.subjectLength = subjectLength;
			return this;
		}
		
		public Builder addHsp(Hsp hsp){
			if(hsp ==null){
				throw new NullPointerException("hsp can not be null");
			}
			hsps.add(hsp);
			return this;
		}
		
		
		
		
		public BlastHit build(){
			return new BlastHitImpl(this);
		}
		
	}
}
