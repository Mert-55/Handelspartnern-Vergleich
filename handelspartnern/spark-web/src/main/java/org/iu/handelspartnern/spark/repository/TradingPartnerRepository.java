package org.iu.handelspartnern.spark.repository;

import org.iu.handelspartnern.common.entity.TradingPartner;
import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import org.iu.handelspartnern.spark.config.DatabaseConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class TradingPartnerRepository {

    private final SessionFactory sessionFactory;

    public TradingPartnerRepository(DatabaseConfig databaseConfig) {
        this.sessionFactory = databaseConfig.getSessionFactory();
    }

    public List<TradingPartner> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TradingPartner ORDER BY dateModified DESC", TradingPartner.class).list();
        }
    }

    public Optional<TradingPartner> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            TradingPartner partner = session.get(TradingPartner.class, id);
            return Optional.ofNullable(partner);
        }
    }

    public List<TradingPartner> findWithFilters(PartnerType type, PartnerStatus status, String search) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM TradingPartner tp WHERE 1=1");

            if (type != null) {
                hql.append(" AND tp.type = :type");
            }
            if (status != null) {
                hql.append(" AND tp.status = :status");
            }
            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(tp.name) LIKE :search");
            }
            hql.append(" ORDER BY tp.dateModified DESC");

            Query<TradingPartner> query = session.createQuery(hql.toString(), TradingPartner.class);

            if (type != null) {
                query.setParameter("type", type);
            }
            if (status != null) {
                query.setParameter("status", status);
            }
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }

            return query.list();
        }
    }

    public TradingPartner save(TradingPartner partner) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(partner);
            session.getTransaction().commit();
            return partner;
        }
    }

    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            TradingPartner partner = session.get(TradingPartner.class, id);
            if (partner != null) {
                session.delete(partner);
            }
            session.getTransaction().commit();
        }
    }

    public long countByStatus(PartnerStatus status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(tp) FROM TradingPartner tp WHERE tp.status = :status",
                    Long.class);
            query.setParameter("status", status);
            return query.uniqueResult();
        }
    }

    public boolean existsById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            TradingPartner partner = session.get(TradingPartner.class, id);
            return partner != null;
        }
    }
}