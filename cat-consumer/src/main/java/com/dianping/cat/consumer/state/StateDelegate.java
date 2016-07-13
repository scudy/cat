package com.dianping.cat.consumer.state;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Constants;
import com.dianping.cat.config.app.MobileConfigManager;
import com.dianping.cat.configuration.mobile.entity.Item;
import com.dianping.cat.consumer.state.model.entity.StateReport;
import com.dianping.cat.consumer.state.model.transform.DefaultNativeBuilder;
import com.dianping.cat.consumer.state.model.transform.DefaultNativeParser;
import com.dianping.cat.consumer.state.model.transform.DefaultSaxParser;
import com.dianping.cat.report.ReportBucketManager;
import com.dianping.cat.report.ReportDelegate;
import com.dianping.cat.task.TaskManager;
import com.dianping.cat.task.TaskManager.TaskProlicy;

@Named(type = ReportDelegate.class, value = StateAnalyzer.ID)
public class StateDelegate implements ReportDelegate<StateReport> {

	@Inject
	private TaskManager m_taskManager;

	@Inject
	private ReportBucketManager m_bucketManager;

	@Inject
	private MobileConfigManager m_mobileConfigManager;

	@Override
	public void afterLoad(Map<String, StateReport> reports) {
	}

	@Override
	public void beforeSave(Map<String, StateReport> reports) {
	}

	@Override
	public byte[] buildBinary(StateReport report) {
		return DefaultNativeBuilder.build(report);
	}

	@Override
	public String buildXml(StateReport report) {
		return report.toString();
	}

	@Override
	public boolean createHourlyTask(StateReport report) {
		Date startTime = report.getStartTime();
		String domain = report.getDomain();

		m_taskManager.createTask(startTime, domain, StateAnalyzer.ID, TaskProlicy.ALL);
		m_taskManager.createTask(startTime, domain, Constants.REPORT_ROUTER, TaskProlicy.HOULY);
		m_taskManager.createTask(startTime, domain, Constants.APP_DATABASE_PRUNER, TaskProlicy.DAILY);
		m_taskManager.createTask(startTime, domain, Constants.METRIC_GRAPH_PRUNER, TaskProlicy.DAILY);
		m_taskManager.createTask(startTime, domain, Constants.WEB_DATABASE_PRUNER, TaskProlicy.DAILY);
		m_taskManager.createTask(startTime, domain, Constants.CMDB, TaskProlicy.HOULY);
		m_taskManager.createTask(startTime, domain, Constants.REPORT_JAR, TaskProlicy.HOULY);
		m_taskManager.createTask(startTime, domain, Constants.REPORT_HEAVY, TaskProlicy.ALL);
		m_taskManager.createTask(startTime, domain, Constants.REPORT_UTILIZATION, TaskProlicy.ALL);
		m_taskManager.createTask(startTime, domain, Constants.REPORT_SERVICE, TaskProlicy.ALL);

		for (Item app : m_mobileConfigManager.queryApps()) {
			m_taskManager.createTask(startTime, app.getValue(), Constants.APP, TaskProlicy.DAILY);
			m_taskManager.createTask(startTime, app.getValue(), Constants.CRASH, TaskProlicy.DAILY);
		}

		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);

		// for daily report aggreation done
		if (hour >= 4) {
			m_taskManager.createTask(startTime, domain, Constants.CURRENT_REPORT, TaskProlicy.DAILY);
			m_taskManager.createTask(startTime, domain, Constants.REPORT_CLIENT, TaskProlicy.DAILY);
		}
		// clear local report
		m_bucketManager.clearOldReports();
		return true;
	}

	@Override
	public String getDomain(StateReport report) {
		return report.getDomain();
	}

	@Override
	public StateReport makeReport(String domain, long startTime, long duration) {
		StateReport report = new StateReport(domain);

		report.setStartTime(new Date(startTime));
		report.setEndTime(new Date(startTime + duration - 1));

		return report;
	}

	@Override
	public StateReport mergeReport(StateReport old, StateReport other) {
		StateReportMerger merger = new StateReportMerger(old);

		other.accept(merger);
		return old;
	}

	@Override
	public StateReport parseBinary(byte[] bytes) {
		return DefaultNativeParser.parse(bytes);
	}

	@Override
	public StateReport parseXml(String xml) throws Exception {
		StateReport report = DefaultSaxParser.parse(xml);

		return report;
	}
}
