package com.manage.pic.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TimeTakenInfo {


    private int year;
    private int month;
    private int day;
    private String dateTime;


}
