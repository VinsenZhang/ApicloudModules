## select 插件

* require 插件

```
var select = null;

apiready = function(){
	    	manager = api.require('select');

	    	}

```

* 方法调用--选择



```
       var json = {
           "items":[
               {
                   "type":"input",
                   "name":"input",
                   "value":"请输入"
               },
               {
                   "type":"checkbox",
                   "name":"checkbox",
                   "value":"hehe"
               },
               {
                   "type":"switch",
                   "name":"switch" ,
                   "value":"hehe"
               },{
                   "type":"switch",
                   "name":"switch",
                   "value":"hehe"
               },{
                   "type":"switch",
                   "name":"switch",
                   "value":"hehe"
               },
               {
                   "type":"radio",
                   "name":"radio",
                   "values":[
                       {
                           "value":"1"
                       },
                       {
                           "value":"2"
                       },
                       {
                           "value":"3"
                       },
                       {
                           "value":"4"
                       },
                       {
                           "value":"5"
                       }
                   ]
               },
               {
                   "type":"select",
                   "name":"select",
                   "values":[
                       {
                           "value":"1"
                       },
                       {
                           "value":"2"
                       },
                       {
                           "value":"3"
                       },
                       {
                           "value":"4"
                       },
                       {
                           "value":"5"
                       }
                   ]
               }
           ]
       };

	var param = {params:json};
			var resultCallback = function(ret, err){
				document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
	        selectModule.choose(param, resultCallback);

```


* 方法调用--关闭



```
	        select.close(resultCallback);
```


*  结果返回

* 返回的json 的key 是传参的name字段。

```
{
    "select":{
           "result":""
        },
    "checkbox":{
            "result":"开启"
         },
    "switch1":{
            "result":"开启"
         },
    "switch2":{
            "result":"开启"
         },
    "switch3":{
            "result":"开启"
         },
    "radio":{
            "result":"3"
         }
}

```