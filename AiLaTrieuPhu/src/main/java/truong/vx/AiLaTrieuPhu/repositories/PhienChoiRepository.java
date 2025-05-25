package truong.vx.AiLaTrieuPhu.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import truong.vx.AiLaTrieuPhu.models.PhienChoi;

@Repository
public interface PhienChoiRepository extends JpaRepository<PhienChoi, Integer> {
	
}