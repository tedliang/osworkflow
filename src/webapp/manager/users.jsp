<%@ page import="com.opensymphony.user.UserManager,
				 java.util.Collection,
				 java.util.Iterator,
				 com.opensymphony.user.User,
				 java.net.URLEncoder"%>

<%
	UserManager um = UserManager.getInstance();

	if (request.getParameter("adduser") != null)
	{
		User newuser = um.createUser(request.getParameter("name"));
		newuser.setPassword(request.getParameter("password"));
		newuser.setEmail(request.getParameter("email"));
	}
	else if (request.getParameter("deluser") != null)
	{
		User removeuser = um.getUser(request.getParameter("deluser"));
		removeuser.remove();
	}
%>

<h1>Users</h1>
<hr>

<ol>
<%
	Collection users = um.getUsers();
	for (Iterator iterator = users.iterator(); iterator.hasNext();)
	{
		User user = (User) iterator.next();
		%>
		<li><%= user.getName() %> (<%= user.getEmail() %>) [<a href="viewuser.jsp?name=<%= URLEncoder.encode(user.getName()) %>">View</a> | <a href="users.jsp?deluser=<%= URLEncoder.encode(user.getName()) %>">Del</a>]</li>
		<%
	}
%>
</ol>

<hr>

<form action="users.jsp">
<b>Add User</b><br>
Name: <input type="text" name="name"><br>
Password: <input type="password" name="password"><br>
Email: <input type="text" name="email"><br>
<input type="submit" name="adduser" value="Add User">
</form>

<%@ include file="nav.jsp" %>
