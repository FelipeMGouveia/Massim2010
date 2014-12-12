#! /bin/sh
server=../../target/agentcontest-2010-1.0-jar-with-dependencies.jar
conf=../conf/

echo "Please choose a number and then press enter:"
count=0
for i in $( ls $conf )
do
  if [ -f $conf/$i ]
  then
    echo $count: $i
    count=`expr $count + 1`
  fi
done

read number

count=0
for i in $( ls $conf )
do
  if [ -f $conf/$i ]
  then
    if [ $number -eq $count ]
    then
      conf+=$i
    fi
    count=`expr $count + 1`
  fi
done

echo "Starting server: $conf"

java -DentityExpansionLimit=2000000 -DelementAttributeLimit=1000000 -Xss20000k -cp $server massim.monitor.AgentController $conf
