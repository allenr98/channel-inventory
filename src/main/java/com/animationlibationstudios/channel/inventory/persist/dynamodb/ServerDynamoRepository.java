package com.animationlibationstudios.channel.inventory.persist.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Perform CRUD operations to a DynamoDB instance using the AWS SDK.
 */
@Component
@Profile("dynamoDb")
public class ServerDynamoRepository extends BaseDynamoDbDao implements RoomStorePersister {

    private static final Logger LOG = LoggerFactory.getLogger(ServerDynamoRepository.class);

    @PostConstruct
    void init() { listMyTables(); }
//        CreateTableRequest request = new CreateTableRequest().withTableName("server");
//
//        request.withKeySchema(new KeySchemaElement()
//                .withAttributeName("serverName")
//                .withKeyType(KeyType.HASH));
//
//        TableUtils.createTableIfNotExists(client, request);
//    }

    /**
     * Table Object Class.
     */
    @DynamoDBTable(tableName="server")
    public static class Server {
        private String serverName;
        private Room[] rooms;

        Server() {}

        Server(String serverName) {
            this.serverName = serverName;
            this.setRooms();
        }

        @DynamoDBHashKey(attributeName="serverName")
        String getServerName() {
            return serverName;
        }
        void setServerName(String serverName) {
            this.serverName = serverName;
        }

        @DynamoDBAttribute(attributeName="rooms")
        Room[] getRooms() { return rooms; }
        void setRooms(Room[] rooms) { this.rooms = rooms; }

        void setRooms() {
            Room[] roomArray = null;

            if (serverName != null && !serverName.isEmpty()) {
                HashMap<String, Room> roomMap = RoomStore.DataStore.get(serverName);
                if (roomMap != null && roomMap.size() > 0) {
                    roomArray = new Room[roomMap.size()];
                    int index = 0;
                    for (String key : roomMap.keySet()) {
                        roomArray[index++] = roomMap.get(key);
                    }
                }
            }

            this.rooms = roomArray;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Server)) return false;
            Server that = (Server) o;
            return Objects.equals(getServerName(), that.getServerName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getServerName(), getRooms());
        }
    }

    @Override
    public void writeServer(String serverName) {
        Server serverContents = new Server(serverName);
        mapper.save(serverContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public HashMap<String, Room> readServer(String serverName) throws IOException {
        Server serverContents = mapper.load(Server.class, serverName);

        // Add the read data to the local cache.
        HashMap<String, Room> rooms = new HashMap<>();
        for (Room room: serverContents.getRooms()) {
            rooms.put(room.getChannel(), room);
        }

        return rooms;
    }


    private void listMyTables() {

        ListTablesResult tables = client.listTables();

        for (String tableName :tables.getTableNames()) {
            LOG.info("TABLE: " + tableName);
            tableDescribe(tableName);
        }
    }

    private void tableDescribe(String tableName) {
        System.out.println("Describing " + tableName);

        DescribeTableResult tableDescription = client.describeTable(tableName);
        LOG.info(new Gson().toJson(tableDescription));
    }
}