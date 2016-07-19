// Javascript functions for spawning resources and managing them.

// Keep a count of all created resources.
var resourceCounter = 0;
var labelCounter = 0;
var variableCounter = 0;
var imageCounter = 0;

var resourceArr = [];
resourceArr.name = "resourceArr";
var labelArr = [];
labelArr.name = "labelArr";
var variableArr = [];
variableArr.name = "variableArr";
var imageArr = [];
imageArr.name = "imageArr";

// Main resource management function. 
// Opens up a dialog box for managing resource parameters as well as
// deletion of a resource.
function manageResource(resourceID, resourceDivID){
  
  // Set up a dialog box for editing a resource.
  // Dialog box effects when opening and closing one.
  $( '#resourceControl' ).dialog({
    autoOpen: false,
    show: {
      effect: 'blind',
      duration: 500
    },
    hide: {
      //effect: 'explode',
      effect: 'blind',
      duration: 500
    },
    title: 'New ' + document.getElementById(resourceID).getAttribute('resourceType')
  });

  // Reset the resourceControl div each time a resource is clicked.
  $('#resourceControl').empty();

  // Get the value of the resource
  var resourceValue = document.getElementById(resourceID).textContent;
  if (document.getElementById(resourceID).tagName.toLowerCase() == "input")
	resourceValue = document.getElementById(resourceID).value;
  if (document.getElementById(resourceID).tagName.toLowerCase() == "img")
	resourceValue = document.getElementById(resourceID).src;

  // Display a resource's ID.
  var id_label = document.createElement('label');
  id_label.innerHTML = 'Resource ID: ';
  var id_input = document.createElement('input');
  id_input.setAttribute('id', 'id_input');
  //id_input.setAttribute('readOnly', 'true');
  $(id_input).val(resourceID);
  // Select the id field when the dialog opens
  $(id_input).select();
  //id_input.addEventListener('change', function(){ setResource(resourceID) });

  // Display a resource's value.
  var value_label = document.createElement('label');
  value_label.innerHTML = 'Resource Value: ';
  var value_input = document.createElement('input');
  value_input.id = 'value_input';
  $(value_input).val(resourceValue);
  //value_input.addEventListener('change', function(){ setResource(resourceID) });

  // Display a resource's dimensions - width
  var width_label = document.createElement('label');
  width_label.innerHTML = 'Resource Width: ';
  var width_input = document.createElement('input');
  width_input.id = 'width_input';
  $(width_input).val($(document.getElementById(resourceID).parentElement).width());
  //width_input.addEventListener('change', function(){ setResource(resourceID) });

  // Display a resource's dimensions - height
  var height_label = document.createElement('label');
  height_label.innerHTML = 'Resource Height: ';
  var height_input = document.createElement('input');
  height_input.id = 'height_input';
  $(height_input).val($(document.getElementById(resourceID).parentElement).height());
  //height_input.addEventListener('change', function(){ setResource(resourceID) });

  var changeButton = document.createElement('button');
  changeButton.id = 'changeButton';
  changeButton.innerHTML = 'Okay';
  changeButton.addEventListener('click', function(){ setResource(resourceID) });

  var deleteButton = document.createElement('button');
  deleteButton.id = 'deleteButton';
  deleteButton.innerHTML = 'Delete Resource';
  deleteButton.addEventListener('click', function(){ delResource(resourceID,resourceDivID) });

  var bringToFront = document.createElement('button');
  bringToFront.id = 'bringToFront';
  bringToFront.innerHTML = 'Bring to Front';
  bringToFront.addEventListener('click', function(){ bringDivToFront(resourceDivID) });

  var sendToBack = document.createElement('button');
  sendToBack.id = 'sendToBack';
  sendToBack.innerHTML = 'Send to Back';
  sendToBack.addEventListener('click', function(){ sendDivToBack(resourceDivID) });

  $( '#resourceControl' ).append(id_label,id_input,'<br>',value_label,value_input,'<br>');
  $( '#resourceControl' ).append(width_label,width_input,'<br>',height_label,height_input,'<br>');
  $( '#resourceControl' ).append(bringToFront, sendToBack, '<br>');
  $( '#resourceControl' ).append(deleteButton, '<br>', changeButton);

  // Clicks the Okay button when the user presses Enter in any of the input boxes
  $('#id_input').keypress(function(e){
    if (e.which == 13){
	console.log("Pressing Okay with resource " + resourceID);
	console.log("resourceDivID is " + resourceDivID + ", and its child is " + document.getElementById(resourceDivID).firstChild.id);
        //$("#changeButton").click();
	setResource(resourceID);
    }
  });
  $('#value_input').keypress(function(e){
    if (e.which == 13){
	console.log("Pressing Okay with resource " + resourceID);
	console.log("resourceDivID is " + resourceDivID + ", and its child is " + document.getElementById(resourceDivID).firstChild.id);
        //$("#changeButton").click();
	setResource(resourceID);
    }
  });
  $('#width_input').keypress(function(e){
    if (e.which == 13){
	console.log("Pressing Okay with resource " + resourceID);
	console.log("resourceDivID is " + resourceDivID + ", and its child is " + document.getElementById(resourceDivID).firstChild.id);
        //$("#changeButton").click();
	setResource(resourceID);
    }
  });
  $('#height_input').keypress(function(e){
    if (e.which == 13){
	console.log("Pressing Okay with resource " + resourceID);
	console.log("resourceDivID is " + resourceDivID + ", and its child is " + document.getElementById(resourceDivID).firstChild.id);
        //$("#changeButton").click();
	setResource(resourceID);
    }
  });

  $('#resourceControl').dialog('open');
}

function bringDivToFront(resourceDivID) {
  $('#sheetContainer').append(document.getElementById(resourceDivID));
}
function sendDivToBack(resourceDivID) {
  $('#sheetContainer').prepend(document.getElementById(resourceDivID));
}

function setResource(resourceID){
  var temp = document.getElementById(resourceID);
  var newId = document.getElementById('id_input');
  var newVal = document.getElementById('value_input');
  var newWidth = document.getElementById('width_input');
  var newHeight = document.getElementById('height_input');
  var sheet = document.getElementById('sheetContainer');
  var maxWidth = $(sheet).width();
  var maxHeight= $(sheet).height();
  console.log('Changing resource: ' + resourceID);
  console.log('Resource type is: ' + temp.getAttribute('resourceType'));

  // Change dimensions
  console.log('Changing dimensions.');
  console.log('Current dimensions: ' + $(temp).width() + " x " + $(temp).height());
  console.log('New dimensions: ' + $(newWidth).val() + " x " + $(newHeight).val());
  if ($(newWidth).val()) {
	if ($(newWidth).val() > maxWidth) {
	  window.alert("This value exceeds the maximum width of " + maxWidth + "!");
	  //$(newWidth).val($(temp).width());
	  $(newWidth).val($(temp.parentElement).width());
	}
	else if ($(newWidth).val() < 50) {
	  window.alert("A block cannot have a width less than 50!");
	  $(newWidth).val($(temp.parentElement).width());
	  //$(newWidth).val($(temp).width());
	}
	else {
	  //$(temp).width($(newWidth).val());
	  temp.width = $(newWidth).val();
	  $(temp).css("width", $(newWidth).val());
	  temp.parentElement.style.width = $(newWidth).val() + "px"; 
	}
  }
  if ($(newHeight).val()) {
	if ($(newHeight).val() > maxHeight) {
	  window.alert("This value exceeds the maximum height of " + maxHeight + "!");
	  $(newHeight).val($(temp.parentElement).height());
	}
	else if ($(newHeight).val() < 50) {
	  window.alert("A block cannot have a height less than 50!");
	  $(newHeight).val($(temp.parentElement).height());
	}
	else {
	  temp.height = $(newHeight).val();
	  $(temp).css("height", $(newHeight).val());
	  temp.parentElement.style.height= $(newHeight).val() + "px"; 
	}
  }
  //temp.style.width = $('width_input').val();
  //temp.style.height = $('height_input').val();

  //Change id
  var tempArr = resourceArr;
  if (temp.getAttribute('id') != $(newId).val()) {
	switch(temp.getAttribute('resourceType')) {
	  case 'label':
	    tempArr = labelArr;
	    break;
	  case 'variable':
		console.log('Deleting a resource... checking calculations...');
		console.log('calcCounter is ' + calcCounter);
		var regExp = /\[([^\]]+)\]/g;
		for (var k = 0; k < calcCounter; k++) {
		  var variables = [];
		  var tempCalc = calcArr[k].value;
		  if (calcArr[k].targetVar == temp.id) {
			window.alert('This variable is targeted by the calculation ' + calcArr[k].name + '! It cannot be changed!');
			return false;
		  }
		  // Fill the variables array with the strings inside square brackets (excluding the brackets themselves)
		  tempCalc.replace(regExp, function(g0,g1) { variables.push(g1); });
		  if (variables) {
		    for (var i = 0; i < variables.length; i++) {
		      console.log('Found ' + variables[i] + ' in the calculation string.');
		      if (variables[i] == temp.id) {
			window.alert('This variable is used in the calculation ' + calcArr[k].name + '! It cannot be changed!');
			return false;
		      }	
		    }
		  }
	        }
	    tempArr = variableArr;
	    break;
	  case 'image':
	    tempArr = imageArr;
	    break;
	}

	if (idAlreadyExists($(newId).val())) {
		var attemptedId = $(newId).val()
		while (idAlreadyExists($(newId).val())) {
			$(newId).val($(newId).val() + "_");			
		}
		window.alert("The ID " + attemptedId + " already existed. This resource's name has been changed to " + $(newId).val());
	}
	while (idAlreadyExists($(newId).val())) {
		$(newId).val($(newId).val() + "_");			
	}
	while (!($(newId).val())) {
		$(newId).val('blank');
		while (idAlreadyExists($(newId).val())) {
			$(newId).val($(newId).val() + "_");			
		}
	}
	console.log("New id is " + $(newId).val());
	if (!($(newId).val())) {
		$(newId).val('blank');			
		while (idAlreadyExists($(newId).val())) {
			$(newId).val($(newId).val() + "_");			
		}
	}
	console.log("Old id: " + temp.getAttribute('id') + "; New id: " + $(newId).val());

	$(temp).prop('id', $(newId).val());
  }

  // Change value
  switch(temp.getAttribute('resourceType')){
    case 'label':
      if ($(temp).text != $(newVal).val()) {
	      console.log('Changing a label.');
	      $(temp).text($(newVal).val());
      }
      break;
    case 'variable':
      if ($(temp).val() != $(newVal).val()) {
	      console.log('Changing a variable.');
	      temp.setAttribute('resourceValue', $(newVal).val());
	      $(temp).val($(newVal).val());
      }
      break;
    case 'image':
      if ($(temp).attr("src") != $(newVal).val()) {
	      console.log('Changing an image.');
	      temp.setAttribute('resourceValue', $(newVal).val());

	      // TODO: upload image instead of urls
	      $(temp).attr("src", $(newVal).val());
	      
	      // Load a new image object to get its dimensions
	      var img = new Image();
	      img.src = $(newVal).val();
	      // img needs to load before we can access its dimensions
	      img.onload = function () {
		var width = (img.width > 50 ? img.width : 50);
		var height = (img.height > 50 ? img.height : 50);
		// Change our original image's dimensions
		$(temp).width(width);
		$(temp).height(height);
		img.remove();

		// We'll need to reduce the image's size while maintaing aspect ratio
		var ratio = Math.min(maxWidth / $(temp).width(), maxHeight / $(temp).height());
		if ($(temp).width() > maxWidth) {
			$(temp).css("width", $(temp).width() * ratio);
			$(temp).css("height", $(temp).height() * ratio);
		}
		if ($(temp).height() > maxHeight) {
			$(temp).css("width", $(temp).width() * ratio);
			$(temp).css("height", $(temp).height() * ratio);
		}

		temp.parentElement.style.width = temp.width + "px"; 
		temp.parentElement.style.height = temp.height + "px"; 

		// Update the width and height shown in the dialogue boxes
		$(newWidth).val($(temp).width());
		$(newHeight).val($(temp).height());
	}
     }
      break;
    default:
      console.log('Nothing changed due to unknown resource type.');
  }

    // Change the hide effect to match the opening effect
    $( '#resourceControl' ).dialog({
    hide: {
      effect: 'blind',
      duration: 500
    }
  });

  // Close resourceControl when the Okay button is pressed
  $('#resourceControl').dialog ('close');

}

function delResource(resourceID,resourceDivID){
 
  function getResourceIndex(resourceID, resourceType){

    tempArr = [];

    switch (resourceType){
      case 'label':
        tempArr = labelArr;
        break;
      case 'variable':
        tempArr = variableArr;
        break;
      case 'image':
        tempArr = imageArr;
        break;
      default:
        console.log('Unknown resource type. Not finding resource index.');
    }

    for (i = 0; i < tempArr.length; i++){
      if (tempArr[i].id == resourceID){
        //console.log('We found it');
	console.log(resourceID + " was found in " + tempArr.name + "[" + i + "]");
	return i;
      }
    }
  }

  var tempResource = document.getElementById(resourceID);
  if (tempResource.getAttribute('resourceType') == 'variable') {
	console.log('Deleting a resource... checking calculations...');
	console.log('calcCounter is ' + calcCounter);
	var regExp = /\[([^\]]+)\]/g;
	for (var k = 0; k < calcCounter; k++) {
	  var variables = [];
	  var temp = calcArr[k].value;
	  if (calcArr[k].targetVar == temp.id) {
	  	window.alert('This variable is targeted by the calculation ' + calcArr[k].name + '! It cannot be deleted!');
		return false;
	  }	
	  // Fill the variables array with the strings inside square brackets (excluding the brackets themselves)
	  temp.replace(regExp, function(g0,g1) { variables.push(g1); });
	  if (variables) {
	    for (var i = 0; i < variables.length; i++) {
	      console.log('Found ' + variables[i] + ' in the calculation string.');
	      if (variables[i] == tempResource.id) {
		window.alert('This variable is used in the calculation ' + calcArr[k].name + '! It cannot be deleted!');
		return false;
	      }	
	    }
          }
      }
  }

  // Reduce the resource counters
  var tempResource = document.getElementById(resourceID);
  switch (tempResource.getAttribute('resourceType')){
    case 'label':
      console.log('reducing labelCounter');
      labelCounter--;
      if (labelCounter < 0) { labelCounter = 0 }
      break;
    case 'variable':
      console.log('reducing variableCounter');
      variableCounter-- ;
      if (variableCounter < 0) { variableCounter = 0 }
      break;
    case 'image':
      console.log('reducing imageCounter');
      imageCounter-- ;
      if (imageCounter < 0) {imageCounter = 0 }
      break;
    default:
      console.log('An unknown type was found. Not reducing any counters.');
  } 
  // Remove the element from the array
  index = getResourceIndex(resourceID, tempResource.getAttribute('resourceType'));
  tempArr.splice(index, 1);


  // Remove the div from the array
  divIndex = findResourceIdInArray(resourceArr, resourceDivID);
  resourceArr.splice(divIndex, 1);
  resourceCounter--;

  var tempDiv = document.getElementById(resourceDivID);
  var tempResource = document.getElementById(resourceID);
  console.log('Removing resourceDiv: ' + resourceDivID + ', which is ' + tempDiv);
  console.log('Inside it is ' + resourceID + ', which is ' + tempResource);
  //$(tempDiv).hide("explode", {pieces: 16}, 500);
  $(tempResource).hide("explode", {pieces: 16}, 500);
  $(tempDiv).remove();
  //$(tempResource).remove();

  // Change the hide effect
  $( '#resourceControl' ).dialog({
    hide: {
      effect: 'explode',
      duration: 500
    }
  });

  // Close resourceControl when a resource is deleted
  $('#resourceControl').dialog ('close');
}

// This is for us, for testing and such
function listResources(tempArr, tempCounter) {
	console.log("Listing the " + tempCounter + " elements of array " + tempArr.name);
	for (i = 0; i < tempCounter; i++) {
		console.log("↪" + tempArr[i].id);
	}
}

function findResourceIdInArray(tempArr, tempID) {
	var elementPos = tempArr.map(function(element) { return element.id; }).indexOf(tempID);
	if (elementPos != -1) console.log(tempID + " was found in " + tempArr.name + "[" + elementPos + "]");
	return elementPos;
}

// Returns true if newId exists in any of the three specialized resource arrays
function idAlreadyExists(newId) {
	return (findResourceIdInArray(labelArr, newId) != -1 || findResourceIdInArray(variableArr, newId) != -1 || findResourceIdInArray(imageArr, newId) != -1);
}

function createResourceDiv(){
  var newResourceDiv = document.createElement('div');
  newResourceDiv.className = 'resourceDiv block';
  newResourceDiv.id = 'resourceDiv' + resourceCounter;
  // Force a unique name... might cause problems?
  while (findResourceIdInArray(resourceArr, newResourceDiv.id) != -1) {
	newResourceDiv.id = newResourceDiv.id + '_';	
  }
  newResourceDiv.setAttribute('resourceType', 'resourceDiv');
  newResourceDiv.setAttribute('data-x', 0);
  newResourceDiv.setAttribute('data-y', 0);
  resourceArr.push(newResourceDiv);

  return newResourceDiv;
}

function spawnLabel(){
  var newLabel = document.createElement('label');
  var width = 50;
  var height = 50;
  newLabel.id = 'label' + labelCounter;
  //if (findResourceIdInArray(labelArr, newLabel.id) != -1) {
  // Force a unique name... might cause problems?
  while (idAlreadyExists(newLabel.id)) {
	newLabel.id = newLabel.id + '_';	
  }
  newLabel.innerHTML = 'Label value';
  newLabel.setAttribute('resourceType', 'label');
  labelArr.push(newLabel);

  newLabel.width = width;
  newLabel.height = height;
  $(newLabel).css("width", width);
  $(newLabel).css("height", height);

  var resourceDiv = createResourceDiv();
  resourceDiv.addEventListener('dblclick', function(){ manageResource(newLabel.id, resourceDiv.id) });
  resourceDiv.style.width = '50px';
  resourceDiv.style.height = '50px';
  resourceDiv.style.backgroundColor = 'transparent';
  resourceDiv.appendChild(newLabel);

  labelCounter++;
  resourceCounter++;
  document.getElementById('sheetContainer').appendChild(resourceDiv);

  manageResource(newLabel.id, resourceDiv.id);
}

function spawnVariable(){
  var newVariable = document.createElement('input');
  var width = 50;
  var height = 50;
  newVariable.id = 'variable' + variableCounter;
  //if (findResourceIdInArray(variableArr, newVariable.id) != -1) {
  // Force a unique name... might cause problems?
  while (idAlreadyExists(newVariable.id)) {
	newVariable.id = newVariable.id + '_';	
  }
  newVariable.placeholder = 'Variable value';
  newVariable.readOnly = 'true';
  newVariable.setAttribute('resourceType', 'variable');
  newVariable.setAttribute('resourceValue', 0);
  variableArr.push(newVariable);

  newVariable.width = width;
  newVariable.height = height;
  $(newVariable).css("width", width);
  $(newVariable).css("height", height);

  var resourceDiv = createResourceDiv();
  resourceDiv.addEventListener('dblclick' , function(){ manageResource(newVariable.id, resourceDiv.id) });
  resourceDiv.style.width = '50px';
  resourceDiv.style.height = '50px';
  resourceDiv.style.backgroundColor = 'transparent';
  resourceDiv.appendChild(newVariable);

  variableCounter++;
  resourceCounter++;
  document.getElementById('sheetContainer').appendChild(resourceDiv);

  manageResource(newVariable.id, resourceDiv.id);
}

function spawnImage() {
  var newImage= document.createElement('img');
  newImage.id = 'image' + imageCounter;
  //if (findResourceIdInArray(imageArr, newImage.id) != -1) {
  // Force a unique name... might cause problems?
  while (idAlreadyExists(newImage.id)) {
	newImage.id = newImage.id + '_';	
  }
  //newImage.placeholder = 'http://orig09.deviantart.net/fe9c/f/2014/231/5/5/dark_souls_sif_by_zedotagger-d7vvhqh.gif';
  newImage.placeholder = 'http://www.animatedgif.net/animals/dogs/walkdog_e0.gif';
  newImage.src = newImage.placeholder;
  newImage.setAttribute('resourceType', "image");
  newImage.setAttribute('resourceValue', 0);
  imageArr.push(newImage);

  // Load a new image object to get its dimensions
  var img = new Image();
  img.src = newImage.placeholder;
  // img needs to load before we can access its dimensions
  img.onload = function () {
	  var width = (img.width > 50 ? img.width : 50);
	  var height = (img.height > 50 ? img.height : 50);
	  // Change our original image's dimensions
	  // This is done do that resourceDiv will receive proper dimensions instead of undefined, as it would if the image was not fully loaded
	  newImage.width = width;
	  newImage.height = height;
	  $(newImage).css("width", width);
	  $(newImage).css("height", height);
	  img.remove();

	  var resourceDiv = createResourceDiv();
	  resourceDiv.addEventListener('dblclick' , function(){ manageResource(newImage.id, resourceDiv.id) });
	  resourceDiv.style.width = newImage.width + 'px';
	  resourceDiv.style.height = newImage.height + 'px';
	  resourceDiv.style.backgroundColor = 'transparent';
	  resourceDiv.appendChild(newImage);

	  imageCounter++;
	  resourceCounter++;
	  document.getElementById('sheetContainer').appendChild(resourceDiv);

	  manageResource(newImage.id, resourceDiv.id);
  }
}
