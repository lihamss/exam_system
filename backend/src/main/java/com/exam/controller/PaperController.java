package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Paper;
import com.exam.entity.Question;
import com.exam.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    @GetMapping("/page")
    public Result<Page<Paper>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String keyword) {
        return Result.ok(paperService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<Paper> getById(@PathVariable Long id) {
        return Result.ok(paperService.getById(id));
    }

    @GetMapping("/{id}/questions")
    public Result<List<Question>> getQuestions(@PathVariable Long id) {
        return Result.ok(paperService.getQuestionsByPaperId(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Map<String, Object> body) {
        Paper paper = new Paper();
        if (body.get("id") != null) paper.setId(Long.valueOf(body.get("id").toString()));
        paper.setName((String) body.get("name"));
        paper.setDuration(body.get("duration") != null ? Integer.valueOf(body.get("duration").toString()) : 60);
        @SuppressWarnings("unchecked")
        List<Number> qids = (List<Number>) body.get("questionIds");
        @SuppressWarnings("unchecked")
        List<Number> scores = (List<Number>) body.get("scores");
        List<Long> questionIds = qids != null ? qids.stream().map(Number::longValue).toList() : List.of();
        List<Integer> scoreList = scores != null ? scores.stream().map(Number::intValue).toList() : List.of();
        paperService.save(paper, questionIds, scoreList);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.deleteById(id);
        return Result.ok();
    }
}
