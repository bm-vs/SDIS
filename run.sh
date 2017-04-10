cd out
if [ $# -eq 3 ] ; then
	java server.Peer $1 $2 $3 224.0.0.3 4445 224.0.0.3 4444 224.0.0.3 4446
elif [ $# -eq 2 ] ; then
	java server.Peer 2.0 $1 $2 224.0.0.3 4445 224.0.0.3 4444 224.0.0.3 4446
elif [ $# -eq 0 ] ; then
	echo "Usage:" $0 serverId RmiName
	echo "This will initialize a peer using default addresses and ports. This default also has all enhancements applied. The id and rmi object name you have to provide."
	echo

	echo "Usage:" $0 version serverId RmiName
	echo "The version is one of the following 1.0 1.1 1.2 1.3 2.0."
	echo "1.0 Represents the base implementation."
	echo "1.* Represents an enhancement each."
	echo "2.0 Represents the application of all enhancements implemented."
	echo "Server Id is an integer."
fi
