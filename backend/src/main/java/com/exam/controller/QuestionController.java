package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Question;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/page")
    public Result<Page<Question>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(required = false) String subject,
                                       @RequestParam(required = false) String difficulty) {
        return Result.ok(questionService.page(pageNum, pageSize, type, subject, difficulty));
    }

    @GetMapping("/list")
    public Result<List<Question>> listByIds(@RequestParam List<Long> ids) {
        return Result.ok(questionService.listByIds(ids));
    }

    @GetMapping("/{id}")
    public Result<Question> getById(@PathVariable Long id) {
        return Result.ok(questionService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Question question) {
        questionService.save(question);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        questionService.deleteById(id);
        return Result.ok();
    }
}
