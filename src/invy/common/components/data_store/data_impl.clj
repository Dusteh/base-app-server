(ns invy.common.components.data-store.data-impl
  (:require [next.jdbc :as next.jdbc]
            [next.jdbc.sql :as next.jdbc.sql]
            [clojure.string :as s]
            [clojure.tools.logging :as log :refer :all]))

(defn build-data-source [db-spec]
  "Builds a next.jdbc connection from a db-spec"
  (next.jdbc/get-connection db-spec))

(defprotocol Table
  "Protocol for a table can be used to spec out specefic tablbles"
  (creates [self]
           "Functionality to create a table")
  (destroys [self] ;; these are typically noops but are here for the sake of completness and my mental health
            "Functionality to destroy a table")
  (alters [self] ;; these are typically noops but are here for the sake of completness and my mental health
          "Functionality to alter a table")
  (inserts [self record]
          "Functionality to create a record")
  (updates [self record where-map]
          "Functionality to upsert a record") 
  (deletes [self where-map]
          "Functionality to delete a record")
  (reads [self query]
         "Functionality to read a record(s)"))

(defrecord TableImplRecord [name columns db-spec]
  ;"Default Table implementation, contains the information needed to create the table and do basic CRUD opperations
  ;
  ;Examples 
  ;(let [table (->TableImplRecord \"users\" [{:col-name \"id\" :col-dt \"uuid PRIMARY KEY DEFAULT gen_random_uuid()\"}
  ;                                       {:col-name \"name\" :col-dt \"text\"}
  ;                                       {:col-name \"email\" :col-dt \"text\"}
  ;                                       {:col-name \"password\" :col-dt \"text\"}
  ;                                       {:col-name \"created_at\" :col-dt \"timestamp DEFAULT now()\"}
  ;                                       {:col-name \"updated_at\" :col-dt \"timestamp DEFAULT now()\"}]
  ;                               db-spec)])
  ;"
  Table
  (creates [self]
    (next.jdbc/execute! (build-data-source db-spec) 
                        [(str "create table if not exists " name " (" 
                             (s/join ", " (map (fn [col] (str (:col-name col) " " (:col-dt col))) columns)) ")")]))
  (destroys [self] (throw (Exception. "Not Implemented")));;noop
  (alters [self] (throw (Exception. "Not Implemented")));;noop
  (inserts [self record]
    (next.jdbc.sql/insert! (build-data-source db-spec) name record))
  (updates [self record where-map]
    (next.jdbc.sql/update! (build-data-source db-spec) name record where-map))
  (deletes [self where-map]
    (next.jdbc.sql/delete! (build-data-source db-spec) name where-map))
  (reads [self query]
    (next.jdbc.sql/find-by-keys(build-data-source db-spec) name query)))

(defn load-table-defenitions [config db-spec]
  "Loads the table defenitions from the config
  Example record:
  {:name \"users\"
   :columns [{:col-name \"id\" :col-dt \"uuid PRIMARY KEY DEFAULT gen_random_uuid()\"}
             {:col-name \"name\" :col-dt \"text\"}
             {:col-name \"email\" :col-dt \"text\"}
             {:col-name \"password\" :col-dt \"text\"}
             {:col-name \"created_at\" :col-dt \"timestamp DEFAULT now()\"}
             {:col-name \"updated_at\" :col-dt \"timestamp DEFAULT now()\"}]
   :db-spec {:dbtype \"postgresql\"
             :dbname \"invy\"
             :host \"localhost\"
             :port 5432
             :user \"postgres\"
             :password \"invy\"}}"
  (mapv (fn [table] (map->TableImplRecord (assoc table :db-spec db-spec))) config))
