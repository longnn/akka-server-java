package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.RoleModel;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RoleDao {

    public List<RoleModel> getRoleByIds(Long[] ids) {
        Session session = null;
        List<RoleModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RoleModel> query = session.createQuery("from RoleModel where id in :ids");
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

    public List<RoleModel> getAll(String name, Integer limit, Integer offset) {
        Session session = null;
        List<RoleModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<RoleModel> criteria = builder.createQuery(RoleModel.class);
            Root<RoleModel> role = criteria.from(RoleModel.class);
            criteria.select(role);
            if (name != null) criteria.where(builder.like(role.get("name"), "%" + name + "%"));
            Query<RoleModel> query = session.createQuery(criteria);
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

    public RoleModel findById(long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RoleModel> query = session.createQuery("from RoleModel where id=:id");
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public Long insert(RoleModel role) {
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

    public boolean update(RoleModel role) {
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

    public boolean delete(RoleModel role) {
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
