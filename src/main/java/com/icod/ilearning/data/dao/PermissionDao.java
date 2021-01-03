package com.icod.ilearning.data.dao;

import com.icod.ilearning.data.model.PermissionModel;
import com.icod.ilearning.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class PermissionDao {

    public List<PermissionModel> getByIds(Long[] ids){
        Session session = null;
        List<PermissionModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<PermissionModel> query = session.createQuery("from PermissionModel where id in :ids");
            query.setParameterList("ids",ids);
            result = query.getResultList();
        } catch (Exception e){

        }
        return result;
    }

    public List<PermissionModel> getAll(String name,Integer limit, Integer offset){
        Session session = null;
        List<PermissionModel> result = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PermissionModel> criteria = builder.createQuery(PermissionModel.class);
            Root<PermissionModel> permission = criteria.from(PermissionModel.class);
            criteria.select(permission);
            if(name!=null) criteria.where(builder.like(permission.get("name"),"%"+name+"%"));
            Query<PermissionModel> query = session.createQuery(criteria);
            if (limit != null) query.setMaxResults(limit);
            if (offset != null) query.setFirstResult(offset);
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(session!=null) session.close();
        }
        return result;
    }
    public PermissionModel findById(long id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<PermissionModel> query = session.createQuery("from PermissionModel where id=:id");
            query.setParameter("id",id);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session!=null) session.close();
        }
    }
    public Long insert(PermissionModel permission){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long id = (Long)session.save(permission);
            session.getTransaction().commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(session!=null) session.close();
        }
    }
    public boolean update(PermissionModel permission){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(permission);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(session!=null) session.close();
        }
    }
    public boolean delete(PermissionModel permission){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(permission);
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
