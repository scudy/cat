<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.cat.report.page.appmetric.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.cat.report.page.appmetric.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.cat.report.page.appmetric.Model" scope="request" />

<a:mobile>
 	<script type="text/javascript">
	 	function getTime(date) {
			var myDate = date;
			var myMonth = new Number(myDate.getMonth());
			var month = myMonth + 1;
			var day = myDate.getDate();
			var myHour = new Number(myDate.getHours());
			var myMinute = new Number(myDate.getMinutes());
			
			if(month < 10){
				month = '0' + month;
			}
			if(day < 10){
				day = '0' + day;
			}
			if(myHour < 10){
				myHour = '0' + myHour;
			}
			if(myMinute < 10){
				myMinute = '0' + myMinute;
			}
			return myDate.getFullYear() + "-" + month + "-" + day + " " + myHour + ":" + myMinute;
		}
	 	
	 	function query(field,networkCode,appVersionCode,channelCode,platformCode) {
			var times = $("#time").val().split(" ");
			var period = times[0];
			var start = times[1];
			var end = $("#endTime").val();
			var command = $("#command").val().split('|')[0];
			var code = $("#code").val();
			var network = "";
			var version = "";
			var connectionType = "";
			var platform = "";
			var city = "";
			var operator = "";
			var source = "";
			if(typeof(networkCode) == "undefined"){
				network = $("#network").val();
			}else{
				network = networkCode;
			}
			if(typeof(appVersionCode) == "undefined"){
				version = $("#version").val();
			}else{
				version = appVersionCode;
			}
			if(typeof(channelCode) == "undefined"){
				connectionType = $("#connectionType").val();
			}else{
				connectionType = channelCode;
			}
			if(typeof(platformCode) == "undefined"){
				platform = $("#platform").val();
			}else{
				platform = platformCode;
			}
			if(typeof(cityCode) == "undefined"){
				city = $("#city").val();
			}else{
				city = cityCode;
			}
			if(typeof(operatorCode) == "undefined"){
				operator = $("#operator").val();
			}else{
				operator = operatorCode;
			}
			if(typeof(sourceCode) == "undefined"){
				source = $("#source").val();
			}else{
				source = sourceCode;
			}
			var split = ";";
			var commandId = ${model.command2IdJson}[command].id;
			var query1 = period + split + commandId + split + code + split
					+ network + split + version + split + connectionType
					+ split + platform + split + city + split + operator + split + source + split + start + split + end;
			var query2 = "";
			var value = document.getElementById("checkbox").checked;

			if (value) {
				var times2 = $("#time2").val().split(" ");
				var period2 = times2[0];
				var start2 = times2[1];
				var end2 = $("#endTime2").val();
				var command2 = $("#command2").val().split('|')[0];
				var commandId2 = ${model.command2IdJson}[command2].id;
				var code2 = $("#code2").val();
				var network2 = $("#network2").val();
				var version2 = $("#version2").val();
				var connectionType2 = $("#connectionType2").val();
				var platform2 = $("#platform2").val();
				var city2 = $("#city2").val();
				var operator2 = $("#operator2").val();
				var source2 = $("#source2").val();
				query2 = period2 + split + commandId2 + split + code2 + split
						+ network2 + split + version2 + split + connectionType2
						+ split + platform2 + split + city2 + split
						+ operator2 + split + source2 + split + start2 + split + end2;
			}

			var checkboxs = document.getElementsByName("typeCheckbox");
			var type = "";

			for (var i = 0; i < checkboxs.length; i++) {
				if (checkboxs[i].checked) {
					type = checkboxs[i].value;
					break;
				}
			}
			
			if(typeof(field) == "undefined"){
				field = "";
			}
			if(typeof(sort) == "undefined"){
				sort = "";
			}
			var commandId = $('#command').val();
			var commandId2 = $('#command2').val();
			var href = "?query1=" + query1 + "&query2=" + query2 + "&type="
					+ type + "&groupByField=" + field + "&sort=" + sort
					+"&commandId="+commandId+"&commandId2="+commandId2+"&appId="+$("#appId").val()+"&appId2="+$("#appId2").val();
			window.location.href = href;
		}
		
		$(document).ready(
				function() {
					$('#App_report').addClass('active');
					$('#metricTrend').addClass('active');
					
					$('#time').datetimepicker({
						format:'Y-m-d H:i',
						step:30,
						maxDate:0
					});
					$('#endTime').datetimepicker({
						datepicker:false,
						format:'Y-m-d H:i',
						step:30,
						maxDate:0
					});
					$('#time2').datetimepicker({
						format:'Y-m-d H:i',
						step:30,
						maxDate:0
					});
					$('#endTime2').datetimepicker({
						datepicker:false,
						format:'Y-m-d H:i',
						step:30,
						maxDate:0
					});
					
					var query1 = '${payload.query1}';
					var words = query1.split(";");

					if (typeof(words[0]) != 'undefined' && words[0].length == 0) {
						$("#time").val(getTime(new Date(new Date().getTime() - 2*3600000)));
					} else {
						$("#time").val(getTime(new Date(new Number(words[0]))));
					}
					
					if (typeof(words[1]) != 'undefined' && words[1].length == 0) {
						$("#endTime").val(getTime(new Date()));
					} else {
						$("#endTime").val(getTime(new Date(new Number(words[1]))));
					}
					
					var data = ${model.lineChart.jsonString};
					graphMetricChart(document.getElementById('${model.lineChart.id}'), data);
				});
	</script>
	
		<%@include file="linechartDetail.jsp"%>
</a:mobile>