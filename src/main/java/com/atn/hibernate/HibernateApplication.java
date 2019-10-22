package com.atn.hibernate;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HibernateApplication {

	public static void main(String[] args) {
		SpringApplication.run(HibernateApplication.class, args);
	}

	// Create the SessionFactory when you start the application.
	private static final SessionFactory SESSION_FACTORY;
	private static Logger LOG = Logger.getLogger(HibernateApplication.class.getName());
	public static AtomicInteger atomicInteger = new AtomicInteger(100);

	/**
	 * Initialize the SessionFactory instance.
	 */
	static {
		// Create a Configuration object.
		Configuration config = new Configuration();
		// Configure using the application resource named hibernate.cfg.xml.
		config.configure();
		// Extract the properties from the configuration file.
		Properties prop = config.getProperties();

		// Create StandardServiceRegistryBuilder using the properties.
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(prop);

		// Build a ServiceRegistry
		ServiceRegistry registry = builder.build();

		// Create the SessionFactory using the ServiceRegistry
		SESSION_FACTORY = config.buildSessionFactory(registry);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			// ADD STUDENT
			create(2, "samir", 25);
			// Print all the Students
			List<Student> students = readAll();
			if (students != null) {
				for (Student stu : students) {
					LOG.info("Student : {} "+stu);
				}
			}

			// NEVER FORGET TO CLOSE THE SESSION_FACTORY
			SESSION_FACTORY.close();
		};
	}

	/**
	 * Create a new Student.
	 *
	 * @param name
	 * @param age
	 */
	public static void create(int id, String name, int age) {
		// Create a session
		Session session = SESSION_FACTORY.openSession();
		Transaction transaction = null;
		try {
			// Begin a transaction
			transaction = session.beginTransaction();
			Student stu = new Student();
			stu.setId(id);
			stu.setName(name);
			stu.setAge(age);
			// Save the student
			session.save(stu);
			// Commit the transaction
			transaction.commit();
		} catch (HibernateException ex) {
			// If there are any exceptions, roll back the changes
			if (transaction != null) {
				transaction.rollback();
			}
			// Print the Exception
			ex.printStackTrace();
		} finally {
			// Close the session
			session.close();
		}
	}

	/**
	 * Read all the Students.
	 *
	 * @return a List of Students
	 */
	public static List<Student> readAll() {
		List<Student> students = null;
		// Create a session
		Session session = SESSION_FACTORY.openSession();
		Transaction transaction = null;
		try {
			// Begin a transaction
			transaction = session.beginTransaction();
			students = session.createQuery("FROM Student").list();
			// Commit the transaction
			transaction.commit();
		} catch (HibernateException ex) {
			// If there are any exceptions, roll back the changes
			if (transaction != null) {
				transaction.rollback();
			}
			// Print the Exception
			ex.printStackTrace();
		} finally {
			// Close the session
			session.close();
		}
		return students;
	}

	/**
	 * Delete the existing Student.
	 *
	 * @param id
	 */
	public static void delete(int id) {
		// Create a session
		Session session = SESSION_FACTORY.openSession();
		Transaction transaction = null;
		try {
			// Begin a transaction
			transaction = session.beginTransaction();
			// Get the Student from the database.
			Student stu = (Student) session.get(Student.class, Integer.valueOf(id));
			// Delete the student
			session.delete(stu);
			// Commit the transaction
			transaction.commit();
		} catch (HibernateException ex) {
			// If there are any exceptions, roll back the changes
			if (transaction != null) {
				transaction.rollback();
			}
			// Print the Exception
			ex.printStackTrace();
		} finally {
			// Close the session
			session.close();
		}
	}

	/**
	 * Update the existing Student.
	 *
	 * @param id
	 * @param name
	 * @param age
	 */
	public static void upate(int id, String name, int age) {
		// Create a session
		Session session = SESSION_FACTORY.openSession();
		Transaction transaction = null;
		try {
			// Begin a transaction
			transaction = session.beginTransaction();
			// Get the Student from the database.
			Student stu = (Student) session.get(Student.class, Integer.valueOf(id));
			// Change the values
			stu.setName(name);
			stu.setAge(age);
			// Update the student
			session.update(stu);

			// Commit the transaction
			transaction.commit();
		} catch (HibernateException ex) {
			// If there are any exceptions, roll back the changes
			if (transaction != null) {
				transaction.rollback();
			}
			// Print the Exception
			ex.printStackTrace();
		} finally {
			// Close the session
			session.close();
		}
	}

}
