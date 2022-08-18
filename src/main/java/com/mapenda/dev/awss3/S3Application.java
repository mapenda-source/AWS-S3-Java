package com.mapenda.dev.awss3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class S3Application {

    private static final AWSCredentials credentials;
    private static String bucketName;

    static {
        credentials = new BasicAWSCredentials(
                "AKIAXF344B3PXRRCOGFU",
                "YGhT9T2boS5ljKBpWnBVQBZRWw4q6SVgiAsoQ+HN"
        );
    }

    public static void main(String[] args) throws IOException {

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();

        AWSS3Service awsService = new AWSS3Service(s3client);

        bucketName = "mapenda-bucket";

        //creating a bucket
        if(awsService.doesBucketExist(bucketName)) {
            System.out.println("Bucket name is not available."
                    + " Try again with a different Bucket name.");
            return;
        }
        awsService.createBucket(bucketName);

        //list all the buckets
        for(Bucket s : awsService.listBuckets() ) {
            System.out.println(s.getName());
        }

        //deleting bucket
        //awsService.deleteBucket("mapenda-bucket-test2");

        //uploading object
        awsService.putObject(
                bucketName,
                "Document/hello.txt",
                new File("C:\\Users\\Mapenda\\OneDrive - UQAM\\Bureau\\hello.txt")
        );

        //listing objects
        ObjectListing objectListing = awsService.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }

        //downloading an object
        S3Object s3object = awsService.getObject(bucketName, "Document/hello.txt");
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        FileUtils.copyInputStreamToFile(inputStream, new File("C:\\Users\\Mapenda\\OneDrive - UQAM\\Bureau\\hello.txt"));

        //copying an object
        awsService.copyObject(
                "mapenda-bucket",
                "picture/pic.png",
                "mapenda-bucket2",
                "Document/picture.png"
        );

        //deleting an object
        awsService.deleteObject(bucketName, "Document/hello.txt");

        //deleting multiple objects
        String objkeyArr[] = {
                "Document/hello2.txt",
                "Document/picture.png"
        };

        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest("mapenda-bucket")
                .withKeys(objkeyArr);
        awsService.deleteObjects(delObjReq);
    }
}