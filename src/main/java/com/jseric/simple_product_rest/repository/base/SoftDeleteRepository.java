package com.jseric.simple_product_rest.repository.base;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoftDeleteRepository<T, ID> extends CrudRepository<T, ID> {

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted IS NULL")
    List<T> findAll();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.deleted IS NULL")
    Optional<T> findById(ID id);

    @Override
    @Query("SELECT count(e) FROM #{#entityName} e WHERE e.deleted IS NULL")
    long count();

    @Override
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE #{#entityName} e SET e.deleted = CURRENT_TIMESTAMP WHERE e.id = ?1")
    void deleteById(ID id);
}
