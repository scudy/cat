package com.dianping.cat.report.page.appmetric;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.command.entity.Command;
import com.dianping.cat.config.app.AppCommandConfigManager;
import com.dianping.cat.config.app.AppMetricConfigManager;
import com.dianping.cat.config.app.MobileConfigManager;
import com.dianping.cat.config.app.MobileConstants;
import com.dianping.cat.configuration.mobile.entity.Item;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.graph.LineChart;
import com.dianping.cat.report.page.appmetric.display.AppGraphCreator;
import com.dianping.cat.report.page.appmetric.service.MetricQueryEntity;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private AppGraphCreator m_appGraphCreator;

	@Inject
	private MobileConfigManager m_mobileConfigManager;

	@Inject
	private AppCommandConfigManager m_appCommandConfigManager;

	@Inject
	private AppMetricConfigManager m_appMetricConfigManager;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "appmetric")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	private MetricQueryEntity buildMetricQuery(String query, boolean createDefault) {

		if (StringUtils.isNotEmpty(query)) {
			return new MetricQueryEntity(query);
		} else if (createDefault) {
			String id = m_appMetricConfigManager.getConfig().getAppMetrics().values().iterator().next().getId();
			MetricQueryEntity entity = new MetricQueryEntity();

			entity.setMetric(id);
			return entity;
		} else {
			return null;
		}
	}

	@Override
	@OutboundActionMeta(name = "appmetric")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		normalize(payload, model);

		switch (action) {
		case VIEW:
			MetricQueryEntity query1 = buildMetricQuery(payload.getQuery1(), true);
			MetricQueryEntity query2 = buildMetricQuery(payload.getQuery2(), false);
			LineChart linechart = m_appGraphCreator.buildLineChart(query1, query2);

			model.setLineChart(linechart);
			break;
		}

		model.setAction(action);
		model.setPage(ReportPage.APPMETRIC);

		if (!ctx.isProcessStopped()) {
			m_jspViewer.view(ctx, model);
		}
	}

	private void normalize(Payload payload, Model model) {
		model.setApps(buildApps());
		model.setPlatforms(m_mobileConfigManager.queryConstantItem(MobileConstants.PLATFORM));
		model.setVersions(m_mobileConfigManager.queryConstantItem(MobileConstants.VERSION));
		model.setCommands(m_appCommandConfigManager.queryCommands());
		model.setAppMetrics(m_appMetricConfigManager.getConfig().getAppMetrics());
	}

	private Map<Integer, Item> buildApps() {
		Map<Integer, Item> apps = new HashMap<Integer, Item>();
		Map<Integer, Item> sources = m_mobileConfigManager.queryConstantItem(MobileConstants.SOURCE);
		Map<String, List<Command>> namespaces = m_appCommandConfigManager.queryNamespace2Commands();

		for (Entry<Integer, Item> entry : sources.entrySet()) {
			String namespace = entry.getValue().getValue();

			if (namespaces.containsKey(namespace)) {
				apps.put(entry.getKey(), entry.getValue());
			}
		}
		return apps;
	}

}
