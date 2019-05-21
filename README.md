# linux-job-worker

This project consists of a Linux job worker and a client interface. The worker provides an RPC API to start, stop, and query the status of a job. Additionally, the server will provide a method of authentication/authorization. The client interface will provide commands to start, stop and query a job.

## Design Doc

- [Google Docs](https://docs.google.com/document/d/1-QvIUmbmiVxjSXkEQQJDLnIlh8yJWYTRquue8eT_Cck/edit?usp=sharing)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

To build and run the application you need:

- [JDK 1.8](https://www.oracle.com/technetwork/java/javase/downloads/index.html) [(Download)](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) [(Install)](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Maven 3](http://maven.apache.org/index.html) [(Download)](http://maven.apache.org/download.cgi) [(Install)](http://maven.apache.org/install.html)

### Installing

Clone Repo

```
git clone https://github.com/bernardjkim/linux-job-worker.git
```

cd to directory

```
cd linux-job-worker
```

build project

```
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

```
java -jar target/client.jar [args...]
```

## Built With

- [gRPC-Java](https://github.com/grpc/grpc-java) - gRPC library for Java
