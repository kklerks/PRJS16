$('#loginDiv').dialog({ autoOpen: false });
$('#signupDiv').dialog({ autoOpen: false });
$('#helpDiv').dialog({ autoOpen: false });
$('#sheetListDiv').dialog({ autoOpen: false });
var helpJSON = undefined;
var sheetListJSON = undefined;
function openLogin() {
    $('#loginDiv').dialog("open");
}
function closeLogin() {
    $('#loginDiv').dialog("close");
}
function openSignup() {
    $('#signupDiv').dialog("open");
}
function closeSignup() {
    $('#signupDiv').dialog("close");
}
function openHelp() {
    $.getJSON('help.json', function(response) {
	    $('#helpDiv').dialog({ width: 720 });
	    $('#helpDiv').dialog("open");
	    console.log('helpJSON is ' + response);
	    helpJSON = response;

	    $('#helpText').text(helpJSON[0]['content']);
	    var helpTopics = $('#helpTopics').empty();
	    //Iterate through list of help topics, apply them to the list in the box as links
	    for (var i = 0; i < helpJSON.length; i++) {
		helpTopics.append('<a href="javascript:void(0)" onclick="openTopic(' + i + ');">' +
		    helpJSON[i]["header"] + '</a><br>');
	    }
    });
}
function openTopic(index) {
    $('#helpText').text(helpJSON[index]['content']);
}
function closeHelp() {
    $('#helpDiv').dialog("close");
    helpJSON = undefined;
}
function openSheetList() {
    if (confirm("This will clear any unsaved changes. Are you sure?")) {

        //from newSheet() to clear sheet
        var container = document.getElementById('sheetContainer');
        while (container.firstChild) {
            container.removeChild(container.firstChild);
        }
        container.style.height = "500px";



        $('#sheetListDiv').dialog("open");

        sheetListJSON = listSheets();
        console.log(JSON.stringify(sheetListJSON));

        var sheetList = $('#sheetList').empty();
        for (var i = 0; i < sheetListJSON.length; i++) {
            sheetList.append('<a href="javascript:void(0)" onclick="loadSheet(' + sheetListJSON[i].sheet_id + '); closeSheetList();">' + sheetListJSON[i].sheet_name + '</a><br>');
        }
    }

}
function closeSheetList() {
    $('#sheetListDiv').dialog("close");
    sheetListJSON = undefined;
}
