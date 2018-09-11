# simple-oak-app

Simple Jackrabbit OAK based application for my own learning / testing.

## How to run

    $ mvn spring-boot:run

which exposes [JCR Webdav Server](http://jackrabbit.apache.org/jcr/components/jackrabbit-jcr-server.html#JCR_Webdav_Server) at http://localhost:8080/.

## How to test

    $ curl -v --user admin:admin http://localhost:8080/default/
    $ curl -v --user admin:admin http://localhost:8080/default/jcr:root/

