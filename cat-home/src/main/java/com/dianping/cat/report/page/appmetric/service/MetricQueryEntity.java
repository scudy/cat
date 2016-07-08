package com.dianping.cat.report.page.appmetric.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.helper.Splitters;
import org.unidal.lookup.util.StringUtils;

import com.dianping.cat.Cat;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.report.page.appmetric.AppMetricConstants;
import com.dianping.cat.report.page.appmetric.display.MetricType;

public class MetricQueryEntity {

	private long m_start;

	private long m_end;

	private String m_metric;

	private MetricType m_type;

	private Map<String, String> m_tags = new HashMap<String, String>();

	public MetricQueryEntity(long start, long end, String metric, String type) {
		m_start = start;
		m_end = end;
		m_metric = metric;
		m_type = MetricType.getByName(type, MetricType.AVG);
	}

	// start;end;metric;aggregation;appId;version;platform;t1:v1;t2:v2..
	public MetricQueryEntity(String query) {
		List<String> fields = Splitters.by(";").split(query);
		int size = fields.size();

		try {
			String startStr = fields.get(0);
			m_start = StringUtils.isEmpty(startStr) ? TimeHelper.getCurrentHour(-2).getTime() : Long.parseLong(startStr);

			String endStr = fields.get(1);
			m_end = StringUtils.isEmpty(endStr) ? TimeHelper.getCurrentHour(1).getTime() : Long.parseLong(endStr);

			m_metric = fields.get(2);
			m_type = MetricType.getByName(fields.get(3), MetricType.AVG);
			String appId = fields.get(4);
			String version = fields.get(5);
			String platform = fields.get(6);

			if (StringUtils.isNotEmpty(appId)) {
				m_tags.put("appId", appId);
			}

			if (StringUtils.isNotEmpty(version)) {
				m_tags.put("version", version);
			}

			if (StringUtils.isNotEmpty(platform)) {
				m_tags.put("platform", platform);
			}

			for (int i = 7; i < size; i++) {
				String kvs = fields.get(i);
				String[] values = kvs.split(":");
				String value = values[1];

				if (StringUtils.isNotEmpty(value)) {
					m_tags.put(values[0], value);
				}
			}
		} catch (Exception e) {
			Cat.logError(e);
		}
	}

	public void addTag(String key, String value) {
		m_tags.put(key, value);
	}

	public long getEnd() {
		return m_end;
	}

	public String getMetric() {
		return m_metric;
	}

	public long getStart() {
		return m_start;
	}

	public Map<String, String> getTags() {
		return m_tags;
	}

	public MetricType getType() {
		return m_type;
	}

	public void setAggregation(MetricType type) {
		m_type = type;
	}

	public void setEnd(int end) {
		m_end = end;
	}

	public void setMetric(String metric) {
		m_metric = metric;
	}

	public void setStart(int start) {
		m_start = start;
	}

	public void setTags(Map<String, String> tags) {
		m_tags = tags;
	}

	public String toQueryString() {
		StringBuilder sb = new StringBuilder();

		sb.append("start=").append(m_start).append("&");

		if (m_end > 0) {
			sb.append("end=").append(m_end).append("&");
		}

		sb.append("m=").append(m_type.getName()).append(":").append(m_metric).append(".")
		      .append(AppMetricConstants.METRIC_DOMAIN);

		if (m_tags.size() > 0) {
			sb.append("{");

			for (Entry<String, String> entry : m_tags.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");
		}

		return sb.toString();
	}
}
