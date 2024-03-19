FROM maven:3.8.4-openjdk-11-slim
RUN mvn -Dmaven.test.skip=true package
FROM flink:1.18.1-scala_2.12-java11
RUN mkdir -p $FLINK_HOME/usrlib
COPY lib/flink-table-planner_2.12-1.18.1.jar $FLINK_HOME/lib/flink-table-planner_2.12-1.18.1.jar
COPY target/blog-flink-cdc-kubernetes-application-mode-demo-1.0-SNAPSHOT.jar $FLINK_HOME/usrlib/demo.jar