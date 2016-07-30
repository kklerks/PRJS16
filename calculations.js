var calcCounter = 0;

var calcArr = [];
calcArr.name = 'calcArr';

function manageCalculations() {
  console.log('Opening the calculations dialog...');
  if (variableCounter == 0) {
	window.alert('There must exist at least one variable on which calculations are to be performed!');
	return false;
  }
  
  $('#calcControl').dialog({
    autoOpen: false,
    show: {
      effect: 'blind',
      duration: 500
    },
    hide: {
      effect: 'blind',
      duration: 500
    },
    modal: true,
    title: 'Calculations'
  });

  $('#calcControl').empty();

  var calcButtonArray = [];

  for (var i=0; i < calcCounter; i++) {
    (function () {
    var temp = i;
    var calcButton = document.createElement('button');
    calcButton.id = 'calcButton' + i;
    console.log('Name is ' + calcArr[i].name);
    calcButton.innerHTML = calcArr[i].name;
    calcButton.addEventListener('click', function(){ newCalculation(temp) });
    $('#calcControl').append($(calcButton));
    $('#calcControl').append('<br />');
    }());
  }

  var newButton = document.createElement('button');
  newButton.id = 'newButton';
  newButton.innerHTML = 'New Calculation';
  newButton.addEventListener('click', function(){ newCalculation() });

  var delButton= document.createElement('button');
  delButton.id = 'delButton';
  delButton.innerHTML = 'Delete Last Calculation';
  delButton.addEventListener('click', function(){ delCalculation() });

  $('#calcControl').append($(newButton));
  //$('#calcControl').append($('<br />'));
  //$('#calcControl').append($(delButton));

  $('#calcControl').dialog('open');
}

function delCalculation(index = -1, confirmDeletion=true) {
	if (calcCounter > 0) {
		if (confirmDeletion && !window.confirm('Really delete ' + (index == -1 ? calcArr[calcCounter-1].name : calcArr[index].name) + '?')) return false;
		if (index == -1) {
			calcArr.pop();
		}
		else {
			calcArr.splice(index, 1);
		}
		calcCounter--;
		if (confirmDeletion) {
			$(".ui-dialog-content").dialog("close");
			manageCalculations();
		}
	}
}

// calc indicates the index of the calculation in calcArr (if editing); -1 indicates a new calculation
function newCalculation(calc = -1) {
  console.log('Opening the new calculation dialog with calc = ' + calc);
  $('#calcControl').dialog('close');

  $('#newCalcControl').dialog({
    autoOpen: false,
    show: {
      effect: 'blind',
      duration: 500
    },
    hide: {
      effect: 'blind',
      duration: 500
    },
    modal: true,
    width: 350,
    title: 'New calculation'
  });

  $('#newCalcControl').empty();

  var name_label = document.createElement('label');
  name_label.innerHTML = 'Calculation name: ';
  var value_label= document.createElement('label');
  value_label.innerHTML = 'Calculation: ';
  var nameInput = document.createElement('input');
  nameInput.id = 'nameInput';
  if (calc != -1) nameInput.value = calcArr[calc].name;
  var textInput = document.createElement('input');
  textInput.id = 'calcInput';
  if (calc != -1) textInput.value = calcArr[calc].value;

  var targetVar = $('<select>');
  for (var i=0; i<variableArr.length; i++) {
    targetVar.append($('<option>').attr('value', variableArr[i].id).text(variableArr[i].id));
  }
  if (calc != -1) $(targetVar).val(calcArr[calc].targetVar);

  var targetEnemy = $('<select>');
  targetEnemy.append($('<option>').attr('value', 'self').text('Self'));
  targetEnemy.append($('<option>').attr('value', 'player').text('Other player'));
  targetEnemy.append($('<option>').attr('value', 'manyPlayers').text('Multiple Players'));
  if (calc != -1) $(targetEnemy).val(calcArr[calc].targetPlayer);

  var createButton = document.createElement('button');
  createButton.id = 'createButton ';
  // If we are adiing a new calculation, set the button's text to Create, otherwise to Change
  (calc == -1 ? createButton.innerHTML = 'Create' : createButton.innerHTML = 'Change');
  createButton.addEventListener('click', function(){ createCalculation($(nameInput).val(), $(textInput).val(), $(targetVar).val(), $(targetEnemy).val(), calc) });
  var delButton= document.createElement('button');
  delButton.id = 'delButton';
  delButton.innerHTML = 'Delete';
  delButton.addEventListener('click', function(){ delCalculation(calc) });


  $('#newCalcControl').append($(name_label), $(nameInput), '<br />');
  $('#newCalcControl').append($(value_label), $(textInput), '<br />');
  $('#newCalcControl').append('Target variable: ', targetVar, '<br />');
  $('#newCalcControl').append('Target player: ', targetEnemy, '<br />');
  if (calc != -1) $('#newCalcControl').append($(delButton));
  $('#newCalcControl').append($(createButton));

  $('#newCalcControl').dialog('open');
}

function createCalculation(name, calculation, targetVar, targetEnemy, counter = -1) {
  if (!name) {
	alert('A calculation must have a name!');
	return false;
  }
  for (var i = 0; i < calcArr.length; i++) {
	if (counter != i && name == calcArr[i].name) {
		alert('A calculation with that name already exists!');
		return false;
	}
  }
  console.log('Creating a calculation that targets ' + targetEnemy + ' on ' + targetVar);
  if (targetVar == null || targetEnemy == null) {
	window.alert('A target variable or player has not been selected!');
	return false;
  }
  // --------------------------------------------------------------------------
  // Check that all the strings inside square brackets are variables
  var regExp = /\[([^\]]+)\]/g;
  var variables = [];
  var temp = calculation;
  console.log('temp is ' + temp);
  // Fill the variables array with the strings inside square brackets (excluding the brackets themselves)
  temp.replace(regExp, function(g0,g1) { variables.push(g1); });
  if (variables) {
    for (var i = 0; i < variables.length; i++) {
      console.log('Found ' + variables[i] + ' in the calculation string.');
      var variableExists = false;
      if (variables[i] == '?') {
	variableExists = true;
      }
      else {
        for (var j = 0; j < variableArr.length; j++) {
          if (variables[i] == variableArr[j].id) {
            variableExists = true;
	    console.log(variables[i] + ' exists at variableArr[' + j + ']');
	  }
        }
      }
      if (!variableExists) {
	console.log('INVALID INPUT: ' + variables[i] + ' is not a proper variable!');
	window.alert(variables[i] + ' is not a proper variable!');
	return false;
      }
    }
  }
  // -------------------------------------------------------------------------
 
  // -------------------------------------------------------------------------
  // Check to make sure that the input is a valid expression
  // We do this by replacing every variable with a constant and then evaluating and hoping we don't get an error
  // Probably not the best way
  temp = calculation;
  // We allow a @ before variables to indicate that the variable belongs to the targeted player; but we have to remove the @ to check that the expression syntax is valid
  temp = temp.replace(/\@\[/g, '[');
  console.log('temp is ' + temp);
  temp = temp.replace(regExp, 2); 
  try {
    math.eval(temp);
  }
  catch(err) {
    window.alert('The calculation cannot be evaluated! Please check the syntax of the expression.');
    return false;
  }
  console.log(math.eval(temp));
  if (!isFinite(math.eval(temp))) {
    window.alert('The calculation is invalid because it can result in an infinite or undefined number!');
    return false;
  }

  temp = calculation;
  temp = temp.replace(/\@\[/g, '[');
  console.log('temp is ' + temp);
  temp = temp.replace(regExp, 0); 
  try {
    math.eval(temp);
  }
  catch(err) {
    window.alert('The calculation cannot be evaluated! Please check the syntax of the expression.');
    return false;
  }
  console.log(math.eval(temp));
  if (!isFinite(math.eval(temp))) {
    window.alert('The calculation is invalid because it can result in an infinite or undefined number!');
    return false;
  }
  // -------------------------------------------------------------------------

  if (counter == -1) {
    calcArr[calcCounter] = new Object();
    calcArr[calcCounter].value = calculation;
    console.log('Setting name to ' + name);
    calcArr[calcCounter].name = name;
    calcArr[calcCounter].targetVar = targetVar;
    calcArr[calcCounter].targetPlayer = targetEnemy;
    calcCounter++;
  }
  else {
    calcArr[counter] = new Object();
    calcArr[counter].value = calculation;
    console.log('Setting name to ' + name);
    calcArr[counter].name = name;
    calcArr[counter].targetVar = targetVar;
    calcArr[counter].targetPlayer = targetEnemy;
  }
  console.log('Adding the calculation ' + calculation + ', at calcArr[' + (calcCounter-1) + ']');
  $('#newCalcControl').dialog('close');
  manageCalculations();
}
