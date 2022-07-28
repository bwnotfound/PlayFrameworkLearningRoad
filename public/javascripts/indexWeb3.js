"use strict"

const csrfToken = document.getElementById("csrfToken").value;
const validateUserRoute = document.getElementById("validateUser").value;
const tasksRoute = document.getElementById("tasks").value;
// const urlLoadLoginPage =    document.getElementById("loadLoginPage").value;
// const urlLogin =            document.getElementById("login").value;
// const urlLogout =           document.getElementById("logout").value;
// const urlRegisterContent =  document.getElementById("registerContent").value;
// const urlTaskListReload =   document.getElementById("taskListReload").value;

function login() {
    const username = document.getElementById("login-username").value;
    const password = document.getElementById("login-password").value;
    fetch(validateUserRoute, {
        method: "POST",
        headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
        body: JSON.stringify({ username, password })
    }).then(result => result.json()).then(data => {
        if (data == true) {
            document.getElementById("login-section").hidden = true;
            document.getElementById("task-section").hidden = false;
            loadTasks();
        }
    });
}

function loadTasks() {
    const ul = document.getElementById("task-list");
    fetch(tasksRoute).then(result => result.json()).then(json => {
        const status = json["status"];
        if (status == false) {

        }
        else if (status == true) {
            const tasks = json["tasks"];
            console.log(tasks);
            for (const task of tasks) {
                const li = document.createElement("li");        
                li.appendChild(document.createTextNode(task));
                ul.appendChild(li);
            };
        }
        else{
            console.log(json);
            console.log(status);
            console.log("unknown error occurred in loadTasks");
        }

    });
}

// function loadLoginPage(){
//     $("#contents").load(urlLoadLoginPage, function (responseText, textStatus, XMLHttpRequest) {
//         console.log("loginPage load successfully")
//         if (textStatus == "success") {
//             $("#buttonLogin").hide();
//         }
//     });
// }

// function login() {
//     const username = $("#login-username").val();
//     const password = $("#login-password").val();
//     $.post(
//         urlLogin ,
//         {username,password,csrfToken},
//         data=>{
//             $("#contents").html(data);
//         }
//         );
// }

// function logout(){
//     $("#contents").load(urlLogout);
// }

// function register() {
//     const username = $("#register-username").val();
//     const password = $("#register-password").val();
//     // $("#contents").load("/web2/createUser?username=" + username + "&password=" + password);
//     console.log("Ok into function");
//     $.get("/web2/createUser?username=" + username + "&password=" + password, function (data) {
//         console.log("Ok into huidiao function");
//         console.log(data);
//         if (data == "false") {
//             $("#register-username").val("s");
//             $("#register-password").val("s");
//         }
//         else {
//             $("#contents").html(data);
//         }

//     });
// }

// function registerContent() {
//     $("#register-div").load(urlRegisterContent);
// }

// function createUser() {
//     const username = $("#login-username").val();
//     const password = $("#login-password").val();
//     console.log("Ok into function");
//     $.get("/web2/validate?username=" + username + "&password=" + password, function (data) {
//         console.log("Ok into huidiao function");
//         console.log(data);

//         if (data.innerHTML == "false") {
//             $("#register-username").val("123");
//         }
//         else {
//             $("#contents").html(XMLHttpRequest.responseXML);
//         }

//     });
// }

// function taskListReload() {
//     $("#contents").load(urlTaskListReload)
// }

// function deleteTask(index) {
//     $.get("/web2/deleteTask?index=" + index);
//     taskListReload();
// }

// function addTask() {
//     const task = $("#addTask").val()
//     $.get("/web2/addTask?task=" + encodeURIComponent(task))
//     taskListReload();
// }