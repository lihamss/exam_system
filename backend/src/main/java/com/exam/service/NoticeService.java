package com.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Notice;
import com.exam.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public List<Notice> listLatest(int limit) {
        return noticeMapper.selectList(
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime).last("limit " + limit));
    }

    public Page<Notice> page(Integer pageNum, Integer pageSize) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        return noticeMapper.selectPage(page,
                new LambdaQueryWrapper<Notice>().orderByDesc(Notice::getCreateTime));
    }

    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }

    public void save(Notice notice) {
        if (notice.getId() == null) {
            noticeMapper.insert(notice);
        } else {
            noticeMapper.updateById(notice);
        }
    }

    public void deleteById(Long id) {
        noticeMapper.deleteById(id);
    }
}
