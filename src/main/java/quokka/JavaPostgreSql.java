package quokka;

import quokka.models.Account;
import quokka.models.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class JavaPostgreSql {


    public static int saveAccount(String first_name, String last_name, String email, char[] password){
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        Account acc = new Account(first_name, last_name, email, password);

        session.save(acc);
        t.commit();
        System.out.println("Successfully saved");
        int savedAccountId = acc.getId();

        session.close();
        factory.close();
        return savedAccountId;
    }

    public static Account authenticateUserAndGetAccount(String email, char[] password) {
        Transaction transaction = null;

        try (SessionFactory sessionFactory = createSessionFactory(); Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(Account.class);
            criteria.add(Restrictions.eq("email", email));

            Account account = (Account) criteria.uniqueResult();

            if (account != null && Arrays.equals(account.getPassword(), password)) {
                return account;
            } else {
                return null; // Authentication failed
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    private static SessionFactory createSessionFactory() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        return meta.getSessionFactoryBuilder().build();
    }


    public static int saveSong(String songTitle, String songArtist, String songAlbum, LocalDate releaseDate){
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        Song song = new Song(songTitle, songArtist, songAlbum, releaseDate);

        session.save(song);
        t.commit();
        System.out.println("Successfully saved report");
        int savedSongId = song.getId();
        session.close();
        factory.close();
        return savedSongId;
    }


    public static Account getAccountById(int id) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Account account = session.get(Account.class, id);

        session.close();
        factory.close();

        return account;
    }

    //for: each email has a single corresponding account
    public static Account getAccountByEmail(String email) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();


        Criteria criteria = session.createCriteria(Account.class);
        criteria.add(Restrictions.eq("email", email));

        Account account = (Account) criteria.uniqueResult();

        session.close();
        factory.close();

        return account;
    }
    //for: an email can correspond to multiple accounts ?
    public static List<Account> getAccountsByEmail(String email) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Criteria criteria = session.createCriteria(Account.class);
        criteria.add(Restrictions.eq("email", email));

        List<Account> accounts = criteria.list();

        session.close();
        factory.close();

        return accounts;
    }

    public static ObservableList<Account> getAllAccounts() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Criteria criteria = session.createCriteria(Account.class);
        List<Account> accounts = criteria.list();

        session.close();
        factory.close();

        return FXCollections.observableArrayList(accounts);
    }



    public static Song getSongById(int id) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Song song = session.get(Song.class, id);

        session.close();
        factory.close();

        return song;
    }

    public static List<Song> getSongsByTitle(String title) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Criteria criteria = session.createCriteria(Account.class);
        criteria.add(Restrictions.eq("songTitle", title));

        List<Song> songs = criteria.list();

        session.close();
        factory.close();

        return songs;
    }

    public static ObservableList<Song> getAllSongs() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();

        Criteria criteria = session.createCriteria(Song.class);
        List<Song> songs = criteria.list();

        session.close();
        factory.close();

        return FXCollections.observableArrayList(songs);
    }


    public static void associateSongWithAccount(Account account, Song song) {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata meta = new MetadataSources(ssr)
                .getMetadataBuilder()
                .build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        account.getSongs().add(song);
        session.update(account);

        t.commit();
        session.close();
        factory.close();
    }


}