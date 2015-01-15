<!--[if lt IE 9]>
<div  class="browserOverlay">
</div>
<div id="browser-warning">
		<p>Upgrade to a modern browser.</p>
</div>
<![endif]-->
<div id="toolbar">
<div id="menu">
<a href="/">Home</a>
${reportLink}
<#if currentUser.hasRole("Admin")>
<a href="/user/">Admin</a>
</#if>
</div>
<div id="userinfo">
	<a href="/user/${currentUser.username?html}">
	<#if currentUser.name?? && currentUser.name?length gt 0>
		${currentUser.name?html}
	<#else>
		${currentUser.username?html}
	</#if>
	</a>
</div>
</div>
<div id="content">
