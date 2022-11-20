var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#info").html("");
}

function connect() {
    var socket = new SockJS('/game-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/tchu-events', function (notification) {
            showNotification(JSON.parse(notification.body));
        });
        console.log('Connected twice: ' + frame);
        stompClient.subscribe('/user/queue/tchu-events', function (notification) {
                             showNotification(JSON.parse(notification.body));

        });
    });

}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function sendName() {
    stompClient.send("/app/tchu", {}, JSON.stringify({'metaAction': $("#meta-action").val(), 'playAction': $("#play-action").val(), 'data': $("#data").val()}));
}

function showNotification(message) {
    $("#info").append("<tr><td>" + message.messageId + ": " + message.data + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

