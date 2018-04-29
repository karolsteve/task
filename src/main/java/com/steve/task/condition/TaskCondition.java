package com.steve.task.condition;

import java.io.Serializable;

/**
 * @author Steve Tchatchouang
 */

public interface TaskCondition extends Serializable {
    boolean isOk();
}
