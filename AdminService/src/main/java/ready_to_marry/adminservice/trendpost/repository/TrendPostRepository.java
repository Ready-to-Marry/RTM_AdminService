package ready_to_marry.adminservice.trendpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;

import java.util.List;

public interface TrendPostRepository extends JpaRepository<TrendPost, Long> {

}
