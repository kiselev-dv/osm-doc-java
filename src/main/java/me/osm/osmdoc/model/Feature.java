//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.07 at 12:54:41 AM YEKT 
//


package me.osm.osmdoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 * 				Основной элемент описывающий определенный тип объектов в OSM.
 * 				
 * 				Main element which describes particular OSM type. 				
 * 			
 * 
 * <p>Java class for feature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="feature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://map.osm.me/osm-doc-part}titleDescription"/>
 *         &lt;element name="keyword" type="{http://map.osm.me/osm-doc-part}langString" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://map.osm.me/osm-doc-part}tags" maxOccurs="unbounded"/>
 *         &lt;element name="trait" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="group-tags" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://map.osm.me/osm-doc-part}more-tags" minOccurs="0"/>
 *         &lt;group ref="{http://map.osm.me/osm-doc-part}iconsWikiNote"/>
 *         &lt;element name="applyed-to" type="{http://map.osm.me/osm-doc-part}applyedTo"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feature", propOrder = {
    "title",
    "description",
    "keyword",
    "tags",
    "trait",
    "moreTags",
    "icon",
    "wiki",
    "note",
    "applyedTo"
})
public class Feature
    implements Serializable
{

    private final static long serialVersionUID = 2L;
    @XmlElement(required = true)
    protected String title;
    protected String description;
    protected List<LangString> keyword;
    @XmlElement(required = true)
    protected List<Tags> tags;
    protected List<Feature.Trait> trait;
    @XmlElement(name = "more-tags")
    protected MoreTags moreTags;
    protected List<String> icon;
    protected List<String> wiki;
    protected String note;
    @XmlList
    @XmlElement(name = "applyed-to", required = true)
    protected List<TagValueType> applyedTo;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the keyword property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keyword property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeyword().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LangString }
     * 
     * 
     */
    public List<LangString> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<LangString>();
        }
        return this.keyword;
    }

    /**
     * 
     * 						Теги по которым определяется принадлежность объекта осм данному типу.
     * 						Например:
     * 						
     * 						Those tags describes how to select features, or how to say, is 
     * 						any particular osm feature matches described osm-docc type.
     * 						For example:
     * 						
     * 						[tags]
     * 							[tag]
     * 								[tag]some-key[/tag]
     * 							[/tag]
     * 							[tag]
     * 								[tag]another-key[/tag]
     * 								[val value="some-value"/]
     * 							[/tag]
     * 						[/tags]
     * 						
     * 						В данном примере будут выбраны все объекты с тегом 'some-key' вне зависимости
     * 						от значения, и тегом 'another-key' со значением 'some-value'.
     * 						Т.е. элементы tag внутри элемента tags объединяются через 'И'.
     * 						
     * 						In this example all objects with tag 'some-key' and 'another-key' with value 'some-value'
     * 						will be selected. Tag value for 'some-key' doesn't matters.
     * 						'tag' elements inside 'tags' element combined via 'AND'. 
     * 						
     * 						Количество элементов 'tags' не ограничено. Элементы 'tags' объединяются через ИЛИ.
     * 						Тоесть для описания:
     * 						
     * 						Number of 'tags' elements are unbounded. All 'tags' are combined via 'OR'.
     * 						So for this declaration:
     * 						
     * 						[tags deprecated="true"]
     * 							[tag]
     * 								[tag]shop[/tag]
     * 								[val value="car_repair"/]
     * 							[/tag]
     * 						[/tags]
     * 						[tags]
     * 							[tag]
     * 								[tag]craft[/tag]
     * 								[val value="car_repair"/]
     * 							[/tag]
     * 						[/tags]
     * 						
     * 						В выборку попадут как shop=car_repair так и craft=car_repair.
     * 						При этом, deprecated="true" указывает какую комбинацию тегов использовать
     * 						при записи данных (например в персетах JOSM)
     * 						
     * 						shop=car_repair and craft=car_repair will be selected.
     * 						And deprecated="true" specifys which tags combination
     * 						will be used for writing (in JOSM presets for eg.)  
     * 					Gets the value of the tags property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tags property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTags().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tags }
     * 
     * 
     */
    public List<Tags> getTags() {
        if (tags == null) {
            tags = new ArrayList<Tags>();
        }
        return this.tags;
    }

    /**
     * Gets the value of the trait property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trait property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrait().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Feature.Trait }
     * 
     * 
     */
    public List<Feature.Trait> getTrait() {
        if (trait == null) {
            trait = new ArrayList<Feature.Trait>();
        }
        return this.trait;
    }

    /**
     * 
     * 						Дополнительные теги объекта. Такие как например кухня для ресторана.					    
     * 					
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
     * Gets the value of the icon property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the icon property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIcon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIcon() {
        if (icon == null) {
            icon = new ArrayList<String>();
        }
        return this.icon;
    }

    /**
     * Gets the value of the wiki property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wiki property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWiki().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getWiki() {
        if (wiki == null) {
            wiki = new ArrayList<String>();
        }
        return this.wiki;
    }

    /**
     * Gets the value of the note property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the applyedTo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the applyedTo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApplyedTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TagValueType }
     * 
     * 
     */
    public List<TagValueType> getApplyedTo() {
        if (applyedTo == null) {
            applyedTo = new ArrayList<TagValueType>();
        }
        return this.applyedTo;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="group-tags" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Trait
        implements Serializable
    {

        private final static long serialVersionUID = 2L;
        @XmlValue
        protected String value;
        @XmlAttribute(name = "group-tags")
        protected Boolean groupTags;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the groupTags property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isGroupTags() {
            return groupTags;
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

}
