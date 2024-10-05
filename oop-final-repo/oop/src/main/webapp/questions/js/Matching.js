const boxElements = document.querySelectorAll(".box");
// console.log("box elements", boxElements)
boxElements.forEach(elem => {
    elem.addEventListener("dragstart", dragStart);
    // elem.addEventListener("drag", drag);
    elem.addEventListener("dragend", dragEnd);
    elem.addEventListener("dragenter", dragEnter);
    elem.addEventListener("dragover", dragOver);
    elem.addEventListener("dragleave", dragLeave);
    elem.addEventListener("drop", drop);
});

// Drag and Drop Functions

function dragStart(event) {
    event.target.classList.add("drag-start");
    event.dataTransfer.setData("text", event.target.id);
}

function dragEnd(event) {
    event.target.classList.remove("drag-start");
}

function dragEnter(event) {
    if(!event.target.classList.contains("drag-start")) {
        event.target.classList.add("drag-enter");
    }
}

function dragOver(event) {
    event.preventDefault();
}

function dragLeave(event) {
    event.target.classList.remove("drag-enter");
}

function drop(event) {
    event.preventDefault();
    event.target.classList.remove("drag-enter");
    const draggableElementId = event.dataTransfer.getData("text");
    const droppableElementId = event.target.id;
    if(draggableElementId !== droppableElementId) {
        const draggableElement = document.getElementById(draggableElementId);
        const droppableElementBgColor = event.target.style.backgroundColor;
        const droppableElementTextContent = event.target.querySelector("span").textContent;

        event.target.style.backgroundColor = draggableElement.style.backgroundColor;
        event.target.querySelector("span").textContent = draggableElement.querySelector("span").textContent;
        event.target.id = draggableElementId;
        draggableElement.style.backgroundColor = droppableElementBgColor;
        draggableElement.querySelector("span").textContent = droppableElementTextContent;
        draggableElement.id = droppableElementId;
    }
}