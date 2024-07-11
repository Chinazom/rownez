package com.scenic.rownezcoreservice.service.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class GcpFileStorage {
    @Autowired
    Storage storage;

    List <String> acceptableContentType = List.of("image/jpeg", "image/jpeg");
    Logger logger = LoggerFactory.getLogger(GcpFileStorage.class);
    private String bucketId = "rownez-hotel-storage";
    public void uploadFile(MultipartFile multipartFile, String category) throws FileStorageServiceException {
        logger.info("uploading file to GCS bucket");
        validateFile ( multipartFile);
        StringBuilder roomImageDirectory = new StringBuilder("room-images")
                .append("/")
                .append(category)
                .append("/")
                .append(multipartFile.getOriginalFilename());
        try {
             storage.create(
                    BlobInfo.newBuilder(bucketId, String.valueOf(roomImageDirectory)).build(),
                    multipartFile.getBytes());
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void delFile (String storagePath){
        logger.info("Deleting all images for room");
        getFilenameList(storagePath).forEach(name -> {
            storage.delete(bucketId, name);
        });

    }
    private Map<String, byte[]> getAllImagesRoom(String storagePath) {
        Set<String> listOfFiles = getFilenameList(storagePath);
        Map<String, byte[]> roomImages = new HashMap<>();

        int index;
        for (String filename : listOfFiles) {
            StringBuilder buf = new StringBuilder(filename);
            index = filename.indexOf("/") +1;
            buf.replace(0, index, "");
            roomImages.put(String.valueOf(buf), storage.readAllBytes(bucketId, filename));
        }
        return roomImages;
    }
    private Set<String> getFilenameList (String storagePath){
        logger.info("Fetching list of file name for room ");
        Set<String> listOfFiles = new HashSet<>();
        Page<Blob> blobs =
                storage.list(
                        bucketId,
                        Storage.BlobListOption.prefix(storagePath));

        for (Blob blob : blobs.iterateAll()) {
            listOfFiles.add(blob.getName());
        }
        return listOfFiles;
    }
    private void validateFile (MultipartFile multipartFile) throws FileStorageServiceException {
        logger.info("Validating file...");
        if (multipartFile == null){
            throw new FileStorageServiceException("No file found");
        }
        if (!acceptableContentType.contains(multipartFile.getContentType())){
            throw new FileStorageServiceException("Supported file type is only png and jpeg/ file extension tyoe not supported");
        }
        logger.info("Validation complete.");
    }
}
