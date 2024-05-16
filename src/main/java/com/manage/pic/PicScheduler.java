package com.manage.pic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.pic.model.MediaInfo;
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
    private MediaFileInfoHelper mediaFileInfoHelper;


    @Scheduled(fixedDelayString = "${fixed.Delay}", initialDelayString = "${initial.delay}")
    public void processMediaFileJob() {
        LOGGER.info("======== Process Media(Image/Videos) Files job started ========");
        LOGGER.info("source dir path : " + srcDirPath);
        LOGGER.info("destination dir path : " + destDirPath);

        File srcDirFile = new File(srcDirPath);
        File destDirFile = new File(destDirPath);

        if (srcDirFile.isDirectory() && destDirFile.isDirectory()) {

            File[] imageFiles = srcDirFile.listFiles((dir, name) -> (name.endsWith(".jpg") || name.endsWith(".JPG")));
            if (Objects.nonNull(imageFiles)) {
                processImageFiles(imageFiles, destDirFile);
            }
            File[] videoFiles = srcDirFile.listFiles((dir, name) -> name.endsWith(".mp4"));
            if (Objects.nonNull(videoFiles)) {
                processVideoFiles(videoFiles, destDirFile);
            }

        } else {
            LOGGER.warn("Either is Source OR Destination directory is not exist !");
        }


        LOGGER.info("======== Process Media(Image/Videos) Files job Completed ========");
    }


    private void processImageFiles(File[] imageFiles, File destDirFile) {

        LOGGER.info("No. of Image files found : " + imageFiles.length);
        LOGGER.info("---------------------------------------");
        for (File imageFile : imageFiles) {

            MediaInfo mediaInfo = mediaFileInfoHelper.retrieveImageInfo(imageFile);
            if (Objects.nonNull(mediaInfo)) {

                try {
                    //create required folders
                    String locationOfImage = createImageLocationDir(destDirFile, mediaInfo.getTakenInfo());
                    LOGGER.info("target locationOfImage : " + locationOfImage);


                    File jsonFile = new File(locationOfImage + File.separator + mediaInfo.getFileName().replaceAll(".jpg", ".json"));
                    objectMapper.writeValue(jsonFile, mediaInfo);
                    LOGGER.info("json file created : " + jsonFile.getAbsolutePath());
                    //String photoInfoJsonString = objectMapper.writeValueAsString(photoInfo);


                    FileInputStream imageFileIs = new FileInputStream(imageFile);
                    String imageFileCheckSum = DigestUtils.md5DigestAsHex(imageFileIs);
                    imageFileIs.close();
                    LOGGER.info("imageFileCheckSum : " + imageFileCheckSum);

                    File destImageFile = new File(locationOfImage + File.separator + mediaInfo.getFileName());
                    boolean isImageFileAlreadyExist = destImageFile.exists();
                    LOGGER.info("is DestImageFile already Exists : " + isImageFileAlreadyExist);

                    LOGGER.info("Copying image file from = " + imageFile.getAbsolutePath());
                    LOGGER.info("To Secondary Location = " + destImageFile.getAbsolutePath());
                    int noOfBytesCopied = FileCopyUtils.copy(imageFile, destImageFile);
                    LOGGER.info("No. of Bytes copied = " + noOfBytesCopied);
                    FileInputStream destFileIs = new FileInputStream(destImageFile);
                    String destImageFileCheckSum = DigestUtils.md5DigestAsHex(destFileIs);
                    destFileIs.close();
                    LOGGER.info("Copied imageFileCheckSum : " + destImageFileCheckSum);

                    if (imageFileCheckSum.equals(destImageFileCheckSum)) {
                        LOGGER.info("Copied File verified successfully with MD5 checksum");
                        LOGGER.info("Deleting image File : " + imageFile.getAbsolutePath());
                        boolean isImageFileDeleted = imageFile.delete();
                        LOGGER.info("Source image File deleted ? : " + isImageFileDeleted);

                    }
                } catch (IOException e) {
                    LOGGER.error("IoException while processing image file", e);
                    continue;
                }


            }
            LOGGER.info("---------------------------------------");
        }

    }

    private void processVideoFiles(File[] videoFiles, File destDirFile) {

        LOGGER.info("No. of Video files found : " + videoFiles.length);
        LOGGER.info("---------------------------------------");
        for (File videoFile : videoFiles) {

            MediaInfo mediaInfo = mediaFileInfoHelper.retrieveVideoInfo(videoFile);
            if (Objects.nonNull(mediaInfo)) {

                try {
                    //create required folders
                    String locationOfVideo = createImageLocationDir(destDirFile, mediaInfo.getTakenInfo());
                    LOGGER.info("target locationOfVideo : " + locationOfVideo);


                    File jsonFile = new File(locationOfVideo + File.separator + mediaInfo.getFileName().replaceAll(".mp4", ".json"));
                    objectMapper.writeValue(jsonFile, mediaInfo);
                    LOGGER.info("json file created : " + jsonFile.getAbsolutePath());

                    FileInputStream imageFileIs = new FileInputStream(videoFile);
                    String videoFileCheckSum = DigestUtils.md5DigestAsHex(imageFileIs);
                    imageFileIs.close();
                    LOGGER.info("videoFileCheckSum : " + videoFileCheckSum);

                    File destVideoFile = new File(locationOfVideo + File.separator + mediaInfo.getFileName());
                    boolean isVideoFileAlreadyExist = destVideoFile.exists();
                    LOGGER.info("is destVideoFile already Exists : " + isVideoFileAlreadyExist);

                    LOGGER.info("Copying video file from = " + videoFile.getAbsolutePath());
                    LOGGER.info("To Secondary Location = " + destVideoFile.getAbsolutePath());
                    int noOfBytesCopied = FileCopyUtils.copy(videoFile, destVideoFile);
                    LOGGER.info("No. of Bytes copied = " + noOfBytesCopied);
                    FileInputStream destFileIs = new FileInputStream(destVideoFile);
                    String destVideoFileCheckSum = DigestUtils.md5DigestAsHex(destFileIs);
                    destFileIs.close();
                    LOGGER.info("Copied destVideoFileCheckSum : " + destVideoFileCheckSum);

                    if (videoFileCheckSum.equals(destVideoFileCheckSum)) {
                        LOGGER.info("Copied File verified successfully with MD5 checksum");
                        LOGGER.info("Deleting Video File : " + videoFile.getAbsolutePath());
                        boolean isVidFileDeleted = videoFile.delete();
                        LOGGER.info("Source Video File deleted ? : " + isVidFileDeleted);

                    }
                } catch (IOException e) {
                    LOGGER.error("IoException while processing vid file", e);
                    continue;
                }


            }
            LOGGER.info("---------------------------------------");
        }

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
