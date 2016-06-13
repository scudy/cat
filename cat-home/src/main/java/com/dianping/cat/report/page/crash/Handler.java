package com.dianping.cat.report.page.crash;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dianping.cat.helper.JsonBuilder;
import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.page.crash.display.CrashLogDetailInfo;
import com.dianping.cat.report.page.crash.display.CrashLogDisplayInfo;
import com.dianping.cat.report.page.crash.service.CrashLogQueryEntity;
import com.dianping.cat.report.page.crash.service.CrashLogService;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {

	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private CrashLogService m_crashLogService;

	private JsonBuilder m_jsonBuilder = new JsonBuilder();

	private void buildAppCrashGraph(Payload payload, Model model) {
		CrashLogQueryEntity entity = payload.getCrashLogQuery();
		CrashLogDisplayInfo info = m_crashLogService.buildCrashGraph(entity);

		model.setCrashLogDisplayInfo(info);
	}

	private CrashLogDisplayInfo buildAppCrashLog(Payload payload) {
		CrashLogQueryEntity entity = payload.getCrashLogQuery();
		CrashLogDisplayInfo info = m_crashLogService.buildCrashLogDisplayInfo(entity);

		return info;
	}

	private void buildAppCrashLogDetail(Payload payload, Model model) {
		CrashLogDetailInfo info = m_crashLogService.queryCrashLogDetailInfo(payload.getId());

		model.setCrashLogDetailInfo(info);
	}

	private void buildAppCrashTrend(Payload payload, Model model) {
		CrashLogDisplayInfo info = m_crashLogService.buildCrashTrend(payload.getCrashLogTrendQuery1(),
		      payload.getCrashLogTrendQuery2());
		model.setCrashLogDisplayInfo(info);
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "crash")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "crash")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		model.setAction(payload.getAction());
		model.setPage(ReportPage.CRASH);

		switch (action) {
		case APP_CRASH_LOG:
			CrashLogDisplayInfo displayInfo = buildAppCrashLog(payload);

			model.setCrashLogDisplayInfo(displayInfo);
			break;
		case APP_CRASH_LOG_JSON:
			displayInfo = buildAppCrashLog(payload);

			model.setFetchData(m_jsonBuilder.toJson(displayInfo));
			break;
		case APP_CRASH_LOG_DETAIL:
			buildAppCrashLogDetail(payload, model);
			break;
		case APP_CRASH_GRAPH:
			buildAppCrashGraph(payload, model);
			break;
		case APP_CRASH_TREND:
			buildAppCrashTrend(payload, model);
			break;
		}

		if (!ctx.isProcessStopped()) {
			m_jspViewer.view(ctx, model);
		}
	}
}
