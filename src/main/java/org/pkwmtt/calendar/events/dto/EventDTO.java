package org.pkwmtt.calendar.events.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.pkwmtt.calendar.adnotations.CorrectFutureDate;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class EventDTO {
    int id;
    String title;
    String description;

    @CorrectFutureDate
    Date startDate;

    @CorrectFutureDate
    Date endDate;
    
    String type;
    List<String> superiorGroups;
}
