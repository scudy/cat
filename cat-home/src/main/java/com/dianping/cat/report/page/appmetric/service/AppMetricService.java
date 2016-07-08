package com.dianping.cat.report.page.appmetric.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.helper.JsonBuilder;

@Named
public class AppMetricService implements LogEnabled {

	private static final String OPEN_TSDB_URL = "http://opentsdb.dx.dabai.vip.sankuai.com/api/query";

	private Logger m_logger;

	public static void main(String[] args) {
		AppMetricService service = new AppMetricService();
		long current = 1467261425;
		MetricQueryEntity query = new MetricQueryEntity(current - 3600, current,
		      "frame_groupby_appId_endPoint_platform_version_Minutely.metric_broker_service", "avg");
		query.addTag("platform", "1");

		service.query(query);
	}

	@SuppressWarnings("unchecked")
	public List<AppMetric> query(MetricQueryEntity query) {
		List<AppMetric> metrics = new LinkedList<AppMetric>();
		String url = OPEN_TSDB_URL + "?" + query.toQueryString();
		String result = sendGet(url);
		JsonBuilder jsonBuilder = new JsonBuilder();

		Map<String, Object>[] datas = (Map<String, Object>[]) jsonBuilder.parse(result, Map[].class);

		for (Map<String, Object> data : datas) {
			String metric = (String) data.get("metric");
			List<String> aggregateTags = (List<String>) data.get("aggregateTags");
			Map<String, Double> rawValues = (Map<String, Double>) data.get("dps");
			Map<Long, Double> values = new LinkedHashMap<Long, Double>();

			for (Entry<String, Double> entry : rawValues.entrySet()) {
				values.put(Long.valueOf(entry.getKey()) * 1000, entry.getValue());
			}

			AppMetric appMetric = new AppMetric(metric, query.getType(), aggregateTags, values);

			metrics.add(appMetric);
		}

		return metrics;
	}

	public String sendGet(String url) {
		String result = "";
		BufferedReader in = null;

		try {
			URL realUrl = new URL(url);
			URLConnection connection = realUrl.openConnection();
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			m_logger.equals(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				m_logger.equals(e2);
			}
		}
		return result;
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}
}
