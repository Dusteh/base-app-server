#!/bin/sh
#Only run this script from root
cd "$(pwd)"

tmux split-window -h ./resources/containers/jager.container;
tmux split-window -h ./resources/containers/redis.container;
tmux split-window -h ./resources/containers/seq.container;
tmux split-window -h ./resources/containers/sql.container;
clj -M:dev:cider-clj
