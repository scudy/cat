package com.dianping.cat.report.page.appmetric.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Named;

import com.dianping.cat.Cat;
import com.dianping.cat.helper.JsonBuilder;

@Named
public class AggregationConfigService {

	private static final String URL = "http://data.sankuai.com/dabai/management/db/app/config/upload";

	private static final String METRCI_DOMAIN = "metric_broker_service";

	private static final String TOKEN = "1#@_b!!jqwsuscr5lz8iuztp^yvt28n18!(8#$4%8m$43311%v#";

	@SuppressWarnings("unchecked")
	public boolean update(String metric, String type, String groupbyTags, int time) {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("token=").append(URLEncoder.encode(TOKEN, "utf-8")).append("&");
			sb.append("app_name=").append(URLEncoder.encode(METRCI_DOMAIN, "utf-8")).append("&");
			sb.append("metric=").append(URLEncoder.encode(metric, "utf-8")).append("&");
			sb.append("type=").append(URLEncoder.encode(type, "utf-8")).append("&");
			sb.append("groupby_tags=").append(URLEncoder.encode(groupbyTags, "utf-8")).append("&");
			sb.append("time_granularities=").append(URLEncoder.encode(String.valueOf(time), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			Cat.logError(metric + ":" + type + ":" + groupbyTags, e);
		}
		String result = readFromPost(URL, sb.toString());
		JsonBuilder builder = new JsonBuilder();
		Map<String, Object> ret = (Map<String, Object>) builder.parse(result, Map.class);

		if (ret.containsKey("OK")) {
			double code = (Double) ret.get("OK");

			return code == 0.0;
		} else {
			return false;
		}
	}

	public String readFromPost(String url, String content) {
		String result = "OK";
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		OutputStreamWriter writer = null;
		InputStream in = null;

		try {
			URL postUrl = new URL(url);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setConnectTimeout(500);
			connection.setReadTimeout(5000);

			if (StringUtils.isNotEmpty(content)) {
				writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(content);
				writer.flush();
			}

			result = Files.forIO().readFrom(connection.getInputStream(), "utf-8");
		} catch (Exception e) {
			return e.toString();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

}
