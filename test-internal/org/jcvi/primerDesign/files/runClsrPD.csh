#!/bin/csh

echo "Running Primer Design now..."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/PrimerDesigner.pl -p /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/target.config

echo "Converting GFF to PS/PDF..."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/VisualizationTools/GFF_to_PS.pl /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/pipeline_results.gff

echo "Renaming PDF file to include template name..."
mv /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/pipeline_results.gff.pdf /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/1_pipeline_results.pdf

echo "Performing Primer Critiquor as a sanity check."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/PrimerCritiquor.pl -f /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/primers.fasta -p /usr/local/scratch/PrimerDesigner/0731243243566186/0/results/contig00001/1/target.config

