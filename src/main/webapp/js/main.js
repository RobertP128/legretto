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

}

function onGetTableClick(){
    getTable();
}

function getTable(){
    $.get( servletURL+"getTable", function( data ) {

    }).done(function(data) {
        $( "#getTableResult" ).html( JSON.stringify(data,null,3) );
    }).fail(function(e) {
        alert( "error in Ajax Request"+e );
    });
}

function onPrintAllCardsClick(){
    for(let x=0;x<40;x++){
        let cardTemplate=$("#templates .card").clone();
        cardTemplate.html(x);
        $("#allCardsBox").append(cardTemplate);
    }
}