package ready_to_marry.adminservice.mainbanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.mainbanner.entity.MainBanner;

import java.util.List;

public interface MainBannerRepository extends JpaRepository<MainBanner, Long> {
    List<MainBanner> findAllByOrderByPriorityAsc();
}