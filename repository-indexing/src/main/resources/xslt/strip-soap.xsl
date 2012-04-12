<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope"
    exclude-result-prefixes="xs soapenv"
    version="2.0">
    
    <xsl:template match="/">
        <xsl:copy-of select="/soapenv:Envelope/soapenv:Body/*"/>
    </xsl:template>
    
</xsl:stylesheet>