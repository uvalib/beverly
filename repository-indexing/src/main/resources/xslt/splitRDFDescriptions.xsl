<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="yes"/>
    <!-- we temporarily drop the rdf namespace to deal with this:
    https://issues.apache.org/jira/browse/CAMEL-4913
    until SMX upgrades Camel to 2.9 -->
    <xsl:template match="rdf:RDF">
        <RDF>
            <xsl:for-each select="rdf:Description">
                <xsl:for-each select="*[@rdf:resource]">
                    <Description about="{../@rdf:about}">
                        <isIndexedBy resource="{@rdf:resource}"/>
                    </Description>
                </xsl:for-each>
            </xsl:for-each>
        </RDF>
    </xsl:template>
</xsl:stylesheet>