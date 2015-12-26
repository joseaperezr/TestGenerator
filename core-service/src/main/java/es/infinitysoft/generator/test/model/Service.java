package es.infinitysoft.generator.test.model;

import java.util.ArrayList;
import java.util.List;



/**
 * Estructura donde volcaremos los datos del wsdl
 */
public class Service {

    public String name;
    //
    public String portType;
    //
    public List<Method> methods = new ArrayList<Method>();
    //
    public List<String> imports = new ArrayList<String>();

    public Service() {
    }

    public Service(String name, String portType) {
        this.name = name;
        this.portType = portType;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the portType
	 */
	public String getPortType() {
		return portType;
	}

	/**
	 * @param portType the portType to set
	 */
	public void setPortType(String portType) {
		this.portType = portType;
	}

	/**
	 * @return the methods
	 */
	public List<Method> getMethods() {
		return methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}

	/**
	 * @return the imports
	 */
	public List<String> getImports() {
		return imports;
	}

	/**
	 * @param imports the imports to set
	 */
	public void setImports(List<String> imports) {
		this.imports = imports;
	}

}