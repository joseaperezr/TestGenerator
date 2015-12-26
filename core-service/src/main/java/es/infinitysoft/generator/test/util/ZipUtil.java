package es.infinitysoft.generator.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	//
	private static ZipUtil instance = null;
	//
	private List<String> fileList = new ArrayList<String>();

	//
	public static ZipUtil getInstance() {
		if (instance == null) {
			instance = new ZipUtil();
		}
		return instance;
	}

	/*
	 * public static void main( String[] args ) { 
	 * ZipUtil appZip = new ZipUtil(); 
	 * appZip.generateFileList(new File(SOURCE_FOLDER));
	 * appZip.zipIt(OUTPUT_ZIP_FILE); }
	 */

	/**
	 * Zip it
	 * 
	 * @param zipFile
	 *            output ZIP file location
	 */
	public void zipIt(String zipFile, String sourceFolder) {
		byte[] buffer = new byte[1024];
		try {
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			System.out.println("Output to Zip : " + zipFile);

			for (String file : this.fileList) {

				System.out.println("File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(sourceFolder
						+ File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			// remember close it
			zos.close();

			System.out.println("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 */
	public void generateFileList(File node, String sourceFolder) {
		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString(),
					sourceFolder));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename), sourceFolder);
			}
		}
	}

	/**
	 * Format the file path for zip
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file, String sourceFolder) {
		int desde = sourceFolder.length() + 2;
		int hasta = file.length();
		String sub =file.substring(desde,hasta);  
		return sub;
	}
}