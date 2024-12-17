(ns invy.utils.constants)


(def constants-map
  {})

(defn g ([key-val] (g key-val, :en)) 
        ([key-val, lang]
         (or (-> constants-map key-val lang)  
             key-val)))
