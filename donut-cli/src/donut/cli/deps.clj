(ns donut.cli.deps
  (:require
   [babashka.deps :as deps]
   [cheshire.core :as json]))

(defn add-dependency
  "Download the library at the given version and add it to the classpath.

  `lib` is a symbol like `party.donut/generate` and `version` is an mvn
  version string."
  [lib version]
  (deps/add-deps {:deps {lib {:mvn/version version}}}))

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
  "Look up the latest version of `lib` and add it to the classpath."
  [lib]
  (add-dependency lib (latest-version lib)))
