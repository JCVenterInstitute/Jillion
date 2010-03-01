# $Id: Runner.pm 754 2009-10-01 16:24:31Z dkatzel $
#
# File: JavaRunner.pm
# Authors: Jeff Sitz
#
#  Copyright @ 2008, J. Craig Venter Institute (JCVI).  All rights reserved.
#
# A Perl object for executing Java applications
#

=head1 NAME

JavaRunner - A Perl object for executing Java applications

=head1 SYNOPSIS

use Java::Runner;

=head1 DESCRIPTION

A Perl object for executing Java applications

=cut

package Java::Runner;

use strict;

use IO::Dir;
use Cwd qw( realpath );

## configuration management 
our $JAVARUNNER_VERSION = "1.0.0";
our $JAVARUNNER_BUILD = (qw/$Revision: 754 $/ )[1];
our $JAVARUNNER_VERSION_STRING = "v$JAVARUNNER_VERSION Build $JAVARUNNER_BUILD";

# #########################################################################
#
#    Export Variables and Routines
#
# #########################################################################
BEGIN 
{
    use Exporter ();
    use vars qw(@EXPORT @EXPORT_OK @ISA %EXPORT_TAGS);

    @ISA         = qw(Exporter);
    @EXPORT      = qw( );
    %EXPORT_TAGS = (
                    VERSION => [qw($JAVARUNNER_VERSION $JAVARUNNER_BUILD $JAVARUNNER_VERSION_STRING)],
                    CHECKS => [],
                    UTIL => []
                   );
    @EXPORT_OK   = ();
    
    Exporter::export_tags('VERSION');
    Exporter::export_tags('CHECKS');
    Exporter::export_tags('UTIL');
}

use vars @EXPORT;
use vars @EXPORT_OK;


#
#  Constants
#
my $PATH_SEP = '/';
my $DEFAULT_JAVA_HOME = '/usr/local/java';
my $DEFAULT_JAVA_BIN = 'bin';
my $DEFAULT_JAVA_EXE = 'java';

sub firstExisting(@)
{
    for my $dir (@_)
    {
        return $dir if (-e $dir);
    }
    
    return undef;
}

#
#  Paths
#
my %JAVA_HOME = (
                  '5' => '/usr/local/java/1.5.0',
                  '6' => '/usr/local/java/1.6.0',
                  '5-32b' => firstExisting('/usr/local/java/1.5.0-32bit', '/usr/local/java/1.5.0'),
                  '6-32b' => firstExisting('/usr/local/java/1.6.0-32bit', '/usr/local/java/1.6.0'),
                );

sub new
{
    my $pkg = shift;
    my %params = @_;
    my $self = {
                'mainClass' => undef,
                'mainJar' => undef,
                'nativeLibPath' => [],
                'classLoc' => [],
                'params' => [],
                'properties' => {},
                'maxHeap' => undef,
                'initHeap' => undef,
                
                'javaHome' => undef,
                'javaBin' => undef,
                'javaExe' => undef,
                'useGCOverheadLimit' =>1,
               };

    bless $self, $pkg;
    
    $self->setJVMFromEnvironment();
    $self->setClasspathFromEnvironment();
    $self->setNativeLibraryPathFromEnvironment();

    return $self;
}
sub disableGCOverheadLimit($){
	my $self = @_;
	$self->{'useGCOverheadLimit'} = undef;
}
sub enableGCOverheadLimit($){
	my $self = @_;
	$self->{'useGCOverheadLimit'} = 1;
}
sub setJVMFromEnvironment($)
{
    my ($self) = @_;
    
    my $jvmHome = ($ENV{'JAVA_HOME'} or $ENV{'JAVA_ROOT'} or $ENV{'JRE_HOME'} or $ENV{'JDK_HOME'} or $ENV{'SDK_HOME'});
    $self->javaHome($jvmHome) if (defined $jvmHome);
    $self->javaBin($ENV{'JAVA_BINDIR'}) if (defined $ENV{'JAVA_BINDIR'});
    $self->javaExe($ENV{'JAVA_EXE'}) if (defined $ENV{'JAVA_EXE'});
}

sub setClasspathFromEnvironment($)
{
    my ($self) = @_;
    
    my %loc_set = ();
    
    if (defined $ENV{'CLASSPATH'})
    {
        for my $loc (split(/:/, $ENV{'CLASSPATH'}))
        {
            unless (exists $loc_set{$loc})
            {
                $self->addClassLocation($loc);
                $loc_set{$loc} = 1;
            }
        }      
    }
}

sub setNativeLibraryPathFromEnvironment($)
{
    my ($self) = @_;
    
    my %path_set = ();
    
    if (defined $ENV{'LD_LIBRARY_PATH'})
    {
        for my $path (split(/:/, $ENV{'LD_LIBRARY_PATH'}))
        {
            unless (exists $path_set{$path})
            {
                $self->addNativeLibraryPath($path);
                $path_set{$path} = 1;
            }
        }      
    }
}

sub addNativeLibraryPath($$)
{
    my ($self, $path) = @_;
    
    push(@{$self->{'nativeLibPath'}}, $path);
}

sub clearNativeLibraryPath($)
{
    my ($self) = @_;
    
    $self->{'nativeLibPath'} = [];
}

sub useJavaPreset($$)
{
    my ($self, $preset) = @_;
    
    my $home = $JAVA_HOME{$preset};
    
    $self->javaHome($home) if (defined $home);
}

sub javaHome($$)
{
    my ($self, $home) = @_;
    
    if (defined $home)
    {
        $self->{'javaHome'} = $home;
        $self->{'javaBin'} = undef;
        $self->{'javaExe'} = undef;
    }
    
    return $self->{'javaHome'} if (defined $self->{'javaHome'});
    return $DEFAULT_JAVA_HOME;
}

sub javaBin($$)
{
    my ($self, $bin) = @_;
    
    if (defined $bin)
    {
        $self->{'javaBin'} = $bin;
        $self->{'javaExe'} = undef;
    }
    
    return $self->{'javaBin'} if (defined $self->{'javaBin'});
    return $self->javaHome() . $PATH_SEP . $DEFAULT_JAVA_BIN;
}

sub javaExe($$)
{
    my ($self, $exe) = @_;
    
    $self->{'javaExe'} = $exe if defined $exe;
    
    return $self->{'javaExe'} if (defined $self->{'javaExe'});
    return $self->javaBin() . $PATH_SEP . $DEFAULT_JAVA_EXE;
}

sub mainClass($$)
{
    my ($self, $newMain) = @_;
    
    $self->{'mainClass'} = $newMain if defined $newMain;
    
    return $self->{'mainClass'};
}

sub mainJar($$)
{
    my ($self, $newMain) = @_;
    
    $self->{'mainJar'} = $newMain if defined $newMain;
    
    return $self->{'mainJar'};
}

sub initialHeapSize($$)
{
    my ($self, $size) = @_;
    
    $self->{'initHeap'} = $size if defined $size;
    
    return $self->{'initHeap'};
}

sub maxHeapSize($$)
{
    my ($self, $size) = @_;
    
    $self->{'maxHeap'} = $size if defined $size;
    
    return $self->{'maxHeap'};
}

sub addParameters($@)
{
    my ($self, @params) = @_;
    
    push(@{$self->{'params'}}, @params);
}

sub parameters($)
{
    my ($self) = @_;
    
    return @{$self->{'params'}};
}

sub addClassLocation($$)
{
    my ($self, $path) = @_;
    
    push(@{$self->{'classLoc'}}, realpath($path));
}

sub addJarDirectory($$)
{
    my ($self, $jardir) = @_;
    
    my %jars = ();
    
    my $dir = new IO::Dir;
    if ($dir->open($jardir))
    {
        ENTRY: while( my $filename = $dir->read())
        {
            my $path = $jardir . '/' . $filename;
            
            next ENTRY if (-d $path);
            
            $self->addClassLocation($path);
        }
    }
    else
    {
        print STDERR "Failed to open JAR directory: $jardir\n";
    }
    
    foreach my $jar (sort { lc($a) cmp lc($b) } keys %jars)
    {
        print("+ Adding $jar\n");
        $self->addClassLocation($jar);
    }
}

sub clearClassPath($)
{
    my ($self) = @_;
    
    $self->{'classLoc'} = [];
}

sub getClassPath()
{
    my ($self) = @_;
    
    return join(':', @{$self->{'classLoc'}});
}

sub setProperty($$$)
{
    my ($self, $property, $value) = @_;
    
    $self->{'properties'}{$property} = $value;
}

sub properties($)
{
    my ($self) = @_;
    
    return %{$self->{'properties'}};
}

sub getCommandArgumentList($)
{
    my ($self) = @_;

    my @args = ();
    
    # Add the JVM executable
    push(@args, $self->javaExe());
    
    # Add JVM options
    push(@args, sprintf("-Xms%dm", $self->{'initHeap'})) if (defined $self->{'initHeap'});
    push(@args, sprintf("-Xmx%dm", $self->{'maxHeap'})) if (defined $self->{'maxHeap'});
    #turn off GC Overhead Limit?
    push(@args, "-XX:-UseGCOverheadLimit") if($self->{'useGCOverheadLimit'});
    # Add the classpath
    if (scalar(@{$self->{'classLoc'}}) > 0)
    {
        push(@args, "-classpath");
        push(@args, $self->getClassPath());
    }
    
    # Add properties
    for my $prop (keys %{$self->{'properties'}})
    {
        my $value = $self->{'properties'}{$prop};
        push(@args, ("-D" . $prop . '=' . $value));
    }
    
    # Add the execution target (main jar or main class)
    if (defined $self->{'mainJar'})
    {
        push(@args, "-jar");
        push(@args, $self->{'mainJar'});
    }
    elsif (defined $self->{'mainClass'})
    {
        push(@args, $self->{'mainClass'});
    }
    else
    {
        return undef;
    }
    
    # Add the execution parameters
    push(@args, $self->parameters());
    
    return @args;
}

sub execute($)
{
    my ($self) = @_;
    
    # Set the LD_LIBRARY_PATH
    if (scalar(@{$self->{'nativeLibPath'}}) > 0)
    {
        my $ld_lib_path = join(':', @{$self->{'nativeLibPath'}});
        $ENV{'LD_LIBRARY_PATH'} = $ld_lib_path;
    }
    else
    {
        delete $ENV{'LD_LIBRARY_PATH'};
    }
    
    my @args = $self->getCommandArgumentList();
    exec @args;
}

sub getCommandLine($)
{
    my ($self) = @_;

    my $cmd = "";
    for my $arg ($self->getCommandArgumentList())
    {
        $cmd .= ' ' if (length $cmd > 0);
        $cmd .= $arg;
    }
    
    return $cmd;
}