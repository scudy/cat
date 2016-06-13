package com.dianping.cat.report.page.crash;

import org.unidal.web.mvc.view.annotation.ModelMeta;

import com.dianping.cat.mvc.AbstractReportModel;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.page.crash.display.CrashLogDetailInfo;
import com.dianping.cat.report.page.crash.display.CrashLogDisplayInfo;

@ModelMeta("crash")
public class Model extends AbstractReportModel<Action, ReportPage, Context> {

	private CrashLogDetailInfo m_crashLogDetailInfo;

	private CrashLogDisplayInfo m_crashLogDisplayInfo;

	private String m_fetchData;

	public Model(Context ctx) {
		super(ctx);
	}

	public CrashLogDetailInfo getCrashLogDetailInfo() {
		return m_crashLogDetailInfo;
	}

	public CrashLogDisplayInfo getCrashLogDisplayInfo() {
		return m_crashLogDisplayInfo;
	}

	public String getFetchData() {
		return m_fetchData;
	}

	public void setCrashLogDetailInfo(CrashLogDetailInfo crashLogDetailInfo) {
		m_crashLogDetailInfo = crashLogDetailInfo;
	}

	public void setCrashLogDisplayInfo(CrashLogDisplayInfo crashLogDisplayInfo) {
		m_crashLogDisplayInfo = crashLogDisplayInfo;
	}

	public void setFetchData(String fetchData) {
		m_fetchData = fetchData;
	}

	@Override
	public String getDomain() {
		return getDisplayDomain();
	}

	@Override
	public Action getDefaultAction() {
		return Action.APP_CRASH_LOG;
	}
}
