package com.red.education.module.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.module.exam.vo.WrongQuestionVO;

/**
 * 错题本Service接口
 */
public interface WrongQuestionService {

    /**
     * 获取我的错题列表
     */
    Page<WrongQuestionVO> getMyWrongQuestions(Long userId, Integer isMastered, Integer current, Integer size);

    /**
     * 标记错题已掌握
     */
    void markAsMastered(Long userId, Long id);

    /**
     * 删除错题记录
     */
    void deleteWrongQuestion(Long userId, Long id);

    /**
     * 重做错题
     * 
     * @return 是否回答正确
     */
    boolean redoQuestion(Long userId, com.red.education.module.exam.dto.RedoQuestionDTO redoQuestionDTO);
}
