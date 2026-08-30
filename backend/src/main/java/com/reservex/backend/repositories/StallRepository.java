package com.reservex.backend.repositories;

import com.reservex.backend.entity.Stall;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.List;

public interface StallRepository extends JpaRepository<Stall, Integer> {

    List<Stall> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer excludeId);

    boolean existsByGridRowAndGridCol(int gridRow, int gridCol);

    boolean existsByGridRowAndGridColAndIdNot(int gridRow, int gridCol, Integer excludeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stall s WHERE s.id = :id")
    Optional<Stall> findByIdWithPessimisticLock(@Param("id") Integer id);
}
