<%@ page import="com.opensymphony.user.UserManager,
				 com.opensymphony.user.User,
				 java.util.Collection,
				 java.net.URLEncoder,
				 com.opensymphony.user.Group,
                 java.util.Iterator"%>


<%
	UserManager um = UserManager.getInstance();
	User user = um.getUser(request.getParameter("name"));

	if (request.getParameter("add") != null)
	{
		String[] addGroupNames = request.getParameterValues("outgroups");
		for (int i = 0; i < addGroupNames.length; i++)
		{
			String addGroupName = addGroupNames[i];
			Group addGroup = um.getGroup(addGroupName);
			user.addToGroup(addGroup);
		}
	}
	else if (request.getParameter("remove") != null)
	{
		String[] removeGroupNames = request.getParameterValues("ingroups");
		for (int i = 0; i < removeGroupNames.length; i++)
		{
			String removeGroupName = removeGroupNames[i];
			Group removeGroup = um.getGroup(removeGroupName);
			user.removeFromGroup(removeGroup);
		}
	}
%>

<h1>Edit Groups : <%= user.getName() %></h1>
<hr>

<form action="editusergroups.jsp" method="get">

	<table cellpadding=0 cellspacing=0 border=0 bgcolor=666666 width=80% align="center"><tr><td>
	<table cellpadding=4 cellspacing=1 border=0 width=100% bgcolor=666666>
	<tr>
		<td bgcolor=f0f0f0 valign="top" align="center" nowrap width="45%"><b>Other Groups</b></td>
		<td bgcolor=f0f0f0 width="10%">&nbsp;</td>
		<td bgcolor=f0f0f0 valign="top" align="center" nowrap width="45%"><b>Member Of</b></td>
	</tr>
	<tr>
		<td bgcolor=ffffff valign="top" align="center">
			<% Collection allgroups = um.getGroups(); %>
			<select name="outgroups"
				size="7"
				multiple>
                    <% for (Iterator iterator = allgroups.iterator(); iterator.hasNext();) {
                        Group outgroup = (Group) iterator.next();
                    %>

						<% if (user.getGroups() == null || !user.getGroups().contains(outgroup.getName())) { %>
						<option value="<%= outgroup.getName() %>"><%= outgroup.getName() %></option>
						<% } %>
					<% } %>
			</select>
		</td>
		<td bgcolor=ffffff align="center">
            <input type="submit" name="add" value="&gt;&gt;">
			<br>
			<input type="submit" name="remove" value="&lt;&lt;">
		</td>
		<td bgcolor=ffffff valign="top" align="center">
			<select name="ingroups"
				size="7"
				multiple
				class="maincontent">
					<% if (user.getGroups() != null) { %>
                        <% for (Iterator iterator = user.getGroups().iterator(); iterator.hasNext();) {
                            String ingroup = (String) iterator.next();
                        %>
							<option value="<%= ingroup %>"><%= ingroup %></option>
						<% } %>
					<% } %>
			</select>
		</td>
	</tr>
	<tr>
		<td bgcolor=ffffff valign=top colspan=3 align=center>
			<input type="hidden" name="executed" value="true">
			<input type="hidden" name="name" value="<%= user.getName() %>">
			<input type="button" value="Done" onclick="location.href='viewuser.jsp?name=<%= URLEncoder.encode(user.getName()) %>'">
		</td>
	</tr>
	</table>
	</td></tr></table>

</form>

<%@ include file="nav.jsp" %>