<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>JSP - Hello World</title>
  <script src="js/jquery-3.7.0.min.js"></script>
  <script src="js/main.js"></script>
  <link rel="stylesheet" href="styles/main.css">
</head>
<body>
<pre id="getTableResult">zzzz</pre>
<h1><%= "Legretto" %></h1>
<br/>
<button id="printAllCards">Print All Cards</button>
<button id="getTable">Get Table</button>
<div id="allCardsBox"></div>


<!-- templates-->
<div id="templates" style="display: none">
  <div class="card"></div>
</div>
</body>
</html>