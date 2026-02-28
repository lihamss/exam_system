package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer totalScore;
    private Integer duration;
    private LocalDateTime createTime;
}
