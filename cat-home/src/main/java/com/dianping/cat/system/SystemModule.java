package com.dianping.cat.system;

import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "s", defaultInboundAction = "config", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({

com.dianping.cat.system.page.login.Handler.class,

com.dianping.cat.system.page.config.Handler.class,

com.dianping.cat.system.page.plugin.Handler.class,

com.dianping.cat.system.page.router.Handler.class,

com.dianping.cat.system.page.web.Handler.class,

com.dianping.cat.system.page.project.Handler.class,

com.dianping.cat.system.page.app.Handler.class,

com.dianping.cat.system.page.business.Handler.class,

com.dianping.cat.system.page.permission.Handler.class,

com.dianping.cat.system.page.launch.Handler.class
})
public class SystemModule extends AbstractModule {

}
