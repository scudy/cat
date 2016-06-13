package com.dianping.cat.report.page.crash.service;

import java.util.Date;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.config.app.MobileConfigManager;
import com.dianping.cat.home.app.entity.AppReport;
import com.dianping.cat.report.app.AbstractAppReportService;

@Named
public class CrashStatisticReportService extends AbstractAppReportService<AppReport> {

	@Inject
	private MobileConfigManager m_mobileConfigManager;
	
	@Override
   public AppReport makeReport(String domain, Date start, Date end) {
	   // TODO Auto-generated method stub
	   return null;
   }

	@Override
   public AppReport queryDailyReport(int namespace, Date start, Date end) {
	   // TODO Auto-generated method stub
	   return null;
   }

}
