package com.dianping.cat.report.page.appmetric.service;

import java.text.SimpleDateFormat;
import java.util.Date;
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

	private Date m_start;

	private Date m_end;

	private String m_metric;

	private MetricType m_type = MetricType.AVG;

	private Map<String, String> m_tags = new HashMap<String, String>();

	public MetricQueryEntity(Date start, Date end, String metric, String type) {
		m_start = start;
		m_end = end;
		m_metric = metric;
		m_type = MetricType.getByName(type, MetricType.AVG);
	}

	public MetricQueryEntity() {
		m_start = TimeHelper.getCurrentHour(-2);
		m_end = TimeHelper.getCurrentHour(1);
	}

	// start;end;metric;aggregation;appId;version;platform;t1:v1;t2:v2..
	public MetricQueryEntity(String query) {
		List<String> fields = Splitters.by(";").split(query);
		int size = fields.size();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String startStr = fields.get(0);

			if (StringUtils.isNotEmpty(startStr)) {
				m_start = sdf.parse(startStr);
			} else {
				m_start = TimeHelper.getCurrentHour(-2);
			}

			String endStr = fields.get(1);

			if (StringUtils.isNotEmpty(endStr)) {
				m_end = sdf.parse(endStr);
			} else {
				m_end = TimeHelper.getCurrentHour();
			}

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

	public Date getEnd() {
		return m_end;
	}

	public String getMetric() {
		return m_metric;
	}

	public Date getStart() {
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

	public void setEnd(Date end) {
		m_end = end;
	}

	public void setMetric(String metric) {
		m_metric = metric;
	}

	public void setStart(Date start) {
		m_start = start;
	}

	public void setTags(Map<String, String> tags) {
		m_tags = tags;
	}

	public String toQueryString() {
		StringBuilder sb = new StringBuilder();

		sb.append("start=").append(m_start.getTime()).append("&");

		sb.append("end=").append(m_end.getTime()).append("&");

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
