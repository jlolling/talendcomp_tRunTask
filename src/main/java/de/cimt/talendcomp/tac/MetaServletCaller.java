package de.cimt.talendcomp.tac;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MetaServletCaller {
	
	private static final Logger logger = Logger.getLogger(MetaServletCaller.class);
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (args == null || args.length == 0) {
			printUsage();
			System.exit(1);
		}
		String action = args[0];
		if ("migrateDatabase".equalsIgnoreCase(action)) {
			if (args.length < 2) {
				printUsage();
				System.out.println("Command to migrate a database: java -jar cimt-metaservletcaller-<version>.jar migrateDatabase /path/to/your-configuration.properties");
				System.exit(1);
			}
			String configFilePath = args[1];
			try {
				migrateDatabase(configFilePath);
			} catch (Exception e) {
				logger.error("migrateDatabase failed: " + e.getMessage());
				System.exit(2);
			}
		} else {
			printUsage();
			System.out.println("Unknown action: " + action);
			System.exit(1);
		}
		System.exit(0);
	}
	
	private static void migrateDatabase(String configFile) throws Exception {
		Properties properties = loadConfiguration(configFile);
		boolean debug = "true".equals(properties.getProperty("debug"));
		if (debug) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
			Logger.getRootLogger().info("Debug level set");
		}
		System.out.println("Setup migrate database request...");
		TACConnection conn = new TACConnection(properties.getProperty("targetTacUrl"));
		MigrateDatabaseAction action = new MigrateDatabaseAction(conn);
		action.setDbConfigPassword(properties.getProperty("dbConfigPassword"));
		action.setSourceUser(properties.getProperty("sourceUser"));
		action.setSourcePasswd(properties.getProperty("sourcePasswd"));
		action.setSourceUrl(properties.getProperty("sourceUrl"));
		action.setTargetUser(properties.getProperty("targetUser"));
		action.setTargetPasswd(properties.getProperty("targetPasswd"));
		action.setTargetUrl(properties.getProperty("targetUrl"));
		System.out.println("Send migrate database request...");
		action.execute();
	}

	private static void printUsage() {
		System.out.println("Caller for TAC-Metaservlet");
		System.out.println("@cimt AG version 1.0");
		System.out.println("Usage: java -jar cimt-metaservletcaller-<version>.jar <action> [<more parameters>]");
	}
	
	public static Properties loadConfiguration(String configFilePath) throws Exception {
		if (configFilePath == null || configFilePath.trim().isEmpty()) {
			throw new IllegalArgumentException("configuration file path cannot be null or empty!");
		}
		File configFile = new File(configFilePath);
		if (configFile.isAbsolute() == false) {
			configFile = new File(System.getProperty("work.dir"), configFilePath);
		}
		if (configFile.exists() == false) {
			throw new Exception("Given configuration file: " + configFile.getAbsolutePath() + " does not exist!");
		}
		System.out.println("Load configuration file: " + configFile.getAbsolutePath());
		Properties properties = new Properties();
		InputStream in = new FileInputStream(configFile);
		try {
			properties.load(in);
		} catch (Exception e) {
			logger.error("loadConfiguration failed: " + e.getMessage());
			throw e;
		} finally {
			in.close();
		}
		return properties;
	}

}
