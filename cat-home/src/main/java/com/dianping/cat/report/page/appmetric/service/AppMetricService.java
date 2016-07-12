package com.dianping.cat.report.page.appmetric.service;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.helper.Files;
import org.unidal.helper.Urls;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.helper.JsonBuilder;

@Named
public class AppMetricService {
	
	public static final String METRIC_DOMAIN = "metric_broker_service";


	private static final String OPEN_TSDB_URL = "http://opentsdb.dx.dabai.vip.sankuai.com/api/query";

	@SuppressWarnings("unchecked")
	public List<AppMetric> query(MetricQueryEntity query) {
		List<AppMetric> metrics = new LinkedList<AppMetric>();
		String url = OPEN_TSDB_URL + "?" + query.toQueryString();

		try {
			InputStream is = Urls.forIO().connectTimeout(500).readTimeout(5000).openStream(url);
			String result = Files.forIO().readFrom(is, "utf-8");
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
		} catch (Exception e) {
			Cat.logError(e);
		}

		return metrics;
	}

}
