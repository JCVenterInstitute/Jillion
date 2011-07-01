package org.jcvi.primerDesign.gridJob;

import java.io.File;

/**
* Created by IntelliJ IDEA.
* User: aresnick
* Date: Jul 28, 2010
* Time: 5:10:36 PM
* To change this template use File | Settings | File Templates.
*/
public class PrimerDesignerArrayGridJob {
    private String projectCode;
    private String architecture;

    private File configFile;
    private File gffFile;
    private File renamedPdfFile;
    private File primerFastaFile;

    public PrimerDesignerArrayGridJob(String projectCode,
                                      String architecture,
                                      File configFile,
                                      File gffFile,
                                      File renamedPdfFile,
                                      File primerFastaFile) {
        this.projectCode = projectCode;
        this.architecture = architecture;
        this.configFile = configFile;
        this.gffFile = gffFile;
        this.renamedPdfFile = renamedPdfFile;
        this.primerFastaFile = primerFastaFile;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public String getArchitecture() {
        return architecture;
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getGffFile() {
        return gffFile;
    }

    public File getRenamedPdfFile() {
        return renamedPdfFile;
    }

    public File getPrimerFastaFile() {
        return primerFastaFile;
    }

    public static class Builder {
        private String projectCode;
        private String architecture;

        private File configFile;
        private File gffFile;
        private File renamedPdfFile;
        private File primerFastaFile;

        public Builder() {
        }

        public void setProjectCode(String projectCode) {
            this.projectCode = projectCode;
        }

        public void setArchitecture(String architecture) {
            this.architecture = architecture;
        }

        public void setConfigFile(File configFile) {
            this.configFile = configFile;
        }

        public void setGffFile(File gffFile) {
            this.gffFile = gffFile;
        }

        public void setRenamedPdfFile(File renamedPdfFile) {
            this.renamedPdfFile = renamedPdfFile;
        }

        public void setPrimerFastaFile(File primerFastaFile) {
            this.primerFastaFile = primerFastaFile;
        }

        public PrimerDesignerArrayGridJob build() {
            return new PrimerDesignerArrayGridJob(projectCode,
                                                  architecture,
                                                  configFile,
                                                  gffFile,
                                                  renamedPdfFile,
                                                  primerFastaFile);
        }

    }
}