package com.manage.pic;

import com.manage.pic.model.MediaInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
public class MediaFileInfoHelperTest {



    @InjectMocks
    private MediaFileInfoHelper mediaFileInfoHelper;


    @Test
    public void testRetrieveImageInfo_Success() {

        File imageFile = new File("src/test/resources/IMG_20230827_182808.jpg");

        MediaInfo mediaInfo =  mediaFileInfoHelper.retrieveImageInfo(imageFile);
        Assertions.assertNotNull(mediaInfo);
        System.out.println(mediaInfo.toString());

    }



}
