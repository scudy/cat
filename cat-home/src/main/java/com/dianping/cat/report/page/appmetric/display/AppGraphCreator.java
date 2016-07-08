package com.dianping.cat.report.page.appmetric.display;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;

import com.dianping.cat.Constants;
import com.dianping.cat.app.AppCommandData;
import com.dianping.cat.app.AppDataField;
import com.dianping.cat.command.entity.Code;
import com.dianping.cat.config.app.AppCommandConfigManager;
import com.dianping.cat.config.app.MobileConfigManager;
import com.dianping.cat.config.app.MobileConstants;
import com.dianping.cat.report.graph.LineChart;
import com.dianping.cat.report.page.appmetric.service.AppMetric;
import com.dianping.cat.report.page.appmetric.service.AppMetricService;
import com.dianping.cat.report.page.appmetric.service.MetricQueryEntity;

public class AppGraphCreator {

	@Inject
	private AppMetricService m_AppMetricService;

	@Inject
	private AppCommandConfigManager m_appConfigManager;

	@Inject
	private MobileConfigManager m_mobileConfigManager;

	public LineChart buildChartData(final Map<String, List<AppMetric>> datas) {
		LineChart lineChart = new LineChart();
		lineChart.setId("app");
		lineChart.setUnit("");
		lineChart.setHtmlTitle("自定义App监控");

		for (Entry<String, List<AppMetric>> entry : datas.entrySet()) {
			List<AppMetric> data = entry.getValue();
			if (data.size() > 0) {
				AppMetric metric = data.get(0);

				lineChart.add(entry.getKey(), metric.getValues());
			}
		}
		return lineChart;
	}

	public LineChart buildLineChart(MetricQueryEntity queryEntity1, MetricQueryEntity queryEntity2) {
		Map<String, List<AppMetric>> datas = new LinkedHashMap<String, List<AppMetric>>();

		if (queryEntity1 != null) {
			List<AppMetric> data = m_AppMetricService.query(queryEntity1);

			datas.put(Constants.CURRENT_STR, data);
		}

		if (queryEntity2 != null) {
			List<AppMetric> data = m_AppMetricService.query(queryEntity2);

			datas.put(Constants.COMPARISION_STR, data);
		}
		return buildChartData(datas);
	}

	public Pair<Integer, String> buildPieChartFieldTitlePair(int command, AppCommandData data, AppDataField field) {
		String title = "Unknown";
		int keyValue = -1;

		switch (field) {
		case OPERATOR:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> operators = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.OPERATOR);
			com.dianping.cat.configuration.mobile.entity.Item operator = null;
			keyValue = data.getOperator();

			if (operators != null && (operator = operators.get(keyValue)) != null) {
				title = operator.getValue();
			}
			break;
		case APP_VERSION:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> appVersions = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.VERSION);
			com.dianping.cat.configuration.mobile.entity.Item appVersion = null;
			keyValue = data.getAppVersion();

			if (appVersions != null && (appVersion = appVersions.get(keyValue)) != null) {
				title = appVersion.getValue();
			}
			break;
		case CITY:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> cities = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.CITY);
			com.dianping.cat.configuration.mobile.entity.Item city = null;
			keyValue = data.getCity();

			if (cities != null && (city = cities.get(keyValue)) != null) {
				title = city.getValue();
			}
			break;
		case CONNECT_TYPE:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> connectTypes = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.CONNECT_TYPE);
			com.dianping.cat.configuration.mobile.entity.Item connectType = null;
			keyValue = data.getConnectType();

			if (connectTypes != null && (connectType = connectTypes.get(keyValue)) != null) {
				title = connectType.getValue();
			}
			break;
		case NETWORK:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> networks = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.NETWORK);
			com.dianping.cat.configuration.mobile.entity.Item network = null;
			keyValue = data.getNetwork();

			if (networks != null && (network = networks.get(keyValue)) != null) {
				title = network.getValue();
			}
			break;
		case PLATFORM:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> platforms = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.PLATFORM);
			com.dianping.cat.configuration.mobile.entity.Item platform = null;
			keyValue = data.getPlatform();

			if (platforms != null && (platform = platforms.get(keyValue)) != null) {
				title = platform.getValue();
			}
			break;
		case SOURCE:
			Map<Integer, com.dianping.cat.configuration.mobile.entity.Item> sources = m_mobileConfigManager
			      .queryConstantItem(MobileConstants.SOURCE);
			com.dianping.cat.configuration.mobile.entity.Item source = null;
			keyValue = data.getSource();

			if (sources != null && (source = sources.get(keyValue)) != null) {
				title = source.getValue();
			}
			break;
		case CODE:
			Map<Integer, Code> codes = m_appConfigManager.queryCodeByCommand(command);
			Code code = null;
			keyValue = data.getCode();

			if (codes != null && (code = codes.get(keyValue)) != null) {
				title = code.getName();
				if (code.getNetworkStatus() == 0) {
					title = "<span class='text-success'>【成功】</span>" + title;
				} else {
					title = "<span class='text-error'>【失败】</span>" + title;
				}
			}
			break;
		}
		if ("Unknown".equals(title)) {
			title += " [ " + keyValue + " ]";
		}
		return new Pair<Integer, String>(keyValue, title);
	}
}
