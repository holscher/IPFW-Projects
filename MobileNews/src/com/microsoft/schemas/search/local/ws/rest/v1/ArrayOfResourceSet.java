//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-146 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.03 at 11:43:25 AM EST 
//


package com.microsoft.schemas.search.local.ws.rest.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfResourceSet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfResourceSet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResourceSet" type="{http://schemas.microsoft.com/search/local/ws/rest/v1}ResourceSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfResourceSet", propOrder = {
    "resourceSet"
})
public class ArrayOfResourceSet {

    @XmlElement(name = "ResourceSet", nillable = true)
    protected List<ResourceSet> resourceSet;

    /**
     * Gets the value of the resourceSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceSet }
     * 
     * 
     */
    public List<ResourceSet> getResourceSet() {
        if (resourceSet == null) {
            resourceSet = new ArrayList<ResourceSet>();
        }
        return this.resourceSet;
    }

}
