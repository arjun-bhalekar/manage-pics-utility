package com.manage.pic;

import com.manage.pic.model.PhotoInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
public class PhotoInfoHelperTest {



    @InjectMocks
    private PhotoInfoHelper photoInfoHelper;


    @Test
    public void testRetrieveImageInfo_Success() {

        File imageFile = new File("src/test/resources/IMG_20230827_182808.jpg");

        PhotoInfo photoInfo =  photoInfoHelper.retrieveImageInfo(imageFile);
        Assertions.assertNotNull(photoInfo);
        System.out.println(photoInfo.toString());

    }



}
