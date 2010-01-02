<?xml version="1.0"?>
<!-- Uncommons Antlib customisation layer for DocBook chunked XHTML 1.1 output. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0">
  <xsl:import href="../docbook-xsl/xhtml-1_1/chunk.xsl"/>
  <xsl:import href="../docbook-xsl/xhtml-1_1/highlight.xsl"/>

  <!-- Don't generate empty anchors that render strangely in Safari and Firefox. --> 
  <xsl:param name="generate.id.attributes" select="1" />

  <!-- Over-ride template to use an empty span instead of an empty anchor to avoid
       browser rendering bugs. -->
  <xsl:template match="d:indexterm">
    <xsl:variable name="id">
      <xsl:call-template name="object.id"/>
    </xsl:variable>
    <span id="{$id}" class="indexterm"/>
  </xsl:template>
  
</xsl:stylesheet>