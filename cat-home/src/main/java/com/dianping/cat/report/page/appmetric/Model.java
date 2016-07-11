package com.dianping.cat.report.page.appmetric;

import java.util.Map;

import org.unidal.web.mvc.view.annotation.EntityMeta;
import org.unidal.web.mvc.view.annotation.ModelMeta;

import com.dianping.cat.command.entity.Command;
import com.dianping.cat.configuration.app.metric.entity.AppMetric;
import com.dianping.cat.configuration.mobile.entity.Item;
import com.dianping.cat.mvc.AbstractReportModel;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.graph.LineChart;

@ModelMeta("appmetric")
public class Model extends AbstractReportModel<Action, ReportPage, Context> {

	@EntityMeta
	private LineChart m_lineChart;

	private Map<Integer, Item> m_platforms;

	private Map<Integer, Item> m_versions;

	private Map<Integer, Item> m_apps;

	private Map<Integer, Command> m_commands;

	private Map<String, AppMetric> m_appMetrics;

	public Model(Context ctx) {
		super(ctx);
	}

	public Map<String, AppMetric> getAppMetrics() {
		return m_appMetrics;
	}

	public Map<Integer, Item> getApps() {
		return m_apps;
	}

	public Map<Integer, Command> getCommands() {
		return m_commands;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	@Override
	public String getDomain() {
		return getDisplayDomain();
	}

	public LineChart getLineChart() {
		return m_lineChart;
	}

	public Map<Integer, Item> getPlatforms() {
		return m_platforms;
	}

	public Map<Integer, Item> getVersions() {
		return m_versions;
	}

	public void setAppMetrics(Map<String, AppMetric> appMetrics) {
		m_appMetrics = appMetrics;
	}

	public void setApps(Map<Integer, Item> apps) {
		m_apps = apps;
	}

	public void setCommands(Map<Integer, Command> commands) {
		m_commands = commands;
	}

	public void setLineChart(LineChart lineChart) {
		m_lineChart = lineChart;
	}

	public void setPlatforms(Map<Integer, Item> platforms) {
		m_platforms = platforms;
	}

	public void setVersions(Map<Integer, Item> versions) {
		m_versions = versions;
	}

}
