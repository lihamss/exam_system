package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.AnswerRecord;
import com.exam.entity.Exam;
import com.exam.entity.ExamRecord;
import com.exam.entity.PaperQuestion;
import com.exam.entity.Question;
import com.exam.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamRecordService {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final ExamMapper examMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final PaperMapper paperMapper;

    /** 开始考试：创建考试记录 */
    @Transactional
    public ExamRecord startExam(Long userId, Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) throw new RuntimeException("考试不存在");
        if (!"published".equals(exam.getStatus())) throw new RuntimeException("考试未发布");
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) throw new RuntimeException("考试未开始");
        if (now.isAfter(exam.getEndTime())) throw new RuntimeException("考试已结束");

        ExamRecord exist = examRecordMapper.selectOne(
                new LambdaQueryWrapper<ExamRecord>()
                        .eq(ExamRecord::getUserId, userId)
                        .eq(ExamRecord::getExamId, examId)
                        .last("limit 1"));
        if (exist != null) {
            if (exist.getSubmitTime() != null) {
                if (Boolean.FALSE.equals(exam.getAllowRetake()))
                    throw new RuntimeException("该考试不允许补考");
            } else {
                return exist; // 未交卷则继续
            }
        }

        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setExamId(examId);
        record.setStartTime(now);
        record.setGradeStatus("submitted");
        examRecordMapper.insert(record);

        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, exam.getPaperId()));
        List<Long> qids = pqs.stream().map(PaperQuestion::getQuestionId).collect(Collectors.toList());
        if (Boolean.TRUE.equals(exam.getShuffle())) Collections.shuffle(qids);
        for (Long qid : qids) {
            AnswerRecord ar = new AnswerRecord();
            ar.setExamRecordId(record.getId());
            ar.setQuestionId(qid);
            answerRecordMapper.insert(ar);
        }
        return record;
    }

    /** 提交答案并自动评分 */
    @Transactional
    public void submitAnswer(Long recordId, Long userId, Map<Long, String> answers) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) throw new RuntimeException("记录不存在");
        if (record.getSubmitTime() != null) throw new RuntimeException("已交卷");

        Exam exam = examMapper.selectById(record.getExamId());
        Long paperId = exam != null ? exam.getPaperId() : null;
        List<AnswerRecord> list = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, recordId));
        int totalScore = 0;
        for (AnswerRecord ar : list) {
            String userAns = answers != null ? answers.get(ar.getQuestionId()) : null;
            if (userAns != null) ar.setUserAnswer(userAns.trim());
            Question q = questionMapper.selectById(ar.getQuestionId());
            int questionScore = 0;
            if (paperId != null) {
                PaperQuestion pq = paperQuestionMapper.selectOne(
                        new LambdaQueryWrapper<PaperQuestion>()
                                .eq(PaperQuestion::getPaperId, paperId)
                                .eq(PaperQuestion::getQuestionId, ar.getQuestionId()));
                questionScore = pq != null && pq.getScore() != null ? pq.getScore() : (q != null && q.getScore() != null ? q.getScore() : 0);
            } else if (q != null && q.getScore() != null) questionScore = q.getScore();
            if (q != null && isObjective(q.getType())) {
                int s = scoreObjective(q.getCorrectAnswer(), questionScore, ar.getUserAnswer());
                ar.setScore(s);
                totalScore += s;
            }
            answerRecordMapper.updateById(ar);
        }
        record.setTotalScore(totalScore);
        record.setSubmitTime(LocalDateTime.now());
        record.setGradeStatus("graded");
        examRecordMapper.updateById(record);
    }

    private boolean isObjective(String type) {
        return "single".equals(type) || "multiple".equals(type) || "judge".equals(type);
    }

    private int scoreObjective(String correctAnswer, int questionScore, String userAnswer) {
        if (userAnswer == null || userAnswer.isBlank()) return 0;
        String correct = correctAnswer != null ? correctAnswer.trim() : "";
        String u = userAnswer.trim();
        if (correct.equalsIgnoreCase(u)) return questionScore;
        return 0;
    }

    /** 教师批改主观题 */
    @Transactional
    public void gradeSubjective(Long recordId, Map<Long, Integer> scores) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) return;
        List<AnswerRecord> list = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, recordId));
        int add = 0;
        for (AnswerRecord ar : list) {
            if (scores != null && scores.containsKey(ar.getQuestionId())) {
                int s = scores.get(ar.getQuestionId());
                ar.setScore(s);
                add += s;
                answerRecordMapper.updateById(ar);
            }
        }
        record.setTotalScore((record.getTotalScore() != null ? record.getTotalScore() : 0) + add);
        record.setGradeStatus("graded");
        examRecordMapper.updateById(record);
    }

    public ExamRecord getById(Long id) {
        return examRecordMapper.selectById(id);
    }

    public List<ExamRecord> listByUserId(Long userId) {
        return examRecordMapper.selectList(
                new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getUserId, userId).orderByDesc(ExamRecord::getId));
    }

    public Page<ExamRecord> pageByExamId(Long examId, Integer pageNum, Integer pageSize) {
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        return examRecordMapper.selectPage(page,
                new LambdaQueryWrapper<ExamRecord>().eq(ExamRecord::getExamId, examId).orderByDesc(ExamRecord::getId));
    }

    public List<AnswerRecord> listAnswerByRecordId(Long recordId) {
        return answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getExamRecordId, recordId));
    }
}
