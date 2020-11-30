package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.UserModel;
import com.icod.ilearning.util.HibernateUtil;
import com.icod.ilearning.util.SecurityUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public List<UserModel> getAll(String name) {
        Session session = null;
        List<UserModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UserModel> criteria = builder.createQuery(UserModel.class);
            Root<UserModel> user = criteria.from(UserModel.class);
            criteria.select(user);
            if (name != null) criteria.where(builder.like(user.get("name"), "%" + name + "%"));
            result = session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return result;
    }

    public UserModel findUserLogin(String email,String password) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<UserModel> query = session.createQuery("from UserModel where email=:email and password=:password");
            query.setParameter("email", email);
            query.setParameter("password", SecurityUtil.md5(password));
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public UserModel findById(long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<UserModel> query = session.createQuery("from UserModel where id=:id and status = 1");
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public Long insert(UserModel user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long id = (Long) session.save(user);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean update(UserModel user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean delete(UserModel user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean isEmailExists(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = session.createQuery("select count(id) from UserModel where email=:email");
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            return count.equals(0L) ? false : true;
        } catch (Exception e) {
            return true;
        } finally {
            if (session != null) session.close();
            ;
        }
    }
}
