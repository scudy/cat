package com.dianping.cat.config.app;

import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.config.content.ContentFetcher;
import com.dianping.cat.configuration.app.metric.entity.AppMetric;
import com.dianping.cat.configuration.app.metric.entity.AppMetricConfig;
import com.dianping.cat.configuration.app.metric.transform.DefaultSaxParser;
import com.dianping.cat.core.config.Config;
import com.dianping.cat.core.config.ConfigDao;
import com.dianping.cat.core.config.ConfigEntity;
import com.dianping.cat.task.TimerSyncTask;
import com.dianping.cat.task.TimerSyncTask.SyncHandler;

@Named
public class AppMetricConfigManager implements Initializable {

	@Inject
	protected ConfigDao m_configDao;

	@Inject
	protected ContentFetcher m_fetcher;

	private int m_configId;

	private long m_modifyTime;

	private AppMetricConfig m_config;

	private static final String CONFIG_NAME = "app-metric-config";

	public AppMetricConfig getConfig() {
		return m_config;
	}

	public Map<String, AppMetric> queryAppMetrics() {
		return m_config.getAppMetrics();
	}

	@Override
	public void initialize() throws InitializationException {
		try {
			Config config = m_configDao.findByName(CONFIG_NAME, ConfigEntity.READSET_FULL);
			String content = config.getContent();

			m_configId = config.getId();
			m_modifyTime = config.getModifyDate().getTime();
			m_config = DefaultSaxParser.parse(content);
		} catch (DalNotFoundException e) {
			try {
				String content = m_fetcher.getConfigContent(CONFIG_NAME);
				Config config = m_configDao.createLocal();

				config.setName(CONFIG_NAME);
				config.setContent(content);
				m_configDao.insert(config);
				m_configId = config.getId();
				m_config = DefaultSaxParser.parse(content);
			} catch (Exception ex) {
				Cat.logError(ex);
			}
		} catch (Exception e) {
			Cat.logError(e);
		}
		if (m_config == null) {
			m_config = new AppMetricConfig();
		}

		TimerSyncTask.getInstance().register(new SyncHandler() {

			@Override
			public String getName() {
				return CONFIG_NAME;
			}

			@Override
			public void handle() throws Exception {
				refreshConfig();
			}
		});
	}

	public boolean insert(String xml) {
		try {
			m_config = DefaultSaxParser.parse(xml);

			return storeConfig();
		} catch (Exception e) {
			Cat.logError(e);
			return false;
		}
	}

	private void refreshConfig() throws Exception {
		Config config = m_configDao.findByName(CONFIG_NAME, ConfigEntity.READSET_FULL);
		long modifyTime = config.getModifyDate().getTime();

		synchronized (this) {
			if (modifyTime > m_modifyTime) {
				String content = config.getContent();
				AppMetricConfig reportReloadConfig = DefaultSaxParser.parse(content);

				m_config = reportReloadConfig;
				m_modifyTime = modifyTime;
			}
		}
	}

	private boolean storeConfig() {
		synchronized (this) {
			try {
				Config config = m_configDao.createLocal();

				config.setId(m_configId);
				config.setKeyId(m_configId);
				config.setName(CONFIG_NAME);
				config.setContent(m_config.toString());
				m_configDao.updateByPK(config, ConfigEntity.UPDATESET_FULL);
			} catch (Exception e) {
				Cat.logError(e);
				return false;
			}
		}
		return true;
	}

}
