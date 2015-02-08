<#-- @ftlvariable name="" type="se.solit.dwtemplate.resources.DayReportView" -->
<!DOCTYPE HTML>
<html>
<head>
<#include "head.ftl">
</head>
<body class="report">
	<#include "top.ftl">
	${tabs(3)}
	<div id="DetailsReport" class="report">
		<div id="dateBar">
		<div id="year">${year}</div><div id="month">${month}</div><div id="day"> ${dayOfmonth} (${day})</div>
		</div>
	<p>
	<p>
	<h2> ${taskName} </h2>
	<hr>
	<table class="timeTable" cellspacing=0>
		<tr><th>&nbsp;</th><th>Start</th><th>&nbsp;&nbsp;</th><th>End</th><th>&nbsp;</th><tr>
        	<#list times as time>
        		<tr>
        		<td>
        		</td>
        		<td>
        		${time.start.toString('HH:mm')}
        		</td>
        		<td>
        		</td>
        		<td>
				${time.stop.toString('HH:mm')}
        		</td>
        		<td>
        		</td>
        		<td>
        		<a href="/time/edit/${time.getID()}">
        		<button type="button">Edit</button>
        		</a>
        		</td>
        		</tr>
			</#list>
			<tr>
			<td>
			Total:
			</th>
			<td cellspan="2">
			${total}
			</td>
	</table>
	<#include "bottom.ftl">
</body>
</html>
