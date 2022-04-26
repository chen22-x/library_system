layui.use(['form', 'element', 'layer'], function () {
    let form = layui.form;
    let element = layui.element;
    let layer = layui.layer;

    form.on('submit(btn_addUser)', function (data) {
        addUser();
        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
});


//添加用户的ajax方法
function addUser() {
    $.ajax({
        async: false,
        type: 'post',
        url: '/addUser',
        data: $('#addUserForm').serialize(),
        success: function (data) {
            if (data.toString() == "true") {
                layer.msg("添加成功!", {icon: 1, time: 1500});

                // 1500ms后 重新加载页面 , 将更改后的内容重新加载到页面
                setTimeout(function () {
                    location.reload();
                }, 1500);
            } else {
                layer.msg("添加失败!", {icon: 2, time: 1500});
            }
        },
        error: function (data) {
            layer.msg("添加失败", {icon: 2});
        }
    });
};