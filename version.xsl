<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:pom="http://maven.apache.org/POM/4.0.0"
    >
  <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
  <xsl:template match="/pom:project">
    <xsl:value-of select="./pom:version/text()" />
  </xsl:template>
  
</xsl:stylesheet>
