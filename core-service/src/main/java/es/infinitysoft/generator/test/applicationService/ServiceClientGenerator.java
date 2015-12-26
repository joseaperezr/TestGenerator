package es.infinitysoft.generator.test.applicationService;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;
import es.infinitysoft.generator.test.StaticResources;

/**
 *
 *
 * @author José Antonio Pérez Reyes - 20/08/2015.
 */
public class ServiceClientGenerator {
	//
	private static final Logger log = LoggerFactory.getLogger(ServiceClientGenerator.class);
	//
	private static ServiceClientGenerator instance = null;
    //
	protected ServiceClientGenerator() {
		// Exists only to defeat instantiation.
	}
    //
	public static ServiceClientGenerator getInstance() {
		if (instance == null) {
			instance = new ServiceClientGenerator();
		}
		return instance;
	}

    /**
     *
     * @param args
     */
    //public static void main(String[] args) {
    public void createClientByWsdl(String wsdlInput,String rootFolder,String folderOutput) {
         try {
        	 log.info("Start generation client WS.");
        	 createClientWSProyecto(rootFolder,folderOutput);
        	 getPOM(rootFolder + StaticResources.ROOT_POM_TEMPLATE + StaticResources.POM_TEMPLATE_NAME, wsdlInput,folderOutput);
             log.info("Generation client WS finished.");
        } catch (Exception ex) {
            log.error("");
            log.error("Houston, We've had a problem");
            log.error("Error description: ");
            log.error(ex.getMessage());
        }
    }

    /**
     *
     *
     * @param fileName
     */
    private static void getPOM(String fileName, String wsdl,String rootOuptput) {
        try {
            // Open file to process.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuilder contenido = new StringBuilder();
            // Se recorre el fichero a tratar.
            // contenido.append(XML_FORMAT + SALTO_LINEA);
            while ((line = bufferedReader.readLine()) != null) {
                //log.debug(line);
                contenido.append(line);
                contenido.append(StaticResources.SALTO_LINEA);
                if (line.trim().equals("<wsdlOption>")) {
                    contenido.append(getWsdls(wsdl));
                }
            }
            bufferedReader.close();
            saveFile(contenido, rootOuptput + StaticResources.ROOT_POM + StaticResources.POM_NAME);
            log.debug("pom generated.");
        } catch (FileNotFoundException ex) {
            log.error("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            log.error("Error reading file '" + fileName + "'");
            log.error(ex.getMessage());
        }
    }

    /*
    private static StringBuilder getServiceNamesWsdls(String fileName, String serviceName) {
        try {
            // Open file to process.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            StringBuilder contenido = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                line = line.replace(SERVICE_NAME_TYPE, serviceName);
                line = line.replace(SERVICE_NAME_VARIABLE, serviceName.substring(0, 1) + serviceName.substring(1, serviceName.length()));

                contenido.append(line);
                contenido.append(SALTO_LINEA);
                if (line.trim().equals(MARK_START_CLIENTS_CODE)) {
                    contenido.append(getWsdls(fileName));
                }

            }

            bufferedReader.close();
            System.out.println("generated.");
            return contenido;

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return null;
    }*/

    private static StringBuilder getWsdls(String fileName) {
        StringBuilder wsdls = new StringBuilder();
        //"http://193.200.100.7:8084/axis2/services/ContactService?wsdl"
        wsdls.append("<wsdl>"
                + fileName
                + "</wsdl>" + StaticResources.SALTO_LINEA);
        return wsdls;
    }

    /**
     * Creator of folder for aplication generated
     *
     * @throws IOException
     *
    private static void createFolder() throws IOException {
        //
        File folder = new File("." + ROOT_MODEL);
        folder.mkdirs();
        //
        folder = new File("." + ROOT_UI);
        folder.mkdirs();
        //
        folder = new File("." + ROOT_UTIL);
        folder.mkdirs();
        //
        folder = new File("." + ROOT_RESOURCES);
        folder.mkdirs();
        //
        folder = new File("." + ROOT_XAML);
        folder.mkdirs();

        File fileSource = new File(ROOT_SOURCES_HIBERNATE);
        File fileTarget = new File(ROOT_UTIL);

        FileUtils.copyFile(fileSource, fileTarget);

        fileSource = new File(ROOT_SOURCES_POM);
        fileTarget = new File(ROOT_POM);

        FileUtils.copyFile(fileSource, fileTarget);
    }*/

    /**
     * Guarda en disco el fichero.
     *
     * @param xml : contenido
     * @param fileName : nombre de fichero
     */
    private static void saveFile(StringBuilder xml, String fileName) {
        try {
            byte[] buffer = xml.toString().getBytes();
            FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(buffer);
            outputStream.close();
        } catch (IOException ex) {
            log.error("Error writing file '" + fileName + "'");
            log.error(ex.getMessage());
        }
    }
    
    
    /**
	 * Creator of folder for aplication generated
	 * 
	 * @throws IOException
	 */
	private void createClientWSProyecto(String rootFolder, String baseOutput) throws IOException {
		File fileSource = new File(rootFolder + StaticResources.ROOT_CLIENT_WS_TEMPLATE);
		File fileTarget = new File(baseOutput + StaticResources.ROOT_CLIENT_WS_GENERATED);
		FileUtils.copyDirectory(fileSource, fileTarget);
	}

    
}
