<br/>
[告警时间：${date} ${start}][Warning阈值：${warning}][Error阈值：${error}][实际值：${count}]
<br/><br/>
 <#if module == "ALL">
     <a href='http://cat.dp/cat/r/app?op=appCrashLog&crashLogQuery.day=${date}&crashLogQuery.startTime=${start}&crashLogQuery.endTime=${end}&crashLogQuery.appName=${appName}&crashLogQuery.query=;;;;'>点击此处查看详情</a><br/>
</#if>
 <#if module != "ALL">
    <a href='http://cat.dp/cat/r/app?op=appCrashLog&crashLogQuery.day=${date}&crashLogQuery.startTime=${start}&crashLogQuery.endTime=${end}&crashLogQuery.appName=${appName}&crashLogQuery.query=;;${module};;'>点击此处查看详情</a><br/>
</#if>
    