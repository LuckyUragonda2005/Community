package CommunityApplication.Repo;

import CommunityApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);


    @Query("SELECT u FROM User u WHERE " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
            "(:maritalStatus IS NULL OR u.maritalStatus = :maritalStatus) AND " +
            "u.dob BETWEEN :dobTo AND :dobFrom")
    List<User> filterUsers(@Param("gender") String gender,
                           @Param("maritalStatus") String maritalStatus,
                           @Param("dobTo") LocalDate dobTo,
                           @Param("dobFrom") LocalDate dobFrom);




}
