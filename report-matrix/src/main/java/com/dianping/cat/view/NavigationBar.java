package com.dianping.cat.view;

import com.dianping.cat.matrix.report.ReportPage;

import org.unidal.web.mvc.Page;

public class NavigationBar {
   public Page[] getVisiblePages() {
      return new Page[] {
   
      ReportPage.MATRIX

		};
   }
}