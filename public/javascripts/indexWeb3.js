"use strict"

const csrfToken = document.getElementById("csrfToken").value;
const validateUserRoute = document.getElementById("validateUserRoute").value;
const createUserRoute = document.getElementById("createUserRoute").value;
const tasksRoute = document.getElementById("tasksRoute").value;
const addTaskRoute = document.getElementById("addTaskRoute").value;
const deleteTaskRoute = document.getElementById("deleteTaskRoute").value;
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
        const status = data["status"];
        console.log(status);
        if (status == true) {
            loadTasks();
            document.getElementById("login-section").hidden = true;
            document.getElementById("task-section").hidden = false; 
        }
        else{
            document.getElementById("login-message").innerHTML="Login failed.";
            setTimeout(()=>{document.getElementById("login-message").innerHTML=null;}, 1000);
        }
    });
}

function loadTasks() {
    const ul = document.getElementById("task-list");
    fetch(tasksRoute).then(result => result.json()).then(json => {
        const status = json["status"];
        if (status == false) {
            console.log("status went false in loadTasks");
            document.getElementById("task-message").innerHTML="Failed to load task.";
            setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
        }
        else if (status == true) {
            const tasks = json["tasks"];
            ul.innerHTML=null;
            for (let i = 0; i < tasks.length; i++) {
                const li = document.createElement("li");        
                li.appendChild(document.createTextNode(tasks[i]));
                li.onclick = deleteTask;
                li.value = i;
                ul.appendChild(li);
            };
        }
        else{
            console.log("status error occurred in loadTasks");
        }
    }).catch(()=>{console.log("unknown error occurred in loadTasks");});
}

function addTask(){
    const task = document.getElementById("addTask").value;
    fetch(addTaskRoute,{
        method: "POST",
        headers: {"Content-Type": "application/json", "Csrf-Token": csrfToken},
        body: JSON.stringify(task)
    }).then(res=>res.json()).then(data=>{
        const status = data["status"];
        if(status==true){
            loadTasks();
            document.getElementById("addTask").value = null;
        }
        else{
            document.getElementById("task-message").innerHTML="Failed to add task.";
            setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
            console.log("Error occurred when add task.")
        }
    })
}

function deleteTask(){
    const index = this.value;
    console.log(index)
    fetch(deleteTaskRoute,{
        method: "POST",
        headers: {"Content-Type": "application/json", "Csrf-Token": csrfToken},
        body: JSON.stringify(index)
    }).then(res=>res.json()).then(data=>{
        const status = data["status"];
        if(status==true){
            loadTasks();
        }
        else{
            document.getElementById("task-message").innerHTML="Failed to delete task.";
            setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
            console.log("Error occurred when delete task.");
        }
    })
}

function registerContent() {
    document.getElementById("login-section").hidden = true;
    document.getElementById("register-section").hidden = false;
}

function loginContent() {
    document.getElementById("login-section").hidden = false;
    document.getElementById("register-section").hidden = true;
}

function register(){
    const username = document.getElementById("register-username").value;
    const password = document.getElementById("register-password").value;
    fetch(createUserRoute, {
        method: "POST",
        headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
        body: JSON.stringify({ username, password })
    }).then(result => result.json()).then(data => {
        const status = data["status"];
        if (status == true) {
            loadTasks();
            document.getElementById("register-section").hidden = true;
            document.getElementById("task-section").hidden = false;
        }
        else{
            document.getElementById("register-message").innerHTML="Failed to register.";
            setTimeout(()=>{document.getElementById("register-message").innerHTML=null;}, 1000);
            console.log("User already exist");
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