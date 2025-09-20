package org.iu.handelspartnern.spring.repository;

import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TradingPartnerRepository extends JpaRepository<TradingPartner, Long> {

    List<TradingPartner> findByStatusOrderByDateModifiedDesc(PartnerStatus status);

    List<TradingPartner> findByTypeAndStatusOrderByNameAsc(PartnerType type, PartnerStatus status);

    List<TradingPartner> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    @Query("SELECT tp FROM TradingPartner tp WHERE " + "(:type IS NULL OR tp.type = :type) AND "
            + "(:status IS NULL OR tp.status = :status) AND "
            + "(:search IS NULL OR LOWER(tp.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "ORDER BY tp.dateModified DESC")
    List<TradingPartner> findWithFilters(@Param("type") PartnerType type, @Param("status") PartnerStatus status,
            @Param("search") String search);

    long countByStatus(PartnerStatus status);
}
