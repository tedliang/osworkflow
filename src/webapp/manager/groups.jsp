<%@ page import="com.opensymphony.user.UserManager,
				 java.util.Collection,
				 java.util.Iterator,
				 com.opensymphony.user.User,
				 java.net.URLEncoder,
				 com.opensymphony.user.Group"%>

<%
	UserManager um = UserManager.getInstance();

	if (request.getParameter("addGroup") != null)
	{
		Group newgroup = um.createGroup(request.getParameter("name"));
	}
	else if (request.getParameter("delGroup") != null)
	{
		Group removegroup = um.getGroup(request.getParameter("delGroup"));
		removegroup.remove();
	}
%>

<h1>Groups</h1>
<hr>
<ol>
<%
	Collection groups = um.getGroups();
	for (Iterator iterator = groups.iterator(); iterator.hasNext();)
	{
		Group group = (Group) iterator.next();
		%>
		<li><%= group.getName() %> [<a href="viewgroup.jsp?name=<%= URLEncoder.encode(group.getName()) %>">View</a> | <a href="groups.jsp?delGroup=<%= URLEncoder.encode(group.getName()) %>">Del</a>]</li>
		<%
	}
%>
</ol>

<hr>

<form action="groups.jsp">
<b>Add Group</b><br>
Name: <input type="text" name="name"><br>
<input type="submit" name="addGroup" value="Add group">
</form>

<%@ include file="nav.jsp" %>
