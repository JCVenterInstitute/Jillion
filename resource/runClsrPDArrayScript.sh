#!/bin/bash

read CONFIG_FILE
read RESULTS_GFF_FILE
read RESULTS_RENAMED_PDF
read PRIMER_FASTA_FILE

echo "Running Primer Design now..."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/PrimerDesigner.pl -p $CONFIG_FILE

echo "Converting GFF to PS/PDF..."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/VisualizationTools/GFF_to_PS.pl $RESULTS_GFF_FILE

echo "Renaming PDF file to include template name..."
mv $RESULTS_GFF_FILE.pdf $RESULTS_RENAMED_PDF

echo "Performing Primer Critiquor as a sanity check."
/local/ifs_devel/BCIS/src/autofinish/trunk/primer_design/PrimerDesigner/PrimerCritiquor.pl -f $PRIMER_FASTA_FILE -p $CONFIG_FILE

