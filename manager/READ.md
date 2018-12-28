## manager 插件

* require 插件

```
var manager = null;

apiready = function(){
	    	manager = api.require('manager');

	    	}

```

* 方法调用

```
	var resultCallback = function(ret, err){
				document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
	        manager.getAppsInfo(resultCallback);
```


*  结果返回

```
{
    "result":[
        {
            "appName":"",
            "appVersion":"",
            "appIcon":"",
            "packageName":""
        },
        {
            "appName":"",
            "appVersion":"",
            "appIcon":"",
            "packageName":""
        }
    ]
}

```