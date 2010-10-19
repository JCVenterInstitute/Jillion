package org.jcvi.primerDesign.gridJob;

import java.io.File;

/**
* Created by IntelliJ IDEA.
* User: aresnick
* Date: Jul 28, 2010
* Time: 5:10:36 PM
* To change this template use File | Settings | File Templates.
*/
public class PrimerDesignerGridJob {
    private String projectCode;
    private String architecture;
    private File gridJobScript;
    private File stdOutputLocation;
    private File stdErrorLocation;

    public PrimerDesignerGridJob(String projectCode,
                                 String architecture,
                                 File gridJobScript,
                                 File stdOutputLocation,
                                 File stdErrorLocation) {
        this.projectCode = projectCode;
        this.architecture = architecture;
        this.gridJobScript = gridJobScript;
        this.stdOutputLocation = stdOutputLocation;
        this.stdErrorLocation = stdErrorLocation;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public String getArchitecture() {
        return architecture;
    }

    public File getGridJobScript() {
        return gridJobScript;
    }

    public File getStdOutputLocation() {
        return stdOutputLocation;
    }

    public File getStdErrorLocation() {
        return stdErrorLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimerDesignerGridJob that = (PrimerDesignerGridJob) o;

        if (architecture != null ? !architecture.equals(that.architecture) : that.architecture != null) return false;
        if (!gridJobScript.equals(that.gridJobScript)) return false;
        if (projectCode != null ? !projectCode.equals(that.projectCode) : that.projectCode != null) return false;
        if (!stdErrorLocation.equals(that.stdErrorLocation)) return false;
        if (!stdOutputLocation.equals(that.stdOutputLocation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectCode != null ? projectCode.hashCode() : 0;
        result = 31 * result + (architecture != null ? architecture.hashCode() : 0);
        result = 31 * result + gridJobScript.hashCode();
        result = 31 * result + stdOutputLocation.hashCode();
        result = 31 * result + stdErrorLocation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PrimerDesignerGridJob");
        sb.append("{projectCode=").append(projectCode);
        sb.append(", architecture=").append(architecture);
        sb.append(", gridJobScript=").append(gridJobScript);
        sb.append(", stdOutputLocation=").append(stdOutputLocation);
        sb.append(", stdErrorLocation=").append(stdErrorLocation);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private String projectCode;
        private String architecture;
        private File gridJobScript;
        private File stdOutputLocation;
        private File stdErrorLocation;

        public Builder() {
        }

        public void setProjectCode(String projectCode) {
            this.projectCode = projectCode;
        }

        public void setArchitecture(String architecture) {
            this.architecture = architecture;
        }

        public void setGridJobScript(File gridJobScript) {
            this.gridJobScript = gridJobScript;
        }

        public void setStdOutputLocation(File stdOutputLocation) {
            this.stdOutputLocation = stdOutputLocation;
        }

        public void setStdErrorLocation(File stdErrorLocation) {
            this.stdErrorLocation = stdErrorLocation;
        }

        public PrimerDesignerGridJob build() {
            // validateData();
            return new PrimerDesignerGridJob(projectCode,
                                             architecture,
                                             gridJobScript,
                                             stdOutputLocation,
                                             stdErrorLocation);
        }

        private void validateData() {
            if ( gridJobScript == null ||
                 !gridJobScript.isFile() ||
                 !gridJobScript.canExecute() ) {
                throw new IllegalStateException(
                    "gridJobScript " + ( (gridJobScript == null) ? "null " : gridJobScript.getAbsolutePath() )
                        + " is not an executable file"
                );
            }

            if ( stdOutputLocation == null ||
                 !stdOutputLocation.isDirectory() ||
                 !stdOutputLocation.canWrite() ) {
                throw new IllegalStateException(
                    "stdOutputLocation " + ( (stdOutputLocation == null) ? "null " : stdOutputLocation.getAbsolutePath() )
                        + " is not a writable directory"
                );
            }

            if ( stdErrorLocation == null ||
                 !stdErrorLocation.isDirectory() ||
                 !stdErrorLocation.canWrite() ) {
                throw new IllegalStateException(
                    "stdErrorLocation " + ( (stdErrorLocation == null) ? "null " : stdErrorLocation.getAbsolutePath() )
                        + " is not a writable directory"
                );
            }
        }
    }
}
