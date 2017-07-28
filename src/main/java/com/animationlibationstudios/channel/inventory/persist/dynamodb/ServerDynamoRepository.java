package com.animationlibationstudios.channel.inventory.persist.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Perform CRUD operations to a DynamoDB instance using the AWS SDK.
 */
@Component
@Profile("dynamoDb")
public class ServerDynamoRepository extends BaseDynamoDbDao implements RoomStorePersister {

    /**
     * Table Object Class.
     */
    @DynamoDBTable(tableName="servers")
    public static class Server {
        private String serverName;
        private Room[] rooms;

        @DynamoDBHashKey(attributeName = "serverName")
        public String getServerName() {
            return serverName;
        }
        public void setServerName(String serverName) { this.serverName = serverName; }

        @DynamoDBAttribute(attributeName="rooms")
        public Room[] getRooms() { return rooms; }
        public void setRooms(Room[] rooms) { this.rooms = rooms; }

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
        Map<String, Room> serverContents = RoomStore.DataStore.get(serverName);
        mapper.save(serverContents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Room> readServer(String serverName) throws IOException {
        return mapper.load(Map.class, serverName);
    }
}
