# R-F-D
Resume an incomplete downloaded file


The project ‘RESUMABLE FILE DOWNLOADER’ proposes a system in which the transfer of bytes is done effectively and efficiently. The bytes are checked and verified for their requirement at client, before being sent over the network by the server. The bytes received as a part of the file are appended to the earlier bytes/parts of the file, finally creating a complete and updated file.



1) SYSTEM ANALYSIS

The effectiveness and efficiency of the server depends on the amount of data it has to provide and the time it takes to serve the client. The lesser the time taken, the better is the performance. In the system described and applied in this project, ‘RESUMABLE FILE DOWNLOADER’ the main idea used is to send the least sufficient bytes to the client, so that the serving time for the client can be minimized.
For this purpose, I have used certain parameters that are provided to the server along with the HTTP Request for downloading the certain file. The server receives these headers and verifies them for errors and null values. This task is performed by the module called DownloadController. As per the conditions either the request id forwarded to next module for downloading/updating the file, or else a HTTP Response Code for the Error is sent in the response.
On the positive conditions the DownloadController forwards the request to the Upload Module. Here if the ‘Downloaded’ HTTP Request header is found to have ‘0’ as its value, then the Request is forwarded to the Download Module to serve the client with the complete file resource, else Upload Module compare the other Headers in HTTP Request with the Headers of File at Server.
During the comparison, if it is found that the file at Server has been modified after the ‘Last-Modified’ HTTP Request Header, then the complete file has to be transferred from the very beginning, and hence, a fresh HTTP Request is generated by client program along with a new file, discarding all the previously downloaded bytes. This Request is then further handled by the Download Module after the re-verifications by the DownloadController. The other condition triggers the further processing which leads to the transfer of the selected minimum bytes in the Response to the Request.
To select the required minimum bytes, the module compares the headers and calculates the exact next byte, from which the data is to be transferred.

1.1. REQUIRED FILE HEADERS
File Headers help in precise recognition of the file resource, and the parameter passing to the Server, and thus play a crucial role in deciding the flow control of the program. The Headers used are –
i. Content-Disposition -
This header contains the File name, hence used for verifying the filename.
ii. Last-Modified -
This header contains Date and Time when the file was last modified, and hence is very useful for checking the updates in the File at server.
iii. Downloaded –
This header contains the length of the file (i.e. the bytes) which is already downloaded/stored in the disk. The header helps in determining the next byte to be sent and thus is important for the Update Module.
iv. Buffer_Range -
This is an optional header which defines the buffer size of the client. The larger the buffer size, the lesser the stream transfer cycles, and better transfer rates. Default size kept in the Project is 1024 bytes.

1.2. HTTP RESPONSE CODES
There are certain pre-defined Response Codes in HTTP. These codes are quite helpful in denoting and understanding the various Server Responses, Exceptions and Errors. The Codes used in the Project are –
i. SC_PRECONDITION_FAILED Status code (412) indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
ii. SC_NOT_FOUND Status code (404) indicating that the requested resource is not available.
iii. SC_RESET_CONTENT Status code (205) indicating that the agent SHOULD reset the document view which caused the request to be sent. Here I have used it used as an indication of File Change/Update at server.

1.3. CLIENT PROGRAM – FILE HANDLING
For the client privacy and safety, the client’s data is not accessible from the server. Therefore, to read the file at the Client’s end, various permissions are required which are not readily accepted by the client. Thus, a solution to this trouble came up with the idea of a client-side program which will interact with the server on behalf of the client. Since, it would be installed at client’s platform, thus, the files can be easily modified and created as per the HTTP Responses.
Thus the program sends a HTTP Request to the Server for the file the Client desires, creates/downloads the file from the server to local disk, modifies/resumes the incomplete download, and discard the old bytes by over-writing the same file as per the HTTP Response of the Server to the Request of Client.



2) SYSTEM DESCRIPTION

The client first makes a request for downloading file to its local disk. For this, the client program requests the file name which is to downloaded and along with certain file headers: “Content-Disposition” – Header containing file-name, “Last-Modified” – Date for last modification (header here contains the null value, but would be required at later stages), “Byte_Range” – defines the buffer size of the Client in bytes for effective transmission, and “Downloaded” – containing the number of bytes already stored for the file (for new download, the value is set to ‘0’). The HTTP Request thus generated is send to the Server.
The Server receives the request and is first processed by the ControllerModule. Here the File-Headers send along the request are extracted and verified for their existence and values. If either “Content-Disposition” or “Last-Modified” Header is found missing, the required criteria is not fulfilled and “Prerequisite Condition Failed” HTTP Response is set back to the client, or else if the controller finds that the requested file is not at server, then respective HTTP Response is send. Further the value of “Downloaded” Header in HTTP Request is checked. If the value stored in the Header is found to be ‘0’, the request is forwarded to Download module, else the request is forwarded to Update Module on the Server.
Once the request arrives in Download Module, all the HTTP Response Headers are set and Byte Stream is opened on the HTTP Connection. The “Byte_Range” value is read from the HTTP Request and a buffer of same size is made for data to read, stored and send over the network, and the file is downloaded and saved by the client program at the local disk, in the default downloads folder.
In the Update module, the “Last-Modified” Header in the HTTP Request is read, and compared to the “Last-Modified” File Header form the File at server. If the file at Server has been modified recently, then all the downloaded bytes become useless, and hence “File Modified at Server” HTTP Response is send back, which when received by the client program, first discards the previously downloaded bytes creating new file of the same name, and then sends a fresh HTTP Request back to the server.
In other case, where the file has not been modified, the module reads the number of bytes equal to the value in “Downloaded” Header in HTTP Request, and sends the data bytes to the client from the exact next byte in a buffer capacity of “Buffer_Range” Header in HTTP Request. The data received by client program is appended to the file and thus the incompletely downloaded file data is utilized and partial data bytes are downloaded from the server, creating a Complete Desired File.



BIBLIOGRAPHY
[1] http://blog.netgloo.com/2014/10/27/using-mysql-in-spring-boot-via-spring-data-jpa-and-hibernate/
[2] https://wimdeblauwe.wordpress.com/2013/12/16/creating-a-rest-web-application-in-4-classes/
[3] https://www.youtube.com/watch?v=D6nJSyWB-xA
[4] http://ufasoli.blogspot.ch/2014/02/spring-boot-and-spring-data-jpa.html
[5] https://gist.github.com/davinkevin/b97e39d7ce89198774b4
[6] http://datum-bits.blogspot.fr/2013/01/implementing-http-byte-range-requests_30.html
[7] http://balusc.blogspot.in/2009/02/fileservlet-supporting-resume-and.html
