package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.RefreshTokenModel;
import com.icod.ilearning.data.model.RoleModel;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class RefreshTokenDao {



    public RefreshTokenModel find(String refreshToken) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RefreshTokenModel> query = session.createQuery("from RefreshTokenModel where token=:refreshToken");
            query.setParameter("refreshToken", refreshToken);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public RefreshTokenModel findByUserId(long userId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<RefreshTokenModel> query = session.createQuery("from RefreshTokenModel where userId=:userId");
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public Long insert(RefreshTokenModel refreshToken) {
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

    public boolean update(RefreshTokenModel refreshToken) {
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

    public boolean delete(RefreshTokenModel refreshToken) {
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
