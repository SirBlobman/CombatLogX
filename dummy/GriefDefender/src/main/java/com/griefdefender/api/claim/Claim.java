package com.griefdefender.api.claim;

import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.griefdefender.api.Subject;
import com.griefdefender.api.permission.Context;
import com.griefdefender.api.permission.option.Option;

@SuppressWarnings("UnstableApiUsage")
public interface Claim {
    default <T> T getActiveOptionValue(TypeToken<T> type, Option<T> option, Subject subject, Set<Context> contexts) {
        throw new UnsupportedOperationException("Dummy Method");
    }
}