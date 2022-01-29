(function(){
  let native = ["brightness","torch","vibrate","notification","uikit","calc"];
  let _ = {
    lamina:{
      version:1, 
      support:true
    }
  };
  if(!window.lamina){
    _.lamina.support = false;
    window["lam"] = _.lamina;
    return;
  }

  _.lamina.permissionChangeEvent = () => {
    alert("dispatching event");
    let event = document.createEvent('Event');
    event.initEvent('lam-permission-change', true, true);
    document.dispatchEvent(event);
    alert("dispatched event");
  }

  let __ = window.lamina;
  
  native.forEach((type)=>{
    _.lamina[type] = {
      requestPermission:()=>{
        __.requestPermission(type);
      },
      isPermitted: ()=>{
        let data = __.isPermitted(type);
        return (data != 0);
      }
    }
  });
  
  // brightness
  _.lamina[native[0]]["get"] = function(){
    let data = __.getBrightness();
    if(data == -1){
      throw Error(ERROR["2"]);
    }
    return data;
  }
  _.lamina[native[0]]["set"] = function(value){
    if(typeof value != "number"){
      throw Error(ERROR["3"]);
    }
    if(value < 0 && value > 100){
      throw Error(ERROR["4"]);
    }
    let data = __.setBrightness(value);
    if(data == -1){
      throw Error(ERROR["2"]);
    }
    return data;
  }
  
  //torch
  _.lamina[native[1]]["on"] = function(){
    let data = __.turnOnTorch();
    if(data == -1){
      throw Error(ERROR["2"]);
    }
  }
  _.lamina[native[1]]["off"] = function(){
    let data = __.turnOffTorch();
    if(data == -1){
      throw Error(ERROR["2"]);
    }
  }
  
  //vibrate
  _.lamina[native[2]]["run"] = function(...num){
    if(num.length == 0){
      throw Error(ERROR["4"]);
    } else if(num.length  == 1){
      __.vibrate(num[0]);
    } else if(num.length > 1){
      __.vibrate(num[0]);
      num.shift();
      setTimeout(()=>{
        num.shift();
        _.lamina[native[2]]["run"](...num);
      },num[0]);
    }
  }
  
  //notification
  _.lamina[native[3]]["notify"] = function(title,message){
    if(!title){
      throw Error(ERROR["5"]);
    }
    if(!message){
      throw Error(ERROR["5"]);
    }
    __.showNotification(title,message);
  }
  
  //uikit
  _.lamina[native[4]]["alert"] = function(title,message){
    if(!title){
      title = "";
    }
    if(!message){
      message = "";
    }
    __.nativeAlert(title,message);
  }
  _.lamina[native[4]]["toast"] = function(title,time){
    if(!title){
      title = "";
    }
    if(!time){
      time = 1;
    }
    __.showToast(title,time);
  }
  
  //calc
  _.lamina[native[5]]["do"] = function(code){
    if(!code){
      throw new Error(ERROR["6"])
    }
    return __.calc(code);
  }
  
  window["lam"] = _.lamina;
  
})();
