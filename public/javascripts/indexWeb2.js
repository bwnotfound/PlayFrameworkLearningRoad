
const csrfToken = $("#csrfToken").val();


function loadLoginPage(){
    $("#contents").load("/web2/login", function (responseText, textStatus, XMLHttpRequest) {
        console.log("loginPage load successfully")
        if (textStatus == "success") {
            $("#buttonLogin").hide();
        }
    });
}

function login() {
    const username = $("#login-username").val();
    const password = $("#login-password").val();
    $.post(
        "/web2/validate" ,
        {username,password,csrfToken},
        data=>{
            $("#contents").html(data);
        }
        );
}

function logout(){
    $("#contents").load("/web2/logout");
}

function register() {
    const username = $("#register-username").val();
    const password = $("#register-password").val();
    // $("#contents").load("/web2/createUser?username=" + username + "&password=" + password);
    console.log("Ok into function");
    $.get("/web2/createUser?username=" + username + "&password=" + password, function (data) {
        console.log("Ok into huidiao function");
        console.log(data);
        if (data == "false") {
            $("#register-username").val("s");
            $("#register-password").val("s");
        }
        else {
            $("#contents").html(data);
        }

    });
}

function registerContent() {
    $("#register-div").load("/web2/registerContent");
}

function createUser() {
    const username = $("#login-username").val();
    const password = $("#login-password").val();
    console.log("Ok into function");
    $.get("/web2/validate?username=" + username + "&password=" + password, function (data) {
        console.log("Ok into huidiao function");
        console.log(data);

        if (data.innerHTML == "false") {
            $("#register-username").val("123");
        }
        else {
            $("#contents").html(XMLHttpRequest.responseXML);
        }

    });
}

function taskListReload() {
    $("#contents").load("/web2/taskListContent")
}

function deleteTask(index) {
    $.get("/web2/deleteTask?index=" + index);
    taskListReload();
}

function addTask() {
    const task = $("#addTask").val()
    $.get("/web2/addTask?task=" + encodeURIComponent(task))
    taskListReload();
}