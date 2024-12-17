## Invy: Personal inventory management


### Startup
Invy service starts in dev mode by default when not supplying startup arguments 

#### To Run:
* Start dev support services via 
    * `./resources/containers/jager.container`
    * `./resources/containers/redis.container`
    * `./resources/containers/seq.container`
    * `./resources/containers/sql.container`
* Then run: `clj -M:dev` or `clj -M:dev:cider-clj`
* Once in a REPL execute `(start {})` from the `users` namespace
#### To Stop:
* From REPL:
    * `(in-ns 'invy.service.core)`
    * `(stop-service)`
