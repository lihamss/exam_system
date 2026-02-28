package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Exam;
import com.exam.entity.Paper;
import com.exam.mapper.ExamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamMapper examMapper;

    public Page<Exam> page(Integer pageNum, Integer pageSize, String status, String keyword) {
        Page<Exam> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Exam> q = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) q.eq(Exam::getStatus, status);
        if (keyword != null && !keyword.isBlank()) q.like(Exam::getName, keyword);
        q.orderByDesc(Exam::getId);
        return examMapper.selectPage(page, q);
    }

    /** 学生端：可参加的考试（已发布且在时间范围内） */
    public List<Exam> listAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return examMapper.selectList(
                new LambdaQueryWrapper<Exam>()
                        .eq(Exam::getStatus, "published")
                        .le(Exam::getStartTime, now)
                        .ge(Exam::getEndTime, now)
                        .orderByDesc(Exam::getStartTime));
    }

    public Exam getById(Long id) {
        return examMapper.selectById(id);
    }

    public void save(Exam exam) {
        if (exam.getId() == null) {
            if (exam.getShuffle() == null) exam.setShuffle(true);
            if (exam.getShowAnswer() == null) exam.setShowAnswer(true);
            if (exam.getAllowRetake() == null) exam.setAllowRetake(false);
            examMapper.insert(exam);
        } else {
            examMapper.updateById(exam);
        }
    }

    public void deleteById(Long id) {
        examMapper.deleteById(id);
    }
}
