goog.provide('jayq.util');
goog.require('cljs.core');
jayq.util.map__GT_js = (function map__GT_js(m){
var out = {};
var G__3657 = cljs.core.seq.call(null,m);
while(true){
if(G__3657)
{var vec__3658 = cljs.core.first.call(null,G__3657);
var k = cljs.core.nth.call(null,vec__3658,0,null);
var v = cljs.core.nth.call(null,vec__3658,1,null);
(out[cljs.core.name.call(null,k)] = v);
{
var G__3659 = cljs.core.next.call(null,G__3657);
G__3657 = G__3659;
continue;
}
} else
{}
break;
}
return out;
});
jayq.util.wait = (function wait(ms,func){
return setTimeout(func, ms);
});
/**
* @param {...*} var_args
*/
jayq.util.log = (function() { 
var log__delegate = function (v,text){
var vs = ((cljs.core.string_QMARK_.call(null,v))?cljs.core.apply.call(null,cljs.core.str,v,text):v);
return console.log(vs);
};
var log = function (v,var_args){
var text = null;
if (goog.isDef(var_args)) {
  text = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);
} 
return log__delegate.call(this, v, text);
};
log.cljs$lang$maxFixedArity = 1;
log.cljs$lang$applyTo = (function (arglist__3660){
var v = cljs.core.first(arglist__3660);
var text = cljs.core.rest(arglist__3660);
return log__delegate(v, text);
});
log.cljs$lang$arity$variadic = log__delegate;
return log;
})()
;
/**
* Recursively transforms ClojureScript maps into Javascript objects,
* other ClojureScript colls into JavaScript arrays, and ClojureScript
* keywords into JavaScript strings.
*/
jayq.util.clj__GT_js = (function clj__GT_js(x){
if(cljs.core.string_QMARK_.call(null,x))
{return x;
} else
{if(cljs.core.keyword_QMARK_.call(null,x))
{return cljs.core.name.call(null,x);
} else
{if(cljs.core.map_QMARK_.call(null,x))
{var obj = {};
var G__3663 = cljs.core.seq.call(null,x);
while(true){
if(G__3663)
{var vec__3664 = cljs.core.first.call(null,G__3663);
var k = cljs.core.nth.call(null,vec__3664,0,null);
var v = cljs.core.nth.call(null,vec__3664,1,null);
(obj[clj__GT_js.call(null,k)] = clj__GT_js.call(null,v));
{
var G__3665 = cljs.core.next.call(null,G__3663);
G__3663 = G__3665;
continue;
}
} else
{}
break;
}
return obj;
} else
{if(cljs.core.coll_QMARK_.call(null,x))
{return cljs.core.apply.call(null,cljs.core.array,cljs.core.map.call(null,clj__GT_js,x));
} else
{if("\uFDD0'else")
{return x;
} else
{return null;
}
}
}
}
}
});
