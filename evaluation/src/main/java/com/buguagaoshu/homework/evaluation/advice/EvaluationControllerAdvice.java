package com.buguagaoshu.homework.evaluation.advice;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.common.enums.ReturnCodeEnum;
import com.buguagaoshu.homework.evaluation.controller.HomeworkController;
import com.buguagaoshu.homework.evaluation.exception.UserDataFormatException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static com.buguagaoshu.homework.common.enums.ReturnCodeEnum.SYSTEM_ERROR;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-15 15:16
 * basePackages = {"com.buguagaoshu.homework.evaluation.controller"}
 * 控制层异常处理
 */
@RestControllerAdvice(basePackages = {"com.buguagaoshu.homework.evaluation.controller"})
public class EvaluationControllerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseDetails handleValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        HashMap<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach((item)->{
            map.put(item.getField(), item.getDefaultMessage());
        });
        return ResponseDetails.ok(ReturnCodeEnum.DATA_VALID_EXCEPTION).put("data", map);
    }

    @ExceptionHandler(value = UserDataFormatException.class)
    public ResponseDetails userDataFormatHandException(UserDataFormatException userDataFormatException) {
        return ResponseDetails
                .ok(ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), userDataFormatException.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseDetails handException(Throwable throwable) {
        return ResponseDetails.ok(SYSTEM_ERROR.getCode(), throwable.getMessage());
    }

}
