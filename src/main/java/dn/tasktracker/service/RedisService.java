package dn.tasktracker.service;

import java.lang.ref.SoftReference;
import java.util.List;

public interface RedisService {

    SoftReference<String> writeInRedis(Object element, Long keyOfElement);

    void updateInBatch();

    void deleteAllInBatchByKeys (List<Byte> byteList);
}
