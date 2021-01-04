package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.Role;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RoleDao {

    public List<Role> getRoleByIds(Long[] ids) {
        Session session = null;
        List<Role> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Role> query = session.createQuery("from Role where id in :ids");
            query.setParameterList("ids", ids);
            result = query.getResultList();
        } catch (Exception e) {

        } finally {
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    public List<Role> getAll(String name, Integer limit, Integer offset) {
        Session session = null;
        List<Role> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
            Root<Role> role = criteria.from(Role.class);
            criteria.select(role);
            if (name != null) criteria.where(builder.like(role.get("name"), "%" + name + "%"));
            criteria.where(builder.equal(role.get("status"),1));
            Query<Role> query = session.createQuery(criteria);
            if (limit != null) query.setMaxResults(limit);
            if (offset != null) query.setFirstResult(offset);
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return result;
    }

    public Role findById(long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Role> query = session.createQuery("from Role where id=:id");
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public Long insert(Role role) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long id = (Long) session.save(role);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean update(Role role) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(role);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean delete(Role role) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(role);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }
}
