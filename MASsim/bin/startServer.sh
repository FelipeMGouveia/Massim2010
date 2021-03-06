#! /bin/sh

# initialize certain values
date=`date +%Y-%m-%d_%H:%M`
server=../target/agentcontest-2010-1.0-jar-with-dependencies.jar
hostname=`hostname -f`
conf=conf/

# move old result page
#mv /home/massim/www/webapps/massim/index.html /home/massim/www/webapps/massim/moved-at-$date.html

# create folder for log files
mkdir -p backup

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

java -ea -Dcom.sun.management.jmxremote -Xss10000k -Xmx600M  -DentityExpansionLimit=1000000 -DelementAttributeLimit=1000000 -Djava.rmi.server.hostname=$hostname -cp $server massim.server.Server --conf $conf | tee backup/$date-$hostname.log

# create results page
#cd ../target/classes
#java massim.webclient.CreateResultsPage /home/massim/www/webapps/massim/xslt/createResultsHTML.xslt ../../configsScriptsEtc/GridSimulation_report.xml
#cp Tournament.html /home/massim/www/webapps/massim/index.html

# make backup of the report
#mv ../../configsScriptsEtc/GridSimulation_report.xml ../../configsScriptsEtc/backup/$date-$hostname-result.xml
