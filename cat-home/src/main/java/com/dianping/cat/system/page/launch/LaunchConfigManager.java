package com.dianping.cat.system.page.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.config.content.ContentFetcher;
import com.dianping.cat.configuration.launch.entity.LaunchConfig;
import com.dianping.cat.configuration.launch.entity.Network;
import com.dianping.cat.configuration.launch.entity.Server;
import com.dianping.cat.configuration.launch.entity.ServerGroup;
import com.dianping.cat.configuration.launch.transform.DefaultSaxParser;
import com.dianping.cat.core.config.Config;
import com.dianping.cat.core.config.ConfigDao;
import com.dianping.cat.core.config.ConfigEntity;
import com.dianping.cat.task.TimerSyncTask;
import com.dianping.cat.task.TimerSyncTask.SyncHandler;

public class LaunchConfigManager implements Initializable {

	@Inject
	protected ConfigDao m_configDao;

	@Inject
	protected ContentFetcher m_fetcher;

	private int m_configId;

	private long m_modifyTime;

	private LaunchConfig m_config;

	private static final String CONFIG_NAME = "launch-config";

	private static final String DEFAULT = "default";

	private Map<String, List<SubnetInfo>> m_subNetInfos = new HashMap<String, List<SubnetInfo>>();

	private Map<String, String> m_ipToGroupInfo = new LinkedHashMap<String, String>() {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, String> eldest) {
			return size() > 5000;
		}

	};

	public LaunchConfig getConfig() {
		return m_config;
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
			m_config = new LaunchConfig();
		}

		refreshNetInfo();

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

	private String queryGroupBySubnet(String ip) {
		for (Entry<String, List<SubnetInfo>> entry : m_subNetInfos.entrySet()) {
			List<SubnetInfo> subnetInfos = entry.getValue();
			String group = entry.getKey();

			for (SubnetInfo info : subnetInfos) {
				try {
					if (info.isInRange(ip)) {
						return group;
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}
		return null;
	}

	public List<Server> queryServersGroupByIp(String ip) {
		String group = m_ipToGroupInfo.get(ip);

		if (group == null) {
			group = queryGroupBySubnet(ip);

			if (group == null) {
				group = DEFAULT;
			}

			m_ipToGroupInfo.put(ip, group);
		}

		ServerGroup serverGroup = m_config.findServerGroup(group);

		if (serverGroup != null && m_config.getServerGroups().containsKey(group)) {
			return new ArrayList<Server>(m_config.findServerGroup(group).getServers().values());
		} else {
			return Collections.emptyList();
		}
	}

	private void refreshConfig() throws Exception {
		Config config = m_configDao.findByName(CONFIG_NAME, ConfigEntity.READSET_FULL);
		long modifyTime = config.getModifyDate().getTime();

		synchronized (this) {
			if (modifyTime > m_modifyTime) {
				String content = config.getContent();
				LaunchConfig launchConfig = DefaultSaxParser.parse(content);

				m_config = launchConfig;
				m_modifyTime = modifyTime;
				refreshNetInfo();
			}
		}
	}

	private void refreshNetInfo() {
		Map<String, List<SubnetInfo>> subNetInfos = new HashMap<String, List<SubnetInfo>>();

		for (Entry<String, ServerGroup> netPolicy : m_config.getServerGroups().entrySet()) {
			ArrayList<SubnetInfo> infos = new ArrayList<SubnetInfo>();

			if (!DEFAULT.equals(netPolicy.getKey())) {

				for (Entry<String, Network> entry : netPolicy.getValue().getNetworks().entrySet()) {
					try {
						SubnetUtils subnetUtils = new SubnetUtils(entry.getValue().getId());
						SubnetInfo netInfo = subnetUtils.getInfo();

						infos.add(netInfo);
					} catch (Exception e) {
						Cat.logError(e);
					}
				}
				subNetInfos.put(netPolicy.getKey(), infos);
			}
		}

		m_subNetInfos = subNetInfos;
		m_ipToGroupInfo.clear();
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
