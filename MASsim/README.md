== Sistemas Multiagentes / Servidor ==

1. No Eclipse, criar um novo Run configuration com a configuração:
	* Main class: massim.server.Server
	* Program arguments: --conf bin/conf/lecture-mas2010.xml
	* VM Arguments: -Xss10000k -Xmx2G -DentityExpansionLimit=10000000 -DelementAttributeLimit=10000000 -Djava.rmi.server.hostname=localhost
2. Criar outro Run configuration para o Cow Monitor:
	* Main class: massim.monitor.CowMonitor
	* Program arguments: -rmihost localhost -rmiport 1099
	* VM Arguments: -Xss20000k
2. Rodar o servidor, rodar o Cow Monitor, enviar um ENTER no console do servidor e rodar o cliente.	

