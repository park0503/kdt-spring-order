<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
        <title>Home</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    </head>
    <body class="container-fluid">
        <h1>KDT Spring Web</h1>
        <img src="<c:url value="/resources/profile.jpg" />" class="img-fluid">
        <p>The time on the server is ${serverTime}</p>
        <h2>Customer Table</h2>
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Name</th>
                <th scope="col">Email</th>
                <th scope="col">Created At</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="customer" items="${customers}">
            <tr>
                <th scope="row">${customer.customerId}</th>
                <td>${customer.name}</td>
                <td>${customer.email}</td>
                <td>${customer.createdAt}</td>
            </tr>
            </c:forEach>
            </tbody>
        </table>
    </body>
</html>