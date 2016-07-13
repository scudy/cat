package com.dianping.cat.configuration;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.helper.Files;
import org.unidal.helper.Urls;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;
import com.dianping.cat.configuration.client.transform.DefaultSaxParser;
import com.dianping.cat.message.spi.MessageTree;
import com.site.helper.JsonBuilder;

@Named(type = ClientConfigManager.class)
public class DefaultClientConfigManager implements LogEnabled, ClientConfigManager {

	private static final String PROPERTIES_FILE = EnviromentHelper.PROPERTIES_FILE;

	private ClientConfig m_config;

	private volatile double m_sample = 1d;

	private volatile boolean m_block = false;

	private String m_routers;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();

	private AtomicTreeParser m_atomicTreeParser = new AtomicTreeParser();

	private Logger m_logger;

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public String getDomain() {
		if(m_config!=null){
			return m_config.getDomain();
		}else{
			return "unknown";
		}
	}

	@Override
	public int getMaxMessageLength() {
		if (m_config == null) {
			return 5000;
		} else {
			return m_config.getMaxMessageSize();
		}
	}

	@Override
	public String getRouters() {
		if (m_routers == null) {
			refreshConfig();
		}
		return m_routers;
	}

	public double getSampleRatio() {
		return m_sample;
	}

	private String getServerConfigUrl() {
		if (m_config == null) {
			return null;
		} else {
			List<Server> servers = m_config.getServers();
			int size = servers.size();
			int index = (int) (size * Math.random());

			if (index >= 0 && index < size) {
				Server server = servers.get(index);

				Integer httpPort = server.getHttpPort();

				if (httpPort == null || httpPort == 0) {
					httpPort = 8080;
				}
				return String.format("http://%s:%d/cat/s/router?domain=%s&ip=%s&op=json", server.getIp().trim(), httpPort,
				      getDomain(), NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
			}
		}
		return null;
	}

	@Override
	public List<Server> getServers() {
		if (m_config == null) {
			return Collections.emptyList();
		} else {
			return m_config.getServers();
		}
	}

	@Override
	public int getTaggedTransactionCacheSize() {
		return 1024;
	}

	public void initialize() {
		String clientXml = Cat.getCatHome() + "client.xml";
		File configFile = new File(clientXml);

		m_logger.info("client xml path " + clientXml);

		ClientConfig globalConfig = null;
		String xml = null;

		if (configFile != null && configFile.exists()) {
			try {
				xml = Files.forIO().readFrom(configFile.getCanonicalFile(), "utf-8");

				globalConfig = DefaultSaxParser.parse(xml);
				m_logger.info("Global config file found." + xml);
			} catch (Exception e) {
				m_logger.error("error when parse xml " + xml, e);
				globalConfig = new ClientConfig();
			}
		} else {
			try {
				xml = EnviromentHelper.fetchClientConfig();

				globalConfig = DefaultSaxParser.parse(xml);
				m_logger.info("Global config file found." + xml);
			} catch (Exception e) {
				m_logger.error("error when parse remote server xml " + xml, e);
				globalConfig = new ClientConfig();
			}
		}
		globalConfig.setDomain(String.valueOf(loadProjectName()));

		m_config = globalConfig;
		m_logger.info("init cat with client config:" + m_config);
	}

	@Override
	public void initialize(ClientConfig config) {
		try {
			if (config != null) {
				m_config = config;
				m_logger.info("setup cat with config:" + config);
			}
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
			m_config = new ClientConfig();
		}
	}

	@Override
	public boolean isAtomicMessage(MessageTree tree) {
		return m_atomicTreeParser.isAtomicMessage(tree);
	}

	public boolean isBlock() {
		return m_block;
	}

	@Override
	public boolean isCatEnabled() {
		if (m_config == null) {
			return false;
		} else {
			return m_config.isEnabled();
		}
	}

	@Override
	public boolean isDumpLocked() {
		if (m_config == null) {
			return false;
		} else {
			return m_config.isDumpLocked();
		}
	}

	private String loadProjectName() {
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
					m_logger.info(String.format("Find domain name %s from app.properties.", appName));
				} else {
					m_logger.info(String.format("Can't find app.name from app.properties."));
					return null;
				}
			} else {
				m_logger.info(String.format("Can't find app.properties in %s", PROPERTIES_FILE));
			}
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
		return appName;
	}

	public void refreshConfig() {
		String url = getServerConfigUrl();

		try {
			InputStream inputstream = Urls.forIO().readTimeout(2000).connectTimeout(1000).openStream(url);
			String content = Files.forIO().readFrom(inputstream, "utf-8");
			KVConfig routerConfig = (KVConfig) m_jsonBuilder.parse(content.trim(), KVConfig.class);

			m_routers = routerConfig.getValue("routers");
			m_sample = Double.valueOf(routerConfig.getValue("sample").trim());
			m_block = Boolean.valueOf(routerConfig.getValue("block").trim());

			String startTypes = routerConfig.getValue("startTransactionTypes");
			String matchTypes = routerConfig.getValue("matchTransactionTypes");

			m_atomicTreeParser.init(startTypes, matchTypes);
		} catch (Exception e) {
			m_logger.warn("error when connect cat server config url " + url);
		}
	}

}
