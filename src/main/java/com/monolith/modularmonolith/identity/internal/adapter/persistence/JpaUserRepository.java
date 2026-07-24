package com.monolith.modularmonolith.identity.internal.adapter.persistence;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<User> findByEmailWithProfiles(String email) {
        String jpql = "SELECT u FROM User u " +
                "LEFT JOIN FETCH u.studentProfile " +
                "LEFT JOIN FETCH u.teacherProfile " +
                "LEFT JOIN FETCH u.adminProfile " +
                "WHERE u.email = :email";
        List<User> result = em.createQuery(jpql, User.class)
                .setParameter("email", email)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<User> findByIdWithProfiles(Long id) {
        String jpql = "SELECT u FROM User u " +
                "LEFT JOIN FETCH u.studentProfile " +
                "LEFT JOIN FETCH u.teacherProfile " +
                "LEFT JOIN FETCH u.adminProfile " +
                "WHERE u.id = :id";
        List<User> result = em.createQuery(jpql, User.class)
                .setParameter("id", id)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<User> findAllByIdWithProfiles(List<Long> ids) {
        String jpql = "SELECT u FROM User u " +
                "LEFT JOIN FETCH u.studentProfile " +
                "LEFT JOIN FETCH u.teacherProfile " +
                "LEFT JOIN FETCH u.adminProfile " +
                "WHERE u.id IN :ids";
        return em.createQuery(jpql, User.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public Page<User> findAllWithFilters(ProfileType profileType, Boolean active, String search, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = buildPredicates(cb, user, profileType, active, search);
        cq.where(predicates.toArray(new Predicate[0]));

        Long total = executeCountQuery(profileType, active, search);

        List<User> result = em.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(result, pageable, total);
    }

    public long countByProfileType(ProfileType profileType) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.profileType = :profileType";
        return em.createQuery(jpql, Long.class)
                .setParameter("profileType", profileType)
                .getSingleResult();
    }

    public long countByActive(boolean active) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.active = :active";
        return em.createQuery(jpql, Long.class)
                .setParameter("active", active)
                .getSingleResult();
    }

    public long countByProfileTypeAndActive(ProfileType profileType, boolean active) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.profileType = :profileType AND u.active = :active";
        return em.createQuery(jpql, Long.class)
                .setParameter("profileType", profileType)
                .setParameter("active", active)
                .getSingleResult();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<User> user,
                                            ProfileType profileType, Boolean active, String search) {
        List<Predicate> predicates = new ArrayList<>();
        if (profileType != null) {
            predicates.add(cb.equal(user.get("profileType"), profileType));
        }
        if (active != null) {
            predicates.add(cb.equal(user.get("active"), active));
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(user.get("email")), like),
                    cb.like(cb.lower(user.get("firstName")), like),
                    cb.like(cb.lower(user.get("lastName")), like),
                    cb.like(cb.lower(user.get("username")), like)
            ));
        }
        return predicates;
    }

    private Long executeCountQuery(ProfileType profileType, Boolean active, String search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> user = cq.from(User.class);
        cq.select(cb.count(user));
        cq.where(buildPredicates(cb, user, profileType, active, search).toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }
}