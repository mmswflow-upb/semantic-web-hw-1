<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="skill-level" select="''"/>

    <xsl:template match="/">
        <table border="1" cellpadding="6" cellspacing="0" style="border-collapse:collapse; width:100%; font-family:sans-serif;">
            <thead>
                <tr style="background:#333; color:#fff;">
                    <th>ID</th>
                    <th>Title</th>
                    <th>Cuisine 1</th>
                    <th>Cuisine 2</th>
                    <th>Difficulty</th>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="//recipe"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="recipe">
        <xsl:variable name="rowColor">
            <xsl:choose>
                <xsl:when test="@difficulty = $skill-level">#FFFF99</xsl:when>
                <xsl:otherwise>#90EE90</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <tr style="background-color:{$rowColor};">
            <td><xsl:value-of select="@id"/></td>
            <td><xsl:value-of select="title"/></td>
            <td><xsl:value-of select="cuisineTypes/cuisineType[1]/@type"/></td>
            <td><xsl:value-of select="cuisineTypes/cuisineType[2]/@type"/></td>
            <td><xsl:value-of select="@difficulty"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
