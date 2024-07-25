package cmpt276.group.demo.models.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByIsAvailable(boolean available);
    Room findByName(String name);
}
