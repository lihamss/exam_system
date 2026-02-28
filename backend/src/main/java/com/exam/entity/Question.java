package com.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** single / multiple / judge / fill / short */
@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    /** 正确答案，多选用逗号分隔如 A,B */
    private String correctAnswer;
    private Integer score;
    /** easy / medium / hard */
    private String difficulty;
    private String subject;
}
