#!/usr/bin/env bash
source /etc/profile ~/.bash_profile
export JAVA="$(which java)"
if [ $(find . -name jk*.jar) ]; then
    mv -f jk*.jar app.jar
fi
JAVA_OPT="${JAVA_OPT} -server -Xms4g -Xmx4g -Xmn512m"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:+DisableExplicitGC"
JAVA_OPT="${JAVA_OPT} -XX:ErrorFile=java_error_%p.log -XX:+PrintGCDetails -Xloggc:gc.log"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
JAVA_OPT="${JAVA_OPT} -Dspring.profiles.active="$1

#nohup $JAVA ${JAVA_OPT} -jar jk-service.jar > console.log 2>&1 & echo $! > run.pid

if [ $(whoami) = www ]; then
    outfile="/dev/null"
    if [[ $1 = test ]]; then
        outfile="console.log"
    fi
    nohup $JAVA ${JAVA_OPT} -jar app.jar > $outfile 2>&1 & echo $! > run.pid
else
    sudo -u www ./$0 $@
fi
