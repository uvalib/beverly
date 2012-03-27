<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Feb 14, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> ajs6f</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">

        <add>
            <doc>
                <xsl:for-each-group select="//field" group-by="@name">
                    <xsl:for-each-group select="current-group()" group-by=".">
                        <xsl:copy-of select="."/>
                    </xsl:for-each-group>
                </xsl:for-each-group>
            </doc>
        </add>
    </xsl:template>

</xsl:stylesheet>
