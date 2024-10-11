
<%@ page import="java.io.*"%>
<%
    Error error = (Error) request.getAttribute("error");
    String err = error.getMessage();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Error- FRAMEWORK</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- <link rel="stylesheet" href=""> -->
        <script>
            function escapeHtml(text) {
                var map = {
                    '&': '&amp;',
                    '<': '&lt;',
                    '>': '&gt;',
                    '"': '&quot;',
                    "'": '&#039;',
                    '\n': '<br/>'
                };
                return text.replace(/[&<>"'\n]/g, function(m) { return map[m]; });
            }
        </script>
    </head>
    <body>

        <h1>ERROR - FRAMEWORK</h1>
        <pre id="errorMsg"></pre>
        <script>
            var err = `<%= err.replaceAll("\r\n", "\n") %>`;
            document.getElementById('errorMsg').innerHTML = escapeHtml(err);
        </script>     

    </body>
</html>