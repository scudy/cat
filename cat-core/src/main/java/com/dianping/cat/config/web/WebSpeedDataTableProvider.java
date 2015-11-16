package com.dianping.cat.config.web;

import java.util.Map;

import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.mapping.TableProvider;

import com.dianping.cat.web.WebSpeedData;

public class WebSpeedDataTableProvider implements TableProvider {
	private String m_logicalTableName = "web-speed-data";

	private String m_physicalTableName = "web_speed_data";

	private String m_dataSourceName = "web";

	@Override
	public String getDataSourceName(Map<String, Object> hints) {
		return m_dataSourceName;
	}

	@Override
	public String getLogicalTableName() {
		return m_logicalTableName;
	}

	@Override
	public String getPhysicalTableName(Map<String, Object> hints) {
		WebSpeedData webSpeedData = (WebSpeedData) hints.get(QueryEngine.HINT_DATA_OBJECT);

		return m_physicalTableName + "_" + webSpeedData.getSpeedId();
	}

	public void setDataSourceName(String dataSourceName) {
		m_dataSourceName = dataSourceName;
	}

	public void setLogicalTableName(String logicalTableName) {
		m_logicalTableName = logicalTableName;
	}
}
