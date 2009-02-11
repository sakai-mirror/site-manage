<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<!-- page layout -->
				<!-- layout for the first page -->
				<fo:simple-page-master master-name="roster" page-width="8.5in" page-height="11in" margin-top=".5in" margin-bottom=".5in" margin-left=".5in" margin-right=".5in">
					<fo:region-body margin-top="1.0cm" />
					<fo:region-before precedence="true" extent="1.0cm" />
					<fo:region-after precedence="true" extent="1.0cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<!-- end: defines page layout -->
			<!-- actual layout -->
			<fo:page-sequence master-reference="roster">
				<fo:static-content flow-name="xsl-region-before">
					<fo:block font-size="12pt" font-family="sans-serif" line-height="1cm" space-after.optimum="1pt" color="black" text-align="right" padding-top="0pt">
						<xsl:value-of select="PARTICIPANTS/SITE_TITLE" /> - <fo:page-number />
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-after"> </fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-size="9pt">
					<fo:table table-layout="fixed" width="100%">
						<fo:table-column column-width="2.5in" />
						<fo:table-column column-width="2in" />
						<fo:table-column column-width=".25in" />
						<fo:table-column column-width=".6in" />
						<fo:table-column column-width=".5in" />
						<fo:table-column column-width=".6in" />
						<fo:table-body>
							<fo:table-row line-height="9pt" background-color="#cccccc" font-weight="bold" display-align="center">
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										NAME
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										SECTION
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										ID
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										CREDITS
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										ROLE
									</fo:block>
								</fo:table-cell>
								<fo:table-cell padding="2pt">
									<fo:block text-align="left">
										STATUS
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<xsl:apply-templates />
						</fo:table-body>
					</fo:table>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="PARTICIPANTS" xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<xsl:apply-templates select="//PARTICIPANT">
			<xsl:sort select="./NAME" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="SITE_TITLE" xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<!--do not render this -->
	</xsl:template>
	<xsl:template match="PARTICIPANT" xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<xsl:choose>
			<xsl:when test="position() mod 2 != 1">
				<fo:table-row line-height="11pt" background-color="#eeeeee">
					<!--
						<fo:table-cell>
						<fo:block font-weight="bold" white-space="nowrap">  <xsl:value-of select="position()" />  </fo:block>
						</fo:table-cell>
					-->
					<xsl:apply-templates />
				</fo:table-row>
			</xsl:when>
			<xsl:otherwise>
				<fo:table-row line-height="11pt" background-color="#ffffff">
					<xsl:apply-templates />
				</fo:table-row>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="NAME" xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:table-cell padding="2pt" white-space="nowrap">
			<xsl:choose>
				<xsl:when test="../ROLE='Instructor'">
					<fo:block font-weight="bold" white-space="nowrap">
						<xsl:value-of select="." />
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block white-space="nowrap">
						<xsl:value-of select="." />
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</fo:table-cell>
	</xsl:template>
	<xsl:template match="SECTION | ROLE | STATUS | ID | CREDITS" xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:table-cell padding="2pt">
			<fo:block>
				<xsl:value-of select="." />
			</fo:block>
		</fo:table-cell>
	</xsl:template>
</xsl:stylesheet>
