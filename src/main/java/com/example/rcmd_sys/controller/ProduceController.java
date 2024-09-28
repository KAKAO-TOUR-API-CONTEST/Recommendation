package com.example.rcmd_sys.controller;

import com.example.rcmd_sys.dto.UserEventVO;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@CrossOrigin(origins="*", allowedHeaders = "*") // 다른 서버에서 호출해야 하거든 ㅋㅋ..
@RequestMapping("/rcmd")
public class ProduceController {

    //그냥 결과값 알려주려고
    private final Logger logger = LoggerFactory.getLogger(ProduceController.class);


    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProduceController(KafkaTemplate<String, Object>kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    //클릭했을 때
    @GetMapping("/click")
    public void selectStoreByClick(@RequestHeader String userAgentName,
                                @RequestParam Long userId,
                                @RequestParam Long storeId
                                ) {

        SimpleDateFormat sdfDate  = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();

        UserEventVO userEventVo = new UserEventVO(sdfDate.format(now), userId, storeId, "click"); //생성자로 객체 생성하고
        //Json포맷의 String타입
        String jsonLog = gson.toJson(userEventVo);


        kafkaTemplate.send("click-topic", jsonLog).addCallback(
                new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error(ex.getMessage(),ex);
                    }
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        logger.info(result.toString());
                    }
                }
        );
    }

    // 취향 등록했을 때
    @GetMapping("/taste")
    public void selectStoreByTaste(@RequestHeader String userAgentName,
                            @RequestParam Long userId,
                            @RequestParam Long storeId
    ) {
        SimpleDateFormat sdfDate  = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();

        UserEventVO userEventVo = new UserEventVO(sdfDate.format(now), userId, storeId,"taste"); //생성자로 객체 생성하고
        //Json포맷의 String타입
        String jsonLog = gson.toJson(userEventVo);


        kafkaTemplate.send("taste-topic", jsonLog).addCallback(
                new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error(ex.getMessage(),ex);
                    }
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        logger.info(result.toString());
                    }
                }
        );

    }

    //북마크 등록했을 때ㅣ
    @GetMapping("/bookmark")
    public void selectStoreByBookmark(@RequestHeader String userAgentName,
                            @RequestParam Long userId,
                            @RequestParam Long storeId
    ) {
        SimpleDateFormat sdfDate  = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();
        UserEventVO userEventVo = new UserEventVO(sdfDate.format(now), userId, storeId,"bookmark"); //생성자로 객체 생성하고
        //Json포맷의 String타입
        String jsonLog = gson.toJson(userEventVo);

        kafkaTemplate.send("bookmark-topic", jsonLog).addCallback(
                new ListenableFutureCallback<SendResult<String, Object>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        logger.error(ex.getMessage(),ex);
                    }
                    @Override
                    public void onSuccess(SendResult<String, Object> result) {
                        logger.info(result.toString());
                    }
                }
        );
    }
    
}