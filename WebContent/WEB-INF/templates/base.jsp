<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<!DOCTYPE html>
<html>
    <head>
        <title>
            <t:insertAttribute name="title" />
        </title>
        <t:insertAttribute name="scripts" />
    </head>
<body>
    <div id="header">
        <t:insertAttribute name="header" />
    </div>
    <div id="menu">
        <t:insertAttribute name="menu" />
    </div>
    <div id="body">
        <t:insertAttribute name="body" />
    </div>
    <div id="footer">
        <t:insertAttribute name="footer" />
    </div>
</body>
</html>
