package cmpt276.group.demo.models.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  EventRepository extends JpaRepository<Event, Integer> {
    Event findByEventCode(String eventCode);
}
