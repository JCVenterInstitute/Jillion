package org.jcvi.jillion.sam.header;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SamVersion {

	private final int major;
	private final int minor;
	
	public SamVersion(int major, int minor) {
		if(major <0){
			throw new IllegalArgumentException("major version number must be >=0");
		}
		if(minor <0){
			throw new IllegalArgumentException("minor version number must be >=0");
		}
		this.major = major;
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}
	
	public boolean isBefore(SamVersion other){
		if(other.getMajor() > major){
			return true;
		}
		if(other.getMajor() < major){
			return false;
		}
		//if we are here, then we are same major version, check minor
		if(other.minor> minor){
			return true;
		}
		return false;
	}
	
	public boolean isAfter(SamVersion other){
		if(isBefore(other)){
			return false;
		}
		//if we are here, then either we are equal
		//or after
		return !equals(other);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SamVersion)) {
			return false;
		}
		SamVersion other = (SamVersion) obj;
		if (major != other.major) {
			return false;
		}
		if (minor != other.minor) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return String.format("%d.%d", major, minor);
	}
	
	private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)$");
	
	public static SamVersion parseVersion(String versionStr){
		Matcher matcher = VERSION_PATTERN.matcher(versionStr);
		if(matcher.matches()){
			return new SamVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		}
		return null;
	}
	
	
}
