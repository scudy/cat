package com.dianping.cat.system.page.launch;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.launch.entity.Server;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private LaunchConfigManager m_launConfigManager;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "launch")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "launch")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();
		ClientConfig config = new ClientConfig();
		List<Server> servers = m_launConfigManager.queryServersGroupByIp(payload.getIp());

		for (Server server : servers) {
			config.addServer(new com.dianping.cat.configuration.client.entity.Server(server.getId()));
		}

		ctx.getHttpServletResponse().getWriter().write(config.toString());
	}

}
