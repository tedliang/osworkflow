<%@ page import="com.opensymphony.user.UserManager,
				 java.util.Collection,
				 java.util.Iterator,
				 com.opensymphony.user.provider.CredentialsProvider,
				 com.opensymphony.user.provider.AccessProvider,
				 com.opensymphony.user.provider.UserProvider"%>
 <h2>Directory</h2>
<ul>
<li><a href="users.jsp">Users</a>
<li><a href="groups.jsp">Groups</a>
</ul>

<hr>

<h2>Providers</h2>
<%
	UserManager um = UserManager.getInstance();
%>

<h4>CredentialsProviders</h4>

<ul>
<%
	Collection credentialProviders = um.getCredentialsProviders();
	for (Iterator iterator = credentialProviders.iterator(); iterator.hasNext();)
	{
		CredentialsProvider provider = (CredentialsProvider) iterator.next();
%>
        <li><%= provider.getClass() %></li>
<%
	}
%>
</ul>

<h4>AccessProviders</h4>

<ul>
<%
	Collection accessProviders = um.getAccessProviders();
	for (Iterator iterator = accessProviders.iterator(); iterator.hasNext();)
	{
		UserProvider provider = (UserProvider) iterator.next();
%>
        <li><%= provider.getClass() %></li>
<%
	}
%>
</ul>

<h4>ProfileProviders</h4>

<ul>
<%
	Collection profileProviders = um.getProfileProviders();
	for (Iterator iterator = profileProviders.iterator(); iterator.hasNext();)
	{
		UserProvider provider = (UserProvider) iterator.next();
%>
        <li><%= provider.getClass() %></li>
<%
	}
%>
</ul>


