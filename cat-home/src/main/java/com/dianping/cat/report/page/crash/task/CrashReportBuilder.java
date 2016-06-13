package com.dianping.cat.report.page.crash.task;

import java.util.Date;

import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.report.task.TaskBuilder;

@Named(type = TaskBuilder.class, value = CrashReportBuilder.ID)
public class CrashReportBuilder implements TaskBuilder {

	public static final String ID = "CRASH";

	@Override
	public boolean buildDailyTask(String name, String domain, Date period) {
		final String reportName = name;
		final String appId = domain;
		final Date reportPeriod = period;

		Threads.forGroup("cat").start(new Threads.Task() {

			@Override
			public void run() {
				runDailyTask(reportName, appId, reportPeriod);
			}

			@Override
			public void shutdown() {
			}

			@Override
			public String getName() {
				return "crash-report-task-" + appId;
			}
		});

		return true;
	}

	@Override
	public boolean buildHourlyTask(String name, String domain, Date period) {
		throw new RuntimeException("daily report builder don't support hourly task");
	}

	@Override
	public boolean buildMonthlyTask(String name, String domain, Date period) {
		throw new RuntimeException("daily report builder don't support monthly task");
	}

	@Override
	public boolean buildWeeklyTask(String name, String domain, Date period) {
		throw new RuntimeException("daily report builder don't support weekly task");
	}

	public void runDailyTask(String reportName, String appId, Date reportPeriod) {
		// TODO Auto-generated method stub

	}

}
