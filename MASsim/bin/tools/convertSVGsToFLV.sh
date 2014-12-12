NUM=0
QUEUE=""

function echoqueue {
	for PID in $QUEUE
	do
		echo -n "$PID "
	done
	echo
}

function queue {
	QUEUE="$QUEUE
$1"
	NUM=$(($NUM+1))
	#echo -n "QUEUE ";echoqueue
}

function dequeue {
	OLDDEQUEUE=$QUEUE
	QUEUE=""
	for PID in $OLDDEQUEUE
	do
		if [ ! "$PID" = "$1" ] ; then
			QUEUE="$QUEUE
$PID"
		fi
	done
	NUM=$(($NUM-1))
	#echo -n "DEQUEUE ";echoqueue
}

function checkqueue {
	OLDCHQUEUE=$QUEUE
	for PID in $OLDCHQUEUE
	do
		if [ ! -d /proc/$PID ] ; then
			dequeue $PID
		fi
	done
	#echo -n "CHECKQUEUE ";echoqueue
}


mkdir -p svgs
sed -e "s/<\/svg>//g" masSim-0.svg > svgs/0.svg
folder=$( pwd )
count=0
for i in $( ls -1 masSim-*svg ); do
  if [ $i != masSim-0.svg ]
  then
    j=${i:7}
    echo $count :: $i :: $j
    cp svgs/0.svg svgs/$j && xml_grep --root "svg/g[@id='scaleSvg']" --wrap '' $i >> svgs/$j && echo "</svg>" >> svgs/$j &
    queue $!
    count=`expr $count + 1`
    while [ $NUM -ge 100 ] # MAX PROCESSES
      do
        checkqueue
        sleep 1
    done
  fi
done

sleep 10
rm svgs/0.svg

mkdir -p pngs
cd svgs
count=0
for i in $( ls -1 *svg ); do
  echo $count :: $i
  name=`echo $i | sed -e "s/.svg//"`
  namevier=`printf "%04d.png" $name`
  #inkscape -D  -w 1024 -f $i -e ../pngs/$namevier
  #rsvg -h 1024 $i ../pngs/$namevier &
  rsvg-convert -h 1024 --background-color=white $i -o ../pngs/$namevier &
  queue $!
  count=`expr $count + 1`
  while [ $NUM -ge 50 ] # MAX PROCESSES
    do
      checkqueue
      sleep 1
  done
done

sleep 10

cd ../pngs
ffmpeg -r 3 -i "%04d.png" -vcodec libx264 -vpre libx264-lossless_ultrafast -an -threads 0 $folder.mp4
cd ..

