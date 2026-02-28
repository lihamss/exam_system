package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Exam;
import com.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/page")
    public Result<Page<Exam>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String keyword) {
        return Result.ok(examService.page(pageNum, pageSize, status, keyword));
    }

    /** 学生端：可参加的考试列表 */
    @GetMapping("/available")
    public Result<List<Exam>> available() {
        return Result.ok(examService.listAvailable());
    }

    @GetMapping("/{id}")
    public Result<Exam> getById(@PathVariable Long id) {
        return Result.ok(examService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Exam exam) {
        examService.save(exam);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.deleteById(id);
        return Result.ok();
    }
}
