package house.maplelandutilback.repository;

import house.maplelandutilback.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Optional<Message> findByContent(String content);
    Optional<List<Message>> findAllByContent(String content);

    @Transactional
    Long deleteByContent(String content);

}
