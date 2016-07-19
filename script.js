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
        // call this function on every dragend event
        onend: function (event) {

        }
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

function saveToJson () {
    var json = {};
    for (var i = 0; i < counter; i++) {
        var block = document.getElementsByClassName('block')[i];
        json[block.getAttribute('id')] = {
            id   : block.getAttribute('id'),
            x    : block.getAttribute('data-x'),
            y    : block.getAttribute('data-y'),
            type : block.getAttribute('type')
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
            newBlock.setAttribute('type', json[key].type);
            newBlock.setAttribute('data-x', json[key].data-x);
            newBlock.setAttribute('data-y', json[key].data-y);
            container.appendChild(newBlock);
        }
    }
}
