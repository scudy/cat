<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>

<jsp:useBean id="ctx" type="com.dianping.cat.system.page.app.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.cat.system.page.app.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.cat.system.page.app.Model" scope="request"/>

<a:mobile>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#userMonitor_config').addClass('active open');
			$('#appMetrics').addClass('active');
			
			var state = '${model.opState}';
			if(state=='Success'){
				$('#state').html('操作成功');
			}else{
				$('#state').html('操作失败');
			}
			setTimeout(function(){
				$('#state').html('&nbsp;');
			},3000);
			
			$(document).delegate('#updateSubmit', 'click', function(e){
				var name = $("#commandName").val();
				var title = $("#commandTitle").val();
				var domain = $("#commandDomain").val();
				var id = $("#commandId").val();
				
				if(name == undefined || name == ""){
					if($("#errorMessage").length == 0){
						$("#commandName").after($("<span class=\"text-danger\" id=\"errorMessage\">  该字段不能为空</span>"));
					}
					return;
				}
				if(title==undefined){
					title = "";
				}
				if(domain==undefined){
					domain="";
				}
				if(id==undefined){
					id="";
				}
				
				window.location.href = "/cat/s/app?op=appSubmit&name="+name+"&title="+title+"&domain="+domain+"&id="+id;
			})
 		});
	</script>
    <table class="table table-striped table-condensed table-bordered table-hover">
	    <thead><tr>
				<th width="auto">id</th>
				<th width="">metric</th>
				<th width="">Tags</th>
				<th width="">
					<a href="?op=appMetricUpdate" class="btn btn-primary btn-xs">
					<i class="ace-icon glyphicon glyphicon-plus bigger-120"></i></a></th>
			</tr>
		</thead>
		
    	<c:forEach var="entry" items="${model.appMetrics}">
	    	<tr><td>${entry.value.id}</td>
			<td>${entry.value.metric}</td>
			<td>
				<c:forEach var="tag" items="${entry.value.tags }">
					${tag.id}
				</c:forEach>
			</td>
			<c:if test="${command.id ne 0 }">
				<td><a href="?op=appMetricUpdate&appMetricId=${entry.value.id}" class="btn btn-primary btn-xs">
					<i class="ace-icon fa fa-pencil-square-o bigger-120"></i></a>
					<a href="?op=appMetricDelete&appMetricId=${entry.value.id}" class="btn btn-danger btn-xs delete" >
					<i class="ace-icon fa fa-trash-o bigger-120"></i></a></td>
				
			</c:if></tr>
    	</c:forEach>
    </table>
</a:mobile>
