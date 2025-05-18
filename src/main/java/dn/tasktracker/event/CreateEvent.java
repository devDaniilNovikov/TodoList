package dn.tasktracker.event;


import dn.tasktracker.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class CreateEvent<T> extends AbstractEvent<T> {

    public CreateEvent(String eventName, boolean deletedAt) {
        super(eventName, deletedAt);
    }

    public CreateEvent(String eventName, boolean deletedAt,String data) {
        super(eventName, deletedAt,data);
    }
    @Override
    public CreateEvent<T> makeEvent(T entity, String eventName) {
        return CreateEvent.<T>builder()
                .eventName(eventName)
                .deletedAt(true)
                .build();
    }

    public CreateEvent<T> makeEvent(T entity, String eventName,String data) {
        return CreateEvent.<T>builder()
                .eventName(eventName)
                .data(data)
                .deletedAt(true)
                .build();
    }

}
