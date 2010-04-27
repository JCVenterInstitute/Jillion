#!/usr/local/bin/perl -w

use strict;

use File::Basename;
my $SCRIPT = basename($0);

# #########################################################################
#
#    Version Strings
#
# #########################################################################
my $VERSION = "0.90";
my $BUILD = (qw/$Revision: Beta $/ )[1];

# #########################################################################
#
#    Load Modules
#
# #########################################################################

#
#  Use Local Modules First
#
use FindBin qw($Bin);
use lib "$Bin";
use lib "$Bin/../perllib";
#
#  Standard USE statments
#
use Getopt::Long;

#
#  Load JavaRunner
#
use Java::Runner;


# #########################################################################
#
#    Global Variables
#
# #########################################################################

# #########################################################################
#
#    Set up the JavaRunner
#
# #########################################################################

my $runner = Java::Runner->new();

$runner->useJavaPreset("6-32b");

$runner->clearClassPath();
$runner->addNativeLibraryPath('/usr/local/n1ge/lib/lx24-x86');
$runner->initialHeapSize(48);

$runner->addClassLocation("$Bin/JavaCommon.jar");
$runner->mainClass("org.jcvi.fasta.ConvertPositions");
#pass all arguments thru as is
foreach my $arg (@ARGV){
	$runner->addParameters($arg);
}


# #########################################################################
#
#    Kick off the JavaRunner
#
# #########################################################################


    $runner->execute();


