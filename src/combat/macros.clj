(ns combat.macros)

(defmacro when-not-empty [field & body]
  `(when (not-empty ~field) ~@body))
