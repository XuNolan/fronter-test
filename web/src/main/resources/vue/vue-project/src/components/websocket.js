let isConnect = false;
let webSocket = null;

let globalCallback = function(e) {console.log(e)};//定义外部接收数据的回调。
let reConnectNum = 0;

let heartCheck = { //目前仅支持单设备。之后再标记当前浏览器终端。
    heartbeatData:{
        msgType: "heartbeat",
        contentType:"heartbeat",
        content:{},
    },
    timeout: 60*1000,
    heartbeat:null,
    start: function (){
        this.heartbeat = setInterval(() =>{
            if(isConnect){
                sendMessage(this.heartbeatData);
            } else {
                this.clear();
            }
        }, this.timeout);
    },
    reset: function (num) {
        this.clear();
        this.timeout = num;
        this.start();
    },
    clear: function () {
        clearInterval(this.heartbeat);
    }
}

export function initWebSocket(websocketUrl, callback){
    //定义的接收socekt数据的函数？
    if(callback){
        if(typeof callback == 'function'){
            globalCallback = callback;
        }else{
            throw new Error("callback is not a function");
        }
    }
    if ("WebSocket" in window) {
        webSocket = new WebSocket(websocketUrl);//创建socket对象
    } else {
        console.log(JSON.stringify({message: '该浏览器不支持websocket!', type: 'warning'}));
        throw new Error("websocket unsupported");
    }
    //------------------- 挂载处理方法；------------------------
    webSocket.onopen = function() {
        console.log("WebSocket连接成功");
        //首次握手
        sendMessage(heartCheck.heartbeatData);
        isConnect = true;
        heartCheck.start();
        reConnectNum = 0;
    };
    //收信
    webSocket.onmessage = function(e) {
        console.log("websocket信息:");
        console.log(e.data);
        const data = JSON.parse(e.data);//根据自己的需要对接收到的数据进行格式化
        globalCallback(data);//将data传给在外定义的接收数据的函数，至关重要。
    };
    //关闭
    webSocket.onclose = function(e) {
        heartCheck.clear();
        isConnect = false; //断开后修改标识
        console.log(e);
        console.log('webSocket已经关闭 (code：' + e.code + ')');
        //被动断开，重新连接
        if(e.code === 1006){
            if(reConnectNum < 3){
                initWebSocket();
                ++reConnectNum;
            }else{
                console.log(JSON.stringify({
                    message: 'websocket连接不上，请刷新页面或联系开发人员!',
                    type: 'warning'
                }));
            }
        }
    };
    //连接发生错误的回调方法
    webSocket.onerror = function(e) {
        heartCheck.clear();
        isConnect = false; //断开后修改标识
        console.log("WebSocket连接发生错误:");
        console.log(e);
    };
}

export function sendMessage(message){
    webSocket.send(JSON.stringify(message));
}

export function closeWebsocket(){
    heartCheck.clear();
    isConnect = false; //断开后修改标识
    console.log('webSocket已经关闭');
    reConnectNum = 0;
}

export{isConnect, webSocket}