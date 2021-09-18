//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.10.14 at 12:46:59 AM ADT 
//


package me.osm.osmdoc.model.v2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://map.osm.me/osm-doc-part}titleDescription"/>
 *         &lt;element name="extends" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://map.osm.me/osm-doc-part}more-tags" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group-tags" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "title",
    "description",
    "_extends",
    "moreTags"
})
@XmlRootElement(name = "trait")
public class Trait {

    @XmlElement(required = true)
    protected List<LangString> title;
    protected List<LangString> description;
    @XmlElement(name = "extends")
    protected List<String> _extends;
    @XmlElement(name = "more-tags")
    protected MoreTags moreTags;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "group-tags")
    protected Boolean groupTags;

    /**
     * Gets the value of the title property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the title property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LangString }
     * 
     * 
     */
    public List<LangString> getTitle() {
        if (title == null) {
            title = new ArrayList<LangString>();
        }
        return this.title;
    }

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LangString }
     * 
     * 
     */
    public List<LangString> getDescription() {
        if (description == null) {
            description = new ArrayList<LangString>();
        }
        return this.description;
    }

    /**
     * Gets the value of the extends property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extends property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtends().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getExtends() {
        if (_extends == null) {
            _extends = new ArrayList<String>();
        }
        return this._extends;
    }

    /**
     * Gets the value of the moreTags property.
     * 
     * @return
     *     possible object is
     *     {@link MoreTags }
     *     
     */
    public MoreTags getMoreTags() {
        return moreTags;
    }

    /**
     * Sets the value of the moreTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link MoreTags }
     *     
     */
    public void setMoreTags(MoreTags value) {
        this.moreTags = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the groupTags property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isGroupTags() {
        if (groupTags == null) {
            return false;
        } else {
            return groupTags;
        }
    }

    /**
     * Sets the value of the groupTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGroupTags(Boolean value) {
        this.groupTags = value;
    }

}