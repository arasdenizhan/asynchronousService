package com.denzhn.asynchronousservicecall.controller;

import com.denzhn.asynchronousservicecall.rules.RuleEngine;
import com.denzhn.asynchronousservicecall.rules.RuleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RuleController {
    private final RuleEngine ruleEngine;

    @GetMapping
    public ResponseEntity<List<String>> execute(@RequestParam("animeName") String animeName, @RequestParam("imageName") String imageName) {
        if (!StringUtils.hasLength(animeName) || !StringUtils.hasLength(imageName)){
            ResponseEntity.badRequest().body(Collections.singletonList("Anime or image name param is null"));
        }
        Map<RuleEnum, String> paramMap = new HashMap<>();
        paramMap.put(RuleEnum.RULE_ONE, animeName);
        paramMap.put(RuleEnum.RULE_TWO, imageName);
        try {
            ruleEngine.validateRules(paramMap);
            ruleEngine.initializeRules(paramMap);
            List<String> strings = ruleEngine.executeRules();
            return strings.isEmpty() ? ResponseEntity.internalServerError().body(Collections.singletonList("Results are empty")) : ResponseEntity.ok(strings);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().body(Collections.singletonList(e.getMessage()));
        }
    }
}
