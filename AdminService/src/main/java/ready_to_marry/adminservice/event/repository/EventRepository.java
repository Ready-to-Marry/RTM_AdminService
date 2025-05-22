package ready_to_marry.adminservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.event.entity.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByMainBannerTrueOrderByPriorityAsc();
}
