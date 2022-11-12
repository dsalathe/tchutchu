var stompClient = null;
var sessionId = "";
//var privateStompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        var url = stompClient.ws._transport.url;
        url = url.replace(
          "ws://localhost:8080/gs-guide-websocket/",  "");
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);
        sessionId = url;

        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        console.log('Connected twice: ' + frame);
        stompClient.subscribe('/user/queue/greetings', function (greeting) {
                             showGreeting(JSON.parse(greeting.body).content);

        });
    });

}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        //privateStompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}
function sendName2() {
    stompClient.send("/app/private-hello", {}, JSON.stringify({'name': $("#name2").val()}));
}
function sendName3() {
    stompClient.send("/app/tchu", {}, JSON.stringify({'metaAction': $("#meta-action").val(), 'playAction': $("#play-action").val(), 'data': $("#data").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#send2" ).click(function() { sendName2(); });
    $( "#send3" ).click(function() { sendName3(); });
});

