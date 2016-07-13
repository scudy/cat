package com.dianping.cat.system.page.launch;

import org.unidal.web.mvc.ViewModel;

import com.dianping.cat.system.SystemPage;

public class Model extends ViewModel<SystemPage, Action, Context> {

	private String m_content;

	public Model(Context ctx) {
		super(ctx);
	}

	public String getContent() {
		return m_content;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public void setContent(String content) {
		m_content = content;
	}
}
