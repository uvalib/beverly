<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:fedora-types="http://www.fedora.info/definitions/1/0/types/"
    xmlns:fedora-api="http://www.fedora.info/definitions/1/0/api/" version="2.0"
    xmlns:soap="http://www.w3.org/2003/05/soap-envelope">

    <xsl:output method="xml" indent="yes"/>
    <!-- get rid of SOAP envelope when api-a is factored into a new service unit -->
    <xsl:template match="/atom:entry">
        <soap:Envelope> 
            <soap:Header> </soap:Header>
            <soap:Body>
                <!-- open a Soap operation element -->
                <xsl:element namespace="http://www.fedora.info/definitions/1/0/types/"
                    name="{atom:title}">
                    <!-- an atom:category with @label element was a parameter in the original
                API-M call: we must translate them back into parameters in ours
            -->
                    <xsl:for-each select="atom:category[@label]">
                        <xsl:element namespace="http://www.fedora.info/definitions/1/0/types/"
                            name="{substring-after(@scheme,':')}">
                            <xsl:value-of select="@term"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </soap:Body>
        </soap:Envelope>
    </xsl:template>

</xsl:stylesheet>
