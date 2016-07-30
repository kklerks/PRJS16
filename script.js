var counter = 0;
var resourceCounter = 0;
var savedJson = undefined;

interact('#sheetContainer').dropzone({
    accept: '.block',
    overlap: 0.75
});

interact('.block')
    .draggable({
        // enable inertial throwing
        inertia: true,
        // keep the element within the area of it's parent
        restrict: {
            restriction: "parent",
            endOnly: false,
            elementRect: { top: 0, left: 0, bottom: 1, right: 1 }
        },
        snap: {
            targets: [
                interact.createSnapGrid({ x: 20, y: 20 })
            ],
            range: Infinity,
            relativePoints: [ { x: 0, y: 0 } ]
        },
        // enable autoScroll
        autoScroll: true,

        // call this function on every dragmove event
        onmove: dragMoveListener,
    })
    .resizable({
        preserveAspectRatio: false,
        edges: { left: true, right: true, bottom: true, top: true },
        restrict: {
            restriction: "parent",
            endOnly: false
        },
        snap: {
            targets: [
                interact.createSnapGrid({ x: 20, y: 20 })
            ],
            range: Infinity,
            relativePoints: [ { x: 0, y: 0 } ]
        }
    })
    .on('resizemove', function (event) {
        var target = event.target,
            x = (parseFloat(target.getAttribute('data-x')) || 0),
            y = (parseFloat(target.getAttribute('data-y')) || 0);

        // update the element's style
	// Don't allow it to become smaller than 50x50
	if (event.rect.width >= 50) {
		target.style.width = event.rect.width + 'px';
	}
	if (event.rect.height >= 50) {
		target.style.height = event.rect.height + 'px';
	}

	console.log("Target dimensions: " + target.style.width + " x " + target.style.height);

	// If the div has a child (eg an img), resize that too
	if (target.childNodes[0]) {
		// Don't allow it to become smaller than 50x50
		console.log("Child element dimensions: " + $(target.childNodes[0]).width() + " x " + $(target.childNodes[0]).height());
		console.log("event.rect dimensions: " + event.rect.width + " x " + event.rect.height);
		if (event.rect.width >= 50) {
			// Setting the width normally doesn't seem to work if we set it previously with jQuery? Using jQuery works in any case
			//target.childNodes[0].width = event.rect.width;
			$(target.childNodes[0]).width($(target).width());//event.rect.width);
		}
		if (event.rect.height >= 50) {
			//target.childNodes[0].height = event.rect.height;
			$(target.childNodes[0]).height($(target).height());//event.rect.height);
		}

		// And make sure the div is smaller than its child (in case of eg label with word wrap)
		console.log("Child element dimensions: " + $(target.childNodes[0]).width() + " x " + $(target.childNodes[0]).height());
		if ($(target.childNodes[0]).width() > $(target).width()) {//event.rect.width) {
			target.style.width = $(target.childNodes[0]).width() + 'px';
			console.log("Changing target width to match child's...");
		}
		if ($(target.childNodes[0]).height() > $(target).height()) {//event.rect.height) {
			target.style.height= $(target.childNodes[0]).height() + 'px';
			console.log("Changing target height to match child's...");
		}
	}

        // translate when resizing from top or left edges
        x += event.deltaRect.left;
        y += event.deltaRect.top;

        target.style.webkitTransform = target.style.transform =
            'translate(' + x + 'px,' + y + 'px)';

        target.setAttribute('data-x', x);
        target.setAttribute('data-y', y);
    });

function dragMoveListener (event) {
    var target = event.target,
    // keep the dragged position in the data-x/data-y attributes
        x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx,
        y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;

    // translate the element
    target.style.webkitTransform =
        target.style.transform =
            'translate(' + x + 'px, ' + y + 'px)';

    // update the posiion attributes
    target.setAttribute('data-x', x);
    target.setAttribute('data-y', y);
}

// this is used later in the resizing and gesture demos
window.dragMoveListener = dragMoveListener;

function spawnBlock () {
    var newBlock = document.createElement('div');
    newBlock.className = 'block';
    newBlock.id = 'block' + counter;
    newBlock.setAttribute('type', "TEST");
    newBlock.setAttribute('data-x', 0);
    newBlock.setAttribute('data-y', 0);
    counter++;
    document.getElementById('sheetContainer').appendChild(newBlock);
}

function getSaveName () {

    //TODO make this a proper prompt, with other sheet names from database

    var msg = "File name: ";
    var name;
    var validate = false;

    while (!validate) {
        name = prompt(msg);

        if (name == '') {
            msg = "Please enter a file name:";
        } else if (!(name === null)) {
            if (name.length > 32) {
                msg = "Please choose a shorter name (32 characters max):";
            } else {
                validate = true; //name is valid
            }
        } else {
            //name is null (user pressed cancel)
            validate = true; //used to exit the loop
        }
    }

    return name;
}

function saveSheet () {
    var file = {};
    file.blocks = {};
    file.calculations = {};
    file.name = getSaveName();
    var allBlocks = document.getElementsByClassName('block');

    if (!(file.name === null)) {
        // Make sure that there are no identical IDs for elements
        // Hideous but functional (?)
        for (var i = 0; i < allBlocks.length; i++) {
          for (var j = 0; j < allBlocks.length; j++) {
            if (i != j && allBlocks[i].firstChild && allBlocks[j].firstChild && (allBlocks[i].firstChild.id == allBlocks[j].firstChild.id || allBlocks[i].id == allBlocks[j].id)) {
              console.log("i is " + i + " and j is " + j + " ... " + allBlocks[i].id + " is the same as " + allBlocks[j].id);
              window.alert("Identical block IDs detected! Something is terribly wrong! The sheet cannot be saved!");
              return 0; 
            }
          }
        }
        for (var i = 0; i < allBlocks.length; i++) {
            var block = allBlocks[i];
	    console.log("Saving " + block.id);
            file.blocks['block' + i] = {
                x      : block.getAttribute('data-x'),
                y      : block.getAttribute('data-y'),
                width  : block.style.width,
                height : block.style.height,
                type   : block.getAttribute('resourceType'),
                value  : block.getAttribute('resourceValue'),
	        id     : block.id,
	        html   : block.innerHTML,
	        childVal : (block.firstChild ? $(block.firstChild).val() : 0)
            };
        }

	for (var i = 0; i < calcCounter; i++) {
		var newCalc = new Object();
		newCalc.value = calcArr[i].value;
		newCalc.name = calcArr[i].name;
		console.log("Saving the calculation " + newCalc.name);
		newCalc.targetVar = calcArr[i].targetVar;
		newCalc.targetPlayer = calcArr[i].targetPlayer;
		file.calculations[i] = newCalc;
	}
    
        var container = document.getElementById('sheetContainer');
        if (container.style.height !== "") {
            file.height = container.style.height;
        }
        saveToDatabase(file)
        savedJson = file;
    }
}

function newSheet() {
    var container = document.getElementById('sheetContainer');
    console.log("resourceCounter is " + resourceCounter);
    if (container.firstChild) {
        if (resourceCounter > 0 && confirm("This will clear any unsaved changes. Are you sure?")) {
            while (container.firstChild) {
                container.removeChild(container.firstChild);
            }
            resourceArr = [];
	    resourceArr.name = 'resourceArr';
	    resourceCounter = 0;
	    labelArr = [];
	    labelArr.name = 'labelArr';
	    labelCounter = 0;
	    variableArr = [];
	    variableArr.name = 'variableArr';
	    variableCounter = 0;
	    toggleCalcButton();
	    imageArr = [];
	    imageArr.name = 'imageArr';
	    imageCounter = 0;
	    calcArr = [];
	    calcArr.name = 'calcArr';
	    calcCounter = 0;
	    container.style.height = "500px";
        }
    }
}

function changeSheetHeight(method) {
    var container = document.getElementById('sheetContainer');
    if (container.style.height === "") {
        container.style.height = "500px";
    }
    if (method === 0) { //increase
        container.style.height = (parseInt(container.style.height) + 100) + "px";
    } else { //decrease
        if (parseInt(container.style.height) > 500) {
            container.style.height = (parseInt(container.style.height) - 100) + "px";
        }
    }
}

function loadSheet (sheet_id) {
    //Do something to confirm current sheet is saved or empty

    var file = savedJson;
    var container = document.getElementById('sheetContainer');
    resourceArr = [];
    resourceArr.name = 'resourceArr';
    resourceCounter = 0;
    labelArr = [];
    labelArr.name = 'labelArr';
    //labelCounter = 0;
    variableArr = [];
    variableArr.name = 'variableArr';
    //variableCounter = 0;
    imageArr = [];
    imageArr.name = 'imageArr';
    //imageCounter = 0;
    calcArr = [];
    calcArr.name = 'calcArr';
    calcCounter = 0;
    
    while (container.firstChild) {
	console.log("Removing " + container.firstChild);
	if (container.firstChild.firstChild) {
		var resource = container.firstChild.firstChild;
		console.log('|-> which contains one ' + resource.getAttribute('resourceType'));
	        resourceCounter--;
		switch (resource.getAttribute('resourceType')) {
			case 'label':
				console.log('Reducing labelCounter.');
				labelCounter--;
				break;
			case 'variable':
				console.log('Reducing variableCounter.');
				variableCounter--;
				console.log('variableCounter is ' + variableCounter);
			        toggleCalcButton();
				break;
			case 'image':
				console.log('Reducing imageCounter.');
				imageCounter--;
				break;
		}
	}
        container.removeChild(container.firstChild);
    }
    
    //var fetchedSheet = openSheetList();
    //console.log(fetchedSheet);
    //console.log(JSON.stringify(fetchedSheet));

    //file = fetchedSheet;
    //readFromJson(file);
    file = fetchSheetFromServer(sheet_id);

    var count = 0;
    for (var key in file.blocks) {
        if (file.blocks.hasOwnProperty(key)) {
	 // We need a closure function, otherwise the event listener will always receive as arguments the id of the most recent block  
	 // We probably shouldn't be adding the event listeners here anyway but until we fix up the loading this will work
	 (function() {
            container.appendChild(document.createElement('div'));
            var newBlock = container.lastChild;
            newBlock.className = 'resourceDiv block';
            newBlock.setAttribute('data-x', file.blocks[key].x);
            newBlock.setAttribute('data-y', file.blocks[key].y);
	    newBlock.style.width = file.blocks[key].width;
            newBlock.style.height = file.blocks[key].height;
            newBlock.style.transform = "translate(" + file.blocks[key].x + "px, " + file.blocks[key].y + "px)";
            newBlock.setAttribute('resourceType', file.blocks[key].type);
            newBlock.setAttribute('resourceValue', file.blocks[key].value);
		console.log("Setting resourceValue to " + file.blocks[key].value);
	    newBlock.id = file.blocks[key].id;
	    newBlock.innerHTML = file.blocks[key].html;
	    resourceArr.push(newBlock);
	    if (newBlock.firstChild) {
		newResource = newBlock.firstChild;
		$(newBlock.firstChild).val(file.blocks[key].childVal);
		resourceCounter++;
		switch (newResource.getAttribute('resourceType')) {
			case 'label':
				labelCounter++;
				labelArr.push(newResource);
				break;
			case 'variable':
				variableCounter++;
				console.log('variableCounter is ' + variableCounter);
			        toggleCalcButton();
				variableArr.push(newResource);
				break;
			case 'image':
				imageCounter++;
				imageArr.push(newResource);
				break;
		}	
	    }
	    var resourceElementID = newBlock.firstChild.id;
	    var divElementID = newBlock.id; 
	    newBlock.addEventListener('dblclick', function(){ manageResource(resourceElementID, divElementID) });
	 }());
        }
    }
    for (var key in file.calculations) {
	if (file.calculations.hasOwnProperty(key)) {
		var newCalc = new Object();
		newCalc.value = file.calculations[key].value;
		newCalc.name = file.calculations[key].name;
		console.log("Loading the calculation " + newCalc.name);
		newCalc.targetVar = file.calculations[key].targetVar;
		newCalc.targetPlayer = file.calculations[key].targetPlayer;
		calcArr.push(newCalc);
		calcCounter++;
	}
    }
    if (file.hasOwnProperty('height')) {
        container.style.height = file.height;
    }
}

function saveToJson () {
    var json = {};
    for (var i = 0; i < counter; i++) {
        var block = document.getElementsByClassName('block')[i];
        json[block.getAttribute('id')] = {
            id   : block.getAttribute('id'),
            x    : block.getAttribute('data-x'),
            y    : block.getAttribute('data-y'),
            type : block.getAttribute('resourceType'),
	    value: block.getAttribute('resourceValue'),
        };
    }
    console.log(JSON.stringify(json));
}

function readFromJson (json) {
    var container = document.getElementById('sheetContainer');
    for (var key in json) {
        if (json.hasOwnProperty(key)) {
            var newBlock = document.createElement('div');
            newBlock.className = 'block';
            newBlock.id = json[key].id;
            newBlock.setAttribute('resourceType', json[key].type);
            newBlock.setAttribute('data-x', json[key].data-x);
            newBlock.setAttribute('data-y', json[key].data-y);
            container.appendChild(newBlock);
        }
    }
}
function saveToDatabase(file) {


    var sheet_name = file.name;
    var sheet_type = "SHEET_TYPE";
    var sheet_file = JSON.stringify(file);
    var sheet_version = "0.0";

    var overwrite_prompt = false;
    var overwrite_confirm = "false";

    console.log(sheet_file);

    $.ajax({
        type:'POST',
        url:'us.php',
        data:{SHEET_NAME:sheet_name,SHEET_TYPE:sheet_type,SHEET_FILE:sheet_file,SHEET_VERSION:sheet_version},
        success: function(data) {
            data = JSON.parse(data);
            if (data.status == "SUCCESS") {
                alert('Sheet uploaded');
            } else if (data.status == "WARNING") {
                //currently the only thing to trigger this is to overwrite a sheet

                if (confirm("A sheet with the same name already exists. Overwrite the existing sheet?")) {
                    overwrite_prompt = true;
                    overwrite_confirm = "true";
                    $.ajax({
                        type:'POST',
                        url:'us.php',
                        data:{SHEET_NAME:sheet_name,SHEET_TYPE:sheet_type,SHEET_FILE:sheet_file,SHEET_VERSION:sheet_version,OVERWRITE_CONFIRM:overwrite_confirm},
                        success: function(data) {
                            data = JSON.parse(data);
                            if (data.status == "SUCCESS") {
                                alert('Sheet updated.');
                            } else {
                                alert("Failed to upload the sheet. The server responded:\n" + data.msg);
                            }
                        }
                    });

                }

            } else {
                alert("Failed to upload the sheet. The server responded:\n" + data.msg);
            }
        }
    });
}

function fetchSheetFromServer(sheet_id) {

    //sheet_id = prompt("Load sheet (id): ");

    $.ajax({
        type:'POST',
        url:'ds.php',
        data:{SHEET_ID:sheet_id},
        success: function(data) {
            data = JSON.parse(data);
            //console.log(JSON.stringify(data,null,2));
            if (data.status == "SUCCESS") {
                file = JSON.parse(data.sheet);
                //console.log(file);
            } else {
                alert("Failed to load sheet. The server responded:\n" + data.msg);
            }
        },
        async: false
    });
    return file;
}

function listSheets() {

    var sheet_list;

    $.ajax({
        type:'POST',
        url:'sl.php',
        data:{},
        success: function(data) {
            data = JSON.parse(data);
            //console.log(JSON.stringify(data,null,2));
            if (data.status == "SUCCESS") {
                sheet_list = data.sheet_list;
            } else {
                alert("Failed to get sheet list. The server responded:\n" + data.msg);
            }
        },
        async: false
    });
    return sheet_list;
}

