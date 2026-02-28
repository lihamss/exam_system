package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** status: draft / published / ended */
@Data
@TableName("exam")
public class Exam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long paperId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    /** 是否乱序 */
    private Boolean shuffle;
    /** 是否允许查看答案 */
    private Boolean showAnswer;
    /** 是否允许补考 */
    private Boolean allowRetake;
}
