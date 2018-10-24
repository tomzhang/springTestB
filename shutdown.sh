#!/usr/bin/env bash
pf="run.pid"
if [ ! -f "$pf" ]; then
    exit 0;
fi

pid=$(cat run.pid)

if [ -d /proc/$pid ];then
    kill -9 $pid
else
    echo "警告:run.id文件中的进程不存在，可能服务存在异常!";
    exit 0
fi


