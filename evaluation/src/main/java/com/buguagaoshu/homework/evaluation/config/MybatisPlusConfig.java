package com.buguagaoshu.homework.evaluation.config;

import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-03 23:15
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 引入分页插件
     * */

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setMaxLimit(50L);
        paginationInnerInterceptor.setOverflow(true);
        OptimisticLockerInnerInterceptor lockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(lockerInnerInterceptor);
        return mybatisPlusInterceptor;
    }
}
