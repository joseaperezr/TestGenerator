package es.infinitysoft.generator.test.config;

import java.util.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.infinitysoft.generator.test.StaticResources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class that configures the global application. It takes the configuration
 * parameters from the pair (key, value) specified at the
 * <i>config.properties</i> file.
 * 
 * @author JAPR
 */
public class Config {
	/** Instance of this singleton class */
	private static Config instance = null;
	//
	private Properties prop = new Properties();
	//
	private InputStream input = null;
	//
	private static final Logger log = LoggerFactory.getLogger(Config.class);

	/**
	 * Constructor of this singleton class.
	 */
	private Config() {
		try {
			input = new FileInputStream(StaticResources.ROOT_CONFIG_PROPERTIES
					+ "config.properties");
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Initializes this singleton class and creates a new instance if it's
	 * already not.
	 * 
	 * @return instance of this singleton class.
	 */
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	/**
	 * Gets the string value of the key property given.
	 * 
	 * @param key
	 *            key property.
	 * @return value of the key property.
	 */
	public String getString(String key) {
		String value = null;
		try {
			value = prop.getProperty(key);
		} catch (MissingResourceException mre) {
			log.warn("Not found " + key + " property. Is it defined?");
			log.error(mre.getMessage());
		}
		return value;
	}
}
