<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.wcdf.model.Player" %>
<%@ page import="com.example.wcdf.model.Indexer" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= request.getAttribute("player") != null && ((Player)request.getAttribute("player")).getPlayerId() > 0 ? "Edit" : "Add" %> Player</title>
    <style>
        * { box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, sans-serif;
            margin: 0; padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: #fff;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h2 {
            margin: 0 0 20px 0;
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover { text-decoration: underline; }
        .error-box {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #dc3545;
        }
        .error-box ul { margin: 10px 0 0 0; padding-left: 20px; }
        .form-group { margin-bottom: 20px; }
        .form-group label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            color: #333;
        }
        .form-group label .required { color: #dc3545; }
        .form-group input, .form-group select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.1);
        }
        .range-info {
            color: #666;
            font-size: 13px;
            margin-top: 5px;
        }
        .btn {
            display: inline-block;
            padding: 10px 25px;
            background: #007bff;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-right: 10px;
        }
        .btn:hover { background: #0056b3; }
        .btn-secondary { background: #6c757d; }
        .btn-secondary:hover { background: #5a6268; }
        .value-group { display: none; }
        .value-group.show { display: block; }
    </style>
</head>
<body>
    <%
    Player player = (Player) request.getAttribute("player");
    List<Indexer> indexers = (List<Indexer>) request.getAttribute("indexers");
    List<String> errors = (List<String>) request.getAttribute("errors");
    boolean isEdit = player != null && player.getPlayerId() > 0;
    %>

    <div class="container">
        <h2><%= isEdit ? "Edit Player" : "Add New Player" %></h2>

        <a href="<%=request.getContextPath()%>/player?action=list" class="back-link">&larr; Back to List</a>

        <% if (errors != null && !errors.isEmpty()) { %>
            <div class="error-box">
                <strong>Please fix the following errors:</strong>
                <ul>
                    <% for (String error : errors) { %>
                        <li><%= error %></li>
                    <% } %>
                </ul>
            </div>
        <% } %>

        <form action="<%=request.getContextPath()%>/player" method="post">
            <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>">
            <% if (isEdit) { %>
                <input type="hidden" name="playerId" value="<%= player.getPlayerId() %>">
            <% } %>

            <div class="form-group">
                <label>Name <span class="required">*</span></label>
                <input type="text" name="name" value="<%= player != null && player.getName() != null ? player.getName() : "" %>" required placeholder="Enter player name">
            </div>

            <div class="form-group">
                <label>Full Name <span class="required">*</span></label>
                <input type="text" name="fullName" value="<%= player != null && player.getFullName() != null ? player.getFullName() : "" %>" required placeholder="Enter full name">
            </div>

            <div class="form-group">
                <label>Age <span class="required">*</span></label>
                <input type="number" name="age" value="<%= player != null && player.getAge() > 0 ? player.getAge() : "" %>" min="1" max="150" required placeholder="Enter age">
            </div>

            <div class="form-group">
                <label>Index</label>
                <select name="indexId" id="indexId" onchange="showValueField()">
                    <option value="" data-min="0" data-max="100">-- Select Index --</option>
                    <% if (indexers != null) {
                        for (Indexer indexer : indexers) {
                            boolean selected = player != null && player.getIndexId() != null && player.getIndexId() == indexer.getIndexId();
                    %>
                        <option value="<%= indexer.getIndexId() %>"
                                data-min="<%= indexer.getValueMin() %>"
                                data-max="<%= indexer.getValueMax() %>"
                                <%= selected ? "selected" : "" %>>
                            <%= indexer.getName() %> (<%= indexer.getValueMin() %> - <%= indexer.getValueMax() %>)
                        </option>
                    <%
                        }
                    } %>
                </select>
            </div>

            <div class="form-group value-group" id="valueGroup">
                <label>Value <span class="required">*</span></label>
                <input type="number" name="indexValue" id="indexValue"
                       value="<%= player != null && player.getIndexValue() != null ? player.getIndexValue() : "" %>"
                       placeholder="Enter value">
                <div id="rangeInfo" class="range-info"></div>
            </div>

            <div style="margin-top: 25px;">
                <button type="submit" class="btn"><%= isEdit ? "Update Player" : "Save Player" %></button>
                <button type="reset" class="btn btn-secondary" onclick="setTimeout(showValueField, 10)">Reset</button>
            </div>
        </form>
    </div>

    <script>
        function showValueField() {
            var select = document.getElementById('indexId');
            var valueGroup = document.getElementById('valueGroup');
            var valueInput = document.getElementById('indexValue');
            var rangeInfo = document.getElementById('rangeInfo');

            if (select.value !== '') {
                var opt = select.options[select.selectedIndex];
                var min = opt.getAttribute('data-min');
                var max = opt.getAttribute('data-max');

                valueGroup.classList.add('show');
                valueInput.min = min;
                valueInput.max = max;
                valueInput.required = true;
                rangeInfo.textContent = 'Valid range: ' + min + ' - ' + max;
            } else {
                valueGroup.classList.remove('show');
                valueInput.required = false;
                valueInput.value = '';
                rangeInfo.textContent = '';
            }
        }
        // Run on page load
        showValueField();
    </script>
</body>
</html>

