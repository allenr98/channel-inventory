package com.animationlibationstudios.channel.inventory.persist.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to contain the AmazonDynamoDb client and configuration "glue" code. All the DynamoDb repositories should
 * extend this.
 */
@Component
public class BaseDynamoDbDao {

    /**
     * AWS SDK DynamoDb Client instance.
     */
    protected AmazonDynamoDB client;

    /**
     * AWS SDK DynamoDb mapper instance that wraps the client and provides generic CRUD operations.
     */
    protected DynamoDBMapper mapper;

    @Autowired
    public void setAmazonDynamoDBClient(AmazonDynamoDB dynamoDbClient) {
        client = dynamoDbClient;
    }

    @PostConstruct
    public void setMapper() {
        mapper = new DynamoDBMapper(client);
    }

    /**
     * Retrieve a list of all the tables in the DynamoDB we're connected to.
     * @return List of table names.
     */
    public List<String> getTableNames() {
        ListTablesResult tables = client.listTables();

        if (tables != null) {
            return tables.getTableNames();
        }

        return new ArrayList<>();
    }
}
