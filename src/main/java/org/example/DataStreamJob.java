/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
@Slf4j
public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        // Sets up the execution environment, which is the main entry point
        // to building Flink applications.
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // mysql props
        ParameterTool parameter = ParameterTool.fromArgs(args);
        String host = parameter.get("mysql.hostname");
        String port = parameter.get("mysql.port");
        String user = parameter.get("mysql.user");
        String pwd = parameter.get("mysql.pwd");
        String db = parameter.get("mysql.db");
        String sourceTable = parameter.get("source.table");
        String sinkTable = parameter.get("sink.table");
        log.info("MySQL地址：{}:{}", host, port);
        // 表结构
        Schema schema = Schema.newBuilder()
                .column("id", DataTypes.INT().notNull())
                .column("name", DataTypes.STRING())
                .column("create_time", DataTypes.TIMESTAMP())
                .primaryKey("id")
                .build();
        TableDescriptor sourceTableDesc = TableDescriptor.forConnector("mysql-cdc")
                .option("hostname", host)
                .option("port", port)
                .option("username", user)
                .option("password", pwd)
                .option("database-name", db)
                .option("table-name", sourceTable)
                .option("server-time-zone", "UTC")
                .option("scan.startup.mode", "earliest-offset")
                .schema(schema)
                .build();
        TableDescriptor sinkTableDesc = TableDescriptor.forConnector("jdbc")
                .option("url", "jdbc:mysql://" + host + ":" + port + "/" + db)
                .option("username", user)
                .option("password", pwd)
                .option("table-name", sinkTable)
                .schema(schema)
                .build();
        tableEnv.createTable("source", sourceTableDesc);
        tableEnv.createTable("sink", sinkTableDesc);

        tableEnv.executeSql("insert into sink select * from source");

    }
}
