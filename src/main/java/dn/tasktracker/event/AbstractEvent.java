package dn.tasktracker.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AbstractEvent<T> {

    private String eventName;
    private boolean deletedAt;
    private List<T> elements = new ArrayList<>();
    private String data;

    public AbstractEvent(String eventName, boolean deletedAt,String data) {
        this.eventName = eventName;
        this.deletedAt = deletedAt;
        this.data = data;
    }

    public AbstractEvent(String eventName, boolean deletedAt) {
        this.eventName = eventName;
        this.deletedAt = deletedAt;
    }

    public AbstractEvent(String eventName, boolean deletedAt,List<T> elements) {
        this.eventName = eventName;
        this.deletedAt = deletedAt;
        this.elements = elements;
    }


    public AbstractEvent<T> makeEvent(T entity,String eventName){
        return AbstractEvent.<T>builder()
                .eventName(eventName)
                .deletedAt(true)
                .build();
    }

    public AbstractEvent<T> makeEvent(List<T> elements,String eventName){
        return AbstractEvent.<T>builder()
                .eventName(eventName)
                .elements(elements)
                .deletedAt(true)
                .build();
    }

    public AbstractEvent<T> makeEvent(T entity,String eventName,String data){
        return AbstractEvent.<T>builder()
                .eventName(eventName)
                .data(data)
                .deletedAt(true)
                .build();
    }


}
