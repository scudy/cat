package com.dianping.cat.report.page.appmetric;

public enum JspFile {
	VIEW("/jsp/report/appmetric/linechart.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}
