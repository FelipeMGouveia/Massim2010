#! /bin/sh
#Author: Chu, Viet Hung vhc@tu-clausthal.de

TEAM1=a
PASS1=1

TEAM2=b
PASS2=1

HOST=$1
NUMBER_OF_AGENT=20

 startAgent ()
{
	i=1
	while [ $i -le $4 ]
		do 
		echo "start: "$1$i":"$2		
		java -Xms8m -Xmx8M  -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar massim.agent.DemoGridAgent -username $1$i -password $2 -host $3 -logpath AgentsLog &		
		i=`expr $i + 1`
		done
}

if [ -z $1 ]
	then
	echo "to start this shell script, please type:"
	echo "sh startAgents [host of massim server]"
	echo "example: sh startAgent.sh agentcontest1.in.tu-clausthal.de"
	exit
fi

echo "Please choose teams to start or type 0 to create your own team.
Example: 1 2 \n"

echo "0> to create your own team"
echo "1> to start team "$TEAM1
echo "2> to start team "$TEAM2
echo "your choice: "
read  team1 team2 team3 team4 team5 team6 team7 team8 team9

for team in $team1 $team2 $team3 $team4 $team5 $team6 $team7 $team8 $team9;
do 

	if [ $team -eq 0 ];
		then
		echo "team name: "
		read teamname
		echo "password: "
		read password
		echo "number of agents: "
		read numberofagent
		startAgent $teamname $password $HOST $numberofagent
	 
	fi
	if [ $team -eq 1 ];
		then
		echo "starting team: "$TEAM1 "......."
		startAgent $TEAM1 $PASS1 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 2 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM2 $PASS2 $HOST	$NUMBER_OF_AGENT
	fi
done



