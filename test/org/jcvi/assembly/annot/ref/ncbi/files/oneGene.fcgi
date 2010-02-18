<html><head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <meta name="robots" content="index, nofollow, noarchive"/>
  <title>NCBI Sequence Viewer v2.0</title>
<!--MUTABLE-->
<!--www.ncbi.nlm.nih.gov:80-->
<!--MUTABLE-->
  <link type="text/css" rel="stylesheet" href="http://www.ncbi.nlm.nih.gov/corehtml/ncbi_test.css"/>
  <link type="text/css" rel="stylesheet" href="../sviewer/viewer.css"/>
  <script type="text/javascript" src="../sviewer/viewer.js"> </script>
  <script type="text/javascript" src="http://www.ncbi.nlm.nih.gov/coreweb/javascript/popupmenu2/popupmenu2_6loader.js"> </script>
</head><body ><form name="frmQueryBox0" action="/sites/entrez" method="get" style="margin:0">
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td align="left" width="130">
              <a href="http://www.ncbi.nlm.nih.gov">
                <img src="http://www.ncbi.nlm.nih.gov/corehtml/left.GIF" width="130" height="45" border="0" alt="NCBI"/>
              </a>
            </td>
            <td align="left">
              <img src="/entrez/query/static/gifs/entrez_nuc.gif" alt="Nucleotide banner"/>
            </td>
            <td align="right">
              <div>
<script language="JavaScript" type="text/javascript"><!--

var myncbi_x = MyNCBI_find();
var myncbi_cu = unescape('http%3A//www.ncbi.nlm.nih.gov/entrez/viewer.fcgi%3F%26amp%3Bcmd%3DDisplay%26amp%3Bdb%3Dnuccore%26amp%3Bextrafeat%3D1016%26amp%3Blinkbar%3Djsmenu3%26amp%3Blist%5Fuids%3D58531092');
function MyNCBI_find()
{
var cs = document.cookie.split('; ');
for(var i = 0; i < cs.length; i++) {
// a name/value pair (a crumb) is separated by an equal sign
var c = cs[i].split('=');
if(c[0] == 'WebCubbyUser') { return c[1]; }
}
return '';
}
function MyNCBI_auto_submit(url)
{
var qm = url.indexOf('?');
if( qm < 0 || qm >= (url.length - 1) ) {
window.location.replace(url);
} else {
var w = (self != top && self.name) ? top.frames[self.name] : self;
var f = '<form name="myncbiautosubmitform" method="POST" action="' + url.substr(0, qm) + '" target="' + w.name + '">';
var q = url.substr(qm + 1).replace(/&amp;/g,'&');
var args = q.split('&');
for(j = 0; j < args.length; j++) {
var p = args[j].split('=');
if(p[0].length < 1) { continue; }
f += '<input type="hidden" name="' + unescape(p[0]);
if( p.length > 1 ) {
p.shift();
f += '" value="' + unescape(p.join('=')).replace(/"/g,'&quot;');
}
f += '"/>';
}
f += '</form>';
w.document.body.innerHTML += f;
w.document.myncbiautosubmitform.submit();
}
}
function MyNCBI_r()
{
var x = MyNCBI_find();
if( x != myncbi_x ) {
myncbi_x = x;
MyNCBI_auto_submit(myncbi_cu);
} else {
window.setTimeout(MyNCBI_r, 500);
}
}
window.setTimeout(MyNCBI_r, 500);
// --></script><table class="medium1" style="border:2px solid #336699;" cellpadding="2" cellspacing="0" id="myncbi_off"><tr><td bgcolor="#336699" align="left"><a href="http://www.ncbi.nlm.nih.gov/sites/myncbi/?"><font color="#FFFFFF"><b>My NCBI</b></font></a></td><td bgcolor="#336699" align="right"><a href="/books/bv.fcgi?rid=helpmyncbi.chapter.MyNCBI" title="My NCBI help"><img border="0" src="http://www.ncbi.nlm.nih.gov/corehtml/query/MyNCBI/myncbihelpicon.gif" alt="My NCBI help"/></a></td></tr><tr><td colspan="2" nowrap="nowrap"><a href="http://www.ncbi.nlm.nih.gov/sites/myncbi/?back_url=http%3A//www.ncbi.nlm.nih.gov/entrez/viewer.fcgi%3F%26amp%3Bcmd%3DDisplay%26amp%3Bdb%3Dnuccore%26amp%3Bextrafeat%3D1016%26amp%3Blinkbar%3Djsmenu3%26amp%3Blist%5Fuids%3D58531092" title="Click to sign in" onclick="MyNCBI_auto_submit('http://www.ncbi.nlm.nih.gov/sites/myncbi/?back_url=http%3A//www.ncbi.nlm.nih.gov/entrez/viewer.fcgi%3F%26amp%3Bcmd%3DDisplay%26amp%3Bdb%3Dnuccore%26amp%3Bextrafeat%3D1016%26amp%3Blinkbar%3Djsmenu3%26amp%3Blist%5Fuids%3D58531092');return false;">[Sign In]</a> <a href="http://www.ncbi.nlm.nih.gov/sites/myncbi/register/?back_url=http%3A//www.ncbi.nlm.nih.gov/entrez/viewer.fcgi%3F%26amp%3Bcmd%3DDisplay%26amp%3Bdb%3Dnuccore%26amp%3Bextrafeat%3D1016%26amp%3Blinkbar%3Djsmenu3%26amp%3Blist%5Fuids%3D58531092" title="Click to register for an account" onclick="MyNCBI_auto_submit('http://www.ncbi.nlm.nih.gov/sites/myncbi/register/?back_url=http%3A//www.ncbi.nlm.nih.gov/entrez/viewer.fcgi%3F%26amp%3Bcmd%3DDisplay%26amp%3Bdb%3Dnuccore%26amp%3Bextrafeat%3D1016%26amp%3Blinkbar%3Djsmenu3%26amp%3Blist%5Fuids%3D58531092');return false;">[Register]</a></td></tr></table></div>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table class="text" border="0" cellspacing="0" cellpadding="2" bgcolor="#000000" width="100%">
          <tr class="text" align="center">
            <td>
              <a href="/sites/entrez?db=PubMed" class="gutter3">
                <font color="#ffffff">PubMed</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Nucleotide" class="gutter3">
                <font color="#ffffff">Nucleotide</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Protein" class="gutter3">
                <font color="#ffffff">Protein</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Genome" class="gutter3">
                <font color="#ffffff">Genome</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Structure" class="gutter3">
                <font color="#ffffff">Structure</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=PMC" class="gutter3">
                <font color="#ffffff">PMC</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Taxonomy" class="gutter3">
                <font color="#ffffff">Taxonomy</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=OMIM" class="gutter3">
                <font color="#ffffff">OMIM</font>
              </a>
            </td>
            <td>
              <a href="/sites/entrez?db=Books" class="gutter3">
                <font color="#ffffff">Books</font>
              </a>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="1" bgcolor="#cccccc">
          <tr>
            <td class="small1" nowrap="1">
                                             Search <small><select name="db" title="Select database for search"><option value="pubmed">PubMed</option><option value="protein">Protein</option><option value="nuccore" selected="1">Nucleotide</option><option value="nucgss">GSS</option><option value="nucest">EST</option><option value="structure">Structure</option><option value="genome">Genome</option><option value="books">Books</option><option value="cancerchromosomes">CancerChromosomes</option><option value="cdd">Conserved Domains</option><option value="gap">dbGaP</option><option value="domains">3D Domains</option><option value="gene">Gene</option><option value="genomeprj">Genome Project</option><option value="gensat">GENSAT</option><option value="geo">GEO Profiles</option><option value="gds">GEO DataSets</option><option value="homologene">HomoloGene</option><option value="journals">Journals</option><option value="mesh">MeSH</option><option value="ncbisearch">NCBI Web Site</option><option value="nlmcatalog">NLM Catalog</option><option value="omia">OMIA</option><option value="omim">OMIM</option><option value="pmc">PMC</option><option value="popset">PopSet</option><option value="probe">Probe</option><option value="proteinclusters">Protein Clusters</option><option value="pcassay">PubChem BioAssay</option><option value="pccompound">PubChem Compound</option><option value="pcsubstance">PubChem Substance</option><option value="snp">SNP</option><option value="taxonomy">Taxonomy</option><option value="toolkit">ToolKit</option><option value="unigene">UniGene</option><option value="unists">UniSTS</option></select></small><input name="cmd" type="hidden" value=""/>
                                             for
                                            <input name="term" size="45" type="text" value=""/> 
                                            <input name="go" type="submit" value="Go" title="Send search request"/> 
                                            <input name="clear" type="button" value="Clear" onclick="this.form.term.value='';this.form.term.focus();" title="Clear search field"/></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <noscript>
    <b>
      <big>
        <font color="#ff0000">You need JavaScript to work with this page.</font>
      </big>
    </b>
  </noscript>
</form><form action="viewer.fcgi" method="get" name="frmViewerBox" style="margin:0">
  <table bgcolor="#c0c0c0" border="0" cellpadding="4" cellspacing="0" width="100%">
    <tr>
      <td nowrap="yes">
        <input name="db" type="hidden" value="nuccore"/>
        <input name="qty" type="hidden" value="1"/>
        <input name="c_start" type="hidden" value="1"/>
        <input id="list_uids" name="list_uids" type="hidden" value="58531092"/>
        <input id="uids" name="uids" type="hidden" value=""/>
        <small> Display 
                                <select name="dopt" title="Choose format of representation" onchange="submit();"><option value="asn">ASN.1</option><option value="gb" selected="1">GenBank</option><option value="gbwithparts">GenBank(Full)</option><option value="fasta">FASTA</option><option value="xml">XML</option><option value="tinyseq">TinySeq XML</option><option value="gbc">INSDSeq XML</option><option value="graph">Graphics</option><option value="gi">GI List</option><option value="brief">Brief</option><option value="docsum">Summary</option></select>
                                 Show
                                <select name="dispmax" onchange="submit()"><option value="1">1</option><option value="2">2</option><option value="5" selected="1">5</option><option value="10">10</option><option value="20">20</option><option value="50">50</option><option value="100">100</option><option value="200">200</option><option value="500">500</option></select><select name="sendto" onchange="do_sendto(this); submit();"><option value="" selected="1">Send to</option><option value="t">Text</option><option value="on">File</option><option value="Add To Clipboard">Clipboard</option></select>
                                     
                                    <span class="group">
                                         Hide: 
                                        <input type="hidden" id="fmt_mask" name="fmt_mask" value="0"/><label title="Hide sequence and translations"><input type="checkbox" id="truncate" name="truncate" value="294912"/>
                                              sequence
                                        </label>
                                         
                                        <label title="Hide features such as variations, CDD regions, CDS products, repeat_regions etc"><input type="checkbox" id="less_feat" name="less_feat" value="504"/>
                                              all but gene, CDS and mRNA features
                                        </label>
                                         
                                    </span></small>
      </td>
    </tr>
  </table>
  <div style="background-color:#c0c0c0" width="100%">
    <table bgcolor="#c0c0c0" border="0" cellpadding="4" cellspacing="0">
      <tr>
        <td nowrap="1">
          <small>
                          <span class="group" title="Show only region of the sequence (from-to)">Range: from 
                        <input type="text" name="from" size="9" maxlength="12" onfocus="if (this.value == 'begin') this.value='';" onblur="if (2 &gt; this.value) this.value='begin';" value="begin"/>
                         to 
                        <input type="text" name="to" size="9" maxlength="12" onfocus="if (this.value == 'end') this.value='';" onblur="if (this.value == '') this.value='end';" value="end"/></span><label for="strand"> 
                            <span class="group" title="Choose strand to project data to"><input type="checkbox" id="strand" name="strand"/>
                                    Reverse complemented strand
                            </span></label></small>
        </td>
        <td nowrap="1">
          <small>
            <input type="hidden" name="extrafeatpresent" value="1"/>
            <span class="group" title="Mark external annotations to include in report">Features:
                    
                      <input type="button" value="+" title="Show full list of annotations" onclick="operate_passive_features(this)"/><span id="passive_features" style="display:none"> 
                        <label id="ef_SNP"><input type="checkbox" id="ef_SNP" name="ef_SNP" value="1"/><span class="defeat" title="No annotation of Variations">SNP</span></label> 
                        <label id="ef_CDD"><input type="checkbox" id="ef_CDD" name="ef_CDD" value="8"/><span class="defeat" title="No annotation of Conserved Domains">CDD</span></label> 
                        <label id="ef_MGC"><input type="checkbox" id="ef_MGC" name="ef_MGC" value="16" checked="1"/><span class="defeat" title="No annotation of Differences vs. the Reference Genome">MGC</span></label> 
                        <label id="ef_HPRD"><input type="checkbox" id="ef_HPRD" name="ef_HPRD" value="32" checked="1"/><span class="defeat" title="No annotation of Post-Translational Modifications from HPRD">HPRD</span></label> 
                        <label id="ef_STS"><input type="checkbox" id="ef_STS" name="ef_STS" value="64" checked="1"/><span class="defeat" title="No annotation of Sequence Tagged Site">STS</span></label> 
                        <label id="ef_tRNA"><input type="checkbox" id="ef_tRNA" name="ef_tRNA" value="128" checked="1"/><span class="defeat" title="No annotation of tRNAs (predicted)">tRNA</span></label> 
                        <label id="ef_microRNA"><input type="checkbox" id="ef_microRNA" name="ef_microRNA" value="256" checked="1"/><span class="defeat" title="No annotation of microRNA">microRNA</span></label> 
                        <label id="ef_Exon"><input type="checkbox" id="ef_Exon" name="ef_Exon" value="512" checked="1"/><span class="defeat" title="No annotation of Exon, or the location on a transcript that is derived from an exon">Exon</span></label></span></span>
          </small>
        </td>
        <td>
          <input type="submit" value="Refresh" id="refresh"/>
        </td>
      </tr>
    </table>
  </div>
  <script type="text/javascript">
                var fmt_mod  = new FormatModifer();
            </script>
</form>
<div class='records'><!-- docsumok 58531092 58531092 1 0 1350 -->
<div class='docsum' id='gi_58531092'>
<table width='100%' border='0' cellpadding='0' cellspacing='0' >
<tr>
<td><input name="uid" type="checkbox" value="58531092"><b>1: </b>&nbsp;<a href='/entrez/viewer.fcgi?db=nuccore&val=58531092'>AB166864</a>.&nbsp;<a class="dblinks" href="javascript:PopUpMenu2_Set(Reports58531092)" onMouseOut="PopUpMenu2_Hide()">Reports<script language="JavaScript1.2">
<!--
var PopUpMenu2_LocalConfig_Reports58531092 = [
  ["TitleText","Reports"]
]
var Reports58531092 = [
  ["UseLocalConfig","Reports58531092","",""],
  ["ASN.1","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=asn'","",""],
  ["XML","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=xml'","",""],
  ["Summary","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=docsum'","",""],
  ["Brief","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=brief'","",""],
  ["FASTA","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=fasta'","",""],
  ["TinySeq XML","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=fasta_xml'","",""],
  ["GenBank","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=gb'","",""],
  ["INSDSeq XML","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=gbc_xml'","",""],
  ["GenBank(Full)","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=gbwithparts'","",""],
  ["GI List","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=gi'","",""],
  ["Graphic","window.top.location='/entrez/viewer.fcgi?list_uids=58531092&db=nuccore&dopt=graph'","",""],
  ["Revision History","window.top.location='/entrez/sutils/girevhist.cgi?val=AB166864'","",""]
]
//-->
</script>
</a>&nbsp;Influenza A virus...[gi:58531092] 
<font color='red'></font></td>
<td align='right' width='15%'><SPAN><script language="JavaScript1.2">
<!--
var PopUpMenu2_LocalConfig_jsmenu3Config = [
  ["ShowCloseIcon","yes"],
  ["Help","window.open('/entrez/query/static/popup.html','Links_Help','resizable=no,scrollbars=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no,alwaysRaised=no,depend=no,width=400,height=500');"],
  ["FrameTarget","_top"],
  ["TitleText"," Links "]
]
var jsmenu3Config = [
  ["UseLocalConfig","jsmenu3Config","",""]
]
//-->
</script>
<script language="JavaScript1.2">
<!--
var Menu58531092 = [
  ["UseLocalConfig","jsmenu3Config","",""],
  ["Protein","/entrez/query.fcgi?itool=nuccore_brief&db=nuccore&cmd=Display&dopt=nuccore_protein&from_uid=58531092","",""],
  ["PubMed","/entrez/query.fcgi?itool=nuccore_brief&db=nuccore&cmd=Display&dopt=nuccore_pubmed&from_uid=58531092","",""],
  ["Taxonomy","/entrez/query.fcgi?itool=nuccore_brief&db=nuccore&cmd=Display&dopt=nuccore_taxonomy&from_uid=58531092","",""],
  ["Related Sequences","/entrez/query.fcgi?itool=nuccore_brief&db=nuccore&cmd=Display&dopt=nuccore_nuccore&from_uid=58531092","",""]
]
//-->
</script>
<a class="dblinks" href="javascript:PopUpMenu2_Set(Menu58531092);" onmouseout="PopUpMenu2_Hide();" TARGET="_self">Links</a></SPAN></td>
</tr>
</table>
</div>
<div class='recordbody'><div class="sequence"><a name="locus_58531092"></a><div class="localnav"><ul class="locals"><li><a href="#feature_58531092" title="Jump to the feature table of this record">Features</a></li><li><a href="#sequence_58531092" title="Jump to the sequence of this record">Sequence</a></li></ul></div>
<pre class="genbank">LOCUS       AB166864                1350 bp    RNA     linear   VRL 03-FEB-2005
DEFINITION  Influenza A virus (A/chicken/Yamaguchi/7/2004(H5N1)) NA gene for
            neuraminidase, complete cds.
ACCESSION   AB166864
VERSION     AB166864.1  GI:58531092
KEYWORDS    .
SOURCE      Influenza A virus (A/chicken/Yamaguchi/7/2004(H5N1))
  ORGANISM  <a href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=266093">Influenza A virus (A/chicken/Yamaguchi/7/2004(H5N1))</a>
            Viruses; ssRNA negative-strand viruses; Orthomyxoviridae;
            Influenzavirus A.
REFERENCE   1
  AUTHORS   Mase,M., Tsukamoto,K., Imada,T., Imai,K., Tanimura,N., Nakamura,K.,
            Yamamoto,Y., Hitomi,T., Kira,T., Nakai,T., Kiso,M., Horimoto,T.,
            Kawaoka,Y. and Yamaguchi,S.
  TITLE     Characterization of H5N1 influenza A viruses isolated during the
            2003-2004 influenza outbreaks in Japan
  JOURNAL   Virology 332 (1), 167-176 (2005)
   PUBMED   <a href="/sites/entrez?cmd=Retrieve&amp;db=pubmed&amp;list_uids=15661149">15661149</a>
REFERENCE   2  (bases 1 to 1350)
  AUTHORS   Mase,M.
  TITLE     Direct Submission
  JOURNAL   Submitted (09-MAR-2004) Masaji Mase, National Institute of Animal
            Health, Department of Infectious Diseases; Kannondai, Tsukuba,
            Ibaraki 305-0856, Japan (E-mail:masema@affrc.go.jp,
            Tel:81-29-838-7760(ex.7760), Fax:81-29-838-7760)
<a name="comment_58531092"></a><a name="feature_58531092"></a>FEATURES             Location/Qualifiers
     source          1..1350
                     /organism="Influenza A virus
                     (A/chicken/Yamaguchi/7/2004(H5N1))"
                     /mol_type="genomic RNA"
                     /strain="A/chicken/Yamaguchi/7/2004"
                     /serotype="H5N1"
                     /db_xref="taxon:<a href="http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=266093">266093</a>"
                     /segment="6"
                     /country="Japan"
     <a href="/entrez/viewer.fcgi?val=58531092&amp;from=1&amp;to=1350&amp;view=gbwithparts">gene</a>            1..1350
                     /gene="NA"
     <a href="/entrez/viewer.fcgi?val=58531092&amp;from=1&amp;to=1350&amp;view=gbwithparts">CDS</a>             1..1350
                     /gene="NA"
                     /codon_start=1
                     /product="neuraminidase"
                     /protein_id="<a href="/entrez/viewer.fcgi?val=BAD89307.1">BAD89307.1</a>"
                     /db_xref="GI:58531093"
                     /translation="MNPNQKIITIGSICMVIGIVSLMLQVGNMISIWVSHSIQTGNQR
                     QAEPISNTKFLTEKAVTSVTLAGNSSLCPISGWAVHSKDNSIRIGSKGDVFVIREPFI
                     SCSHLECRTFFLTQGALLNDKHSNGTVKDRSPHRTLMSCPVGEAPSPYNSRFESVAWS
                     ASACHDGTSWLTIGISGPDNGAVAVLKYNGIITDTIKSWRNNILRTQESECACVNGSC
                     FTVMTDGPSNGQASYKIFKMEKGKVVKSVELDAPNYHYEECSCYPDAGEITCVCRDNW
                     HGSNRPWVSFNQNLEYQIGYICSGVFGDNPRPNDGTGSCGPVSPNGAYGVKGFSFKYG
                     NGVWIGRTKSTNSRSGFEMIWDPNGWTGTDSSFSVKQDIVAITDWSGYSGSFVQHPEL
                     TGLDCIRPCFWVELIRGRPKESTIWTSGSSISFCGVNSDTVGWSWPDGAELPFTIDK"
ORIGIN      
<a name="sequence_58531092"></a>        1 atgaatccaa atcagaagat aataaccatc ggatcaatct gtatggtaat tggaatagtt
       61 agcttaatgt tacaagttgg gaacatgatc tcaatatggg tcagtcattc aattcagaca
      121 gggaatcaac gccaagctga accaatcagc aatactaaat ttcttactga gaaagctgtg
      181 acttcagtaa cattagcggg caattcatct ctttgcccca ttagcggatg ggctgtacac
      241 agtaaggaca acagtataag gatcggttcc aagggggatg tgtttgttat aagagagccg
      301 ttcatctcat gctcccacct ggaatgcaga actttctttt tgactcaggg agccttgctg
      361 aatgacaagc actccaatgg gactgtcaaa gacagaagcc ctcacagaac attaatgagt
      421 tgtcctgtgg gtgaggctcc ctccccatat aactcaaggt ttgagtctgt tgcttggtca
      481 gcaagtgctt gccatgatgg caccagttgg ttgacaattg gaatttctgg cccagacaat
      541 ggggctgtgg ctgtattgaa atacaatggc ataataacag acactatcaa gagttggagg
      601 aacaacatac tgagaactca agagtctgaa tgtgcatgcg taaatggctc ttgctttact
      661 gtaatgactg atggaccaag taatgggcag gcatcatata agatcttcaa aatggaaaaa
      721 gggaaagtgg ttaaatcagt cgaattggat gctcctaatt atcactatga ggaatgctcc
      781 tgttatcctg atgccggcga aatcacatgt gtgtgcaggg ataattggca tggctcaaat
      841 aggccatggg tatctttcaa tcagaatttg gagtatcaaa taggatatat atgcagtggg
      901 gttttcggag acaatccacg ccccaatgat ggaacaggta gttgtggtcc ggtgtcccct
      961 aacggggcat atggggtaaa agggttttca tttaaatacg gcaatggtgt ttggataggg
     1021 agaaccaaaa gcactaattc caggagcggc tttgaaatga tttgggatcc aaatgggtgg
     1081 actggaacgg acagcagctt ttcggtgaag caagatatcg tagcaataac tgattggtca
     1141 ggatatagcg ggagttttgt ccagcatcca gaactgacag gattagattg cataagacct
     1201 tgtttctggg ttgagttaat cagagggcgg cccaaggaga gcacaatttg gactagtggg
     1261 agcagcatat ccttttgtgg tgtaaatagt gacactgtgg gttggtcttg gccagacggt
     1321 gctgagttgc cattcaccat tgacaagtag
//</pre>
<a name="slash_58531092"></a></div></div>
</div><div align="center" class="medium1">
  <p><a href="http://www.ncbi.nlm.nih.gov/About/disclaimer.html">Disclaimer</a> |
                <a href="mailto:info@ncbi.nlm.nih.gov">Write to the Help Desk</a><br/><a href="http://www.ncbi.nlm.nih.gov">NCBI</a> |
                <a href="http://www.nlm.nih.gov">NLM</a> |
                <a href="http://www.nih.gov">NIH</a></p>
  <p> </p>
</div><span style="float:right;font-size:0.8em; color:#ccc;">Last update:<!--MUTABLE--> Thu, 04 Dec 2008 Rev. 147052<!--MUTABLE--></span></body></html>
