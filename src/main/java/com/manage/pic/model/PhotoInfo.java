package com.manage.pic.model;


import lombok.*;

@Setter
@Getter
@Builder
@ToString
public class PhotoInfo {


    private String fileName;

    private TimeTakenInfo takenInfo;

    private LocationInfo locationInfo;

    private String cameraMaker;

    private String cameraModel;
}
