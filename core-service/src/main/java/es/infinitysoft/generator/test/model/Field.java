package es.infinitysoft.generator.test.model;

import java.util.ArrayList;
import java.util.List;

import es.infinitysoft.generator.test.StaticResources;

/**
 * 
 * @author JAPR.
 *
 */
public class Field {

	public String name;
	public String type;
	public boolean nullable;
	public boolean complexType;
	public String maxOccurs;
	// public ComplexType complexType;
	public List<Field> fields = new ArrayList<Field>();

	public StringBuilder showAttributes() {
		StringBuilder out = new StringBuilder();
		out.append("Name: " + name + StaticResources.SALTO_LINEA);
		out.append("Type: " + type + StaticResources.SALTO_LINEA);
		out.append("MaxOccurs: " + maxOccurs + StaticResources.SALTO_LINEA);
		out.append("complexType: " + complexType + StaticResources.SALTO_LINEA);
		return out;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * @param nullable
	 *            the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return the complexType
	 */
	public boolean isComplexType() {
		return complexType;
	}

	/**
	 * @param complexType
	 *            the complexType to set
	 */
	public void setComplexType(boolean complexType) {
		this.complexType = complexType;
	}

	/**
	 * @return the maxOccurs
	 */
	public String getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @param maxOccurs
	 *            the maxOccurs to set
	 */
	public void setMaxOccurs(String maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	/**
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * @param fields
	 *            the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}