console.log("Running Js!")

$("#pRandomText").click(function(){
    console.log("Running Js!");
    const url = "/randomNumber";
    // const request = new XMLHttpRequest();
    // request.open("GET",url);
    // request.send();
    // request.onreadystatechange = ()=>{
    //     if(request.readyState==4 && request.status==200){
    //         const number = request.responseText;
    //         document.getElementById("spanRandomNumber").innerHTML = number;
    //         document.getElementById("inputNum").value = number;
    //     }
    // };
    
    $("#spanRandomNumber").load(url);
});

const elementText = document.getElementById("pRandomString");
elementText.onclick = () => {
    const lengthInput = document.getElementById("inputNum").value;
    const url = "/randomString/" + lengthInput;
    console.log(url)
    fetch(url).then((response)=>{
        return response.text();
    }).then((responseText)=>{
        console.log(responseText);
        document.getElementById("spanRandomString").innerHTML = responseText;
    });
    // 这里由于input可以修改，会导致lengthInput不合法，从而route失效，进入调试界面，需改进
};
