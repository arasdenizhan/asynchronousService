package com.denzhn.asynchronousservicecall.rules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
public class RuleEngine {
    private static final String SUCCESS = "SUCCESS";
    private static final int THREAD_POOL_SIZE = 2;
    private final EnumMap<RuleEnum, Rule> ruleList;
    private final ExecutorService executorService;

    public RuleEngine() {
        this.ruleList = new EnumMap<>(RuleEnum.class);
        ruleList.put(RuleEnum.RULE_ONE, new TestRuleOne());
        ruleList.put(RuleEnum.RULE_TWO, new TestRuleTwo());
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void validateRules(Map<RuleEnum, String> paramMap) throws ExecutionException, InterruptedException {
        List<Future<String>> results = executorService.invokeAll(
                ruleList.entrySet().stream().map(entry -> (Callable<String>) () -> entry.getValue().validate(paramMap.get(entry.getKey()))).toList());
        for(Future<String> stringFuture : results){
            if (!SUCCESS.equals(stringFuture.get())){
                log.error(stringFuture.get());
            }
        }
    }

    public void initializeRules(Map<RuleEnum, String> paramMap) throws ExecutionException, InterruptedException {
        List<Future<String>> results = executorService.invokeAll(
                ruleList.entrySet().stream().map(entry -> (Callable<String>) () -> entry.getValue().initialize(paramMap.get(entry.getKey()))).toList());
        for(Future<String> stringFuture : results){
            if (!SUCCESS.equals(stringFuture.get())){
                log.error(stringFuture.get());
            }
        }
    }

    public List<String> executeRules() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        List<String> resultStrings = new ArrayList<>();
        List<Future<String>> results = executorService.invokeAll(
                ruleList.values().stream().map(rule -> (Callable<String>) rule::execute).toList());
        for(Future<String> stringFuture : results){
            resultStrings.add(stringFuture.get());
        }
        log.info("Rules Execution finished : " + (System.currentTimeMillis() - startTime));
        return resultStrings;
    }
}
