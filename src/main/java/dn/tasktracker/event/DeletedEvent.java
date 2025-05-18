package dn.tasktracker.event;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class DeletedEvent<T> extends AbstractEvent<T> {


    public DeletedEvent(String eventName, boolean deletedAt,List<T> elements,String data) {
        super(eventName, deletedAt,elements,data);
    }

    public DeletedEvent(String eventName, boolean deletedAt,List<T> elements) {
        super(eventName, deletedAt,elements);
    }

    public DeletedEvent(String eventName,boolean deletedAt){
        super(eventName,deletedAt);
    }

    @Override
    public DeletedEvent<T> makeEvent(T entity, String eventName) {
        return DeletedEvent.<T>builder()
                .eventName(eventName)
                .deletedAt(true)
                .build();
    }

    @Override
    public DeletedEvent<T> makeEvent(List<T> entity, String eventName) {
        return DeletedEvent.<T>builder()
                .eventName(eventName)
                .elements(entity)
                .deletedAt(true)
                .build();
    }
}
