package com.dianping.cat.config.app;

import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.mapping.TableProvider;
import org.unidal.lookup.annotation.Named;

@Named(type = TableProvider.class, value = CrashPercentTableProvider.LOGIC_TABLE_NAME)
public class CrashPercentTableProvider implements TableProvider, Initializable {

	public final static String LOGIC_TABLE_NAME = "crash-percent";

	private String m_physicalTableName = "crash_percent";

	private String m_newDataSource = "app_crash";

	@Override
	public void initialize() throws InitializationException {
	}

	@Override
	public String getLogicalTableName() {
		return LOGIC_TABLE_NAME;
	}

	@Override
	public String getDataSourceName(Map<String, Object> hints) {
		return m_newDataSource;
	}

	@Override
	public String getPhysicalTableName(Map<String, Object> hints) {
		return m_physicalTableName;
	}

}
