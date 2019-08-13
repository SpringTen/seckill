//模块化
var seckill = {
    //封装秒杀相关Ajax的url，统一管理，优雅
    URL: {
        //获取服务器当前时间
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        },
        result: function (seckillId, userPhone) {
            return '/seckill/' + seckillId + '/' + userPhone + '/result';
        }
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    //轮询秒杀结果
    repeatResult: function (seckillId, userPhone, node){
        $.get(seckill.URL.result(seckillId, userPhone), {}, function (result) {
            if (result && result['success'] && result['error'] != '-1' && result['error'] != '0') {
                //查到了生成的秒杀成功信息，如果需要进一步操作
                var successKilled = result['data'];
                //如果秒杀成功，显示商品id和用户手机号
                node.html('<span class="label label-success">' + successKilled['seckillId']+ ":" + successKilled['userPhone'] + '</span>');
            }
            //下一步任务，重复轮询结果seckill.repeatResult(seckillId, userPhone, node);
            //如果库存 <0,并且等待时间超过三十秒，则返回失败，使用redisDao查询剩余库存，<=0，
            if (result && result['success'] && result['error'] == '-1') {
                node.html('<span class="label label-success">' + "秒杀失败" + '</span>');
            }
            if (result && result['success'] && result['error'] == '0') {
                node.html('<span class="label label-success">' + "排队中" + '</span>');
                setTimeout(function(){
                    seckill.repeatResult(seckillId, userPhone, node);
                }, 500);
            }

        });
    },
    //执行秒杀
    handleSeckillkill: function (seckillId, node) {
        //获取秒杀地址，控制显示逻辑，实现秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        //执行秒杀前先获取秒杀地址,{}代表Ajax调用的参数，这里不需要
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                //获取秒杀地址对象成功
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //如果秒杀已开启
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl:" + killUrl);
                    //绑定点击事件，只绑定一次，防止用户多次点击增加服务器压力
                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $('#killBtn').addClass('disabled');//,<-$(this)===('#killBtn')->
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                console.log('stateInfo:' + stateInfo);
                                if (state != 2) {
                                    //状态信息不为2，说明出现重复秒杀，或者秒杀结束，或者内部异常
                                    //显示秒杀结果
                                    alert(state);
                                    node.html('<span class="label label-success">' + stateInfo + '</span>');
                                }
                                //排队中
                                if (state == 2) {
                                    alert(state);
                                    //重复轮询，查看是否秒杀成功
                                    //方法是：查看秒杀信息是否已经新增到successkilled表里
                                    var userPhone = $.cookie('killPhone');
                                    seckill.repeatResult(seckillId, userPhone, node);
                                }
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀，浏览器存在计时偏差
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log('result:' + result);
            }
        });
    },
    //计时
    countdown: function (seckillId, nowTime, startTime, endTime) {
        console.log("id: "+seckillId + ' nowTime: ' + nowTime + ' startTime: ' + startTime + ' endTime: ' + endTime);
        //计时操作
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束！');
        } else if (nowTime < startTime) {
            //秒杀未开始，进行倒计时，使用jQuery提供的countdown，每次时间变化都会触发此事件
            var killTime = new Date(startTime);//秒杀开始时间
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //killTime 时间结束后的回调事件
            }).on('finish.countdown', function () {
                //获取秒杀地址，控制显示逻辑，实现秒杀
                seckill.handleSeckillkill(seckillId, seckillBox);
            });
        } else {
            //正在秒杀
            //跟上面killTime时间结束后的处理逻辑一样，抽象出来一个方法
            seckill.handleSeckillkill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail: {
        init: function (params) {
            // alert(params['seckillId']);
            //手机验证和登录，计时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            if (!seckill.validatePhone(killPhone)) {
                //手机号验证不通过
                //绑定phone，控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: false, //禁止位置关闭
                    keyboard: false //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                        //获取input内容
                        var inputPhone = $('#killPhoneKey').val();
                        console.log("inputPhone = " + inputPhone);
                        if (seckill.validatePhone(inputPhone)) {
                            //手机号验证通过，写入cookie
                            $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                            //刷新页面，重新走一遍此流程也就是又重新调用了init方法
                            window.location.reload();
                        } else {
                            $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show();
                        }
                    }
                );
            }
            //已经登录成功
            //显示秒杀倒计时功能
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    //获取服务器时间成功
                    var nowTime = result['data'];
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result: ' + result);
                    alert('result: ' + result);
                }
            });
        }
    }
}