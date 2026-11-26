(ns donut.cli.deps
  (:require
   [babashka.deps :as deps]
   [cheshire.core :as json]
   [clojure.edn :as edn]))

(defn local-roots
  "Libraries to load from a local checkout instead of Clojars, as an EDN map
  of lib symbol to directory in DONUT_LOCAL_LIBS. `dough cli` sets it so
  `donut generate --lib` runs the code under development."
  []
  (some-> (System/getenv "DONUT_LOCAL_LIBS") edn/read-string))

(defn add-dependency
  "Download the library at the given version and add it to the classpath.
  A library in `local-roots` is loaded from its checkout instead, whatever
  the version.

  `lib` is a symbol like `party.donut/generate` and `version` is an mvn
  version string."
  [lib version]
  (deps/add-deps {:deps {lib (if-let [root (get (local-roots) lib)]
                               {:local/root root}
                               {:mvn/version version})}}))

(defn latest-version
  "Look up the latest released version of `lib` on Clojars."
  [lib]
  (let [group    (or (namespace lib) (name lib))
        artifact (name lib)
        url      (format "https://clojars.org/api/artifacts/%s/%s" group artifact)]
    (-> (slurp url)
        (json/parse-string true)
        :latest_release)))

(defn add-dependency-latest
  "Look up the latest version of `lib` and add it to the classpath. A
  library in `local-roots` is loaded from its checkout without a lookup."
  [lib]
  (add-dependency lib (when-not (get (local-roots) lib) (latest-version lib))))
