var mysql = require('mysql');
var db = require('../db.config');
var connection = mysql.createConnection(db['db']);
var query;
var result = {};

var command = req.query.command;
var id = req.query.id;
var name = req.query.name;
var password = req.query.password;
var email = req.query.email;
var status = req.query.status;
var type = req.query.type;
var file = req.query.file;
var version = req.query.version;

switch(command) {
    case "getUser":
        query = getUser(id);
        break;
    case "getUserName":
        query = getUserName(id);
        break;
    case "getUserPassword":
        query = getUserPassword(id);
        break;
    case "getUserEmail":
        query = getUserEmail(id);
        break;
    case "getUserStatus":
        query = getUserStatus(id);
        break;
    case "updateUserName":
        query = updateUserName(name);
        break;
    case "updateUserPassword":
        query = updateUserPassword(password);
        break;
    case "updateUserEmail":
        query = updateUserEmail(email);
        break;
    case "updateUserStatus":
        query = updateUserStatus(status);
        break;
    case "createUser":
        query = createUser(name, password, email, status);
        break;
    case "deleteUser":
        query = deleteUser(id);
        break;
    case "getSheet":
        query = getSheet(id);
        break;
    case "getSheetName":
        query = getSheetName(id);
        break;
    case "getSheetType":
        query = getSheetType(id);
        break;
    case "getSheetFile":
        query = getSheetFile(id);
        break;
    case "getSheetVersion":
        query = getSheetVersion(id);
        break;
    case "updateSheetName":
        query = updateSheetName(id, name);
        break;
    case "updateSheetType":
        query = updateSheetType(id, type);
        break;
    case "updateSheetFile":
        query = updateSheetFile(id, file);
        break;
    case "updateSheetVersion":
        query = updateSheetVersion(id, version);
        break;
    case "createSheet":
        query = createSheet(name, type, file, version);
        break;
    case "deleteSheet":
        query = deleteSheet(id);
        break;
    case "getCalc":
        query = getCalc(id);
        break;
    case "getCalcName":
        query = getCalcName(id);
        break;
    case "getCalcFile":
        query = getCalcFile(id);
        break;
    case "getCalcVersion":
        query = getCalcVersion(id);
        break;
    case "updateCalcName":
        query = updateCalcName(id, name);
        break;
    case "updateCalcFile":
        query = updateCalcFile(id, file);
        break;
    case "updateCalcVersion":
        query = updateCalcVersion(id, version);
        break;
    case "createCalc":
        query = createCalc(name, file, version);
        break;
    case "deleteCalc":
        query = deleteCalc(id);
        break;
    case default:
        //ERROR
        break;
    }
}

connection.connect(function (error) {
    //ERROR
});

connnection.query(query['query'], query['params'], function(error, results, fields) {
    if (error) {
        //PROCESS ERROR
    } else {
        result['results'] = results;
        result['fields'] = fields;
    }
});

connection.end(function(error) {
    //ERROR
});

return result;

var getUser = function(id) {
    var query = {};
    query['query'] = "SELECT user_id, user_name, email, registered FROM Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getUserName = function(id) {
    var query = {};
    query['query'] = "SELECT user_name FROM Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getUserEmail = function(id) {
    var query = {};
    query['query'] = "SELECT email FROM Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getUserPassword = function(id) {
    var query = {};
    query['query'] = "SELECT password_hash FROM Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getUserStatus = function(id) {
    var query = {};
    query['query'] = "SELECT registered FROM Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var updateUserName = function(id, name) {
    var query = {};
    query['query'] = "UPDATE Users SET user_name = ? WHERE user_id = ?";
    query['params'] = [];
    params.push(name);
    params.push(id);
    return query;
}
var updateUserPassword = function(id, password) {
    var query = {};
    query['query'] = "UPDATE Users SET password_hash = ? WHERE user_id = ?";
    query['params'] = [];
    params.push(password);
    params.push(id);
    return query;
}
var updateUserEmail = function(id, email) {
    query['query'] = "UPDATE Users SET email = ? WHERE user_id = ?";
    query['params'] = [];
    params.push(email);
    params.push(id);
    return query;
}
var updateUserStatus = function(id, status) {
    query['query'] = "UPDATE Users SET registered = ? WHERE user_id = ?";
    query['params'] = [];
    params.push(status);
    params.push(id);
    return query;
}
var createUser = function(name, password, email, status) {
    query['query'] = "INSERT INTO TABLE Users(user_name, password_hash, " +
            "email, registered) VALUES (?, ?, ?, ?)";
    query['params'] = [];
    params.push(name);
    params.push(password);
    params.push(email);
    params.push(status);
    return query;
}
var deleteUser = function(id) {
    query['query'] = "DELETE FROM TABLE Users WHERE user_id = ?";
    query['params'] = [];
    params.push(id);
}


var getSheet = function(id) {
    query['query'] = "SELECT * FROM Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getSheetName = function(id) {
    query['query'] = "SELECT sheet_name FROM Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getSheetType = function(id) {
    query['query'] = "SELECT sheet_type FROM Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getSheetFile = function(id) {
    query['query'] = "SELECT sheet_file FROM Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getSheetVersion = function(id) {
    query['query'] = "SELECT version FROM Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var updateSheetName = function(id, name) {
    query['query'] = "UPDATE Sheets SET sheet_name = ? WHERE sheet_id = ?";
    query['params'] = [];
    params.push(name);
    params.push(id);
    return query;
}
var updateSheetType = function(id, type) {
    query['query'] = "UPDATE Sheets SET sheet_type = ? WHERE sheet_id = ?";
    query['params'] = [];
    params.push(type);
    params.push(id);
    return query;
}
var updateSheetFile = function(id, file) {
    query['query'] = "UPDATE Sheets SET sheet_file = ? WHERE sheet_id = ?";
    query['params'] = [];
    params.push(file);
    params.push(id);
    return query;
}
var updateSheetVersion = function(id, version) {
    query['query'] = "UPDATE Sheets SET version = ? WHERE sheet_id = ?";
    query['params'] = [];
    params.push(version);
    params.push(id);
    return query;
}
var createSheet = function(name, type, file, version) {
    query['query'] = "INSERT INTO TABLE Sheets sheet_name, sheet_type, " +
            " sheet_file, version) VALUES (?, ?, ?, ?)";
    query['params'] = [];
    params.push(name);
    params.push(type);
    params.push(file);
    params.push(version);
    return query;
}
var deleteSheet = function(id) {
    query['query'] = "DELETE FROM TABLE Sheets WHERE sheet_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}


var getCalc = function(id) {
    query['query'] = "SELECT * FROM Calculations WHERE calc_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getCalcName = function(id) {
    query['query'] = "SELECT calc_name FROM Calculations WHERE calc_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getCalcFile = function(id) {
    query['query'] = "SELECT calc_file FROM Calculations WHERE calc_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var getCalcVersion = function(id) {
    query['query'] = "SELECT calc_file FROM Calculations WHERE calc_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
var updateCalcName = function(id, name) {
    query['query'] = "UPDATE TABLE Calculations SET calc_name = ? WHERE calc_id = ?";
    query['params'] = [];
    params.push(name);
    params.push(id);
    return query;
}
var updateCalcFile = function(id, file) {
    query['query'] = "UPDATE TABLE Calculations SET calc_file = ? WHERE calc_id = ?";
    query['params'] = [];
    params.push(file);
    params.push(id);
    return query;
}
var updateCalcVersion = function(id, version) {
    query['query'] = "UPDATE TABLE Calculations SET version = ? WHERE calc_id = ?";
    query['params'] = [];
    params.push(version);
    params.push(id);
    return query;
}
var createCalc = function(name, file, version) {
    query['query'] = "INSERT INTO TABLE Calculations(calc_name, calc_file, " +
    "version) VALUES (?, ?, ?)";
    query['params'] = [];
    params.push(name);
    params.push(file);
    params.push(version);
    return query;
}
var deleteCalc = function(id) {
    query['query'] = "DELETE FROM TABLE Calculations WHERE calc_id = ?";
    query['params'] = [];
    params.push(id);
    return query;
}
