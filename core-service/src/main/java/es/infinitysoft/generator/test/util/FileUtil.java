package es.infinitysoft.generator.test.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


/**
 * 
 * @author José Antonio Pérez Reyes.
 *
 */
public class FileUtil {
	
	//
	private static FileUtil instance = null;
	
	//
	public static FileUtil getInstance() {
		if (instance == null) {
			instance = new FileUtil();
		}
		return instance;
	}
	
	
	/**
	 * Creator of folder for aplication generated
	 * 
	 * @throws IOException
	 */
	public void copyDirectory(String source, String target )
			throws IOException {

		File fileSource = new File(source);
		File fileTarget = new File(target);
		FileUtils.copyDirectory(fileSource, fileTarget);
	}
	
	
	/**
	 * Guarda en disco el fichero.
	 * 
	 * @param xml
	 *            : contenido
	 * @param fileName
	 *            : nombre de fichero
	 */
	public void saveFile(StringBuilder content, String fileName) {
		try {
			byte[] buffer = content.toString().getBytes();
			FileOutputStream outputStream = new FileOutputStream(fileName);
			outputStream.write(buffer);
			outputStream.close();
		} catch (IOException ex) {
			System.out.println("Error writing file '" + fileName + "'");
		}
	}

}
