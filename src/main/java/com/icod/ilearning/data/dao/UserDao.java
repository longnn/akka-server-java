package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.User;
import com.icod.ilearning.util.HibernateUtil;
import com.icod.ilearning.util.SecurityUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends CrudDao<User>{

    public UserDao() {
        super(User.class);
    }

    public List<User> getAll(String name) {
        Session session = null;
        List<User> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> user = criteria.from(User.class);
            criteria.select(user);
            if (name != null) criteria.where(builder.like(user.get("name"), "%" + name + "%"));
            result = session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            if(e instanceof NoResultException == false) {
                e.printStackTrace();
            }
        } finally {
            if (session != null) session.close();
        }
        return result;
    }

    public User findUserLogin(String email, String password) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session.createQuery("from User where email=:email and password=:password");
            query.setParameter("email", email);
            query.setParameter("password", SecurityUtil.md5(password));
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            if(e instanceof NoResultException == false) {
                e.printStackTrace();
            }
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public User findById(long id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<User> query = session.createQuery("from User where id=:id");
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean isEmailExists(String email) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Long> query = session.createQuery("select count(id) from User where email=:email");
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
