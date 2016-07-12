<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>

<jsp:useBean id="ctx" type="com.dianping.cat.system.page.app.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.cat.system.page.app.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.cat.system.page.app.Model" scope="request"/>

<a:mobile>
	<res:useJs value="${res.js.local['alarm_js']}" target="head-js" />
	<script type="text/javascript">
		$(document).ready(function() {
			$('#userMonitor_config').addClass('active open');
			$('#appMetrics').addClass('active');
			
			var tags = "";
			
			<c:forEach var="tag" items="${model.appMetric.tags }">
				tags += "${tag.id},";
			</c:forEach>
			tags = tags.substring(0, tags.length - 1);
			$('#tags').val(tags);
		});
		
		$(document).delegate('#updateSubmit', 'click', function(e){
			var id = $("#metricId").val();
			var metric = $("#metric").val().trim();
			var type = $("#type").val();
			var tags = $("#tags").val().trim();
			
			if(metric == undefined || metric == ""){
				if($("#errorMessage").length == 0){
					$("#metric").after($("<span class=\"text-danger\" id=\"errorMessage\">  该字段不能为空</span>"));
				}
				return;
			}
			
			window.location.href = "/cat/s/app?op=appMetricSubmit&appMetric.id="+id+"&appMetric.metric="+metric+"&appMetric.type="+type+"&tags="+tags;
		})
	</script>
	
	<table class="table table-striped table-condensed table-bordered table-hover">
		<c:if test="${not empty model.appMetric }">
			<tr>
			<td>名称</td><td><input name="appMetric.id" value="${model.appMetric.id}" id="metricId" disabled/></td>
			</tr>
		</c:if>
		
		<tr>
			<td>指标</td><td><input name="appMetric.metric" value="${model.appMetric.metric}"  style="width:300px" id="metric" /><span class="text-danger">&nbsp;&nbsp;后续配置在这个规则的告警，会根据此项目名查找需要发送告警的联系人信息(告警人信息来源CMDB)</span><br/>
			</td>
		</tr>
		<tr><td>类型</td><td><select name="appMetric.type" style="width: 100px;" id="type" >
								<option value="avg">求平均</option>
								<option value="sum">求和</option>
								<option value="max">求最大</option>
								<option value="min">求最小</option>
							</select><span class="text-danger">（支持数字、字符）</span><br/>
			</td>
		</tr>
		<tr><td>tags</td><td><input name="appMetric.tags" value="" style="width: 300px;" id="tags" /><span class="text-danger">（支持数字、字符，多个英文逗号隔开）</span><br/>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align:center;"><button class="btn btn-primary btn-sm" id="updateSubmit">提交</button></td>
		</tr>
	</table>

</a:mobile>
