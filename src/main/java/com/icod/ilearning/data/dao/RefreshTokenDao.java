package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.RefreshToken;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;

public class RefreshTokenDao {

    public RefreshToken find(String refreshToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RefreshToken> query = session.createQuery("from RefreshToken where token=:refreshToken");
            query.setParameter("refreshToken", refreshToken);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public RefreshToken findByUserId(long userId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RefreshToken> query = session.createQuery("from RefreshToken where userId=:userId");
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (Exception e) {
            if(e instanceof NoResultException == false) {
                e.printStackTrace();
            }
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public Long insert(RefreshToken refreshToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long id = (Long) session.save(refreshToken);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean update(RefreshToken refreshToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(refreshToken);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    public boolean delete(RefreshToken refreshToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(refreshToken);
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
