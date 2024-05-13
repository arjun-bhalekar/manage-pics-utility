package com.manage.pic.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class LocationInfo {

    private String latitude;
    private String longitude;
    private String altitude;
    private String description;

}
