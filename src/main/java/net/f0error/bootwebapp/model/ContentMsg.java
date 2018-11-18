package net.f0error.bootwebapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Slf4j
@Getter
@Setter
@ToString
public class ContentMsg implements Serializable{

    private Long id;
    private String content;
    private Date date;



}
