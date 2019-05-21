# linux-job-worker

This project consists of a Linux job worker and a client interface. The worker provides an RPC API to start, stop, and query the status of a job.

## Design Doc

- [Google Docs](https://docs.google.com/document/d/1-QvIUmbmiVxjSXkEQQJDLnIlh8yJWYTRquue8eT_Cck/edit?usp=sharing)

## Getting Started

### Prerequisites

To build and run the application you need:

- [JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/index.html) [(Download)](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) [(Install)](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Maven 3](http://maven.apache.org/index.html) [(Download)](http://maven.apache.org/download.cgi) [(Install)](http://maven.apache.org/install.html)

### Installing

Clone repo & build project

```
git clone https://github.com/bernardjkim/linux-job-worker.git
cd linux-job-worker
mvn package
```

### Testing

```
mvn test
```

### Run Application

Run Server

```
java -jar target/server.jar
```

Run Client

cmds include:

- **start [linux cmd]** - start new job
- **stream [linux cmd]** - start new job and stream output
- **stop [job id]** - stop job with matching id
- **status [job id]** - output status/log for job with matching id
- **list** - list all jobs

```
java -jar target/client.jar [cmd]
```

## Demo

![Demo](demo.gif?raw=true "Title")

## Limitations

- Unable to stream output of RPC with a large amount of output. Attempting to
  stream the output of **start yes** will result in a failed RPC with a status
  code of **RESOURCE_EXHAUSTED**. A possible solution might be to split the
  message into chunks with a max chunk size.

## Built With

- [gRPC-Java](https://github.com/grpc/grpc-java) - gRPC library for Java
