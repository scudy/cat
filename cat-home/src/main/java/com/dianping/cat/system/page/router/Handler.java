package com.dianping.cat.system.page.router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.Cat;
import com.dianping.cat.config.sample.SampleConfigManager;
import com.dianping.cat.config.server.ServerFilterConfigManager;
import com.dianping.cat.configuration.property.entity.Property;
import com.dianping.cat.configuration.property.entity.PropertyConfig;
import com.dianping.cat.helper.JsonBuilder;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.home.router.entity.Domain;
import com.dianping.cat.home.router.entity.RouterConfig;
import com.dianping.cat.home.router.entity.Server;
import com.dianping.cat.report.task.TaskBuilder;
import com.dianping.cat.system.page.router.config.KVConfig;
import com.dianping.cat.system.page.router.config.RouterConfigHandler;
import com.dianping.cat.system.page.router.config.RouterConfigManager;
import com.dianping.cat.system.page.router.service.CachedRouterConfigService;
import com.dianping.cat.system.page.router.task.RouterConfigBuilder;

public class Handler implements PageHandler<Context> {

	@Inject
	private CachedRouterConfigService m_cachedReportService;

	@Inject
	private RouterConfigManager m_configManager;

	@Inject
	private SampleConfigManager m_sampleConfigManager;

	@Inject
	private ServerFilterConfigManager m_filterManager;

	@Inject(RouterConfigBuilder.ID)
	private TaskBuilder m_routerConfigBuilder;

	@Inject
	private RouterConfigHandler m_routerConfigHandler;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();

	private Map<String, String> buildKvs(RouterConfig report, String domain, String ip) {
		Map<String, String> kvs = new HashMap<String, String>();
		boolean block = m_configManager.shouldBlock(ip);

		kvs.put("block", String.valueOf(block));
		kvs.put("routers", buildRouterInfo(ip, domain, report));
		kvs.put("sample", String.valueOf(buildSampleInfo(domain, 1.0)));
		kvs.put("startTransactionTypes", m_filterManager.getAtomicStartTypes());
		kvs.put("matchTransactionTypes", m_filterManager.getAtomicMatchTypes());
		return kvs;
	}

	private String buildRouterInfo(String ip, String domain, RouterConfig config) {
		String group = m_configManager.queryServerGroupByIp(ip);
		Domain domainConfig = m_configManager.getRouterConfig().findDomain(domain);
		List<Server> servers = new ArrayList<Server>();

		if (domainConfigNotExist(group, domainConfig)) {
			if (config != null) {
				Domain d = config.findDomain(domain);

				if (d != null && d.findGroup(group) != null) {
					servers = d.findGroup(group).getServers();

					if (servers.isEmpty()) {
						Cat.logError(new RuntimeException("Error when build router config, domain: " + domain));
					}
				}
			}

			if (servers.isEmpty()) {
				servers = m_configManager.queryServersByDomain(group, domain);
			}
		} else {
			servers = domainConfig.findGroup(group).getServers();
		}
		return buildServerStr(servers);
	}

	private double buildSampleInfo(String domain, double defaultValue) {
		com.dianping.cat.sample.entity.Domain domainConfig = m_sampleConfigManager.getConfig().findDomain(domain);

		if (domainConfig != null) {
			defaultValue = domainConfig.getSample();
		}
		return defaultValue;
	}

	private String buildServerStr(List<Server> servers) {
		StringBuilder sb = new StringBuilder();

		for (Server server : servers) {
			sb.append(server.getId()).append(":").append(server.getPort()).append(";");
		}
		return sb.toString();
	}

	private boolean domainConfigNotExist(String group, Domain domainConfig) {
		return domainConfig == null || domainConfig.findGroup(group) == null
		      || domainConfig.findGroup(group).getServers().isEmpty();
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "router")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "router")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();
		RouterConfig report = m_cachedReportService.queryLastRouterConfig();
		String domain = payload.getDomain();
		String ip = payload.getIp();

		switch (action) {
		case API:
			String routerInfo = buildRouterInfo(ip, domain, report);

			model.setContent(routerInfo);
			break;
		case JSON:
			KVConfig config = new KVConfig();
			Map<String, String> kvs = buildKvs(report, domain, ip);

			config.setKvs(kvs);
			model.setContent(m_jsonBuilder.toJson(config));
			break;
		case XML:
			kvs = buildKvs(report, domain, ip);
			PropertyConfig property = new PropertyConfig();

			for (Entry<String, String> entry : kvs.entrySet()) {
				Property p = new Property(entry.getKey());

				p.setValue(entry.getValue());
				property.addProperty(p);
			}
			model.setContent(property.toString());
			break;
		case BUILD:
			Date period = TimeHelper.getCurrentDay(-1);
			boolean ret = m_routerConfigHandler.updateRouterConfig(period);

			model.setContent(String.valueOf(ret));
			break;
		case MODEL:
			if (report != null) {
				model.setContent(report.toString());
			}
			break;
		}

		ctx.getHttpServletResponse().getWriter().write(model.getContent());
	}
}