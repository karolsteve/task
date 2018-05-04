/*
 * Copyright 2018 Steve Tchatchouang
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.steve.task.storage;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskDefaultSerializer<T> implements TaskSerializer<T> {

    @Override
    public String serialize(T task) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(task);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    @Override
    public T deserialize(String serializedTask) throws IOException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(serializedTask, Base64.NO_WRAP));
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
