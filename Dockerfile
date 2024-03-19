FROM flink:1.18.1-scala_2.12-java11
RUN mvn -Dmaven.test.skip=true package
RUN mkdir -p $FLINK_HOME/usrlib
COPY target/blog-flink-cdc-kubernetes-application-mode-demo-1.0-SNAPSHOT.jar $FLINK_HOME/usrlib/demo.jar