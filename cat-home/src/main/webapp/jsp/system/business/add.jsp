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
		<form name="addSubmit" id="form" method="post" action="${model.pageUri}?op=addSubmit&domain=${payload.domain}">
			<span class="text-center text-danger" id="state">&nbsp;</span>
			<table class="table table-striped table-condensed  ">
				<tr>
					<td width="20%" style="text-align:right"  class="text-success">项目名称</td>
					<td width="20%" >
						<c:if test="${not empty payload.domain}">
							<input name="" value="${payload.domain}" readonly required/>
						</c:if>
					</td>
					<td width="25%" style="text-align:right" class="text-success">BusinessKey</td>
					<td width="35%" >
						<c:if test="${not empty model.businessItemConfig.id}">
							<input name="businessItemConfig.id" value="${model.businessItemConfig.id}" readonly required/>
						</c:if>
						<c:if test="${empty  model.businessItemConfig.id}">
							<input name="businessItemConfig.id" value="${model.businessItemConfig.id}" required/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td  style="text-align:right" class="text-success">显示标题</td>
					<td ><input name="businessItemConfig.title" value="${model.businessItemConfig.title}" required/></td>
					<td  style="text-align:right" class="text-success">显示顺序（数字）</td>
					<td ><input  name="businessItemConfig.viewOrder" value="${model.businessItemConfig.viewOrder}" required/></td>
				</tr>
				<tr>
					<td style="text-align:right" class="text-success">是否告警</td>
					<td >
						<c:choose>
							<c:when test="${model.businessItemConfig.alarm}">
								<input type="radio" name="businessItemConfig.alarm" value="true" checked />是&nbsp;&nbsp;&nbsp;	
								<input type="radio" name="businessItemConfig.alarm" value="false" />否
							</c:when>
							<c:otherwise>
						    	<input type="radio" name="businessItemConfig.alarm" value="true" />是&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.alarm" value="false" checked />否
							</c:otherwise>
						</c:choose>
					</td>
					<td style="text-align:right" class="text-success" >显示次数曲线</td>
					<td>
						<c:choose>
							<c:when test="${model.businessItemConfig.showCount}">
								<input type="radio" name="businessItemConfig.showCount" value="true" checked />是&nbsp;&nbsp;&nbsp;	
								<input type="radio" name="businessItemConfig.showCount" value="false" />否
							</c:when>
							<c:otherwise>
						    	<input type="radio" name="businessItemConfig.showCount" value="true" />是&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.showCount" value="false" checked />否
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td style="text-align:right" class="text-success" >显示平均曲线</td>
					<td>
						<c:choose>
							<c:when test="${model.businessItemConfig.showAvg}">
								<input type="radio" name="businessItemConfig.showAvg" value="true" checked />是	&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.showAvg" value="false" />否
							</c:when>
							<c:otherwise>
						    	<input type="radio" name="businessItemConfig.showAvg" value="true" />是&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.showAvg" value="false" checked />否
							</c:otherwise>
						</c:choose>
					</td>
					<td style="text-align:right" class="text-success">显示求和曲线</td>
					<td>
						<c:choose>
							<c:when test="${model.businessItemConfig.showSum}">
								<input type="radio" name="businessItemConfig.showSum" value="true" checked />是	&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.showSum" value="false" />否
							</c:when>
							<c:otherwise>
						    	<input type="radio" name="businessItemConfig.showSum" value="true" />是&nbsp;&nbsp;&nbsp;
								<input type="radio" name="businessItemConfig.showSum" value="false" checked />否
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td style="text-align:center" colspan='4'><input class='btn btn-primary btn-xs' id="addOrUpdateNodeSubmit" type="submit" name="submit" value="提交" /></td>
				</tr>
			<!-- 	<tr>
					<td style="text-align:right" class="text-success">标签</td>
					<td>aaa</td>
				</tr> -->
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