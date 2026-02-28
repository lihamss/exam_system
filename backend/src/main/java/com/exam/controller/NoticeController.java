package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.common.Result;
import com.exam.entity.Notice;
import com.exam.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/latest")
    public Result<List<Notice>> latest(@RequestParam(defaultValue = "5") int limit) {
        return Result.ok(noticeService.listLatest(limit));
    }

    @GetMapping("/page")
    public Result<Page<Notice>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.ok(noticeService.page(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        return Result.ok(noticeService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteById(id);
        return Result.ok();
    }
}
