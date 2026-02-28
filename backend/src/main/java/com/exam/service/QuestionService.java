package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Question;
import com.exam.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionMapper questionMapper;

    public Page<Question> page(Integer pageNum, Integer pageSize, String type, String subject, String difficulty) {
        Page<Question> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Question> q = new LambdaQueryWrapper<>();
        if (type != null && !type.isBlank()) q.eq(Question::getType, type);
        if (subject != null && !subject.isBlank()) q.eq(Question::getSubject, subject);
        if (difficulty != null && !difficulty.isBlank()) q.eq(Question::getDifficulty, difficulty);
        q.orderByDesc(Question::getId);
        return questionMapper.selectPage(page, q);
    }

    public List<Question> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return questionMapper.selectBatchIds(ids);
    }

    public Question getById(Long id) {
        return questionMapper.selectById(id);
    }

    public void save(Question question) {
        if (question.getId() == null) {
            questionMapper.insert(question);
        } else {
            questionMapper.updateById(question);
        }
    }

    public void deleteById(Long id) {
        questionMapper.deleteById(id);
    }
}
