window.$$eventSniffer = {};

(function() {

    (function fakeTouchSupport() {
        var objs = [window, document.documentElement, document];
        var props = ['ontouchstart', 'ontouchmove', 'ontouchcancel', 'ontouchend'];

        for(var o=0; o<objs.length; o++) {
            for(var p=0; p<props.length; p++) {
                if(objs[o] && objs[o][props[p]] == undefined) {
                    objs[o][props[p]] = null;
                }
            }
        }
    })();

    function createTouchEvent(target, eventType, clientX, clientY, pageX, pageY) {
        var e = document.createEvent('Event');
        e.initEvent(eventType, true, true);

        e.ctrlKey = false;
        e.shiftKey = false;
        e.altKey = false;
        e.metaKey = false;

        var pos = {
            clientX: clientX,
            clientY: clientY,
            screenX: pageX,
            screenY: pageY,
            pageX: pageX,
            pageY: pageY
        };
        if(eventType === 'touchstart' || eventType === 'touchmove') {
            e.targetTouches = createTouchList(target, pos);
            e.touches = createTouchList(target, pos);
        } else {
            e.targetTouches = new TouchList();
            e.touches = new TouchList();
        }

        e.changedTouches = createTouchList(target, pos);

        return e;

        function createTouchList(target, pos) {
            var touchList = new TouchList();
            touchList.push(new Touch(target, 1, pos));
            return touchList;
        }

        function Touch(target, identifier, pos) {
            this.identifier = identifier;
            this.target = target;
            this.clientX = pos.clientX;
            this.clientY = pos.clientY;
            this.screenX = pos.screenX;
            this.screenY = pos.screenY;
            this.pageX = pos.pageX;
            this.pageY = pos.pageY;
        }

        function TouchList() {
            var touchList = [];

            touchList.item = function(index) {
                return this[index] || null;
            };

            // specified by Mozilla
            touchList.identifiedTouch = function(id) {
                return this[id + 1] || null;
            };

            return touchList;
        }
    }

    function createKeyboardEvent(eventType, clientX, clientY, pageX, pageY) {
        /*var e = document.createEvent('KeyboardEvent');
        e.initKeyboardEvent(eventType, true, true, window, 0, 69, 0, "", false);*/
        var e = new KeyboardEvent(eventType, {
            key: 'e', charCode: 101, keyCode: 101
        });
        return e;
    }

    function createMouseEvent(target, eventType, clientX, clientY, pageX, pageY) {
        var e = new MouseEvent(eventType, {
            bubbles: true,
            screenX: pageX,
            screenY: pageY,
            clientX: clientX,
            clientY: clientY,
            ctrlKey: false,
            shiftKey: false,
            altKey: false,
            metaKey: false,
            button: 0
        });
        return e;
    }

    function simulateEvent(eventType, xpath) {
        var target = getElementFromXpath(xpath)
            , e;

        var clientRect = target.getBoundingClientRect()
        , clientX = clientRect.left + (Math.floor(clientRect.height / 2))
        , clientY = clientRect.top + (Math.floor(clientRect.width / 2))
        , pageX = document.body.scrollLeft + clientX
        , pageY = document.body.scrollTop + clientY;

        switch(eventType) {
            case 'touchstart':
            case 'touchend':
            case 'touchmove':
            case 'touchleave':
            case 'touchcancel':
                e = createTouchEvent(target, eventType, clientX, clientY, pageX, pageY);
                break;
            case 'click':
            case 'dblclick':
            case 'mousedown':
            case 'mousemove':
            case 'mouseup':
            case 'mouseover':
            case 'mouseout':
            case 'mousewheel':
            case 'wheel':
                e = createMouseEvent(target, eventType, clientX, clientY, pageX, pageY);
                break;
            case 'keyup':
            case 'keydown':
            case 'keypress':
                e = createKeyboardEvent(eventType, clientX, clientY, pageX, pageY);
                break;
        };

        console.log(e);
        target.dispatchEvent(e);
    }

    window.$$eventSniffer.simulateEvent = simulateEvent;

    // Log JavaScript errors
    window.$$eventSniffer.jsErrors = [];

    window.$$eventSniffer.getAndResetJSErrors = function() {
        var clone = window.$$eventSniffer.jsErrors.slice(0);
        window.$$eventSniffer.jsErrors = [];
        return clone;
    };

    window.onerror = function(msg, url, linenumber) {
        window.$$eventSniffer.jsErrors.push({
            msg: msg,
            url: url,
            linenumber: linenumber
        });
    };

    // Declare event types to look for
    var eventTypes = [

    /* MOUSE events */
    'click', 'dblclick', 'mousedown', /*'mouseup', 'mousemove',*/
    'mouseover', 'mouseout', /*'mousewheel', 'wheel',*/

    /* TOUCH events */
    'touchstart', 'touchleave', /*'touchend', 'touchcancel', 'touchmove',*/

    /* KEYBOARD events */
    'keydown', 'keyup', 'keypress'//, 'input'

    /*'select', 'change', 'submit', 'reset', 'focus', 'blur',*/
    ];

    var eventHandlers = [];
    window.$$eventSniffer.eventHandlers = eventHandlers;

    eventHandlers.trigger = function(xpath, event) {

    }

    if(!inIframe()) {
        overrideAddEventListener();

        // Walk subtree once DOM has been loaded
        window.addEventListener('load', function load(event){
            walkSubtree(document);
        },false);
    }

    function overrideAddEventListener() {
        var original = EventTarget.prototype.addEventListener;
        EventTarget.prototype.addEventListener = function() {
            var type = arguments[0];

            addEventHandler({
                type: type,
                xpath: getElementXPath(this) || '/html',
                /*target: this,
                callback: arguments[1]*/
            });

            /* Call original function with given arguments */
            original.apply(this, arguments);
        };  
    }

    function walkSubtree(subtree) {
        for(var i = 0; i < subtree.children.length; i++) {
            var curEl = subtree.children[i];

            // 2. Check programatically registered handlers
            var handlers = extractEventHandlers(curEl, eventTypes);
            addEventHandlers(handlers);

            // 3. Check handlers registered through markup
            //var mkupHandlers = extractMarkupEventHandlers(curEl, eventTypes);
            //addEventHandlers(mkupHandlers);

            if(curEl.children.length > 0)　
                walkSubtree(curEl);
        }
    }

    function extractEventHandlers(element, eventTypes) {
        var handlers = [];

        for(var i = 0; i < eventTypes.length; i++) {
            var eventType = eventTypes[i];
            var callback = element['on' + eventType];
            if(callback)
                handlers.push({
                    type: eventType,
                    xpath: getElementXPath(element) || '/html',
                    /*target: element,
                    callback: callback*/
                });
        }

        return handlers;
    }

    function extractMarkupEventHandlers(element, eventTypes) {
        var handlers = [];

        for(var i = 0; i < eventTypes.length; i++) {
            //var eventType = eventTypes[i];
            var attrName = 'on' + eventTypes[i];
            var callback = element.getAttribute(attrName);
            if(callback)
                handlers.push({
                    element: element,
                    type: eventTypes[i]/*,
                    callback: callback*/
                });
        }

        return handlers;
    }

    function addEventHandler(handler) {
        // Only add from eventHandlers list
        if(eventTypes.indexOf(handler.type) != -1) {
            /*if(!eventHandlers[handler.type])
                eventHandlers[handler.type] = [];*/
            eventHandlers.push(handler);
        }
    }

    function addEventHandlers(handlers) {
        for(var i = 0; i < handlers.length; i++) {
            addEventHandler(handlers[i]);
        }
    }

    // *****************
    // Gesture emulation

    window.$$eventSniffer.gestures = {
        touch: {
            tap: function(xpath) {
                tapOnce(xpath, 40);
            },
            doubletap: function(xpath) {
                tapOnce(xpath, 40);
                setTimeout(function() {
                    tapOnce(xpath, 40);
                }, 100);
            },
            taphold: function(xpath) {
                tapOnce(xpath, 800);
            },
            swiperight: function(xpath) {
                var target = getElementFromXpath(xpath);
                touchSwipe(target, 400, 0, 1, 1);
            },
            swipeleft: function(xpath) {
                var target = getElementFromXpath(xpath);
                touchSwipe(target, 400, 0, -1, 1);
            },
            swipeup: function(xpath) {
                var target = getElementFromXpath(xpath);
                touchSwipe(target, 0, 400, 1, -1);
            },
            swipedown: function(xpath) {
                var target = getElementFromXpath(xpath);
                touchSwipe(target, 0, 400, 1, 1);
            }
        },
        mouse: {
            swiperight: function(xpath) {
                var target = getElementFromXpath(xpath);
                mouseSwipe(target, 400, 0, 1, 1);
            },
            swipeleft: function(xpath) {
                var target = getElementFromXpath(xpath);
                mouseSwipe(target, 400, 0, -1, 1);
            },
            swipeup: function(xpath) {
                var target = getElementFromXpath(xpath);
                mouseSwipe(target, 0, 400, 1, -1);
            },
            swipedown: function(xpath) {
                var target = getElementFromXpath(xpath);
                mouseSwipe(target, 0, 400, 1, 1);
            }
        }
    };

    function tapOnce(xpath, holdTime) {
        $$eventSniffer.simulateEvent('touchstart', xpath);
        setTimeout(function(){
            $$eventSniffer.simulateEvent('touchend', xpath);
        }, holdTime);
    }

    function mouseSwipe(target, dragX, dragY, dirX, dirY) {
        swipe(target, ['mousedown', 'mousemove', 'mouseup'], createMouseEvent, dragX, dragY, dirX, dirY);
    }

    function touchSwipe(target, dragX, dragY, dirX, dirY) {
        swipe(target, ['touchstart', 'touchmove', 'touchend'], createTouchEvent, dragX, dragY, dirX, dirY);
    }

    function swipe(target, eventTypes, eventCtor, dragX, dragY, dirX, dirY) {
        var clientRect = target.getBoundingClientRect()
            , clientX = Math.floor(clientRect.left + (clientRect.width / 2))
            , clientY = Math.floor(clientRect.top + (clientRect.height / 2))
            , pageX = document.body.scrollLeft + clientX
            , pageY = document.body.scrollTop + clientY;

        var eSeries = createEventSeries(target, dragX, dragY, dirX, dirY,
            { clientX: clientX, clientY: clientY, pageX: pageX, pageY: pageY },
            eventTypes, eventCtor);

        for(var i = 0; i < eSeries.length; i++)
            throttleEvent(target, eSeries[i], i * 5);
    }

    function throttleEvent(target, event, time) {
        setTimeout(function() {
            target.dispatchEvent(event);
            console.log(event);
        }, time);
    }

    function createEventSeries(target, dragX, dragY, dirX, dirY, pos, eventTypes, eventCtor) {
        var eventSeries = [];
        eventSeries.push(eventCtor(target, eventTypes[0], pos.clientX, pos.clientY, pos.pageX, pos.pageY));

        for(var i = 0, j = 0; i < dragX || j < dragY; i = Math.min(i + 20, dragX), j = Math.min(j + 20, dragY)) {
            var newClientX = pos.clientX + i * dirX
                , newClientY = pos.clientY + j * dirY
                , newPageX = pos.pageY + i * dirX
                , newPageY = pos.pageY + j * dirY;
            eventSeries.push(eventCtor(target, eventTypes[1], newClientX, newClientY, newPageX, newPageY));
        }

        var lastClientX = pos.clientX + dragX * dirX
            , lastClientY = pos.clientY + dragY * dirY
            , lastPageX = pos.pageX + dragX * dirX
            , lastPageY = pos.pageY + dragY * dirY;

        eventSeries.push(eventCtor(target, eventTypes[2], lastClientX, lastClientY, lastPageX, lastPageY));
        return eventSeries;
    }

    // This code is "borrowed" from the FireBug project (http://getfirebug.com/) which is released under a BSD license

    /**
     * Gets an XPath for an element which describes its hierarchical location.
     */
    function getElementXPath(element)
    {
        if (element && element.id)
            return '//*[@id="' + element.id + '"]';
        else
            return getElementTreeXPath(element);
    };

    function getElementTreeXPath(element)
    {
        var paths = [];

        // Use nodeName (instead of localName) so namespace prefix is included (if any).
        for (; element && element.nodeType == 1; element = element.parentNode)
        {
            var index = 0;
            for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling)
            {
                // Ignore document type declaration.
                if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE)
                    continue;

                if (sibling.nodeName == element.nodeName)
                    ++index;
            }

            var tagName = element.nodeName.toLowerCase();
            var pathIndex = (index ? "[" + (index+1) + "]" : "");
            paths.splice(0, 0, tagName + pathIndex);
        }

        return paths.length ? "/" + paths.join("/") : null;
    };

    function getElementFromXpath(xpath) {
        return document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
    }

    function inIframe () {
        try {
            return window.self !== window.top;
        } catch (e) {
            return true;
        }
    }

})();