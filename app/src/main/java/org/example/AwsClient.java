package org.example;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * AWS S3 client wrapper for managing file uploads to S3 buckets.
 * 
 * <p>This class handles the initialization of AWS S3 clients, bucket management,
 * and file upload operations using the AWS SDK for Java v2.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic bucket creation if it doesn't exist</li>
 *   <li>Configurable AWS region</li>
 *   <li>Asynchronous file uploads using S3TransferManager</li>
 * </ul>
 * 
 * @author SecureStash Team
 * @version 1.0
 */
public class AwsClient {

    /** AWS region for S3 operations */
    private Region region = Region.AP_SOUTHEAST_1;
    
    /** Asynchronous S3 client instance */
    private S3AsyncClient s3;
    
    /** Name of the S3 bucket to use for uploads */
    private String bucket_name = "default-aws-backup";
    
    /** S3 Transfer Manager for efficient file uploads */
    private S3TransferManager transferManager;

    /**
     * Initializes the S3 client and transfer manager.
     * Creates the configured bucket if it doesn't exist.
     */
    private void setupClient(){
        this.s3 = S3AsyncClient.builder()
                .region(this.region)
                .build();

        this.transferManager = S3TransferManager.builder().
                s3Client(s3).
                build();
        boolean exists = false;

        List<Bucket> buckets = listBuckets();

        for (Bucket bucket: buckets){
            if (Objects.equals(bucket.name(), bucket_name)) {
                exists = true;
                break;
            }
        }
        if (!exists){
            create_bucket();
        }



    }

    /**
     * Constructs an AwsClient with default region (AP_SOUTHEAST_1) and bucket name.
     */
    AwsClient(){
        setupClient();
    }
    
    /**
     * Constructs an AwsClient with a custom bucket name and default region.
     * 
     * @param bucket_name the name of the S3 bucket to use
     */
    AwsClient(String bucket_name){
        this.bucket_name = bucket_name;
        setupClient();
    }

    /**
     * Constructs an AwsClient with a custom region and default bucket name.
     * 
     * @param region the AWS region for S3 operations
     */
    AwsClient(Region region){
        this.region = region;
        setupClient();
    }
    
    /**
     * Constructs an AwsClient with custom region and bucket name.
     * 
     * @param region the AWS region for S3 operations
     * @param bucket_name the name of the S3 bucket to use
     */
    AwsClient(Region region, String bucket_name){
        this.region = region;
        this.bucket_name = bucket_name;
        setupClient();
    }

    /**
     * Lists all S3 buckets in the configured region.
     * 
     * @return List of Bucket objects
     * @throws S3Exception if there's an error listing buckets
     */
    public List<Bucket> listBuckets() throws S3Exception {
        CompletableFuture<ListBucketsResponse> response = s3.listBuckets();
        return response.join().buckets();
    }

    /**
     * Creates the configured S3 bucket.
     * This is called automatically if the bucket doesn't exist.
     */
    private void create_bucket(){

        System.out.println("Creating bucket");

        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucket_name)
                .build();

        CompletableFuture<CreateBucketResponse> response = s3.createBucket(bucketRequest);
        System.out.println(response.join());
    }

    /**
     * Uploads a file to the configured S3 bucket.
     * 
     * <p>The file is uploaded using the S3 Transfer Manager for efficient transfer.
     * The operation blocks until the upload is complete.</p>
     * 
     * @param file FileDetails object containing file path and metadata
     * @return CompletedFileUpload containing upload result metadata
     */
    public CompletedFileUpload uploadFile(FileDetails file){

            UploadFileRequest uploadFileRequest = UploadFileRequest.builder()
                    .putObjectRequest(b -> b.bucket(bucket_name).key(file.getFilePath()))
                    .source(file.getAsFile())
                    .build();

        FileUpload fileUpload = transferManager.uploadFile(uploadFileRequest);
        CompletedFileUpload f = fileUpload.completionFuture().join();
        System.out.println("Uploaded File: " + file);
        return f;
    }

    /**
     * Gets the name of the configured S3 bucket.
     * 
     * @return the bucket name
     */
    public String getBucket_name() {
        return bucket_name;
    }

    /**
     * Sets the name of the S3 bucket to use for uploads.
     * 
     * @param bucket_name the new bucket name
     */
    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
    }
}
