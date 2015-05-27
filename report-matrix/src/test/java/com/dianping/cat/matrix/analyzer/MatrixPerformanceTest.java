package com.dianping.cat.matrix.analyzer;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Constants;
import com.dianping.cat.analysis.MessageAnalyzer;
import com.dianping.cat.matrix.model.entity.MatrixReport;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.internal.MockMessageBuilder;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import com.dianping.cat.report.DefaultReportManager.StoragePolicy;
import com.dianping.cat.report.ReportDelegate;
import com.dianping.cat.report.ReportManager;

public class MatrixPerformanceTest extends ComponentTestCase {
	@Before
	public void before() throws Exception {
		defineComponent(ReportManager.class, MatrixAnalyzer.ID, MockMatrixReportManager.class) //
		      .req(ReportDelegate.class, MatrixAnalyzer.ID);
		defineComponent(ReportDelegate.class, MatrixAnalyzer.ID, MatrixDelegate.class);
	}

	public MessageTree buildMessage() {
		Message message = new MockMessageBuilder() {
			@Override
			public MessageHolder define() {
				TransactionHolder t = t("URL", "GET", 112819)
				      //
				      .at(1348374838231L)
				      //
				      .after(1300)
				      .child(t("QUICKIE SERVICE", "gimme_stuff", 1571))
				      //
				      .after(100)
				      .child(e("SERVICE", "event1"))
				      //
				      .after(100).child(e("Excetion", "NullPointException")).after(100)
				      .child(e("Excetion", "NullPointException1")).after(100).child(e("Excetion", "NullPointException2"))
				      .after(100).child(e("Excetion", "NullPointException3")).after(100).child(h("SERVICE", "heartbeat1")) //
				      .after(100).child(t("Cache.mem", "GET", 109358) //
				            .after(1000).child(t("SOME SERVICE", "get", 4345) //
				                  .after(4000).child(t("MEMCACHED", "Get", 279))) //
				            .mark().after(200).child(t("MEMCACHED", "Inc", 319)) //
				            .reset().after(500).child(t("BIG ASS SERVICE", "getThemDatar", 97155) //
				                  .after(1000).mark().child(t("SERVICE", "getStuff", 3760)) //
				                  .reset().child(t("DATAR", "findThings", 94537)) //
				                  .after(200).child(t("THINGIE", "getMoar", 1435)) //
				            ) //
				            .after(100).mark().child(t("Call", "get", 4394) //
				                  .after(1000).mark().child(t("MEMCACHED", "Get", 378)) //
				                  .reset().child(t("MEMCACHED", "Get", 3496)) //
				            ) //
				            .reset().child(t("Service", "get", 4394) //
				                  .after(1000).mark().child(t("MEMCACHED", "Get", 386)) //
				                  .reset().child(t("MEMCACHED", "Get", 322)) //
				                  .reset().child(t("MEMCACHED", "Get", 322)) //
				            ) //
				      ) //
				;

				return t;
			}
		}.build();

		MessageTree tree = new DefaultMessageTree();
		tree.setDomain("cat");
		tree.setHostName("test");
		tree.setIpAddress("test");
		tree.setThreadGroupName("test");
		tree.setThreadId("test");
		tree.setThreadName("test");
		tree.setMessage(message);
		return tree;
	}

	@Test
	public void test() throws Exception {
		MatrixAnalyzer analyzer = (MatrixAnalyzer) lookup(MessageAnalyzer.class, MatrixAnalyzer.ID);
		MessageTree tree = buildMessage();

		long current = System.currentTimeMillis();

		int size = 10000000;
		for (int i = 0; i < size; i++) {
			analyzer.process(tree);
		}
		System.out.println(analyzer.getReport("cat"));
		System.out.println("Cost " + (System.currentTimeMillis() - current) / 1000);
		// 21
	}

	public static class MockMatrixReportManager extends MockReportManager<MatrixReport> {
		private MatrixReport m_report;

		@Inject
		private ReportDelegate<MatrixReport> m_delegate;

		@Override
		public MatrixReport getHourlyReport(long startTime, String domain, boolean createIfNotExist) {
			if (m_report == null) {
				m_report = (MatrixReport) m_delegate.makeReport(domain, startTime, Constants.HOUR);
			}

			return m_report;
		}

		public void setReport(MatrixReport report) {
			m_report = report;
		}

		@Override
      public void destory() {
      }

		@Override
      public Map<String, MatrixReport> loadHourlyReports(long startTime, StoragePolicy policy, int index) {
	      return null;
      }

		@Override
      public void storeHourlyReports(long startTime, StoragePolicy policy, int index) {
      }
	}
}