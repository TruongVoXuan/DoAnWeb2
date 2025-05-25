package truong.vx.AiLaTrieuPhu.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import truong.vx.AiLaTrieuPhu.models.CauHoi;

public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {
	 List<CauHoi> findByCapdo(int capDo);
}


