package com.buguagaoshu.homework.evaluation.model;

import com.buguagaoshu.homework.evaluation.entity.ArticleEntity;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-30 14:06
 */
@Data
public class ReplyComment {
    private CommentModel commentModel;

    private ArticleEntity articleEntity;
}
