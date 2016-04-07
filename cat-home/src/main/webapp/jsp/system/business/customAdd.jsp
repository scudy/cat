<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>

<jsp:useBean id="ctx" type="com.dianping.cat.system.page.business.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.cat.system.page.business.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.cat.system.page.business.Model" scope="request"/>

<a:config>
	<res:useJs value="${res.js.local['jquery.validate.min.js']}" target="head-js" />
	<res:useJs value="${res.js.local['alarm_js']}" target="head-js" />
	<res:useCss value="${res.css.local['select2.css']}" target="head-css" />
	<res:useJs value="${res.js.local['select2.min.js']}" target="head-js" />
	<res:useCss value="${res.css.local['multiple-select.css']}" target="head-css" />
	<res:useJs value="${res.js.local['jquery.multiple.select.js']}" target="head-js" />
	
		<h4 class="text-success text-center">修改业务监控规则</h4>
		<h4 class="text-center text-danger" id="state">&nbsp;</h4>
		<form name="customAddSubmit" id="form" method="post" action="${model.pageUri}?op=customAddSubmit&domain=${payload.domain}">
			<span class="text-center text-danger" id="state">&nbsp;</span>
			<table class="table table-striped table-condensed">
				<tr>
					<td width="20%" style="text-align:right"  class="text-success">项目名称</td>
					<td width="20%" >
						<c:if test="${not empty payload.domain}">
							<input name="" value="${payload.domain}" readonly required/>
						</c:if>
					</td>
					<td width="25%" style="text-align:right" class="text-success">BusinessKey</td>
					<td width="35%" >
						<c:if test="${not empty model.customConfig.id}">
							<input name="customConfig.id" value="${model.customConfig.id}" readonly required/>
						</c:if>
						<c:if test="${empty model.customConfig.id}">
							<input name="customConfig.id" value="${model.customConfig.id}" required/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td  style="text-align:right" class="text-success">显示标题</td>
					<td><input name="customConfig.title" value="${model.customConfig.title}" required/></td>
					<td  style="text-align:right" class="text-success">显示顺序(数字)</td>
					<td><input  name="customConfig.viewOrder" value="${model.customConfig.viewOrder}" required/></td>
				</tr>
				<tr>
					<td style="text-align:right"  class="text-success">规则配置</td>
					<td colspan='3'>
						<textarea style="width:200px;height:80px;" name="customConfig.pattern" required>${model.customConfig.pattern}</textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align:right;color:red">填写提示:</td>
					<td colspan='3'>						
						<span style="color:red">支持跨项目的指标进行四则运算。使用 \${domain,key,type} 来表示一个特定指标，例如 \${cat,test,COUNT}表示cat项目下，BusinessKey为test的指标的次数。type种类包括COUNT,AVG,SUM。</span>
					</td>
				</tr>
				<tr>
					<td style="text-align:center" colspan='4'>
						<input class='btn btn-primary btn-xs' id="addOrUpdateNodeSubmit" type="submit" name="submit" value="提交" />
					</td>
				</tr>
			</table>
		</form>

	<script>
		$(document).ready(function(){
			$('#application_config').addClass('active open');
			$('#businessConfig').addClass('active');
			var state = '${model.opState}';
			if(state=='Success'){
				$('#state').html('操作成功');
			}else if(state=='Fail'){
				$('#state').html('操作失败');
			}
			setInterval(function(){
				$('#state').html('&nbsp;');
			},3000);
		})
	</script>
</a:config>