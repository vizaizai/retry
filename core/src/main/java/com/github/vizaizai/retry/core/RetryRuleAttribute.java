package com.github.vizaizai.retry.core;

import java.io.Serializable;

/**
 * @author liaochongwei
 * @date 2020/12/10 10:00
 */
public class RetryRuleAttribute implements Serializable {

    /**
     * 异常名称
     */
    private final String exceptionName;


    /**
     * @param clazz throwable class; must be {@link Throwable} or a subclass of {@code Throwable}
     */
    public RetryRuleAttribute(Class<?> clazz) {
        if (!Throwable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                    "Cannot construct retry rule from [" + clazz.getName() + "]: it's not a Throwable");
        }
        this.exceptionName = clazz.getName();
    }

    /**
     * Return the pattern for the exception name.
     */
    public String getExceptionName() {
        return this.exceptionName;
    }

    /**
     * Return the depth of the superclass matching.
     * <p>{@code 0} means {@code ex} matches exactly. Returns
     * {@code -1} if there is no match. Otherwise, returns depth with the
     * lowest depth winning.
     */
    public int getDepth(Throwable ex) {
        return getDepth(ex.getClass(), 0);
    }


    private int getDepth(Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(this.exceptionName)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass == Throwable.class) {
            return -1;
        }
        return getDepth(exceptionClass.getSuperclass(), depth + 1);
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RetryRuleAttribute)) {
            return false;
        }
        RetryRuleAttribute rhs = (RetryRuleAttribute) other;
        return this.exceptionName.equals(rhs.exceptionName);
    }

    @Override
    public int hashCode() {
        return this.exceptionName.hashCode();
    }

    @Override
    public String toString() {
        return "RetryRuleAttribute with pattern [" + this.exceptionName + "]";
    }
}
