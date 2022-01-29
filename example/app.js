
function BrightnessAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.brightness.isPermitted();
  });

  React.useEffect(() => {
    document.addEventListener("lam-permission-change", function () {
      setPermitted(window.lam.brightness.isPermitted());
    });
  }, []);

  function onRequestAccess(e) {
    window.lam.brightness.requestPermission();
  }

  function setBrightness(e) {
    window.lam.brightness.set(+e.target.value);
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "Brightness"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to brightness"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("input", { type: "range", min: "0", max: "100", onChange: e => setBrightness(e) }))));




}

function VibrateAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.vibrate.isPermitted();
  });

  function onRequestAccess(e) {
    window.lam.vibrate.requestPermission();
    setPermitted(window.lam.vibrate.isPermitted());
  }

  function vibrate(...values) {
    window.lam.vibrate.run(...values);
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "Vibrate"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to vibrate"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("button", { onClick: () => vibrate(100) }, "Short Vibrate"), /*#__PURE__*/
    React.createElement("button", { onClick: () => vibrate(2000) }, "Long Vibrate"), /*#__PURE__*/
    React.createElement("button", { onClick: () => vibrate(200, 100, 200) }, "Pattern Vibrate 1"), /*#__PURE__*/


    React.createElement("button", { onClick: () => vibrate(50, 20, 100, 50, 50) }, "Pattern Vibrate 2"))));






}

function TorchAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.torch.isPermitted();
  });

  function onRequestAccess(e) {
    window.lam.torch.requestPermission();
    setPermitted(window.lam.torch.isPermitted());
  }

  function torch(state) {
    if (state == "on") {
      window.lam.torch.on();
    } else {
      window.lam.torch.off();
    }
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "Torch"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to torch"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("button", { onClick: () => torch("on") }, "Turn on"), /*#__PURE__*/
    React.createElement("button", { onClick: () => torch("off") }, "Turn off"))));




}

function UIKitAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.uikit.isPermitted();
  });

  function onRequestAccess(e) {
    window.lam.uikit.requestPermission();
    setPermitted(window.lam.uikit.isPermitted());
  }

  function toast(text, time) {
    window.lam.uikit.toast(text, time);
  }

  function customAlert(title, content) {
    window.lam.uikit.alert(title, content);
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "UIKit"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to UIKit"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("button", { onClick: () => toast("Hello", 0) }, "Short Toast"), /*#__PURE__*/
    React.createElement("button", { onClick: () => toast("Hello", 1) }, "Long Toast"), /*#__PURE__*/

    React.createElement("button", { onClick: () => customAlert("I am title", "") }, "Alert"), /*#__PURE__*/


    React.createElement("button", { onClick: () => customAlert("I am title", "I am message") }, "Alert with Message"))));






}

function NotificationAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.notification.isPermitted();
  });

  function onRequestAccess(e) {
    window.lam.notification.requestPermission();
    setPermitted(window.lam.notification.isPermitted());
  }

  function notify(text, message) {
    window.lam.notification.notify(text, message);
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "Notification"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to Notification"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("button", { onClick: () => notify("I am title", "Hello") }, "Notification 1"), /*#__PURE__*/


    React.createElement("button", { onClick: () => notify("I am title", "I am message") }, "Notification 2"))));






}

function CalcAPI() {
  const [permitted, setPermitted] = React.useState(() => {
    return window.lam.calc.isPermitted();
  });

  function onRequestAccess(e) {
    window.lam.calc.requestPermission();
    setPermitted(window.lam.calc.isPermitted());
  }

  function giveNumbers(n) {
    return new Array(n).fill().map((v, i) => i % 50).join(",");
  }

  const code1 = `${giveNumbers(100)}:EACH,POW 2`;
  const code2 = `${giveNumbers(1000)}:EACH,MOD 5`;

  function calc(code) {
    let startTime = performance.now();
    let result = window.lam.calc.do(code);
    let endTime = performance.now();
    window.lam.uikit.alert("Result: time taken(" + (endTime - startTime) + " ms)", "");
  }

  return /*#__PURE__*/(
    React.createElement("div", { className: "box" }, /*#__PURE__*/
    React.createElement("div", { class: "title" }, "Calc"),
    !permitted ? /*#__PURE__*/
    React.createElement("div", { class: "no-access" }, /*#__PURE__*/
    React.createElement("p", null, "You don't have access to calc"), /*#__PURE__*/
    React.createElement("button", { onClick: onRequestAccess }, "Request access")) : /*#__PURE__*/


    React.createElement("div", { class: "controls" }, /*#__PURE__*/
    React.createElement("button", { onClick: () => calc(code1) }, "Code 1"), /*#__PURE__*/


    React.createElement("button", { onClick: () => calc(code2) }, "Code 2"))));






}

function App() {
  return /*#__PURE__*/(
    React.createElement("div", { className: "app" },
    !window.lam.support ? /*#__PURE__*/
    React.createElement("p", null, "Features are not supported in this browser.") : /*#__PURE__*/

    React.createElement(React.Fragment, null, /*#__PURE__*/
    React.createElement(BrightnessAPI, null), /*#__PURE__*/
    React.createElement(VibrateAPI, null), /*#__PURE__*/
    React.createElement(TorchAPI, null), /*#__PURE__*/
    React.createElement(UIKitAPI, null), /*#__PURE__*/
    React.createElement(NotificationAPI, null), /*#__PURE__*/
    React.createElement(CalcAPI, null))));




}

ReactDOM.render( /*#__PURE__*/React.createElement(App, null), document.querySelector("#root"));
