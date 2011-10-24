<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd">

	<xsl:param name="repositoryURL"/>
	<xsl:variable name="repositoryObjectsURL" select="concat($repositoryURL,'/objects/')"/>
	<xsl:template match="@rdf:about|@rdf:resource">
		<!-- adjust RDF URIs to refer to actual repository URLs -->
		<xsl:attribute name="rdf:{local-name(.)}">
			<xsl:choose>
				<xsl:when test="starts-with(.,'info:fedora/')">
					<xsl:value-of select="replace(replace(.,'info:fedora/',$repositoryObjectsURL),'(.*)/([^/]+)','$1/datastreams/$2/content')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	</xsl:template>
	<!-- standard copy template -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
