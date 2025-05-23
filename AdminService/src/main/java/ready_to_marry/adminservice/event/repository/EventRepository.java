package ready_to_marry.adminservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.event.entity.Event;
import ready_to_marry.adminservice.mainbanner.entity.MainBanner;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
}
