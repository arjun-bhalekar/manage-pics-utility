package com.manage.pic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.pic.model.PhotoInfo;
import com.manage.pic.model.TimeTakenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class PicScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PicScheduler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${src.dir.path}")
    private String srcDirPath;

    @Value("${dest.dir.path}")
    private String destDirPath;

    @Autowired
    private PhotoInfoHelper photoInfoHelper;


    @Scheduled(fixedDelayString = "${fixed.Delay}", initialDelayString = "${initial.delay}")
    public void processPics() {
        LOGGER.info("======== process pic job started ========");
        LOGGER.info("source dir path : " + srcDirPath);
        LOGGER.info("destination dir path : " + destDirPath);

        File srcDirFile = new File(srcDirPath);
        File destDirFile = new File(destDirPath);

        if (srcDirFile.isDirectory() && destDirFile.isDirectory()) {

            File[] imageFiles = srcDirFile.listFiles((dir, name) -> (name.endsWith(".jpg") || name.endsWith(".JPG")));
            if (Objects.nonNull(imageFiles)) {
                LOGGER.info("No of Image files found : " + imageFiles.length);
                LOGGER.info("---------------------------------------");
                for (File imageFile : imageFiles) {

                    PhotoInfo photoInfo = photoInfoHelper.retrieveImageInfo(imageFile);
                    if (Objects.nonNull(photoInfo)) {

                        try {
                            //create required folders
                            String locationOfImage = createImageLocationDir(destDirFile, photoInfo.getTakenInfo());
                            LOGGER.info("locationOfImage : " + locationOfImage);


                            File jsonFile = new File(locationOfImage + File.separator + photoInfo.getFileName().replaceAll(".jpg", ".json"));
                            objectMapper.writeValue(jsonFile, photoInfo);
                            LOGGER.info("json file created : " + jsonFile.getAbsolutePath());
                            //String photoInfoJsonString = objectMapper.writeValueAsString(photoInfo);


                            FileInputStream imageFileIs = new FileInputStream(imageFile);
                            String imageFileCheckSum = DigestUtils.md5DigestAsHex(imageFileIs);
                            imageFileIs.close();
                            LOGGER.info("imageFileCheckSum : " + imageFileCheckSum);

                            File destImageFile = new File(locationOfImage + File.separator + photoInfo.getFileName());
                            boolean isImageFileAlreadyExist = destImageFile.exists();
                            LOGGER.info("is DestImageFile already Exists : "+ isImageFileAlreadyExist);

                            LOGGER.info("Copying image file from = " + imageFile.getAbsolutePath());
                            LOGGER.info("To Secondary Location = " + destImageFile.getAbsolutePath());
                            int noOfBytesCopied = FileCopyUtils.copy(imageFile, destImageFile);
                            LOGGER.info("No. of Bytes copied = " + noOfBytesCopied);
                            FileInputStream destAppPdfFileIs = new FileInputStream(destImageFile);
                            String destImageFileCheckSum = DigestUtils.md5DigestAsHex(destAppPdfFileIs);
                            destAppPdfFileIs.close();
                            LOGGER.info("Copied imageFileCheckSum : " + destImageFileCheckSum);

                            if (imageFileCheckSum.equals(destImageFileCheckSum)) {
                                LOGGER.info("Copied File verified successfully with MD5 checksum");
                                LOGGER.info("Deleting image File : "+imageFile.getAbsolutePath());
                                boolean isImageFileDeleted = imageFile.delete();
                                LOGGER.info("Source image File deleted ? : "+isImageFileDeleted);

                            }
                        } catch (IOException e) {
                            LOGGER.error("IoException while writing to json file", e);
                            continue;
                        }


                    }
                    LOGGER.info("---------------------------------------");
                }
            }
        } else {
            LOGGER.warn("Either is Source OR Destination directory is not exist !");
        }


        LOGGER.info("========== process pic job completed =============");
    }


    private String createImageLocationDir(File destRootDirFile, TimeTakenInfo timeTakenInfo) {

        File locationOfImage = new File(destRootDirFile.getAbsolutePath()
                + File.separator
                + timeTakenInfo.getYear()
                + File.separator
                + timeTakenInfo.getMonth());

        boolean createdDirs = locationOfImage.mkdirs();
        LOGGER.info("createdDirs : " + createdDirs);
        return locationOfImage.getAbsolutePath();
    }

}
