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
	 	
	 	function query() {
			var start = $("#time").val();
			var end = $("#endTime").val();
			var metric = $("#command").val();
			var type = $("#type").val();
			var platform = $("#platform").val();
			var appId = $("#appId").val();
			var version = $("#version").val();
			var tags = $("#tags").val();
			var split = ";";
			var query1 = start +split + end + split + metric + split + type + split + appId + split + version + split + platform + split + tags;
			var query2 = "";
			var value = document.getElementById("checkbox").checked;

			if (value) {
				var start2 = $("#time2").val();
				var end2 = $("#endTime2").val();
				var metric2 = $("#command2").val();
				var type2 = $("#type2").val();
				var platform2 = $("#platform2").val();
				var appId2 = $("#appId2").val();
				var version2 = $("#version2").val();
				var tags2 = $("#tags2").val();
				var query2 = start2 +split + end2 + split + metric2 + split + type2 + split + appId2 + split + version2 + split + platform2 + split + tags2;
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
			var href = "?query1=" + query1 + "&query2=" + query2;
			window.location.href = href;
		}
	 	
	 	function check() {
			var value = document.getElementById("checkbox").checked;

			if (value == true) {
				$('#history').slideDown();
				$("#appId2").val($("#appId").val());
				$("#command2").val($("#command").val());
				$("#version2").val($("#version").val());
				$("#platform2").val($("#platform").val());
				$("#time2").val($("#time").val());
				$("#endTime2").val($("#endTime").val());
			} else {
				$('#history').slideUp();
			}
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
						format:'Y-m-d H:i',
						step:30,
						maxDate:0
					});
					
					var query1 = '${payload.query1}';
					var query2 = '${payload.query2}';
					var words = query1.split(";");

					if (typeof(words[0]) == 'undefined' || words[0].length == 0) {
						$("#time").val(getTime(new Date(new Date().getTime() - 2*3600000)));
					} else {
						$("#time").val(words[0]);
					}
					
					if (typeof(words[1]) == 'undefined' || words[1].length == 0) {
						$("#endTime").val(getTime(new Date()));
					} else {
						$("#endTime").val(words[1]);
					}
					
					if(typeof(words[2]) != 'undefined' && words[2].length > 0){
						$('#command').val(words[2]);
					}else{
						$('#command').val("${model.defaultMetric.id}");
					}
					if(typeof(words[3]) != 'undefined' && words[3].length > 0){
						$('#type').val(words[3]);
					}
					if(typeof(words[4]) != 'undefined' && words[4].length > 0){
						$('#appId').val(words[4]);
					}
					if(typeof(words[5]) != 'undefined' && words[5].length > 0){
						$('#version').val(words[5]);
					}
					if(typeof(words[6]) != 'undefined' && words[6].length > 0){
						$('#platform').val(words[6]);
					}
					
					$('#tags').val(words[7]);
					
					
					if (query2 != null && query2 != '') {
						$('#history').slideDown();
						document.getElementById("checkbox").checked = true;
						
						var words = query2.split(";");

						if (typeof(words[0]) === 'undefined' || words[0].length == 0) {
							$("#time2").val(getTime(new Date(new Date().getTime() - 2*3600000)));
						} else {
							$("#time2").val(words[0]);
						}
						if (typeof(words[1]) == 'undefined' || words[1].length == 0) {
							$("#endTime2").val(getTime(new Date()));
						} else {
							$("#endTime2").val(words[1]);
						}
						
						if(typeof(words[2]) != 'undefined' && words[2].length > 0){
							$('#command2').val(words[2]);
						}
						if(typeof(words[3]) != 'undefined' && words[3].length > 0){
							$('#type2').val(words[3]);
						}
						if(typeof(words[4]) != 'undefined' && words[4].length > 0){
							$('#appId2').val(words[4]);
						}
						if(typeof(words[5]) != 'undefined' && words[5].length > 0){
							$('#version2').val(words[5]);
						}
						if(typeof(words[6]) != 'undefined' && words[6].length > 0){
							$('#platform2').val(words[6]);
						}
						$('#tags2').val(words[7]);
					}
					
					 $.widget( "custom.catcomplete", $.ui.autocomplete, {
							_renderMenu: function( ul, items ) {
								var that = this,
								currentCategory = "";
								$.each( items, function( index, item ) {
									if ( item.category != currentCategory ) {
										ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
										currentCategory = item.category;
									}
									that._renderItemData( ul, item );
								});
							}
						});
					
					 var dataa = [];
						
						<c:forEach var="entry" items="${model.appMetrics}">
							var item = {};
							item['label'] = '${entry.value.id}';
							item['category'] ='${entry.value.metric}';
							
							dataa.push(item);
						</c:forEach>
								
						$( "#command" ).catcomplete({
							delay: 0,
							source: dataa
						});
						$( "#command2" ).catcomplete({
							delay: 0,
							source: dataa
						});
					
					var data = ${model.lineChart.jsonString};
					graphMetricChart(document.getElementById('${model.lineChart.id}'), data);
				});
	</script>
	
		<%@include file="linechartDetail.jsp"%>
</a:mobile>