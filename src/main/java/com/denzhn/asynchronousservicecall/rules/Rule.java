package com.denzhn.asynchronousservicecall.rules;

public interface Rule {
    String initialize(String param);

    String validate(String param);

    String execute();
}
