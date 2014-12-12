#! /bin/sh

# initialize certain values
server=../../target/agentcontest-2010-1.0-jar-with-dependencies.jar
conf=../conf
template=$conf/helpers/2010/template.xml
pngs=../maps
date=`date +%Y`

mkdir -p $conf
cd $pngs
pwd
for i in $( ls *.png )
do
  j=${i%.*}
  java -ea -cp $server massim.mapmaker.App $i $template $conf/$date-demo-$j.xml
  sed -i 's#<?xml version="1.0" encoding="UTF-8"?>#<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE conf SYSTEM "helpers/2010/config.dtd">#g' $conf/$date-demo-$j.xml
  sed -i "s#</conf>#\&accounts;\n</conf>#g" $conf/$date-demo-$j.xml
done
cd ..
