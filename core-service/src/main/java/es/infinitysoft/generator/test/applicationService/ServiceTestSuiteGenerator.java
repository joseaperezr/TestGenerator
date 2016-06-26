package es.infinitysoft.generator.test.applicationService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import javax.wsdl.xml.WSDLReader;
import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.WSDLException;

import com.ibm.wsdl.*;

import es.infinitysoft.generator.test.model.Message;
import es.infinitysoft.generator.test.model.Service;
import es.infinitysoft.generator.test.model.Field;
import es.infinitysoft.generator.test.model.Method;
import es.infinitysoft.generator.test.StaticResources;

/**
 * Generador
 * 
 * 
 * 
 * @author José Antonio Pérez Reyes .
 */
public class ServiceTestSuiteGenerator {

	//
	private static final Logger log = LoggerFactory
			.getLogger(ServiceTestSuiteGenerator.class);
	//
	private static final String SALTO_LINEA = "\n";
	//
	private static List<Service> services;
	//
	private static ServiceTestSuiteGenerator instance = null;

	protected ServiceTestSuiteGenerator() {
		// Exists only to defeat instantiation.
	}

	public static ServiceTestSuiteGenerator getInstance() {
		if (instance == null) {
			instance = new ServiceTestSuiteGenerator();
		}
		return instance;
	}

	/**
	 * 
	 * 
	 * @param wsdlInput
	 * @param rootFolder
	 * @param folderOutput
	 */
	public void processWsdl(Service service,String rootFolder,
			String folderOutput) {
		// Parameters validation.
		try {
			//
			//URL wsdl = new URL(wsdlInput);
			//Service service = new Service();
			// Load info about WS.
			//loadInfoWSFromWSDL(wsdl, service, fileName);
			printService(service);
			out("---------------------------------------------------------");
			services = new ArrayList<Service>();
			services.add(service);
			// Generador del proyecto
			createSuiteBase(rootFolder, folderOutput);
			// Generattion the client.
			generatorClient(rootFolder, folderOutput,   
					StaticResources.ROOT_TEST_SUITE_TEMPLATE
							+ StaticResources.ROOT_SRC
							+ StaticResources.ROOT_UTILS_SERVICE_CLIENTS
							+ StaticResources.CLIENT_CLASS_NAME, services);
			// Generador de @Test
			generatorTest(rootFolder + StaticResources.ROOT_TEST_TEMPLATE,
					services, folderOutput);
			log.info("Generation finished.");
		} catch (Exception ex) {
			out("");
			out("Houston, We've had a problem");
			out("Error description: ");
			out(ex.getMessage());
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * 
	 *         private String getNamespace(String fileName) { Definition def =
	 *         null; try { WSDLFactory factory = WSDLFactory.newInstance();
	 *         WSDLReader reader = factory.newWSDLReader();
	 *         reader.setFeature("javax.wsdl.verbose", false);
	 *         reader.setFeature("javax.wsdl.importDocuments", true); def =
	 *         reader.readWSDL(fileName); // reader.readWSDL(null,
	 *         wsdlInterfaceDescriptor.toExternalForm());
	 * 
	 *         } catch (WSDLException e) { e.printStackTrace(); } return
	 *         def.getTargetNamespace(); }
	 */

	/**
	 * 
	 * 
	 * @param fileName
	 * @param services
	 */
	private void generatorTest(String fileName, List<Service> services,
			String folderOutput) {
		try {
			for (Service service : services) {
				// Open file to process.
				FileReader fileReader = new FileReader(fileName);
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line = null;
				StringBuilder contenido = new StringBuilder();
				//
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
					line = line.replace(StaticResources.SERVICE_NAME_TAG,
							service.name);

					if (line.trim().equals(StaticResources.MARK_INSERT_IMPORT)) {
						out(StaticResources.MARK_INSERT_IMPORT);
						out(String.valueOf(service.imports.size()));
						for (String imp : service.imports) {
							out(imp);
							contenido.append("import " + imp + SALTO_LINEA);
						}
					} else {
						contenido.append(line + SALTO_LINEA);
					}
					//
					if (line.trim().equals(
							StaticResources.MARK_START_CLIENTS_CODE)) {
						for (Method method : service.methods) {
							out("Método: " + method.name);
							if ( //method.name.equals("createContact") ||
								 method.name.equals("getContactsCount")
							  || method.name.equals("insImportControll") 
							  || method.name.equals("getAllTypesContact") ||
							  //method.name.equals("updImportControll") ||
							  method.name .equals("updContactKronosToDisable")
							 || method.name.equals("updContactKronosToDisableByImportName")
							 ) {
								contenido.append(generatorTestMethod(method));
							}
						}
					}
				}
				bufferedReader.close();
				saveFile(contenido, folderOutput
						+ StaticResources.ROOT_TEST_SUITE_GENERATED
						+ StaticResources.ROOT_SRC_TEST
						+ StaticResources.ROOT_UTILS_SERVICE_TESTS
						+ service.name + "Test.java");
				System.out.println("Class test generated.");
			}
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("[generatorTest] Error reading file '"
					+ fileName + "'");
		}
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	private StringBuilder generatorTestMethod(Method method) {
		StringBuilder methodTest = new StringBuilder();

		methodTest.append(StaticResources.IDENT_3 + "@Test" + SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + "public void "
				+ method.name + "() {" + SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ "try {" + SALTO_LINEA);
		methodTest.append(getRequest(method));
		// methodTest.append(getType(method.response.element.type) +
		// " result = cliente.get" + method.service.name + "()" + method.name +
		// ".getReturn();");
		// System.out.print("Resultado: " + result.doubleValue());

		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ "System.out.println(\"Resultado: \" + result.toString());"
				+ SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ "Assert.assertTrue(" + getConditionAssert(method.response)
				+ ");" + SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ "} catch (Exception e) {" + SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ StaticResources.IDENT_3 + "e.printStackTrace();"
				+ SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + StaticResources.IDENT_3
				+ "}" + SALTO_LINEA);
		methodTest.append(StaticResources.IDENT_3 + "}" + SALTO_LINEA);
		return methodTest;
	}

	private String getConditionAssert(Message response) {
		if (response.getElement().fields.size() == 1) {
			if (getType(response.getElement().fields.get(0).type,
					response.getElement().fields.get(0).maxOccurs).equals(
					"Boolean")) {
				return "result";
			}
			if (getType(response.getElement().fields.get(0).type,
					response.getElement().fields.get(0).maxOccurs).equals(
					"Integer")
					|| getType(response.getElement().fields.get(0).type,
							response.getElement().fields.get(0).maxOccurs)
							.equals("Long")
					|| getType(response.getElement().fields.get(0).type,
							response.getElement().fields.get(0).maxOccurs)
							.equals("Double")) {
				return "result > 0";
			}

		} else {
			// Implementar caso contrario
		}
		//
		return "result != null";
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	private StringBuilder getRequest(Method method) {
		StringBuilder request = new StringBuilder();
		if (method.request.getElement().getName() == null) {
			request.append(getResponse(method.response));
			request.append("cliente.get" + method.service.name + "()."
					+ method.name + "().getReturn();" + SALTO_LINEA);
		} else {
			request.append(getObjects(method.request.getElement().fields,
					method.request.getElement().name, method.name, false)
					+ SALTO_LINEA);
			request.append(getResponse(method.response));
			request.append(" cliente.get" + method.service.name + "()."
					+ method.name + "("
					+ getObjectsNames(method.request.getElement().fields)
					+ ");");
		}
		return request;
	}

	/**
	 * 
	 * @param response
	 * @return
	 */
	private String getResponse(Message response) {
		if (response.getElement().fields.size() == 1) {
			return getType(response.getElement().fields.get(0).type,
					response.getElement().fields.get(0).maxOccurs)
					+ " result = ";
		} else {
			// Implementar caso contrario
		}
		return null;
	}

	/**
	 * 
	 * @param fields
	 * @param elementName
	 * @param methodName
	 * @param complexTypeChild
	 * @return
	 */
	private StringBuilder getObjects(List<Field> fields, String elementName,
			String methodName, boolean complexTypeChild) {
		StringBuilder object = new StringBuilder();
		out("[getObject]: elementName value: " + elementName);
		for (Field field : fields) {

			field.showAttributes();
			if (field.complexType) {
				object.append(getTypeName(field.type) + " "
						+ normalizationNameField(field.name) + " = new "
						+ getTypeName(field.type) + "();" + SALTO_LINEA);
				object.append(getObjects(field.fields, field.name, methodName,
						true));
			} else {
				out("[getObject]: field name: " + field.name);
				out("[getObject]: field name: "
						+ normalizationNameField(field.name));
				out("[getObject]: elementName: " + elementName);
				if (complexTypeChild) {
					object.append(elementName
							+ "."
							+ getNameMethodSet(normalizationNameField(field.name))
							+ "(");
					if (useObjectFactory(getType(field.type, field.maxOccurs))) {
						object.append("(new es.infinitysoft.ws.entity.xsd.ObjectFactory())."
								+ "create"
								+ getNameFirtCharUpper(elementName)
								+ getNameFirtCharUpper(normalizationNameField(field.name)));
					}
					object.append("("
							+ getRandomByType(field.type, field.maxOccurs)
							+ "));" + SALTO_LINEA);
				} else {
					object.append(getType(getTypeName(field.type),
							field.maxOccurs)
							+ " "
							+ normalizationNameField(field.name)
							+ " = new "
							+ getType(getTypeName(field.type), field.maxOccurs)
							+ "();" + SALTO_LINEA);
					object.append(field.name + " = ");
					if (useObjectFactory(getType(field.type, field.maxOccurs))) {
						object.append(""
								+ getRandomByType(field.type, field.maxOccurs)
								+ ";" + SALTO_LINEA);
					}
				}
			}
		}
		return object;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private boolean useObjectFactory(String type) {
		out("[useObjectFactory] type: " + type);
		return (!(type.equals("boolean") || type.equals("Boolean")
				|| type.equals("Integer") || type.equals("Long") || type
					.equals("Short")));
	}

	/**
	 * 
	 * 
	 * @param type
	 * @return
	 */
	private String getTypeName(String type) {
		String typeName = type.substring(getLastIndex(type, ':') + 1,
				type.length());
		out(typeName);
		return typeName;
	}

	/**
	 * 
	 * @param fields
	 * @return
	 */
	private StringBuilder getObjectsNames(List<Field> fields) {
		StringBuilder object = new StringBuilder();
		for (Field field : fields) {
			object.append(normalizationNameField(field.name) + ", ");
		}
		if (object.length() >= 2) {
			return new StringBuilder(object.substring(0, object.length() - 2));
		}
		return object;
	}

	/**
	 * Obtenemos el tipo Java.
	 * 
	 * @param type
	 * @param maxOccurs
	 * @return
	 */
	private String getType(String type, String maxOccurs) {
		//
		String unbounded = "?";
		if (maxOccurs != null && maxOccurs.equals("unbounded")) {
			unbounded = "List<?>";
		}
		//
		if (type.equals("double") || type.equals("xs:double")) {
			return unbounded.replace("?", "Double");
		}
		//
		if (type.equals("boolean") || type.equals("xs:boolean")) {
			return unbounded.replace("?", "Boolean");
		}
		//
		if (type.equals("string") || type.equals("xs:string")) {
			return unbounded.replace("?", "String");
		}
		//
		if (type.equals("date") || type.equals("xs:date")
				|| type.equals("dateTime") || type.equals("xs:dateTime")) {
			return unbounded.replace("?", "Date");
		}
		//
		if (type.equals("long") || type.equals("xs:long")) {
			return unbounded.replace("?", "Long");
		}
		//
		if (type.equals("int") || type.equals("xs:int")) {
			return unbounded.replace("?", "Integer");
		}
		//
		if (type.equals("short") || type.equals("xs:short")) {
			return unbounded.replace("?", "Short");
		}
		//
		return unbounded.replace("?", "Object");
	}

	/**
	 * Método generador de la clase con los conectores a los WS.
	 * 
	 * @param fileName
	 * @param services
	 */
	private void generatorClient(String rootFolder, String rootOutput, String fileName,
			List<Service> services) {
		try {
			// Open file to process.
			FileReader fileReader = new FileReader(rootFolder + fileName);
			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = null;
			StringBuilder contenido = new StringBuilder();
			out("Generating the clients");
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				contenido.append(line);
				contenido.append(SALTO_LINEA);

				if (line.trim().equals(StaticResources.MARK_START_CLIENTS_CODE)) {
					StringBuilder clientDeclare = new StringBuilder();
					StringBuilder conectors = new StringBuilder();
					for (Service service : services) {
						clientDeclare
								.append(StaticResources.DECLARE_CLIENT_TEMPLATE
										.replace(
												StaticResources.SERVICE_NAME_TYPE,
												service.portType)
										.replace(
												StaticResources.SERVICE_NAME_VARIABLE,
												StaticResources.SERVICE_CLIENT_V
														+ service.name));
						conectors.append(getConector(rootFolder,service) + SALTO_LINEA);

					}
					contenido.append(clientDeclare);
					//
					contenido.append(SALTO_LINEA);
					//
					contenido.append(conectors);
				}
			}
			//
			bufferedReader.close();
			saveFile(contenido, rootOutput
					+ StaticResources.ROOT_TEST_SUITE_GENERATED
					+ StaticResources.ROOT_SRC
					+ StaticResources.ROOT_UTILS_SERVICE_CLIENTS
					+ StaticResources.CLIENT_CLASS_NAME);
			System.out.println("pom generated.");
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("Error reading file '" + fileName + "'");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("Error reading file '" + fileName + "'");
		}
	}

	/**
	 * Construye el conector.
	 * 
	 * @param service
	 * @return
	 */
	private StringBuilder getConector(String rootFolder,Service service)
			throws FileNotFoundException, IOException, Exception {
		// Open file to process.
		FileReader fileReader = new FileReader(rootFolder +
				StaticResources.ROOT_SERVICE_CLIENT_TEMPLATE);
		// Always wrap FileReader in BufferedReader.
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		try {
			out("Getting the connector...");
			String line = null;
			StringBuilder contenido = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				out(line);
				line = line
						.replace(
								StaticResources.SERVICE_NAME_TYPE,
								service.name.substring(0, 1).toUpperCase()
										+ service.name.substring(1,
												service.name.length()))
						.replace(
								StaticResources.SERVICE_NAME_VARIABLE,
								service.name.substring(0, 1).toLowerCase()
										+ service.name.substring(1,
												service.name.length()))
						.replace(StaticResources.SERVICE_NAME_TYPE_CLIENT,
								service.portType.trim())
						.replace(
								StaticResources.SERVICE_NAME_VARIABLE_CLIENT,
								StaticResources.SERVICE_CLIENT_V
										+ service.name.trim());
				contenido.append(StaticResources.IDENT_3 + line + SALTO_LINEA);
			}
			out("connector generated.");
			return contenido;
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.out.println("Unable to open file '"
					+ StaticResources.ROOT_SERVICE_CLIENT_TEMPLATE + "'");
			throw ex;
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.out.println("Error reading file '"
					+ StaticResources.ROOT_SERVICE_CLIENT_TEMPLATE + "'");
			throw ex;
		} finally {
			bufferedReader.close();

		}
	}

	/**
	 * 
	 * 
	 * @param fileName
	 * @param serviceName
	 * @return
	 * 
	 *         private static StringBuilder getServiceNamesWsdls(String
	 *         fileName, String serviceName) { try { // Open file to process.
	 *         FileReader fileReader = new FileReader(fileName); // Always wrap
	 *         FileReader in BufferedReader. BufferedReader bufferedReader = new
	 *         BufferedReader(fileReader); String line = null; StringBuilder
	 *         contenido = new StringBuilder(); while ((line =
	 *         bufferedReader.readLine()) != null) { System.out.println(line);
	 *         line = line.replace(SERVICE_NAME_TYPE, serviceName); line =
	 *         line.replace(SERVICE_NAME_VARIABLE, serviceName.substring(0, 1) +
	 *         serviceName.substring(1, serviceName.length()));
	 * 
	 *         contenido.append(line); contenido.append(SALTO_LINEA); if
	 *         (line.trim().equals(MARK_START_CLIENTS_CODE)) {
	 *         contenido.append(getWsdls(fileName)); }
	 * 
	 *         } bufferedReader.close(); System.out.println("generated.");
	 *         return contenido; } catch (FileNotFoundException ex) {
	 *         System.out.println("Unable to open file '" + fileName + "'"); }
	 *         catch (IOException ex) {
	 *         System.out.println("Error reading file '" + fileName + "'"); }
	 *         return null; }
	 */
	/**
	 * 
	 * @param fileName
	 * @return
	 * 
	 *         private StringBuilder getWsdls(String fileName) { StringBuilder
	 *         wsdls = new StringBuilder(); wsdls.append("<wsdl>" + fileName +
	 *         "</wsdl>" + SALTO_LINEA); return wsdls; }
	 */

	/**
	 * Creator of folder for aplication generated
	 * 
	 * @throws IOException
	 */
	private void createSuiteBase(String rootFolder, String baseOutput)
			throws IOException {

		File fileSource = new File(rootFolder
				+ StaticResources.ROOT_TEST_SUITE_TEMPLATE);
		File fileTarget = new File(baseOutput
				+ StaticResources.ROOT_TEST_SUITE_GENERATED);
		FileUtils.copyDirectory(fileSource, fileTarget);
	}

	/**
	 * Creator of folder for aplication generated
	 * 
	 * @throws IOException
	 * 
	 *             private void createFolder() throws IOException { String
	 *             baseOutput = Config.getInstance().getString(StaticResources.
	 *             KEY_ROOT_TEST_SUITE_OUTPUT); // File folder = new File("." +
	 *             StaticResources.ROOT_MODEL); folder.mkdirs(); // folder = new
	 *             File("." + StaticResources.ROOT_UI); folder.mkdirs(); //
	 *             folder = new File("." + StaticResources.ROOT_UTIL);
	 *             folder.mkdirs(); // folder = new File("." +
	 *             StaticResources.ROOT_RESOURCES); folder.mkdirs(); // folder =
	 *             new File("." + StaticResources.ROOT_XAML); folder.mkdirs();
	 * 
	 *             File fileSource = new
	 *             File(StaticResources.ROOT_SOURCES_HIBERNATE); File fileTarget
	 *             = new File(StaticResources.ROOT_UTIL);
	 * 
	 *             FileUtils.copyFile(fileSource, fileTarget);
	 * 
	 *             fileSource = new File(StaticResources.ROOT_SOURCES_POM);
	 *             fileTarget = new File(StaticResources.ROOT_POM);
	 * 
	 *             FileUtils.copyFile(fileSource, fileTarget); }
	 */

	/**
	 * Conversor types input to types java.
	 * 
	 * @param type
	 * @return
	 * 
	 *         private String getTypeJava(String type) {
	 * 
	 *         System.out.println("Type input: " +
	 *         type.trim().toLowerCase().substring(0, 7)); if
	 *         (type.trim().equals("string")) { return "String"; } if
	 *         (type.trim().equals("int")) { return "Integer"; }
	 * 
	 *         if (type.trim().equals("long")) { return "Long"; }
	 * 
	 *         if (type.trim().toLowerCase().substring(0, 3).equals("date")) {
	 *         return "Date"; }
	 * 
	 *         return "Object"; }
	 */

	/**
	 * Guarda en disco el fichero.
	 * 
	 * @param xml
	 *            : contenido
	 * @param fileName
	 *            : nombre de fichero
	 */
	private void saveFile(StringBuilder xml, String fileName) {
		try {
			byte[] buffer = xml.toString().getBytes();
			FileOutputStream outputStream = new FileOutputStream(fileName);
			outputStream.write(buffer);
			outputStream.close();
		} catch (IOException ex) {
			System.out.println("Error writing file '" + fileName + "'");
		}
	}

	/**
	 * Obtiene el PortName del wsdl
	 * 
	 * @param wsdlInterfaceDescriptor
	 * @param fileName
	 * @return
	 */
	private String getPortName(URL wsdlInterfaceDescriptor)
			throws WSDLException, Exception {
		Definition def = null;
		// try {
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		reader.setFeature("javax.wsdl.importDocuments", true);
		// def = reader.readWSDL(null,
		// wsdlInterfaceDescriptor.toExternalForm());
		def = reader.readWSDL(wsdlInterfaceDescriptor.toString());
		/*
		 * } catch (WSDLException e) { e.printStackTrace(); }
		 */
		Collection<?> bindingList = def.getBindings().values();
		for (Iterator<?> bindingIterator = bindingList.iterator(); bindingIterator
				.hasNext();) {
			BindingImpl bind = (BindingImpl) bindingIterator.next();
			if (!bind.getPortType().isUndefined()) {
				return bind.getPortType().getQName().getLocalPart();
			}
		}
		return "";
	}

	/**
	 * Cargador de datos del wsdl en la estructura de clases.
	 * 
	 * @param wsdlInterfaceDescriptor
	 * @param service
	 * @param fileName
	 */
	public Service loadInfoWSFromWSDL(String wsdl) throws WSDLException, Exception {
		log.info("Loading info WS from WSDL...");
		Service service = new Service();
		
		URL wsdlInterfaceDescriptor = new URL(wsdl);
		
		// Load info about WS.
		Definition def = null;
		// Service service = new Service();
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader reader = factory.newWSDLReader();
		// reader.setFeature("javax.wsdl.verbose", false);
		// reader.setFeature("javax.wsdl.importDocuments", true);
		def = reader.readWSDL(wsdlInterfaceDescriptor.toString());
		System.out.println(def.getServices().size());
		// ServiceImpl ser= (ServiceImpl)def.getServices().get(0);
		System.out.println(def.getTargetNamespace());
		System.out.println(def.getDocumentBaseURI());
		System.out.println(def.getTargetNamespace().substring(
				getLastIndex(def.getTargetNamespace(), '/') + 1,
				def.getTargetNamespace().length()));
		String serviceName = "ContactService";
		Collection<?> bindingList = def.getBindings().values(); // getBindings().values();
		System.out.println(bindingList.size());
		service.name = def.getTargetNamespace().substring(
				getLastIndex(def.getTargetNamespace(), '/') + 1,
				def.getTargetNamespace().length());
		out(" Service Name:  " + service.name);
		service.portType = getPortName(wsdlInterfaceDescriptor);
		out(" Service PortType:  " + service.portType);
		service.imports = getImports(wsdlInterfaceDescriptor); //OJOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
		service.methods = new ArrayList<Method>();
		for (Iterator<?> bindingIterator = bindingList.iterator(); bindingIterator
				.hasNext();) {
			BindingImpl bind = (BindingImpl) bindingIterator.next();
			//
			if (bind.getQName().getLocalPart().replace(serviceName, "")
					.equals("Soap11Binding")) {
				System.out.println(bind.getQName().getLocalPart());
				System.out.println(bind.getBindingOperations().size());
				// Methods / operations.
				for (int i = 0; i < bind.getBindingOperations().size(); i++) {
					Method method = new Method();
					// if (!bind.getPortType().isUndefined()) {
					BindingOperationImpl operation = (BindingOperationImpl) bind
							.getBindingOperations().get(i);
					Operation oper = operation.getOperation();

					method.name = oper.getName();
					method.service = service;
					out(" - Method name: " + method.name);

					// Request
					if (oper.getInput() != null) {
						Message request = new Message();
						es.infinitysoft.generator.test.model.Element element = new es.infinitysoft.generator.test.model.Element();
						request.setName(oper.getInput().getMessage().getQName()
								.getLocalPart());
						out(" -- Input name: " + request.getName());
						element = getElement(oper.getInput().getMessage()
								.getParts().values());
						out(" --- Element name: " + element.name);

						request.setElement(getElement(element,wsdlInterfaceDescriptor));
						method.request = request;
					}
					// Response
					if (oper.getOutput() != null) {
						Message response = new Message();
						es.infinitysoft.generator.test.model.Element element = new es.infinitysoft.generator.test.model.Element();
						response.setName(oper.getOutput().getMessage()
								.getQName().getLocalPart());
						out(" -- Output name: " + response.getName());
						element = getElement(oper.getOutput().getMessage()
								.getParts().values());
						out(" --- Element name: " + element.name);

						response.setElement(getElement(element,wsdlInterfaceDescriptor));
						method.response = response;
					}
					//
					out(" ");
					service.methods.add(method);
				}
			}
		}
		out("Info WS from WSDL load success.");
		return service;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private List<String> getImports(URL  wsdlInterfaceDescriptor) {
		List<String> imports = new ArrayList<String>();
		try {
			for (String tN : getPackageTypes(wsdlInterfaceDescriptor)) {
				URL u = new URL(tN);
				String[] parts = u.getHost().split("\\.");
				String _package = "";
				for (int i = parts.length - 1; i > -1; i--) {
					_package += parts[i] + ".";
				}
				_package += u.getPath().replace("/", "") + ".";
				_package += "*;";// + SALTO_LINEA;
				imports.add(_package);
				// out(_package);
			}
		} catch (Exception e) {
		}
		return imports;
	}

	private es.infinitysoft.generator.test.model.Element getElement(
			Collection<?> partsList) {
		es.infinitysoft.generator.test.model.Element element = new es.infinitysoft.generator.test.model.Element();
		// Collection<?> partsList =
		// oper.getInput().getMessage().getParts().values();
		for (Iterator<?> partIterator = partsList.iterator(); partIterator
				.hasNext();) {
			javax.wsdl.Part part = (javax.wsdl.Part) partIterator.next();
			// System.out.println(" -- Element: " +
			// part.getElementName().getLocalPart());
			element.setName(part.getElementName().getLocalPart());
		}
		return element;
	}

	/**
	 * 
	 * @param source
	 * @param caracter
	 * @return
	 */
	private int getLastIndex(String source, char caracter) {
		//
		for (int i = source.length() - 1; i > 0; i--) {
			if (source.charAt(i) == caracter) {
				return i;
			}
		}
		return -1;
	}

	private void out(String str) {
		log.info(str);
	}

	/**
	 * Extraemos tipos complejos del wsdl
	 */
	public Field getComplexType(URL wsdlInterfaceDescriptor, String complexType, Field field) {
		try {
			// out(complexType);
			//File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(wsdlInterfaceDescriptor.openConnection().getInputStream());

			doc.getDocumentElement().normalize();
			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("wsdl:definitions");

			// Recorremos los nodos.
			for (int temp = 0; temp < nList.getLength(); temp++) {
				org.w3c.dom.Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					// System.out.println("Staff id : " +
					// nNode.getChildNodes().item(0).getNodeName());
					for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
						// Cambios generales que se aplicaran en el estilo
						// normal.
						if (nNode.getChildNodes().item(i).getNodeName()
								.equals("wsdl:types")) {
							// System.out.println("Nodo1 : " +
							// nNode.getChildNodes().item(i).getNodeName());
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode
									.getChildNodes().item(i);
							// out(String.valueOf(eElement.getElementsByTagName("xs:complexType").getLength()));
							if (eElement.getElementsByTagName("xs:complexType")
									.getLength() > 0) {
								for (int x = 0; x < eElement
										.getElementsByTagName("xs:complexType")
										.getLength(); x++) {
									org.w3c.dom.Element elementCT = (org.w3c.dom.Element) eElement
											.getElementsByTagName(
													"xs:complexType").item(x);
									// out(elementCT.getAttribute("name"));
									if (elementCT.getAttribute("name").equals(
											complexType)) {
										// out(elementCT.getAttribute("name"));
										if (elementCT.getElementsByTagName(
												"xs:element").getLength() > 0) {
											for (int y = 0; y < elementCT
													.getElementsByTagName(
															"xs:element")
													.getLength(); y++) {
												Field fieldCT = new Field();
												org.w3c.dom.Element subelement = (org.w3c.dom.Element) elementCT
														.getElementsByTagName(
																"xs:element")
														.item(y);
												out(" ----- "
														+ subelement
																.getAttribute("name")
														+ " - "
														+ subelement
																.getAttribute("type"));

												fieldCT.name = subelement
														.getAttribute("name");
												fieldCT.type = subelement
														.getAttribute("type");
												if (subelement
														.getAttribute(
																"maxOccurs")
														.trim().equals("0")) {
													fieldCT.nullable = true;
												} else {
													fieldCT.nullable = false;
												}
												field.fields.add(fieldCT);
												field.complexType = true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}

	/**
	 * Extraemos elementos del wsdl
	 * 
	 * @param fileName
	 * @param element
	 * @return
	 */
	private es.infinitysoft.generator.test.model.Element getElement(
			es.infinitysoft.generator.test.model.Element element,
			URL wsdlInterfaceDescriptor) {
		try {
			// File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(wsdlInterfaceDescriptor
					.openConnection().getInputStream());

			doc.getDocumentElement().normalize();
			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("wsdl:definitions");

			// Recorremos los nodos.
			for (int temp = 0; temp < nList.getLength(); temp++) {
				org.w3c.dom.Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					// System.out.println("Staff id : " +
					// nNode.getChildNodes().item(0).getNodeName());
					for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
						// Cambios generales que se aplicaran en el estilo
						// normal.
						if (nNode.getChildNodes().item(i).getNodeName()
								.equals("wsdl:types")) {
							// System.out.println("Nodo1 : " +
							// nNode.getChildNodes().item(i).getNodeName());
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode
									.getChildNodes().item(i);
							out("DEBUG: "
									+ String.valueOf(eElement
											.getElementsByTagName("xs:element")
											.getLength()));
							if (eElement.getElementsByTagName("xs:element")
									.getLength() > 0) {
								for (int x = 0; x < eElement
										.getElementsByTagName("xs:element")
										.getLength(); x++) {
									org.w3c.dom.Element elementCT = (org.w3c.dom.Element) eElement
											.getElementsByTagName("xs:element")
											.item(x);
									// out("DEBUG: " +
									// elementCT.getAttribute("name"));
									if (elementCT.getAttribute("name").equals(
											element.name)) {
										out("DEBUG: "
												+ elementCT
														.getAttribute("name"));
										if (elementCT.getElementsByTagName(
												"xs:element").getLength() > 0) {
											for (int y = 0; y < elementCT
													.getElementsByTagName(
															"xs:element")
													.getLength(); y++) {
												org.w3c.dom.Element subelement = (org.w3c.dom.Element) elementCT
														.getElementsByTagName(
																"xs:element")
														.item(y);
												out(" ---- ComplexType name/type: "
														+ subelement
																.getAttribute("name")
														+ " - "
														+ subelement
																.getAttribute("type"));

												Field field = new Field();
												field.name = subelement
														.getAttribute("name");
												field.type = subelement
														.getAttribute("type");
												if (subelement
														.getAttribute("maxOccurs") != null) {
													field.maxOccurs = subelement
															.getAttribute("maxOccurs");
												} else {
													field.maxOccurs = "";
												}

												field = getComplexType(
														wsdlInterfaceDescriptor,
														subelement
																.getAttribute(
																		"type")
																.replace("xs:",
																		"")
																.replace(
																		"ax22:",
																		""),
														field);
												element.fields.add(field);
											}
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return element;
	}

	/**
	 * Extraemos ruta de paquetes de los bean de los tipos complejos del wsdl
	 */
	private List<String> getPackageTypes(URL wsdlInterfaceDescriptor) {
		List<String> packages = new ArrayList<String>();
		try {

			//File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(wsdlInterfaceDescriptor.openConnection().getInputStream() );

			doc.getDocumentElement().normalize();
			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("wsdl:definitions");

			// Recorremos los nodos.
			for (int temp = 0; temp < nList.getLength(); temp++) {
				org.w3c.dom.Node nNode = nList.item(temp);
				// System.out.println("\nCurrent Element :" +
				// nNode.getNodeName());
				if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					// System.out.println("Staff id : " +
					// nNode.getChildNodes().item(0).getNodeName());
					for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
						// Cambios generales que se aplicaran en el estilo
						// normal.
						if (nNode.getChildNodes().item(i).getNodeName()
								.equals("wsdl:types")) {
							// System.out.println("Nodo1 : " +
							// nNode.getChildNodes().item(i).getNodeName());
							org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode
									.getChildNodes().item(i);
							out("DEBUG: "
									+ String.valueOf(eElement
											.getElementsByTagName("xs:schema")
											.getLength()));
							if (eElement.getElementsByTagName("xs:schema")
									.getLength() > 0) {
								for (int x = 0; x < eElement
										.getElementsByTagName("xs:schema")
										.getLength(); x++) {
									org.w3c.dom.Element elementCT = (org.w3c.dom.Element) eElement
											.getElementsByTagName("xs:schema")
											.item(x);
									out("DEBUG: targetNamespace"
											+ elementCT
													.getAttribute("targetNamespace"));
									packages.add(elementCT
											.getAttribute("targetNamespace"));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packages;
	}

	/**
	 * Show info web service.
	 * 
	 * @param service
	 */
	private void printService(Service service) {
		out("********* INFO SERVICE *********");

		out("Service name: " + service.name);
		out("Service portType: " + service.portType);

		out("Number of methods: " + service.methods.size());

		for (Method method : service.methods) {
			//
			out(" - Method name: " + method.name);
			//
			if (method.request != null) {
				out(" -- Method input (request): " + method.request.getName());
				out(" --- Element name: " + method.request.getElement().name);
				out(" --- Element fields: ");
				printField(" ----", method.request.getElement().fields);
			}
			//
			if (method.response != null) {
				out(" -- Method output (response): "
						+ method.response.getName());
				out(" --- Element name: "
						+ method.response.getElement().getName());
				out(" --- Element fields: ");
				printField(" ----", method.response.getElement().fields);
			}
		}
	}

	/**
	 * 
	 * @param identation
	 * @param fields
	 */
	private void printField(String identation, List<Field> fields) {
		for (Field field : fields) {
			out(identation + "- " + field.name + " : " + field.type);
			if (field.complexType) {
				printField(identation + "-", field.fields);
			}
		}
	}

	private String getNameMethodSet(String name) {
		return "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());
	}

	/*
	 * private String getNameMethodGet(String name) { return "get" +
	 * name.substring(0, 1).toUpperCase() + name.substring(1, name.length()); }
	 */
	private String getNameFirtCharUpper(String name) {
		return name.substring(0, 1).toUpperCase()
				+ name.substring(1, name.length());
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private String normalizationNameField(String name) {
		//
		String output = new String();
		boolean found = false;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '_') {
				found = true;
			} else {
				if (found) {
					output += String.valueOf(name.charAt(i)).toUpperCase();
					found = false;
				} else {
					output += name.charAt(i);
				}
			}
		}
		return output;
	}

	/**
	 * Get Random method's by type.
	 * 
	 * @param type
	 * @param maxOccurs
	 * @return
	 */
	private String getRandomByType(String type, String maxOccurs) {

		out("getRandomByType: type --> " + type);
		if (getType(type, maxOccurs).equals("String")) {
			return "BaseUtils.getRandomString()";
		}

		if (getType(type, maxOccurs).equals("Date")
				|| getType(type, maxOccurs).equals("date")) {
			return "BaseUtils.getRandomDate()";
		}

		if (getType(type, maxOccurs).equals("long")
				|| getType(type, maxOccurs).equals("Long")) {
			return "BaseUtils.getRandomLong()";
		}

		if (getType(type, maxOccurs).equals("int")
				|| getType(type, maxOccurs).equals("Integer")) {
			return "BaseUtils.getRandomInt()";
		}

		if (getType(type, maxOccurs).equals("boolean")
				|| getType(type, maxOccurs).equals("Boolean")) {
			return "BaseUtils.getRandomBoolean()";
		}

		if (getType(type, maxOccurs).equals("short")
				|| getType(type, maxOccurs).equals("Short")) {
			return "new Short((short)0)";
		}
		return "null";
	}

}
