# linux-job-worker

This project consists of a Linux job worker and a client interface. The worker provides an RPC API to start, stop, and query the status of a job. Additionally, the server will provide a method of authentication/authorization. The client interface will provide commands to start, stop and query a job.

## Design Doc

- [Google Docs](https://docs.google.com/document/d/1-QvIUmbmiVxjSXkEQQJDLnIlh8yJWYTRquue8eT_Cck/edit?usp=sharing)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

To build and run the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](http://maven.apache.org/download.cgi)

## Built With

- [gRPC-Java](https://github.com/grpc/grpc-java) - gRPC library for Java
