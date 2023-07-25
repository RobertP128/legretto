let baseURL="/legrettoserver/";
let servletURL=baseURL+"hello-servlet/";

let tableData=null;

$("document").ready(function(){
    init();
});

function init(){
    //alert("Im here");
    initHandler()
}

function initHandler(){
    $("#getTable").on("click",onGetTableClick);
    $("#printAllCards").on("click",onPrintAllCardsClick);
    $("#putCard").on("click",onPutCardClick);
    $("#reInit").on("click",onreInitClick);
    $("#closeDebugBox").on("click",onCloseDebugBoxClick);


    function onStartDrag(event, ui){
        //
    }

    $(".draggable").draggable({
        start: onStartDrag,
        stop: function(event, ui){
            $(this).animate({ top: 0, left: 0 }, 'slow');
        }
    });
    $( ".droppable" ).droppable({
        drop: function( event, ui ) {
            let target=$( this );
            let reqData=parseDropTarget(ui.draggable.attr("data"),target.attr("data"));
            putCardRequest(reqData);

            // Update TableData

            //target.html( target.attr("data") +"|"+ ui.draggable.attr("data") );
        }
    });

    function parseDropTarget(srcData,targetData){
        let reqData=
            {
                player: parseInt(srcData[1]),
                slot:parseInt(srcData[3]),
                target:parseInt(targetData[1])
            };
        return reqData;
    }


}

function onCloseDebugBoxClick(){
    $("#getTableResult").html("--");
}

function onGetTableClick(){
    getTable();
}

function getTable(){
    $.get( servletURL+"getTable", function( data ) {

    }).done(function(data) {
        $( "#getTableResult" ).html( JSON.stringify(data,null,3) );
        updateTable(data);
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}


function removeCardColors(cardSlot){
    cardSlot.removeClass("cardColorBack")
    cardSlot.removeClass("cardColorGN")
    cardSlot.removeClass("cardColorRT")
    cardSlot.removeClass("cardColorBL")
    cardSlot.removeClass("cardColorYE")

}

function setCardColor(cardSlot,cardNumber){
    removeCardColors(cardSlot);
    cardSlot.addClass(getCardColorClass(cardNumber));
}

function updateTable(tableData_){
    tableData=tableData_;
    // set the slots
    for(playerIdx=0;playerIdx<4;playerIdx++){
        for(slotIdx=0;slotIdx<4;slotIdx++){
            cardSlot=$(".CardsTable div[data='P"+playerIdx+"S"+slotIdx+"']");
            let cardNumber=tableData.players[playerIdx].slots[slotIdx];
            cardSlot.html(getCardValue(cardNumber));
            setCardColor(cardSlot,cardNumber);
        }
        cardSlot=$(".CardsTable div[data='P"+playerIdx+"SS']");
        let cardNumber=tableData.players[playerIdx].cardFromStackslot;
        cardSlot.html(getCardValue(cardNumber));
        setCardColor(cardSlot,cardNumber);

        cardSlot=$(".CardsTable div[data='P"+playerIdx+"SD']");
        cardNumber=tableData.players[playerIdx].cardFromDeckSlot;
        cardSlot.html(getCardValue(cardNumber));
        setCardColor(cardSlot,cardNumber);

    }

    // set the targets
    for(x=0;x<16;x++){
        let target= $(".CardsTable div[data='T"+x+"']");
        let targetsPointer=tableData.targetsPointer[x];
        let cardNumber=-2;
        if (targetsPointer>=0) {
            cardNumber=tableData.targets[x][targetsPointer];
        }
        else {
            cardNumber=-1;
        }
        target.html(getCardValue(cardNumber));
        setCardColor(target,cardNumber);
    }




}


function getCardColorClass(cardNumber){

    if (cardNumber==-1) return "cardColorBack";

    let colorInt=Math.floor(cardNumber / 10);
    switch (colorInt){
        case 0:
            return "cardColorGN";
        case 1:
            return "cardColorRT";
        case 2:
            return "cardColorBL";
        case 3:
            return "cardColorYE";
    }
}

function getCardValue(cardNumber){
    if (cardNumber==-1) return -1
    return (cardNumber % 10) +1;
}

function onPrintAllCardsClick(){
    for(let x=-1;x<40;x++){
        let cardTemplate=$("#templates .card").clone();
        cardTemplate.html(x);
        cardTemplate.toggleClass(getCardColorClass(x));
        $("#allCardsBox").append(cardTemplate);
    }
}




function onPutCardClick(){
    let reqData=
        {
            player: $("#player").val(),
            slot:$("#slot").val(),
            target:$("#target").val()
        };
        putCardRequest(reqData);
}

function onreInitClick(){
    $.get( servletURL+"init",function( data ) {

    }).done(function(data) {
        //$( "#getTableResult" ).html( JSON.stringify(data,null,3) );
        getTable();
        alert("Table was resetted");
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}


function putCardRequest(reqData){
    let reqDataStr=JSON.stringify(reqData);
    $.get( servletURL+"putCard", {data: reqDataStr},function( data ) {

    }).done(function(data) {
        tableData=data;
        updateTable(data);
        $( "#getTableResult" ).html( JSON.stringify(data,null,3) );
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}