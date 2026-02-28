package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Paper;
import com.exam.entity.PaperQuestion;
import com.exam.entity.Question;
import com.exam.mapper.PaperMapper;
import com.exam.mapper.PaperQuestionMapper;
import com.exam.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaperService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    public Page<Paper> page(Integer pageNum, Integer pageSize, String keyword) {
        Page<Paper> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Paper> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) q.like(Paper::getName, keyword);
        q.orderByDesc(Paper::getId);
        return paperMapper.selectPage(page, q);
    }

    public Paper getById(Long id) {
        return paperMapper.selectById(id);
    }

    public List<Question> getQuestionsByPaperId(Long paperId) {
        List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, paperId));
        if (pqs.isEmpty()) return List.of();
        List<Long> qids = pqs.stream().map(PaperQuestion::getQuestionId).collect(Collectors.toList());
        List<Question> questions = questionMapper.selectBatchIds(qids);
        return pqs.stream()
                .map(pq -> {
                    Question q = questions.stream().filter(x -> x.getId().equals(pq.getQuestionId())).findFirst().orElse(null);
                    if (q != null) q.setScore(pq.getScore());
                    return q;
                })
                .filter(q -> q != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(Paper paper, List<Long> questionIds, List<Integer> scores) {
        if (paper.getId() == null) {
            paperMapper.insert(paper);
        } else {
            paperMapper.updateById(paper);
            paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, paper.getId()));
        }
        if (questionIds != null && !questionIds.isEmpty()) {
            int total = 0;
            for (int i = 0; i < questionIds.size(); i++) {
                Integer s = (scores != null && i < scores.size()) ? scores.get(i) : 5;
                total += s;
                PaperQuestion pq = new PaperQuestion();
                pq.setPaperId(paper.getId());
                pq.setQuestionId(questionIds.get(i));
                pq.setScore(s);
                paperQuestionMapper.insert(pq);
            }
            paper.setTotalScore(total);
            paperMapper.updateById(paper);
        }
    }

    public void deleteById(Long id) {
        paperQuestionMapper.delete(new LambdaQueryWrapper<PaperQuestion>().eq(PaperQuestion::getPaperId, id));
        paperMapper.deleteById(id);
    }
}
