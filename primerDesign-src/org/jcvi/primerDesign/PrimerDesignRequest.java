package org.jcvi.primerDesign;

import org.jcvi.common.core.seq.nuc.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.datastore.DataStore;

import org.jcvi.primerDesign.domain.PrimerDesignTarget;

import java.util.Collection;
import java.io.InputStream;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 3:12:12 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignRequest {
    private String projectCode;
    private String architecture;

    private Collection<PrimerDesignTarget> targets;
    private NucleotideSequenceFastaRecord templateFastaRecord;
    private InputStream primerConfigurationStub;
    private DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords;

    public PrimerDesignRequest() {
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Collection<PrimerDesignTarget> getTargets() {
        return targets;
    }

    public void setTargets(Collection<PrimerDesignTarget> targets) {
        this.targets = targets;
    }

    public NucleotideSequenceFastaRecord getTemplateFastaRecord() {
        return templateFastaRecord;
    }

    public void setTemplateFastaRecord(NucleotideSequenceFastaRecord templateFastaRecord) {
        this.templateFastaRecord = templateFastaRecord;
    }

    public InputStream getPrimerConfigurationStub() {
        return primerConfigurationStub;
    }

    public void setPrimerConfigurationStub(InputStream primerConfigurationStub) {
        this.primerConfigurationStub = primerConfigurationStub;
    }

    public DataStore<NucleotideSequenceFastaRecord> getReferenceFastaRecords() {
        return referenceFastaRecords;
    }

    public void setReferenceFastaRecords(DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords) {
        this.referenceFastaRecords = referenceFastaRecords;
    }

    public static class Builder {

        private PrimerDesignRequest request;

        public Builder() {
            request = new PrimerDesignRequest();
        }

        public Builder setProjectCode(String projectCode) {
            checkBuilderState();
            request.setProjectCode(projectCode);
            return this;
        }

        public Builder setArchitecture(String architecture) {
            checkBuilderState();
            request.setArchitecture(architecture);
            return this;
        }

        public Builder setTargets(Collection<PrimerDesignTarget> targets) {
            checkBuilderState();
            request.setTargets(targets);
            return this;
        }

        public Builder setTemplateFastaRecord(NucleotideSequenceFastaRecord templateFastaRecord) {
            checkBuilderState();
            request.setTemplateFastaRecord(templateFastaRecord);
            return this;
        }

        public Builder setPrimerConfigurationStub(InputStream primerConfigurationStub) {
            checkBuilderState();
            request.setPrimerConfigurationStub(primerConfigurationStub);
            return this;
        }

        public Builder setReferenceFastaRecords(DataStore<NucleotideSequenceFastaRecord> referenceFastaRecords) {
            checkBuilderState();
            request.setReferenceFastaRecords(referenceFastaRecords);
            return this;
        }

        public PrimerDesignRequest build() {
            checkBuilderState();
            PrimerDesignRequest request = this.request;
            this.request = null;
            return request;
        }

        private void checkBuilderState() {
            if ( request == null ) {
                throw new IllegalStateException(
                    "Builder instance has already been used to construct a PrimerDesignRequest object"
                );
            }
        }
    }
}
