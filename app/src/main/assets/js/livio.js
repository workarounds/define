document.onclick = function (e) {
 e = e ||  window.event;
 var element = e.target || e.srcElement;
       if (element.tagName == 'A') {
            window.JSInterface.loadMeaning(element.href);
            return false; // prevent default action and stop event propagation
       }
};