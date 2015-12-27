package es.infinitysoft.generator.test.applicationService;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceExecuteTestSuite {

	//
	private static final Logger log = LoggerFactory
			.getLogger(ServiceExecuteTestSuite.class);
	
	//
	private static ServiceExecuteTestSuite instance = null;
    //
	protected ServiceExecuteTestSuite() {
		// Exists only to defeat instantiation.
	}
    //
	public static ServiceExecuteTestSuite getInstance() {
		if (instance == null) {
			instance = new ServiceExecuteTestSuite();
		}
		return instance;
	}

	public void executeMvnCleanInstall(String root) {

		String s = null;

		try {

			// Determinar en qué SO estamos
			String so = System.getProperty("os.name");

			String comando;

			// Comando para Linux
			if (so.equals("Linux"))
				comando = "ifconfig";

			// Comando para Windows
			else
				//comando = "cmd /c ipconfig";
				comando = "cmd /c cd "  +  root + " && %MAVEN_HOME%\\bin\\mvn clean install && %MAVEN_HOME%\\bin\\mvn site";

			// Ejcutamos el comando
			Process p = Runtime.getRuntime().exec(comando);

			
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// Leemos la salida del comando
			log.info("Ésta es la salida standard del comando:\n");
			while ((s = stdInput.readLine()) != null) {
				log.info(s);
			}

			// Leemos los errores si los hubiera
			log.info("Ésta es la salida standard de error del comando (si la hay):\n");
			while ((s = stdError.readLine()) != null) {
				log.info(s);
			}

			//System.exit(0);
		} catch (IOException e) {
			log.error("Excepción: " + e.getMessage());
			// System.exit(-1);
		}
	}

}
