package com.example.hairstyle.service.implementation;

import com.example.hairstyle.config.FirebaseConfig;
import com.example.hairstyle.payload.request.FileRequest;
import com.example.hairstyle.payload.request.FirebaseCredential;
import com.example.hairstyle.service.FirebaseFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class FirebaseFileServiceImpl implements FirebaseFileService {

    private final FirebaseConfig firebaseConfig;

    private StorageOptions storageOptions;
    private String bucketName;
    private String projectId;


    private FirebaseFileServiceImpl(FirebaseConfig firebaseConfig) throws Exception {
        this.firebaseConfig = firebaseConfig;

        bucketName = firebaseConfig.getBucketName();

        projectId = firebaseConfig.getProjectID();

        var firebaseCredential = createFirebaseCredential();

        this.storageOptions = StorageOptions
                .newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream(firebaseCredential)).build();

    }

    public ResponseEntity uploadFile(MultipartFile multipartFile) throws IOException {
        log.debug("bucket name====" + bucketName);
        var file = convertMultiPartToFile(multipartFile);
        var filePath = file.toPath();
        var objectName = generateFileName(multipartFile);
        var storage = storageOptions.getService();

        var blobId = BlobId.of(bucketName, objectName);
        var blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(filePath));

        log.info("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);

        var uploadedFile = new String[]{"fileUrl", objectName};

        String fileDownloadUri = uploadedFile[0];
        String fileName = uploadedFile[1];
        log.info("fileDownloadUri, {0}" + fileDownloadUri);
        log.info("filename, {0}" + fileName);

        return ResponseEntity.ok().body(new FileRequest(
                fileName,
                multipartFile.getContentType(),
                fileDownloadUri, multipartFile.getSize()));
    }

    public ResponseEntity<Object> downloadFile(String fileName) throws Exception {
        Storage storage = storageOptions.getService();

        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        ReadChannel reader = blob.reader();
        InputStream inputStream = Channels.newInputStream(reader);

        log.info("File downloaded successfully.");

        byte[] content = inputStream.readAllBytes();

        final ByteArrayResource byteArrayResource = new ByteArrayResource(content);

        return ResponseEntity
                .ok()
                .contentLength(content.length)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(byteArrayResource);

    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    private InputStream createFirebaseCredential() throws Exception {

        var privateKey = firebaseConfig.getPrivateKey().replace("\\n", "\n");

        var firebaseCredential = new FirebaseCredential();
        firebaseCredential.setType(firebaseConfig.getType());
        firebaseCredential.setProject_id(projectId);
        firebaseCredential.setPrivate_key_id(firebaseConfig.getPrivateKeyID());
        firebaseCredential.setPrivate_key(privateKey);
        firebaseCredential.setClient_email(firebaseConfig.getClientEmail());
        firebaseCredential.setClient_id(firebaseConfig.getClientID());
        firebaseCredential.setAuth_uri(firebaseConfig.getAuthURI());
        firebaseCredential.setToken_uri(firebaseConfig.getTokenURI());
        firebaseCredential.setAuth_provider_x509_cert_url(firebaseConfig.getAuthProviderX509CertURL());
        firebaseCredential.setClient_x509_cert_url(firebaseConfig.getClientX509CertURL());

        var mapper = new ObjectMapper();
        var jsonString = mapper.writeValueAsString(firebaseCredential);

        //convert jsonString string to InputStream using Apache Commons
        return new ByteArrayInputStream(jsonString.getBytes());
    }

}
