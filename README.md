# FileFtpMicroservice
### Functions:

1. This microservice connects to an FTP server running on another host via MockFtpServer.
2. It uploads files to the FTP server when it receives messages from FileMongoMicroservice or LoginAndRegister.
3. It can remove files from the FTP server when the Kafka Listener receives messages from FileMongoMicroservice.
4. It can stream video and audio files from the FTP server.
5. It can download files from the FTP server.

### In the next version:

- Optimize compression of folders on the FTP server to zip format for download and add functionality to send the status of uploaded files.

Diagram of FileFtpMicroservice:

```mermaid
	flowchart TB
	subgraph FtpServer
		id1[Client Folder]
	end
	subgraph Kafka
		subgraph group-id
			id2[Create File]
			id3[Create Directory]
			id4[Delete File]
			id5[Delete Directory]
			id6[Create User]
		end
	end
	subgraph FileFtpMicroservice
		direction TB
		subgraph KafkaListener
			id7[CreateFile]
			id8[CreateDir]
			id15[DeleteFile]
			id16[DeleteDir]
		end
			id9[StreamFile]
			id10[DownloadFile]
			id11[DownloadZipCompressedFile]
	end
	id12[Client]
	id13[LoginAndRegister]
	id14[FileMongoMicroservice]
	id13 -- Create User --> id6 --> id8 --> id1
	id14 -- Create File --> id2 --> id7 --> id1
	id14 -- Create Folder --> id3 --> id8 --> id1
	id14 -- Delete File --> id4 --> id15 --> id1
	id14 -- Delete Folder --> id5 --> id16 --> id1
	id9 --> id12
	id10 --> id12
	id11 --> id12
	
```
