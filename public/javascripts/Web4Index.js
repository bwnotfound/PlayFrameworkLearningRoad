console.log("Running web4");

const ce = React.createElement;

const csrfToken = document.getElementById("csrfToken").value;
const validateUserRoute = document.getElementById("validateUserRoute").value;
const createUserRoute = document.getElementById("createUserRoute").value;
const tasksRoute = document.getElementById("tasksRoute").value;
const addTaskRoute = document.getElementById("addTaskRoute").value;
const deleteTaskRoute = document.getElementById("deleteTaskRoute").value;

class web4Main extends React.Component {
    constructor(props) {
        super(props);
        this.state = { isLogin: false };

        this.doLogin = this.doLogin.bind(this);
        this.doLogout = this.doLogout.bind(this);
    }

    render() {
        if (this.state.isLogin) {
            return ce(TaskList,{doLogout: this.doLogout});
        }
        else {
            return ce(LoginComponent,{doLogin: this.doLogin});
        }
    }

    doLogin(){
        this.setState({isLogin: true});
    }

    doLogout(){
        this.setState({isLogin: false});
    }

}

class LoginComponent extends React.Component {

    constructor(props) {
        super(props);
        this.state = { loginUsername: "", loginPassword: "", registerUsername: "", registerPassword: "", loginMessage: "", registerMessage: "" };

        this.login = this.login.bind(this);
        this.register = this.register.bind(this);
        this.changeHandler = this.changeHandler.bind(this);
    }

    render() {
        return (
            ce("div", null,
                ce("div", { id: "login-section" },
                    ce("h2", null, "Login:"),
                    ce("br"),
                    "username:",
                    ce("input", { type: "text", id: "loginUsername", value: this.state.loginUsername, onChange: this.changeHandler }),
                    ce("br"),
                    "password:",
                    ce("input", { type: "password", id: "loginPassword", value: this.state.loginPassword, onChange: this.changeHandler }),
                    ce("button", { onClick: this.login }, "login"),
                    ce("span", null, this.state.loginMessage)
                ),
                ce("br"),
                ce("div", { id: "register-section" },
                    ce("h2", null, "Register:"),
                    ce("br"),
                    "username:",
                    ce("input", { type: "text", id: "registerUsername", value: this.state.registerUsername, onChange: this.changeHandler }),
                    ce("br"),
                    "password:",
                    ce("input", { type: "password", id: "registerPassword", value: this.state.registerPassword, onChange: this.changeHandler }),
                    ce("button", { onClick: this.register }, "register"),
                    ce("span", null, this.state.registerMessage)
                )
            )
        );
    }

    login(e) {
        const username = this.state.loginUsername;
        const password = this.state.loginPassword;
        fetch(validateUserRoute, {
            method: "POST",
            headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
            body: JSON.stringify({ username, password })
        }).then(result => result.json()).then(data => {
            const status = data["status"];
            console.log(status);
            if (status == true) {
                this.props.doLogin();
            }
            else {
                this.setState({ loginMessage: "Login failed!" });
            }
        });
    }

    register(e){
        const username = this.state.registerUsername;
        const password = this.state.registerPassword;
        fetch(createUserRoute, {
            method: "POST",
            headers: { "Content-Type": "application/json", "Csrf-Token": csrfToken },
            body: JSON.stringify({ username, password })
        }).then(result => result.json()).then(data => {
            const status = data["status"];
            if (status == true) {
                this.props.doLogin();
            }
            else{
                this.setState({ registerMessage: "Register failed!" });
            }
        });
    }

    changeHandler(e) {
        this.setState({ [e.target.id]: e.target.value })
    }

}

class TaskList extends React.Component {
    
    constructor(props){
        super(props);
        this.state = {tasks:[], newTask:""};

        this.addTask = this.addTask.bind(this);
        this.loadTasks = this.loadTasks.bind(this);
        this.inputHandler = this.inputHandler.bind(this);
        this.deleteHandler = this.deleteHandler.bind(this);
    }

    componentDidMount(){
        this.loadTasks();
    }

    render(){
        return ce("div",null,
            ce("h2",null,"Task List"),
            ce("br"),
            ce("ul",null,
                this.state.tasks.map((task, index) => ce("li",{key:index,onClick:this.deleteHandler},task))
            ),
            ce("br"),
            ce("input",{id:"taskInput", value:this.state.newTask, onChange: this.inputHandler}),
            ce("button",{onClick: this.addTask},"addTask"),
            ce("br"),
            ce("button",{onClick: this.props.doLogout}, "Logout")
        );
    }

    inputHandler(e){
        this.setState({newTask:e.target.value});
    }

    deleteHandler(e){
        const index = e.target.key;
        console.log(index)
        fetch(deleteTaskRoute,{
            method: "POST",
            headers: {"Content-Type": "application/json", "Csrf-Token": csrfToken},
            body: JSON.stringify(index)
        }).then(res=>res.json()).then(data=>{
            const status = data["status"];
            if(status==true){
                this.loadTasks();
            }
            else{
                // document.getElementById("task-message").innerHTML="Failed to delete task.";
                // setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
                // console.log("Error occurred when delete task.");
            }
        })
    }

    addTask(e){
        const task = this.state.newTask;
        fetch(addTaskRoute,{
            method: "POST",
            headers: {"Content-Type": "application/json", "Csrf-Token": csrfToken},
            body: JSON.stringify(task)
        }).then(res=>res.json()).then(data=>{
            const status = data["status"];
            if(status==true){
                this.loadTasks();
                this.setState({newTask:""});
            }
            else{
                // document.getElementById("task-message").innerHTML="Failed to add task.";
                // setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
                // console.log("Error occurred when add task.")
            }
        })
    }

    loadTasks() {
        fetch(tasksRoute).then(result => result.json()).then(json => {
            const status = json["status"];
            if (status == false) {
                // console.log("status went false in loadTasks");
                // document.getElementById("task-message").innerHTML="Failed to load task.";
                // setTimeout(()=>{document.getElementById("task-message").innerHTML=null;}, 1000);
            }
            else if (status == true) {
                this.setState({tasks:json["tasks"]});
            }
            else{
                console.log("status error occurred in loadTasks");
            }
        }).catch(()=>{console.log("unknown error occurred in loadTasks");});
    }

    

}


ReactDOM.render(
    ce(web4Main, null, null),
    document.getElementById("react-root")
);


