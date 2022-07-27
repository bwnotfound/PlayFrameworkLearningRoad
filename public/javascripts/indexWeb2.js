$("#buttonLogin").click(function(){
    $("#contents").load("/login",function(){
        $("#buttonLogin").hide();
    });
});
