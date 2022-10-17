package com.example.MyBookShopApp.config;

import com.example.MyBookShopApp.data.Book;
import com.example.MyBookShopApp.data.BookRepository;
import com.example.MyBookShopApp.data.TestEntity;
import org.aspectj.weaver.ast.Test;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;

@Configuration
public class CommandLineRunnerImpl  implements CommandLineRunner {

    EntityManagerFactory entityManagerFactory;
    BookRepository bookRepository;


    @Autowired
    public CommandLineRunnerImpl(EntityManagerFactory entityManagerFactory, BookRepository bookRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        for (int i = 0;i<5;i++){
//            createTestEntity(new TestEntity());
//        }
//
//
//        TestEntity readTestEntity = readTestEntityById(3L);
//        if (readTestEntity != null){
//            Logger.getLogger(CommandLineRunner.class.toString()).info("read "+readTestEntity.toString());
//        } else {
//            throw new NullPointerException();
//        }
//
//        TestEntity updateTestEntity = updateTestEntityById(5L);
//        if (updateTestEntity != null){
//            Logger.getLogger(CommandLineRunner.class.toString()).info("update "+updateTestEntity.toString());
//        } else {
//            throw new NullPointerException();
//        }
//
//
//
//
//        Logger.getLogger(CommandLineRunner.class.getSimpleName()).info(bookRepository.findBooksByAuthor_FirstName("Doreen").toString());
//        Logger.getLogger(CommandLineRunner.class.getSimpleName()).info(bookRepository.customFindAllBooks().toString());

    }

    private void deleteTestEntityById(Long id) {
        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            TestEntity findEntity = readTestEntityById(id);
            TestEntity mergedTestEntity = (TestEntity) session.merge(findEntity);
            session.remove(mergedTestEntity);
            tx.commit();
        } catch (HibernateException hex){
            if (tx != null){
                tx.rollback();
            } else {
                hex.printStackTrace();
            }
        }
        finally {
            session.close();
        }
    }

    private TestEntity updateTestEntityById(long id) {
        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        Transaction tx = null;
        TestEntity result = null;

        try{
            tx = session.beginTransaction();
            TestEntity findEntity = readTestEntityById(id);
            findEntity.setData("NEW DATA UPDATE");
            result = (TestEntity) session.merge(findEntity);
            tx.commit();
        } catch (HibernateException hex){
            if (tx != null){
                tx.rollback();
            } else {
                hex.printStackTrace();
            }
        }
        finally {
            session.close();
        }
        return result;

    }

    private TestEntity readTestEntityById(long id) {
        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        Transaction tx = null;
        TestEntity result = null;

        try{
            tx = session.beginTransaction();
            result = session.find(TestEntity.class, id);
            tx.commit();
        } catch (HibernateException hex){
            if (tx != null){
                tx.rollback();
            } else {
                hex.printStackTrace();
            }
        }
        finally {
            session.close();
        }
        return result;
    }


    public void createTestEntity(TestEntity entity){
        Session session = entityManagerFactory.createEntityManager().unwrap(Session.class);
        Transaction tx = null;

         try{
             tx = session.beginTransaction();
             entity.setData(entity.getClass().getSimpleName() + entity.hashCode());
             session.save(entity);
             tx.commit();
         } catch (HibernateException hex){
             if (tx != null){
                 tx.rollback();
             } else {
                 hex.printStackTrace();
             }
         }
         finally {
             session.close();
         }

    }
}
