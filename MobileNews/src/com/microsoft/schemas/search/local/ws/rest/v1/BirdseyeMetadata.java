//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-146 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.03 at 11:43:25 AM EST 
//


package com.microsoft.schemas.search.local.ws.rest.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BirdseyeMetadata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BirdseyeMetadata">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.microsoft.com/search/local/ws/rest/v1}ImageryMetadata">
 *       &lt;sequence>
 *         &lt;element name="Orientation" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="TilesX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TilesY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BirdseyeMetadata", propOrder = {
    "orientation",
    "tilesX",
    "tilesY"
})
public class BirdseyeMetadata
    extends ImageryMetadata
{

    @XmlElement(name = "Orientation")
    protected double orientation;
    @XmlElement(name = "TilesX")
    protected int tilesX;
    @XmlElement(name = "TilesY")
    protected int tilesY;

    /**
     * Gets the value of the orientation property.
     * 
     */
    public double getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     */
    public void setOrientation(double value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the tilesX property.
     * 
     */
    public int getTilesX() {
        return tilesX;
    }

    /**
     * Sets the value of the tilesX property.
     * 
     */
    public void setTilesX(int value) {
        this.tilesX = value;
    }

    /**
     * Gets the value of the tilesY property.
     * 
     */
    public int getTilesY() {
        return tilesY;
    }

    /**
     * Sets the value of the tilesY property.
     * 
     */
    public void setTilesY(int value) {
        this.tilesY = value;
    }

}
