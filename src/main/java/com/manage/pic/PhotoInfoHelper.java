package com.manage.pic;

import com.manage.pic.model.LocationInfo;
import com.manage.pic.model.PhotoInfo;
import com.manage.pic.model.TimeTakenInfo;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@Component
public class PhotoInfoHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoInfoHelper.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    public PhotoInfo retrieveImageInfo(File imageFile) {

        if(imageFile.exists()){
            LOGGER.info("retrieving info for image file :"+imageFile.getName());
            try {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(imageFile);


                TiffField tiffField = jpegMetadata.findExifValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                String takenDateTime = tiffField.getValueDescription().replaceAll("\"","").replaceAll("'","");
                LOGGER.info("DateTime taken : " + takenDateTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(takenDateTime));

                TimeTakenInfo timeTakenInfo = TimeTakenInfo.builder()
                        .year(calendar.get(Calendar.YEAR))
                        .month(calendar.get(Calendar.MONTH) + 1)
                        .day(calendar.get(Calendar.DAY_OF_MONTH))
                        .dateTime(dateFormat.format(calendar.getTime()))
                        .build();

                TiffImageMetadata.GpsInfo gpsInfo =  jpegMetadata.getExif().getGpsInfo();
                LocationInfo locationInfo = null;
                if (null != gpsInfo) {
                    final String gpsDescription = gpsInfo.toString();
                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

                    locationInfo = LocationInfo.builder()
                            .longitude(String.valueOf(longitude))
                            .latitude(String.valueOf(latitude))
                            .description(gpsDescription).build();

                }

               ImageMetadata.ImageMetadataItem makeInfo =  jpegMetadata.getItems().stream()
                        .filter(imageMetadataItem ->  imageMetadataItem.toString().startsWith("Make"))
                        .findFirst().orElse(null);

                ImageMetadata.ImageMetadataItem modelInfo =  jpegMetadata.getItems().stream()
                        .filter(imageMetadataItem ->  imageMetadataItem.toString().startsWith("Model"))
                        .findFirst().orElse(null);

                String cameraMaker =  makeInfo!=null? makeInfo.toString(): "NA";
                String cameraModel = modelInfo!=null? modelInfo.toString(): "NA";

                LOGGER.info("fetched info for image file :"+imageFile.getName());
                return PhotoInfo.builder()
                        .fileName("IMG_"+calendar.getTime().getTime()+".jpg")
                        .takenInfo(timeTakenInfo)
                        .locationInfo(locationInfo)
                        .cameraMaker(cameraMaker)
                        .cameraModel(cameraModel)
                        .build();

            } catch (ParseException parseException) {
                LOGGER.error("ParseException while getting Metadata of image :"+imageFile.getName(), parseException);
            } catch (IOException ioException) {
                LOGGER.error("IOException while getting Metadata of image :"+imageFile.getName(), ioException);
            }

        }

        return null;
    }


}
