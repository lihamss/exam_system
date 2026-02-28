package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("answer_record")
public class AnswerRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long questionId;
    private String userAnswer;
    private Integer score;
}
