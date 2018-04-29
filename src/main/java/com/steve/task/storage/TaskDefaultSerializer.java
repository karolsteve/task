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
