/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * Version.java
 *
 * Created: Apr 22, 2009 - 10:46:13 AM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.app;

/**
 * A Version is a simple object capable of tracking a software version.
 * <p>
 * Currently, this supports a limited grammar of version naming. The scheme is
 * intended to represent the most common naming conventions for open source
 * Java applications.  As an example, take the version
 * <strong>1.3.5b2</strong>:
 * <table>
 *   <tbody>
 *     <tr><th>Part</th><th>Description</th></tr>
 *     <tr><td>1</td><td>The codebase version (ie: the major version)</td></tr>
 *     <tr><td>3</td><td>The API version (ie: the minor version)</td></tr>
 *     <tr><td>5</td><td>The patch version (ie: the revision)</td></tr>
 *     <tr><td>b</td><td>The release type; a beta release, in this case</td></tr>
 *     <tr><td>2</td><td>The release number</td></tr>
 *   </tbody>
 * </table>
 * <p>
 * Following these standards, every version starting with "1." would be from the
 * same codebase and therefore expected to share the same base capabilities.
 * Every version starting with "1.3." would use the same (public) API and it
 * would be expected that there are no API incompatibilities between something
 * expecting 1.3.1 and encountering 1.3.9.  From there, version naming starts to
 * become more policy based as each developer can have different ideas about what
 * constitutes a "patch version" or just a new release number as well as the
 * differences between "alpha", "beta" and "release candidate" states.
 *
 * @author jsitz@jcvi.org
 * @author jeff@darkware.org
 */
public class Version implements Comparable<Version>
{
    /**
     * A Release Type is a descriptive label for the expected quality of a
     * software version.  This label is set by policy, not by any quantitative
     * analysis, so some grey areas exist.
     *
     * @author jeff@darkware.org
     */
    public enum ReleaseType
    {
        /** An internal release not intended for the public */
        INTERNAL("x"),
        /** A feature-incomplete version, often offered as a preview. */
        ALPHA("a"),
        /** A feature-complete but icompletely tested version. */
        BETA("b"),
        /** A version which is deemed ready to release in the absence of outstanding bugs. */
        RELEASE_CANDIDATE("rc"),
        /** A full release declared as ready for public use. */
        STABLE("");

        /** The text tag placed into the version string. */
        private final String tag;

        /**
         * Creates a new <code>ReleaseType</code>.
         *
         * @param tag The {@link String} tag placed into the version string.
         */
        private ReleaseType(String tag)
        {
            this.tag = tag;
        }

        /**
         * Retrieves the tag used to declare the type in a full version string.
         *
         * @return The tag for this release type.
         */
        public final String getVersionTag()
        {
            return this.tag;
        }
    }

    /** The size, in bits, of a single version field in the version serial number. */
    private static final int SERIAL_FIELD_SIZE = 8;

    /**
     *  An convenience instance of the Stable release type
     * @see ReleaseType#STABLE
     */
    public static final ReleaseType STABLE = ReleaseType.STABLE;
    /**
     *  An convenience instance of the Release Candidate release type
     * @see ReleaseType#RELEASE_CANDIDATE
     */
    public static final ReleaseType RELEASE_CANDIDATE = ReleaseType.RELEASE_CANDIDATE;
    /**
     *  An convenience instance of the Beta release type
     * @see ReleaseType#BETA
     */
    public static final ReleaseType BETA = ReleaseType.BETA;
    /**
     *  An convenience instance of the Alpha release type
     * @see ReleaseType#ALPHA
     */
    public static final ReleaseType ALPHA = ReleaseType.ALPHA;
    /**
     *  An convenience instance of the Internal release type
     * @see ReleaseType#INTERNAL
     */
    public static final ReleaseType INTERNAL = ReleaseType.INTERNAL;

    /** The version number of the codebase. */
    private final byte codebaseVersion;
    /** The version number of the API (with respect to compatibility). */
    private final byte apiVersion;
    /** The version of the patch release. */
    private final byte patchVersion;
    /** The declared release quality. */
    private final ReleaseType type;
    /** The index of this release within the declared version. */
    private final byte releaseNumber;

    /**
     * Creates a new <code>Version</code>.
     *
     * @param codebaseVersion The version number of the codebase.
     * @param apiVersion The version number of the API (with respect to
     * compatibility).
     * @param patchVersion The version of the patch release.
     * @param type The declared release quality.
     * @param releaseNumber The index of this release within the declared version.
     */
    public Version(int codebaseVersion, int apiVersion, int patchVersion, ReleaseType type, int releaseNumber)
    {
        super();
        this.codebaseVersion = (byte)codebaseVersion;
        this.apiVersion = (byte)apiVersion;
        this.patchVersion = (byte)patchVersion;
        this.type = type;
        this.releaseNumber = (byte)releaseNumber;
    }

    /**
     * Creates a new <code>Version</code>.  It is assumed that the release
     * is the first for this version.
     *
     * @param codebaseVersion The version number of the codebase.
     * @param apiVersion The version number of the API (with respect to
     * compatibility).
     * @param patchVersion The version of the patch release.
     * @param type The declared release quality.
     */
    public Version(int codebaseVersion, int apiVersion, int patchVersion, ReleaseType type)
    {
        this(codebaseVersion, apiVersion, patchVersion, type, 1);
    }

    /**
     * Creates a new <code>Version</code>.  It is assumed that this version
     * is of {@link ReleaseType#STABLE STABLE} quality and it is the first
     * revision for the version.
     *
     * @param codebaseVersion The version number of the codebase.
     * @param apiVersion The version number of the API (with respect to
     * compatibility).
     * @param patchVersion The version of the patch release.
     */
    public Version(int codebaseVersion, int apiVersion, int patchVersion)
    {
        this(codebaseVersion, apiVersion, patchVersion, ReleaseType.STABLE);
    }

    /**
     * Creates a new <code>Version</code>.  It is assumed that this version
     * is the first {@link ReleaseType#STABLE STABLE} version of this API
     * revision.
     *
     * @param codebaseVersion The version number of the codebase.
     * @param apiVersion The version number of the API (with respect to
     * compatibility).
     */
    public Version(int codebaseVersion, int apiVersion)
    {
        this(codebaseVersion, apiVersion, 0);
    }

    /**
     * Calculates a unique serial number for this version.  The serial number
     * is packed such that two serial numbers may be compared numerically.
     * When compared in this manner, the more recent version will have the
     * greater value.
     * <p>
     * While the numerical number is easily displayed and compared, the
     * value may not be identifiable.
     *
     * @return A long integer containing a numerical representation of this
     * version.
     */
    public long getSerialNumber()
    {
        long serial = 0;
        serial += this.codebaseVersion;
        serial <<= Version.SERIAL_FIELD_SIZE;
        serial += this.apiVersion;
        serial <<= Version.SERIAL_FIELD_SIZE;
        serial += this.patchVersion;
        serial <<= Version.SERIAL_FIELD_SIZE;
        serial += this.type.ordinal();
        serial <<= Version.SERIAL_FIELD_SIZE;
        serial += this.releaseNumber;

        return serial;
    }

    /**
     * Checks to see if the given version is at least as recent as this
     * version.
     *
     * @param requirement The version to compare.
     * @return <code>true</code> if this version is greater than or
     * equal to the given requirement version, <code>false</code> if it
     * is not.
     */
    public boolean isAtLeast(Version requirement)
    {
        return this.getSerialNumber() >= requirement.getSerialNumber();
    }

    /**
     * Checks to see if this version matches the API number of the supplied
     * version.  An API is declared by the first two number of the version
     * (specifically, the code base and API version)
     *
     * @param requirement The version to compare against.
     * @return <code>true</code> if the API versions are the same,
     * <code>false</code> if they are not.
     */
    public boolean matchesAPI(Version requirement)
    {
        return (this.codebaseVersion == requirement.codebaseVersion &&
                this.apiVersion == requirement.apiVersion);
    }

    @Override
    public int compareTo(Version o)
    {
        // -1 == this < o
        // 1 == this > o

        final long baseSerial = this.getSerialNumber();
        final long compSerial = o.getSerialNumber();

        if (baseSerial < compSerial) return -1;
        else if (baseSerial == compSerial) return 0;
        else return 1;
    }

    @Override
    public String toString()
    {
        final StringBuilder name = new StringBuilder();

        name.append(this.codebaseVersion)
            .append('.')
            .append(this.apiVersion)
            .append('.')
            .append(this.patchVersion);

        if (!this.type.equals(ReleaseType.STABLE))
        {
            name.append(this.type.getVersionTag())
                .append(this.releaseNumber);
        }

        return name.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.apiVersion;
        result = prime * result + this.codebaseVersion;
        result = prime * result + this.patchVersion;
        result = prime * result + this.releaseNumber;
        result = prime * result + this.type.ordinal();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        final Version other = (Version)obj;
        return (other.getSerialNumber() == this.getSerialNumber());
    }
}
