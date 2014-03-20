package org.jcvi.jillion.sam.header;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * {@code SamVersion}
 * is an object representation of
 * how which version of the SAM format
 * the SAM file is encoded in.
 * @author dkatzel
 *
 */
public final class SamVersion {

	private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)$");
	
	private final int major;
	private final int minor;
	/**
	 * Create a new {@link SamVersion}
	 * instance with the given version info.
	 * @param major the major version; must be >=0.
	 * @param minor the minor version; must be >=0.
	 * @throws IllegalArgumentException if any parameter is < 0.
	 */
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
	/**
	 * Get the major part of this version.
	 * @return an int.
	 */
	public int getMajor() {
		return major;
	}
	/**
	 * Get the minor part of this version.
	 * @return an int.
	 */
	public int getMinor() {
		return minor;
	}
	/**
	 * Is this {@link SamVersion} before
	 * the other given version.  A version is before
	 * if either:
	 * <ol>
	 * <li>This major field is less than the other major field</li>
	 * <li>The major versions are equal but this minor field
	 * is less than the other minor field.</li>
	 * </ol>
	 * @param other the other version to compare to;
	 * can not be null.
	 * @return {@code true} if this version is before
	 * (meets the criteria stated above);
	 * {@code false} otherwise.
	 * @throws NullPointerException if other is null.
	 */
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
	/**
	 * Is this {@link SamVersion} after
	 * the other given version.  A version is after
	 * if either:
	 * <ol>
	 * <li>This major field is greater than the other major field</li>
	 * <li>The major versions are equal but this minor field
	 * is greater than the other minor field.</li>
	 * </ol>
	 * @param other the other version to compare to;
	 * can not be null.
	 * @return {@code true} if this version is after
	 * (meets the criteria stated above);
	 * {@code false} otherwise.
	 * @throws NullPointerException if other is null.
	 */
	public boolean isAfter(SamVersion other){
		if(other.getMajor() < major){
			return true;
		}
		if(other.getMajor() > major){
			return false;
		}
		//if we are here, then we are same major version, check minor
		if(other.minor < minor){
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + major;
		result = prime * result + minor;
		return result;
	}
	/**
	 * Two {@link SamVersion}s
	 * are equal if they have the same
	 * major and minor field values.
	 * {@inheritDoc}
	 */
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
	
	/**
	 * Parse a SAM version string (which is encoded in a SAM file header)
	 * into a {@link SamVersion} object.
	 * @param versionStr the sam version string to parse;
	 * can not be null.
	 * @return a new {@link SamVersion} instance if the versionStr
	 * is a valid SAM version pattern or {@code null} if it is not.
	 * @throws NullPointerException if versionStr is null.
	 */
	public static SamVersion parseVersion(String versionStr){
		if(versionStr ==null){
			throw new NullPointerException("version string can not be null");
		}
		Matcher matcher = VERSION_PATTERN.matcher(versionStr);
		if(matcher.matches()){
			return new SamVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		}
		return null;
	}
	
	
}
