(ns bug.main
  (:require [cljfx.api :as fx]))

(def screen-size 1000)

(defn nodes-ids-sub [context] (keys (fx/sub context :nodes)))
(defn node-sub [context node-id] (get (fx/sub context :nodes) node-id))

(defn node-cmp [{:keys [fx/context node-id]}]
  (let [{:keys [x y]} (fx/sub context node-sub node-id)]
    {:fx/type :rectangle
     :on-mouse-pressed {:event/type ::grab
                        :node-id node-id}
     :on-mouse-released {:event/type ::grab-release}
     :translate-x x
     :translate-y y
     :width  50
     :height 50}))

(defn diagram-view [{:keys [fx/context]}]
  (let [nodes-ids (fx/sub context nodes-ids-sub)]
    (prn "Rendering diagram ")
    {:fx/type :pane
     :pref-width screen-size
     :pref-height screen-size
     :on-mouse-released {:event/type ::grab-release}
     :on-mouse-dragged  {:event/type ::drag}
     :children (when nodes-ids
                 (for [nid nodes-ids]
                   {:fx/type node-cmp
                    :node-id nid}))}))

(defn root-view [{:keys [fx/context]}]
  {:fx/type :stage
   :showing true
   :width screen-size
   :height screen-size
   :scene {:fx/type :scene
           :root {:fx/type diagram-view}}})

(defn handle-event [{:keys [fx/event fx/context node-id] :as ev}]
  (case (:event/type ev)
    ::grab         {:context (fx/swap-context context assoc :grabbed-node node-id)}
    ::grab-release {:context (fx/swap-context context dissoc :grabbed-node)}
    ::drag         {:context (fx/swap-context context (fn [{:keys [grabbed-node] :as ctx}]
                                                        (-> ctx
                                                            (assoc-in [:nodes grabbed-node :x] (.getSceneX event))
                                                            (assoc-in [:nodes grabbed-node :y] (.getSceneY event)))))}))

(defn -main [& args]
  (def *context (atom (fx/create-context {:nodes (->> (repeatedly 20 #(hash-map :x (rand-int screen-size)
                                                                                :y (rand-int screen-size)))
                                                      (zipmap (range)))})))

  (def app (fx/create-app *context
                          :event-handler handle-event
                          :desc-fn (fn [_] {:fx/type root-view})
                          )))
