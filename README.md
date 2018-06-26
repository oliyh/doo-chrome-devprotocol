# doo-chrome-devprotocol

A runner for [doo](https://github.com/bensu/doo) which runs tests in Chrome, using the Chrome Dev Protocol with no need for karma or npm.

[![Clojars Project](https://img.shields.io/clojars/v/doo-chrome-devprotocol.svg)](https://clojars.org/doo-chrome-devprotocol)

## Usage

```clojure
(require '[doo-chrome-devprotocol.core :as dc])

(dc/run "path/to/doo-tests.js")
```

`run` also takes an optional map of options which should be self-explanatory

```clojure
{:port 8899
 :chrome-path "/usr/bin/chromium"
 :chrome-args ["--headless"
               "--disable-gpu" ;; recommended on windows
              ]
 :chrome-launch-timeout 5000
 :doo-message-prefix "doo:"
 :document-load-timeout 60000
 :doo-load-timeout 10000}
```

## Development
[![CircleCI](https://circleci.com/gh/oliyh/doo-chrome-devprotocol.svg?style=svg)](https://circleci.com/gh/oliyh/doo-chrome-devprotocol)

## License

Copyright © 2018 oliyh

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
