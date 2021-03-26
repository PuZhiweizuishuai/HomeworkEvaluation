package com.buguagaoshu.homework.search.controller;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-25 18:35
 */
@RestController
public class SearchController {

    @GetMapping("/search/all")
    public ResponseDetails search() {
        return ResponseDetails.ok();
    }
}
