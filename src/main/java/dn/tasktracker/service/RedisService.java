package dn.tasktracker.service;

import java.lang.ref.WeakReference;

public interface RedisService {

    WeakReference<String> writeInRedis(Object element, Long keyOfElement);

    void updateInBatch();
}
