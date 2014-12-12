@echo off
echo NO SUPPORT FOR WINDOWS
@echo on
java -Xss20000k -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar massim.monitor.CowMonitor -rmihost localhost -rmiport 1099
pause