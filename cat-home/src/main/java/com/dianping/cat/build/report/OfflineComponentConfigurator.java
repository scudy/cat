package com.dianping.cat.build.report;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.report.page.browser.task.WebDatabasePruner;
import com.dianping.cat.report.page.server.task.MetricGraphPruner;
import com.dianping.cat.report.task.cmdb.CmdbInfoReloadBuilder;
import com.dianping.cat.report.task.current.CurrentReportBuilder;

public class OfflineComponentConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(A(CurrentReportBuilder.class));
		all.add(A(CmdbInfoReloadBuilder.class));

		all.add(A(WebDatabasePruner.class));
		all.add(A(MetricGraphPruner.class));

		return all;
	}
}
