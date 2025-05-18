package dn.tasktracker.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class UpdateEvent<T> extends AbstractEvent<T> {


    public UpdateEvent(String eventName, boolean deletedAt,List<T> elements) {
        super(eventName, deletedAt,elements);
    }

    public UpdateEvent(String eventName, boolean deletedAt) {
        super(eventName, deletedAt);
    }

    @Override
    public UpdateEvent<T> makeEvent(T entity, String eventName) {
        return UpdateEvent.<T>builder()
                .eventName(eventName)
                .deletedAt(true)
                .build();
    }
    public UpdateEvent<T> makeEvent(List<T> entity, String eventName) {
        return UpdateEvent.<T>builder()
                .eventName(eventName)
                .elements(entity)
                .deletedAt(true)
                .build();
    }

}
