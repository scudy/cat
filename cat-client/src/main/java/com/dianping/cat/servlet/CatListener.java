package com.dianping.cat.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dianping.cat.Cat;

public class CatListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Cat.destroy();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}
}
