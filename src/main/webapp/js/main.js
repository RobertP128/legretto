let baseURL="/legrettoserver/";
let servletURL=baseURL+"hello-servlet/";


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
            $( this ).html( "" + ui.draggable.attr("data") );
        }
    });


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

function updateTable(tableData){

}



function onPrintAllCardsClick(){
    for(let x=0;x<40;x++){
        let cardTemplate=$("#templates .card").clone();
        cardTemplate.html(x);
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
    let reqDataStr=JSON.stringify(reqData);
    $.get( servletURL+"putCard", {data: reqDataStr},function( data ) {

    }).done(function(data) {
        $( "#getTableResult" ).html( JSON.stringify(data,null,3) );
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}

function onreInitClick(){
    $.get( servletURL+"init",function( data ) {

    }).done(function(data) {
        //$( "#getTableResult" ).html( JSON.stringify(data,null,3) );
        alert("Table was resetted");
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}