package com.steve.task.storage;

import java.io.IOException;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public interface TaskSerializer<T> {
    String serialize(T t) throws IOException;

    T deserialize(String serialized) throws IOException;
}
