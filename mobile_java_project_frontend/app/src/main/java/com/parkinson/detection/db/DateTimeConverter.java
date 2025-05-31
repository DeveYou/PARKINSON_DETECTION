package com.parkinson.detection.db;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Type converter for Room to handle LocalDateTime objects
 */
public class DateTimeConverter {
    
    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return timestamp == null ? null : 
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    
    @TypeConverter
    public static Long fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : 
                dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
} 