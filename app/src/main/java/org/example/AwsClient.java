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

public class AwsClient {

    private Region region = Region.AP_SOUTHEAST_1;
    private S3AsyncClient s3;
    private String bucket_name = "default-aws-backup";
    private S3TransferManager transferManager;


    private void setupClient(){
        Region region = Region.AP_SOUTHEAST_1;
        this.s3 = S3AsyncClient.builder()
                .region(region)
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

    AwsClient(){
        setupClient();
    }
    AwsClient(String bucket_name){
        this.bucket_name = bucket_name;
        setupClient();
    }

    AwsClient(Region region){
        this.region = region;
        setupClient();
    }
    AwsClient(Region region, String bucket_name){
        this.region = region;
        this.bucket_name = bucket_name;
        setupClient();
    }

    public List<Bucket> listBuckets() throws S3Exception {
        CompletableFuture<ListBucketsResponse> response = s3.listBuckets();
        return response.join().buckets();
    }

    private void create_bucket(){

        System.out.println("Creating bucket");

        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucket_name)
                .build();

        CompletableFuture<CreateBucketResponse> response = s3.createBucket(bucketRequest);
        System.out.println(response.join());
    }

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

    public String getBucket_name() {
        return bucket_name;
    }

    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
    }
}
