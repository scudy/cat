package com.dianping.cat.configuration;

import java.io.InputStream;
import java.util.Properties;

import org.unidal.helper.Files;
import org.unidal.helper.Urls;

import com.dianping.cat.Cat;

public class EnvironmentHelper {

	public static final String URL = "http://cat.dianpingoa.com/cat/s/launch";

	public static final String PROPERTIES_FILE = "/META-INF/app.properties";

	public static String fetchClientConfig() throws Exception {
		String url = org.unidal.helper.Properties.forString().fromEnv().fromSystem().getProperty("CAT_PATH", URL)
		      + "?ip=" + NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
		InputStream in = Urls.forIO().readTimeout(3000).connectTimeout(3000).openStream(url);

		return Files.forIO().readFrom(in, "utf-8");
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
