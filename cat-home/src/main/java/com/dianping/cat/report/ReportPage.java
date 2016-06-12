package com.dianping.cat.report;

import org.unidal.web.mvc.Page;
import org.unidal.web.mvc.annotation.ModuleMeta;

public enum ReportPage implements Page {

   HOME("home", "home", "Home", "Home Page", true),

   PROBLEM("problem", "p", "Problem", "Problem Discovered", true),

   TRANSACTION("transaction", "t", "Transaction", "Transaction Summary Report", true),

   EVENT("event", "e", "Event", "Event Summary Report", true),

   HEARTBEAT("heartbeat", "h", "Heartbeat", "Heartbeat Summary Report", true),

   LOGVIEW("logview", "m", "Logview", "Log View Details", false),

   MODEL("model", "model", "Model", "Service Model", false),

   MATRIX("matrix", "matrix", "Matrix", "Matrix", false),

   CROSS("cross", "cross", "Cross", "Cross", true),

   CACHE("cache", "cache", "Cache", "Cache", true),

   STATE("state", "state", "State", "State", true),

   DEPENDENCY("dependency", "dependency", "Dependency", "Dependency", true),

   STATISTICS("statistics", "statistics", "Statistics", "Statistics", false),

   ALTERATION("alteration", "alteration", "Change", "Alteration", false),

   MONITOR("monitor", "monitor", "Monitor", "Monitor", true),

   NETWORK("network", "network", "Network", "Network", false),

   APP("app", "app", "App", "App", true),

   ALERT("alert", "alert", "Alert", "Alert", true),

   OVERLOAD("overload", "overload", "Overload", "Overload", true),

   STORAGE("storage", "storage", "Storage", "Storage", true),

   TOP("top", "top", "Top", "Top", true),

   BROWSER("browser", "browser", "Browser", "Browser", true),

   SERVER("server", "server", "Server", "Server", true),

   BUSINESS("business", "business", "Business", "Business", true),

   APPSTATS("appstats", "appstats", "Appstats", "Appstats", true);

   private String m_name;

   private String m_path;

   private String m_title;

   private String m_description;

   private boolean m_standalone;

   private ReportPage(String name, String path, String title, String description, boolean standalone) {
      m_name = name;
      m_path = path;
      m_title = title;
      m_description = description;
      m_standalone = standalone;
   }

   public static ReportPage getByName(String name, ReportPage defaultPage) {
      for (ReportPage action : ReportPage.values()) {
         if (action.getName().equals(name)) {
            return action;
         }
      }

      return defaultPage;
   }

   public String getDescription() {
      return m_description;
   }

   public String getModuleName() {
      ModuleMeta meta = ReportModule.class.getAnnotation(ModuleMeta.class);

      if (meta != null) {
         return meta.name();
      } else {
         return null;
      }
   }

   @Override
   public String getName() {
      return m_name;
   }

   @Override
   public String getPath() {
      return m_path;
   }

   public String getTitle() {
      return m_title;
   }

   public boolean isStandalone() {
      return m_standalone;
   }

   public ReportPage[] getValues() {
      return ReportPage.values();
   }
}
