package com.opensymphony.workflow;

import com.opensymphony.workflow.loader.ResultDescriptor;

public enum ActionType {
    NEXT, SPLIT, JOIN;
    
    public static ActionType resolveByResultDescriptor(ResultDescriptor result) {
        if (result.getSplit() != 0) {
            return SPLIT;
        }
        if (result.getJoin() != 0) {
            return JOIN;
        }
        return NEXT;
    }
}
