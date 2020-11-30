package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.TeacherModel;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao {
    public List<TeacherModel> getAll(String name){
        Session session = null;
        List<TeacherModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TeacherModel> criteria = builder.createQuery(TeacherModel.class);
            Root<TeacherModel> user = criteria.from(TeacherModel.class);
            criteria.select(user);
            if(name!=null) criteria.where(builder.like(user.get("name"),"%"+name+"%"));
            result = session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(session!=null) session.close();
        }
        return result;
    }
    public TeacherModel findById(long id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<TeacherModel> query = session.createQuery("from TeacherModel where id=:id");
            query.setParameter("id",id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session!=null) session.close();
        }
    }
    public Long insert(TeacherModel user){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long id = (Long)session.save(user);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session!=null) session.close();
        }
    }
    public boolean update(TeacherModel user){
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
            if(session!=null) session.close();
        }
    }
    public boolean delete(TeacherModel user){
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
            if(session!=null) session.close();
        }
    }
}
