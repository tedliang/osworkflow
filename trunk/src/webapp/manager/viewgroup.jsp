<%@ page import="com.opensymphony.user.UserManager,
				 java.util.Collection,
				 com.opensymphony.user.Group,
				 java.util.Iterator,
				 com.opensymphony.user.User,
				 java.net.URLEncoder,
				 com.opensymphony.module.propertyset.PropertySet"%>
<%
	UserManager um = UserManager.getInstance();
	Group group = um.getGroup(request.getParameter("name"));

	PropertySet ps = null;

	if (request.getParameter("propertyAdd") != null) {
		ps = group.getPropertySet();
        setPropertyValue(ps, request.getParameter("propertyKey"), Integer.parseInt(request.getParameter("propertyType")), request.getParameter("propertyValue"));
	}
  else if (request.getParameter("propertyDel") != null)
	{
		ps = group.getPropertySet();
		ps.remove(request.getParameter("propertyDel"));
	}
%>

<h1>View Group : <%= group.getName() %></h1>
<hr>

<h4>Details</h4>
Name: <%= group.getName() %><br>
Class: <%= group.getClass() %><br>

<h4>Providers</h4>
CredentialsProvider: <%= (group.getCredentialsProvider() != null ? group.getCredentialsProvider().getClass().toString() : "None") %><br>
AccessProvider: <%= (group.getAccessProvider() != null ? group.getAccessProvider().getClass().toString() : "None" )%><br>
ProfileProvider: <%= (group.getProfileProvider() != null ? group.getProfileProvider().getClass().toString() : "None")%><br>

<h4>Users</h4>

<%
	Collection users = group.getUsers();
%>
<%= users.size() %> users<br>

<ol>
<%
	for (Iterator iterator = users.iterator(); iterator.hasNext();)
	{
		String userName = (String) iterator.next();
%>
		<li><a href="viewuser.jsp?name=<%= URLEncoder.encode(userName) %>"><%= userName%></a></li>
<%
	}
%>
</ol>

<h4>Properties</h4>
<%
	ps = group.getPropertySet();
	if (ps != null)
	{
		Collection keys = ps.getKeys();
		for (Iterator iterator = keys.iterator(); iterator.hasNext();)
		{
			String key = (String) iterator.next();
%>
		<%= key %> : <%= getPropertyValue(ps, key) %> (<%= getTypeName(ps.getType(key)) %>) [<a href="viewgroup.jsp?name=<%= URLEncoder.encode(group.getName()) %>&propertyDel=<%= URLEncoder.encode(key) %>">Del</a>]<br>
<%
		}
%>

<form action="viewgroup.jsp" method="post">
<input type="hidden" name="name" value="<%= group.getName() %>">
<h4>Add Property</h4>
Key: <input name="propertyKey" type="text"><br>
Type:
<select name="propertyType">
	<% for (int i = 1; i <= 7; i++) { %>
		<option value="<%= i %>"><%= getTypeName(i) %></option>
	<% } %>
</select><br>
Value: <input name="propertyValue" type="text"><br>
<input type="submit" name="propertyAdd" value="Add Property">
</form>

<%
	}
	else
	{
%>
		No PropertySet for this group
<%
	}
%>


<%@ include file="nav.jsp" %>

<%@ include file="includes/propertysetMethods.jsp" %>

