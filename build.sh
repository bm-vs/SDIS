if [ ! -d "out" ] ; then
	mkdir out
fi
javac -sourcepath src -g src/server/Peer.java src/client/Client.java -d out

if [ -f "run.sh" ] ; then
	chmod +x run.sh
fi

if [ $# -eq 1 ] && [ $1 = "first" ] ; then
	cd out
	rmiregistry &
fi

echo "Service built and sent the output to folder out. Run the script run.sh to begin"
echo "If you haven't inserted the command 'rmiregistry &' write the command"
echo $0 "first"
