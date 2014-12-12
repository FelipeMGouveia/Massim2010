#! /bin/sh

if [ -z $1 ]; then
	host=localhost
else
	host=$1
fi

if [ -z $2 ];then
	port=1099
else
	port=$2
fi

java -Xss20000k -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar massim.monitor.CowMonitor -rmihost $host -rmiport $port 
