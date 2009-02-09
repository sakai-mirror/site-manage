<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="/">

	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="simple"
				page-height="29.7cm" 
				page-width="21cm"
				margin-top="1cm" 
				margin-bottom="2cm" 
				margin-left="2.5cm" 
				margin-right="2.5cm">
				<fo:region-body margin-top="3cm"/>
				<fo:region-before extent="3cm"/>
				<fo:region-after extent="1.5cm"/>
			</fo:simple-page-master>
		</fo:layout-master-set>

		<fo:page-sequence master-reference="simple">
			<fo:flow flow-name="xsl-region-body">
				<fo:block>
					<xsl:apply-templates select="PARTICIPANTS"/>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>

	</fo:root>
</xsl:template>

<xsl:template match="PARTICIPANTS">
	<xsl:apply-templates select="SITETITLE"/>
	<xsl:for-each select="PARTICIPANT">
		<fo:block>
			<xsl:apply-templates select="NAME"/>
			<xsl:apply-templates select="SECTION"/>
			<xsl:apply-templates select="ID"/>
			<xsl:apply-templates select="CREDIT"/>
			<xsl:apply-templates select="ROLE"/>
			<xsl:apply-templates select="STATUS"/>
		</fo:block>
	</xsl:for-each>
</xsl:template>

<xsl:template match="SITETITLE"> 
	<fo:block font-size="18pt" 
		font-family="sans-serif" 
		line-height="24pt"
		space-after.optimum="15pt"
		background-color="blue"
		color="white"
		text-align="center"
		padding-top="3pt">
		<xsl:value-of select="."/>
	</fo:block> 
</xsl:template>

<xsl:template match="NAME"> 
	<fo:block font-size="18pt" 
		font-family="sans-serif" 
		line-height="24pt"
		space-after.optimum="15pt"
		background-color="blue"
		color="white"
		text-align="center"
		padding-top="3pt">
		<xsl:value-of select="."/>
	</fo:block> 
</xsl:template>

<xsl:template match="SECTION">
	<fo:block font-size="12pt" 
		font-family="sans-serif" 
		line-height="15pt"
		space-after.optimum="3pt"
		text-align="justify">
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="ID">
	<fo:block font-size="12pt" 
		font-family="sans-serif" 
		line-height="15pt"
		space-after.optimum="3pt"
		text-align="justify">
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="CREDIT">
	<fo:block font-size="12pt" 
		font-family="sans-serif" 
		line-height="15pt"
		space-after.optimum="3pt"
		text-align="justify">
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="ROLE">
	<fo:block font-size="12pt" 
		font-family="sans-serif" 
		line-height="15pt"
		space-after.optimum="3pt"
		text-align="justify">
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="STATUS">
	<fo:block font-size="12pt" 
		font-family="sans-serif" 
		line-height="15pt"
		space-after.optimum="3pt"
		text-align="justify">
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

</xsl:stylesheet>