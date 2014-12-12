@echo off
echo NO SUPPORT FOR WINDOWS
@echo on
java -Xss10000k -Xmx600M  -DentityExpansionLimit=1000000 -DelementAttributeLimit=1000000 -Djava.rmi.server.hostname=localhost -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar  massim.server.Server --conf conf/demo.xml
pause