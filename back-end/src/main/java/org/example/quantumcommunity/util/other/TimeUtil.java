package org.example.quantumcommunity.util.other;

import lombok.extern.slf4j.Slf4j;
import org.example.quantumcommunity.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.example.quantumcommunity.service.article.ArticleService;
import org.springframework.data.redis.core.RedisTemplate;
/**
 * @author xiaol
 */

@Slf4j
@EnableScheduling
public class TimeUtil {
    //long to string
    public String timeStampToString(long timeStamp){
        Instant createInstant = Instant.ofEpochMilli(timeStamp);
        LocalDateTime createLocalDateTime = LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createLocalDateTime.format(dateTimeFormatter);
    }

    //String to long
    public long stringToTimeStamp(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public LocalDateTime timeStampToLocalDateTime(long timeStamp){
        Instant createInstant = Instant.ofEpochMilli(timeStamp);
        return LocalDateTime.ofInstant(createInstant, ZoneId.systemDefault());
    }

    public Date simpleDateToSqlDate(java.util.Date date){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = format.parse(format.format(date));
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
