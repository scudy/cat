package com.dianping.cat.report.page.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Pair;

import com.dianping.cat.Constants;
import com.dianping.cat.configuration.ServerConfigManager;
import com.dianping.cat.consumer.state.model.entity.StateReport;
import com.dianping.cat.helper.TimeHelper;
import com.dianping.cat.report.page.LineChart;
import com.dianping.cat.report.page.PieChart;
import com.dianping.cat.report.page.PieChart.Item;
import com.dianping.cat.report.service.ReportServiceManager;

public class StateGraphBuilder {

	@Inject
	private ReportServiceManager m_reportService;

	@Inject
	private ServerConfigManager m_configManager;

	public Pair<LineChart, PieChart> buildGraph(Payload payload, String key, StateReport report) {
		String domain = payload.getDomain();
		String ips = payload.getIpAddress();

		return buildHourlyGraph(report, domain, key, ips);
	}

	public Pair<LineChart, PieChart> buildGraph(Payload payload, String key) {
		String domain = payload.getDomain();
		Date start = payload.getHistoryStartDate();
		Date end = payload.getHistoryEndDate();
		String ips = payload.getIpAddress();

		return buildHistoryGraph(domain, start, end, key, ips);
	}

	private Pair<LineChart, PieChart> buildHistoryGraph(String domain, Date start, Date end, String key, String ip) {
		List<StateReport> reports = new ArrayList<StateReport>();
		StateHistoryGraphVisitor builder = new StateHistoryGraphVisitor(ip, m_configManager.getUnusedDomains(),
		      start.getTime(), end.getTime(), key);
		StateDistirbutionVisitor visitor = new StateDistirbutionVisitor(key);
		long step;

		if (end.getTime() - start.getTime() <= TimeHelper.ONE_DAY) {
			step = TimeHelper.ONE_HOUR;
		} else {
			step = TimeHelper.ONE_DAY;
		}
		for (long date = start.getTime(); date < end.getTime(); date += step) {
			StateReport report = m_reportService.queryStateReport(domain, new Date(date), new Date(date + step));

			report.accept(builder);
			report.accept(visitor);
		}
		int size = reports.size();
		LineChart linechart = new LineChart();

		linechart.setStart(start).setSize(size).setTitle(key).setStep(step);
		linechart.addSubTitle(key);
		linechart.addValue(builder.getData());

		PieChart piechart = buildPiechart(visitor.getDistribute());

		return new Pair<LineChart, PieChart>(linechart, piechart);
	}

	private Pair<LineChart, PieChart> buildHourlyGraph(StateReport report, String domain, String key, String ip) {
		LineChart linechart = new LineChart();

		if (key.startsWith(Constants.ALL)) {
			StateDisplay display = new StateDisplay(ip, m_configManager.getUnusedDomains());

			report.accept(display);
			report = display.getStateReport();
		}
		StateHourlyGraphVisitor builder = new StateHourlyGraphVisitor(ip, m_configManager.getUnusedDomains(), key, 60);

		report.accept(builder);
		linechart.setStart(report.getStartTime()).setSize(60).setTitle(key).setStep(TimeHelper.ONE_MINUTE);
		linechart.add(key, builder.getData());

		StateDistirbutionVisitor visitor = new StateDistirbutionVisitor(key);
		report.accept(visitor);
		Map<String, Double> distributes = visitor.getDistribute();
		PieChart piechart = buildPiechart(distributes);

		return new Pair<LineChart, PieChart>(linechart, piechart);
	}

	private PieChart buildPiechart(Map<String, Double> distributes) {
		PieChart chart = new PieChart();
		List<Item> items = new ArrayList<Item>();

		for (Entry<String, Double> entry : distributes.entrySet()) {
			Item item = new Item();

			item.setTitle(entry.getKey()).setNumber(entry.getValue());
			items.add(item);
		}

		chart.addItems(items);
		return chart;
	}
}