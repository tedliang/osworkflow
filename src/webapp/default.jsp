<%@ page import="com.opensymphony.util.TextUtils"%>
 <html>
    <head>
        <title>OSWorkflow Example App</title>
    </head>

    <body>

    Welcome to the OSWorkflow Example App. Please log in, or <a href="manager">create an account</a>:
    <p>
    You can also load some reference data by clicking <a href="reference.jsp">here</a>. After creating the test data,
    you can login with the username <b>test</b>, and password <b>test</b>

    <form method="POST" action="login.jsp">
    <table border="0">
        <tr><td>Username:</td><td><input type="text" name="username"></td></tr>
        <tr><td>Password:</td><td><input type="password" name="password"></td></tr>
        <tr><td colspan="2"><input type="submit" value="Login"></td></tr>
    </table>
    </form>

    <% if (TextUtils.parseBoolean(request.getParameter("auth_failed"))) { %>
        <font color="red">Invalid login! Have you created an account yet?</font>
    <% } %>
    <hr>
    Below is a graphical representation of the example workflow. You should review the example.xml workflow definition file to see the various steps and actions available in this example.<br>
    <img src="example.png" border=0 />
    </body>
</html>
