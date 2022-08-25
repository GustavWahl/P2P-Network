


<h1>BitTorrent file sharing application - Hybrid P2P</h1>

The architecture consists of a tracker which is a sort of coordinator and let’s the peers discover other peers. <br/>
Seeders which is the peer with the full copy of the file and the leechers which are downloading from the end user that downloads from the seed and other leecehrs, 
the peers can be both seeders and leachers. The main goal of this project is to distribute the downloading of files through a network, where hosts can both download and upload files to other hosts.

<h3>Tracker</h3>

The tracker will maintain a membership table and will let the peers know which peers are in the network that they can download from. It will also manage torrent sessions called swarms. The end user will connect to a tracker to recieve a list of all peers available for downloading. Also the end user will give it’s information, listening port and the file it’s donwloading, the Tracker will use this to update it’s data about the current swarm/session.
Seeds will let the tracker know that they have a full copy of a certain file, the network can only start a download if there exist a seed with the requested file.

<h3>Peers </h3>

All host will be able to download and upload to each other, a peer can both be a leech and seed.
<ul>
 <li>
<h4>File transfer and piece selection algorithm</h4>
Each file will be broken into 512Kb or smaller parts, the first piece that will be replicated is random, after that we will use Rarest first to chose the least common piece off all peers that have copies to download from another peer. The transfer will also include the hash of the fully constructed file and the hash of a given piece. Once a piece is downloaded it will be available to the other peers in a swarm.
  </li>
  <li>
<h4>Seeders</h4>
Are the peers with the whole file, they will upload the file until everybody who requested a full copy has it. Seeders will also connect to tracker once they become a seeder, to let it know that it has a full copy of the file.
  </li>
  <li>
<h4>Leechers</h4>
The peers without the full copy of the file will act as leechers, they will recieve a list of all peers that have the file from the tracker and will download the file from one or multiple of the peers. It will simultaneously upload the file to other peers in the swarm.
  </li>
</ul>

<h3>File reconstruction and validation</h3>
The pieces downloaded needs to be validated through a checksum, if not valid the host will reject the piece and retry downloading another piece using the selection algorithm.
After all pieces has been downloaded it will reconstruct the file and validate it as well using checksum. The peer will then become a seeder for that file.



<h5>Resources:</h5>

https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.114.4974&rep=rep1&type=pdf
https://web.cs.ucla.edu/classes/cs217/05BitTorrent.pdf
https://www.beautifulcode.co/blog/58-understanding-bittorrent-protocol
https://www.explainthatstuff.com/howbittorrentworks.html
https://www.researchgate.net/figure/BitTorrent-Architecture_fig2_221082210

