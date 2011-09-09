<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="rdf:RDF">
        <rdf:RDF>
            <xsl:for-each select="rdf:Description">
                <xsl:for-each select="*[@rdf:resource]">
                    <rdf:Description rdf:about="{../@rdf:about}">
                        <xsl:copy-of select="."/>
                    </rdf:Description>
                </xsl:for-each>
            </xsl:for-each>
        </rdf:RDF>
    </xsl:template>
</xsl:stylesheet>