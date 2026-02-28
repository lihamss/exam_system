package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 考试记录：某用户参加某次考试的一次记录 */
@Data
@TableName("exam_record")
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long examId;
    private Integer totalScore;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    /** 批改状态: submitted / graded */
    private String gradeStatus;
}
