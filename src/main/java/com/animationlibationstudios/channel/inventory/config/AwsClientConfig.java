package com.animationlibationstudios.channel.inventory.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class AwsClientConfig {
    @Value("${cloud.aws.credentials.rob.accessKey}")
    private String amazonAWSAccessKey;
    @Value("${cloud.aws.credentials.rob.secretKey}")
    private String amazonAWSSecretKey;
    @Value("${cloud.aws.region.static}")
    private String amazonAWSRegion;

    private AWSCredentials getCredentials() {
        return new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return amazonAWSAccessKey;
            }

            @Override
            public String getAWSSecretKey() {
                return amazonAWSSecretKey;
            }
        };
    }

    private String getRegion() {
        return amazonAWSRegion;
    }

    @Bean
    public AmazonDynamoDB dynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getCredentials()))
                .withRegion(getRegion())
                .build();

        return client;
    }
}
