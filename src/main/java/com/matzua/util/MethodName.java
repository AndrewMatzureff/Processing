package com.matzua.util;

import static java.util.function.Function.identity;

public interface MethodName {
    static String at(int offset) {
        return StackWalker.getInstance()
                .walk(identity())
                .skip(offset)
                .findFirst()
                .map(StackWalker.StackFrame::getMethodName)
                .orElseThrow();
    }

    static String current() {
        return at(2);
    }

    static String caller() {
        return at(3);
    }
}
