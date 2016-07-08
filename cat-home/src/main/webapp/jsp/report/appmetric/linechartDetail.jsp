<%@ page contentType="text/html; charset=utf-8"%>
<style>
.form-control {
  height: 30px;
}
</style>
<table>
			<tr>
				<th>
				<div class="input-group" style="float:left;">
	              <span class="input-group-addon">开始</span>
	              <input type="text" id="time" style="width:130px"/>
	            </div>
				<div class="input-group" style="float:left;width:130px">
	              <span class="input-group-addon">结束</span>
        	      <input type="text" id="endTime" style="width:130px;"/></div>
	            <div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">App</span>
					<select id="appId" style="width: 100px;">
						<c:forEach var="item" items="${model.apps}" varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
        	    </div>
				<div class="input-group" style="float:left;width:350px">
					<span class="input-group-addon">命令字</span>
		            <form id="wrap_search" style="margin-bottom:0px;">
						<span class="input-icon" style="width:350px;">
							<input type="text" placeholder="" class="search-input search-input form-control ui-autocomplete-input" id="command" autocomplete="on" data=""/>
							<i class="ace-icon fa fa-search nav-search-icon"></i>
						</span>
					</form>
	            </div>
				</th>
				</tr>
			<tr>
				<th align=left>
				<div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">版本</span>
					<select id="version" style="width: 100px;">
						<option value=''>All</option>
						<c:forEach var="item" items="${model.versions}" varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
	            </div>
	            <div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">平台</span>
					<select id="platform" style="width: 100px;">
						<option value=''>All</option>
						<c:forEach var="item" items="${model.platforms}"
							varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
	            </div>
	            <input class="btn btn-primary btn-sm"
					value="&nbsp;&nbsp;&nbsp;查询&nbsp;&nbsp;&nbsp;" onclick="query()"
					type="submit" /> <input class="btn btn-primary btn-sm" id="checkbox"
					onclick="check()" type="checkbox" /> <label for="checkbox"
					style="display: -webkit-inline-box">选择对比</label>
				</th>
			</tr>
		</table>
		<table id="history" style="display: none">
				<tr>
				<th>
				<div class="input-group" style="float:left;">
	              <span class="input-group-addon">开始</span>
	              <input type="text" id="time2" style="width:130px"/>
	            </div>
	            <div class="input-group" style="float:left;width:60px">
	              <span class="input-group-addon">结束</span>
        	      <input type="text" id="endTime2" style="width:60px;"/></div>
        	    <div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">App</span>
					<select id="appId2" style="width: 100px;">
						<c:forEach var="item" items="${model.apps}" varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
        	    </div>
				<div class="input-group" style="float:left;width:350px">
					<span class="input-group-addon">命令字</span>
		            <form id="wrap_search2" style="margin-bottom:0px;">
						<span class="input-icon" style="width:350px;">
							<input type="text" placeholder="input domain for search" class="search-input search-input form-control ui-autocomplete-input" id="command2" autocomplete="on" data=""/>
							<i class="ace-icon fa fa-search nav-search-icon"></i>
						</span>
					</form>
	            </div>
				</th>
				</tr>
				<tr>
			<th align=left>
				<div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">版本</span>
					<select id="version2" style="width: 100px;">
						<option value=''>All</option>
						<c:forEach var="item" items="${model.versions}" varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
	            </div>
	            <div class="input-group" style="float:left;width:120px">
	              	<span class="input-group-addon">平台</span>
					<select id="platform2" style="width: 100px;">
						<option value=''>All</option>
						<c:forEach var="item" items="${model.platforms}"
							varStatus="status">
							<option value='${item.value.id}'>${item.value.value}</option>
						</c:forEach>
					</select>
	            </div>
	            </th>
			</tr>
		</table>

		<div>&nbsp;
			<label class="btn btn-info btn-sm"><input type="radio"
				name="typeCheckbox" value="request">请求数
			</label><label class="btn btn-info btn-sm"> <input type="radio"
				name="typeCheckbox" value="success">网络成功率</label><label class="btn btn-info btn-sm">
			<input type="radio" name="typeCheckbox" value="businessSuccess">业务成功率
			</label><label class="btn btn-info btn-sm">  <input type="radio"
				name="typeCheckbox" value="delay">成功延时
			</label>
		</div>

	<div>
		<div id="${model.lineChart.id}"></div>
	</div>
	<br/>
