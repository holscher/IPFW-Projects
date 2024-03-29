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
 * <p>Java class for CoverageArea complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageArea">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ZoomMin" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ZoomMax" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="BoundingBox" type="{http://schemas.microsoft.com/search/local/ws/rest/v1}BoundingBox" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageArea", propOrder = {
    "zoomMin",
    "zoomMax",
    "boundingBox"
})
public class CoverageArea {

    @XmlElement(name = "ZoomMin")
    protected int zoomMin;
    @XmlElement(name = "ZoomMax")
    protected int zoomMax;
    @XmlElement(name = "BoundingBox")
    protected BoundingBox boundingBox;

    /**
     * Gets the value of the zoomMin property.
     * 
     */
    public int getZoomMin() {
        return zoomMin;
    }

    /**
     * Sets the value of the zoomMin property.
     * 
     */
    public void setZoomMin(int value) {
        this.zoomMin = value;
    }

    /**
     * Gets the value of the zoomMax property.
     * 
     */
    public int getZoomMax() {
        return zoomMax;
    }

    /**
     * Sets the value of the zoomMax property.
     * 
     */
    public void setZoomMax(int value) {
        this.zoomMax = value;
    }

    /**
     * Gets the value of the boundingBox property.
     * 
     * @return
     *     possible object is
     *     {@link BoundingBox }
     *     
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoundingBox }
     *     
     */
    public void setBoundingBox(BoundingBox value) {
        this.boundingBox = value;
    }

}
