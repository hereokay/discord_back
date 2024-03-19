package house.maplelandutilback.repository;


import house.maplelandutilback.domain.Block;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends MongoRepository<Block, String>{
    Optional<Block> findByContent(String content);
}
