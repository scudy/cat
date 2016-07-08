package com.dianping.cat.report.page.appmetric.service;

import java.util.List;
import java.util.Map;

import com.dianping.cat.report.page.appmetric.display.MetricType;

public class AppMetric {

	private String m_metric;

	private MetricType m_type;

	private List<String> m_tags;

	private Map<Long, Double> m_values;

	public AppMetric(String metric, MetricType type, List<String> tags, Map<Long, Double> values) {
		m_metric = metric;
		m_type = type;
		m_tags = tags;
		m_values = values;
	}

	public String getMetric() {
		return m_metric;
	}

	public List<String> getTags() {
		return m_tags;
	}

	public MetricType getType() {
		return m_type;
	}

	public Map<Long, Double> getValues() {
		return m_values;
	}

	public void setMetric(String metric) {
		m_metric = metric;
	}

	public void setTags(List<String> tags) {
		m_tags = tags;
	}

	public void setValues(Map<Long, Double> values) {
		m_values = values;
	}

}
