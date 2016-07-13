package com.dianping.cat.system.page.launch;

public enum JspFile {
	VIEW("/jsp/system/launch/api.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}
