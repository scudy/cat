package com.dianping.cat.configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.dianping.cat.Cat;

public class EnviromentHelper {

	private static final String HOST = "http://cat.dianpingoa.com/cat/s/server";

	public static final String PROPERTIES_FILE = "/META-INF/app.properties";

	public static List<String> getServers() {
		String host = org.unidal.helper.Properties.forString().fromEnv().fromSystem().getProperty("CAT_PATH", HOST)
		      + "?ip=" + NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
		List<String> list = new ArrayList<String>();

		return list;
	}

	public static String loadAppNameByProperty(String defaultDomain) {
		String appName = null;
		InputStream in = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

			if (in == null) {
				in = Cat.class.getResourceAsStream(PROPERTIES_FILE);
			}
			if (in != null) {
				Properties prop = new Properties();

				prop.load(in);

				appName = prop.getProperty("app.name");

				if (appName != null) {
					return appName;
				} else {
					return defaultDomain;
				}
			}
		} catch (Exception e) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return defaultDomain;
	}

}
