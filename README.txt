Scripts

There are 2 scripts to help build the project and run the peers: build.sh and run.sh respectively

The build.sh builds the project and can be run by using:
./build.sh
./build.sh first (also starts the rmiregistry)

The run.sh initiates a peer with default addresses and ports and can be run by using:
./run.sh <serverId> <rmiName>
./run.sh <version> <serverId> <rmiName>


For manually initializing the peer, it is necessary to be in the 'out' folder creted by running the script build.sh

=====================================================================================================


Peers

The peers can be initialized without using the script using the following command:
java server.Peer <version> <id> <rmiName> <mcAddress> <mcPort> <mbdAddress> <mdbPort> <mdrAddress> <mdrPort>
ex.: java server.Peer 2.0 3 rm3 224.0.0.0 8000 224.0.0.0 8001 224.0.0.0 8002

- Version
1.0 (base version), 1.1 (backup enhancement), 1.2 (restore enhancement), 1.3 (reclaim enhancement), 2.0 (all enhancements)

- Id
should be a unique integer

- mcAddress, mbdAddress, mdrAddress
should be the same in every peer
from 224.0.0.0 to 239.255.255.255

- mcPort, mdbPort, mdrPort
should be the same in every peer
from 1 to 65535


=====================================================================================================


Client

After the peers are initialized these commands can be used to initiate the protocols:
java client.Client <rmiName> BACKUP <filePath> <replicationDegree>
java client.Client <rmiName> RESTORE <filePath>
java client.Client <rmiName> DELETE <filePath>
java client.Client <rmiName> RECLAIM <space>
java client.Client <rmiName> SPACE <space>
java client.Client <rmiName> STATE

with rmiName being the same as the peer the client is going to be associated with.
These commands must be run inside the folder 'out' where the class files are located.


=====================================================================================================














