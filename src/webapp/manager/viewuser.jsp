<%@ page import="com.opensymphony.user.UserManager,
				 com.opensymphony.user.User,
				 com.opensymphony.module.propertyset.PropertySet,
				 java.util.Collection,
				 java.util.Iterator,
				 java.net.URLEncoder,
				 com.opensymphony.user.Group"%>
<%
	UserManager um = UserManager.getInstance();
	User user = um.getUser(request.getParameter("name"));
	PropertySet ps = null;

	if (request.getParameter("propertyAdd") != null) {
		ps = user.getPropertySet();
        setPropertyValue(ps, request.getParameter("propertyKey"), Integer.parseInt(request.getParameter("propertyType")), request.getParameter("propertyValue"));
	}
	else if (request.getParameter("propertyDel") != null)
	{
		ps = user.getPropertySet();
		ps.remove(request.getParameter("propertyDel"));
	}
%>

<h1>User: <%= user.getName() %></h1>
<hr>

<h4>Details</h4>
Name: <%= user.getName() %><br>
Class: <%= user.getClass() %><br>
Full Name: <%= user.getFullName() %><br>
Email: <%= user.getEmail() %><br>

<h4>Providers</h4>
CredentialsProvider: <%= (user.getCredentialsProvider() != null ? user.getCredentialsProvider().getClass().toString() : "None") %><br>
AccessProvider: <%= (user.getAccessProvider() != null ? user.getAccessProvider().getClass().toString() : "None" )%><br>
ProfileProvider: <%= (user.getProfileProvider() != null ? user.getProfileProvider().getClass().toString() : "None")%><br>

<h4>Groups</h4>
<%
	Collection groups = user.getGroups();
%>
<%= groups.size() %> groups<br>

<ol>
<%
	for (Iterator iterator = groups.iterator(); iterator.hasNext();)
	{
		String groupName = (String) iterator.next();
%>
		<li><a href="viewgroup.jsp?name=<%= URLEncoder.encode(groupName) %>"><%= groupName %></a></li>
<%
	}
%>
</ol>
<a href="editusergroups.jsp?name=<%= URLEncoder.encode(user.getName()) %>">Add / Edit Groups</a>

<h4>Properties</h4>
<%
	ps = user.getPropertySet();

	Collection keys = ps.getKeys();
	for (Iterator iterator = keys.iterator(); iterator.hasNext();)
	{
		String key = (String) iterator.next();
%>
	<%= key %> : <%= getPropertyValue(ps, key) %> (<%= getTypeName(ps.getType(key)) %>) [<a href="viewuser.jsp?name=<%= URLEncoder.encode(user.getName()) %>&propertyDel=<%= URLEncoder.encode(key) %>">Del</a>]<br>
<%
	}
%>
<form action="viewuser.jsp" method="post">
<input type="hidden" name="name" value="<%= user.getName() %>">
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
<%@ include file="nav.jsp" %>

<%@ include file="includes/propertysetMethods.jsp" %>
