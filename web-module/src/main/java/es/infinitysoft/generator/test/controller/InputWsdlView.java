package es.infinitysoft.generator.test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import org.primefaces.model.TreeNode;
import org.primefaces.showcase.domain.Document;
import org.primefaces.showcase.service.DocumentService;

import es.infinitysoft.generator.test.model.Service;
import es.infinitysoft.generator.test.service.MethodService;

import javax.annotation.PostConstruct;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.infinitysoft.generator.test.StaticResources;
import es.infinitysoft.generator.test.config.Config;
import es.infinitysoft.generator.test.util.FileUtil;
import es.infinitysoft.generator.test.util.ZipUtil;
import es.infinitysoft.generator.test.applicationService.ServiceClientGenerator;
import es.infinitysoft.generator.test.applicationService.ServiceExecuteTestSuite;
import es.infinitysoft.generator.test.applicationService.ServiceTestSuiteGenerator;

@ManagedBean(name = "inputWsdl")
@SessionScoped
public class InputWsdlView {

	//
	private static final Logger log = LoggerFactory
			.getLogger(InputWsdlView.class);

	private String wsdl;

	private StreamedContent file;

	private TreeNode root;

	private Document selectedDocument;
	
	private String folderName = null;
	
	private String rootReport = null;
	
	private String logs = null;
	
	private StringBuilder outPutExecute = new StringBuilder();
	
	private boolean renderOutPutExecute = false;
	
	

	@ManagedProperty("#{methodService}")
	private MethodService service;

	@PostConstruct
	public void init() {
		root = service.createDocuments(new Service());
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setService(MethodService service) {
		this.service = service;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public InputWsdlView() {
		super();

	}

	public StreamedContent getFile() {
		return file;
	}
	
	
	
	

	/**
	 * @return the rootReport
	 */
	public String getRootReport() {
		return rootReport;
	}

	/**
	 * @param rootReport the rootReport to set
	 */
	public void setRootReport(String rootReport) {
		this.rootReport = rootReport;
	}

	/**
	 * @return the folderName
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * @param folderName the folderName to set
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	/**
	 * 
	 */
	public void loadWsdl() {
		log.info("Start process for test suite generation.");
		try {
			// Get root folder's
			String rootFolder = Config.getInstance().getString(
					StaticResources.KEY_ROOT_TEST_SUITE_OUTPUT);
			folderName = "/" + UUID.randomUUID().toString();
			// Load wsdl data.
			ServiceClientGenerator.getInstance().createClientByWsdl(wsdl,
					rootFolder, rootFolder + folderName);
			Service service = ServiceTestSuiteGenerator.getInstance()
					.loadInfoWSFromWSDL(wsdl);
			// Load data into datatree
			root = this.service.createDocuments(service);
			log.info("Salida: " + folderName);
			
			message("Análisis terminado con éxito.");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void generateSuite() {
		log.info("Start process for test suite generation.");
		try {
			// Get root folder's
			String rootFolder = Config.getInstance().getString(
					StaticResources.KEY_ROOT_TEST_SUITE_OUTPUT);
			if (folderName == null || folderName.equals("")){
			   folderName = "/" + UUID.randomUUID().toString();
			}
			ServiceClientGenerator.getInstance().createClientByWsdl(wsdl,
					rootFolder, rootFolder + folderName);
			// Load wsdl data.
			Service service = ServiceTestSuiteGenerator.getInstance()
					.loadInfoWSFromWSDL(wsdl);
			// Load data into datatree
			root = this.service.createDocuments(service);
			//
			ServiceTestSuiteGenerator.getInstance().processWsdl(service,
					rootFolder, rootFolder + folderName);
			// Compress source generated.
			ZipUtil.getInstance().generateFileList(
					new File(rootFolder + folderName), rootFolder + folderName);
			ZipUtil.getInstance().zipIt(
					rootFolder + folderName + "/source.zip",
					rootFolder + folderName);
			// Get file generated.
			InputStream stream = new FileInputStream(rootFolder + folderName
					+ "/source.zip");
			file = new DefaultStreamedContent(stream, "application/zip",
					"source.zip");
			log.info("Salida: " + folderName);
			message("Generación terminada con éxito.");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	
	/**
	 * 
	 */
	public void executeSuite() {
		log.info("Start process for test suite generation.");
		try {
			// Get root folder's
			String rootFolder = Config.getInstance().getString(
					StaticResources.KEY_ROOT_TEST_SUITE_OUTPUT);
			//String folderName = "/" + UUID.randomUUID().toString();
			
            outPutExecute = ServiceExecuteTestSuite.getInstance().executeMvnCleanInstall(rootFolder + folderName + StaticResources.ROOT_TEST_SUITE_GENERATED);
            renderOutPutExecute =  true;
            
            FileUtil.getInstance().saveFile(outPutExecute, rootFolder + folderName + "/log.log");
            //FileUtil.getInstance().copyDirectory(rootFolder + folderName + "/log.log", "/xampp/htdocs/" + folderName + "/log.log");
            
            FileUtil.getInstance().copyDirectory(rootFolder + folderName, "/xampp/htdocs/" + folderName);
            
            this.rootReport = "http://127.0.0.1" +  folderName + StaticResources.ROOT_TEST_SUITE_GENERATED + "target/site/TestSuite.html"; 
            this.logs = "http://127.0.0.1" +  folderName +"/log.log";
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @return the wsdl
	 */
	public String getWsdl() {
		return wsdl;
	}

	/**
	 * @param wsdl
	 *            the wsdl to set
	 */
	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	private void message(String message) {
		FacesMessage msg = new FacesMessage(message);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public String handleCommand(String command, String[] params) {
		return outPutExecute.toString();
	}

	/**
	 * @return the outPutExecute
	 */
	public StringBuilder getOutPutExecute() {
		return outPutExecute;
	}

	/**
	 * @param outPutExecute the outPutExecute to set
	 */
	public void setOutPutExecute(StringBuilder outPutExecute) {
		this.outPutExecute = outPutExecute;
	}

	/**
	 * @return the renderOutPutExecute
	 */
	public boolean isRenderOutPutExecute() {
		return renderOutPutExecute;
	}

	/**
	 * @param renderOutPutExecute the renderOutPutExecute to set
	 */
	public void setRenderOutPutExecute(boolean renderOutPutExecute) {
		this.renderOutPutExecute = renderOutPutExecute;
	}

	/**
	 * @return the log
	 */
	public String getLogs() {
		return logs;
	}

	/**
	 * @param log the log to set
	 */
	public void setLogs(String log) {
		this.logs = logs;
	}
	
	

	
}
