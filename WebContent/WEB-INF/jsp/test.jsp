<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<h3>Lista użytkowników:</h3>

<table>
    <tr>
        <td>userID</td>
        <td>login</td>
        <td>eMail</td>
        <td>name</td>
        <td>surname;</td>
    </tr>
    <c:forEach var="i" items="${users}">
        <tr>
            <td>${i.userID}</td>
            <td>${i.login}</td>
            <td>${i.eMail}</td>
            <td>${i.name}</td>
            <td>${i.surname}</td>
        </tr>
    </c:forEach>
</table>