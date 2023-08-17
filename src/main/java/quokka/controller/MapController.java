package quokka.controller;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LatLonGraticuleLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import quokka.JavaPostgreSql;
import quokka.models.Account;
import quokka.models.Area;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static quokka.JavaPostgreSql.getAllAccounts;

public class MapController extends Parent {
    @FXML
    private AnchorPane MapPane;

    @FXML
    private StackPane MapStackPane;
    @FXML
    private TextField lat1;
    @FXML
    private TextField lat2;
    @FXML
    private TextField lat3;
    @FXML
    private TextField lat4;


    @FXML
    private TextField lon1;
    @FXML
    private TextField lon2;
    @FXML
    private TextField lon3;
    @FXML
    private TextField lon4;

    private static Account currentAccount;


    WorldWindowGLJPanel worldWind = new WorldWindowGLJPanel();
    LatLonGraticuleLayer graticuleLayer = new LatLonGraticuleLayer();


    public void initialize() {
        worldWind.setModel(new BasicModel());

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(worldWind);

        MapStackPane.getChildren().add(swingNode);

        graticuleLayer.setEnabled(true);
        worldWind.getModel().getLayers().add(graticuleLayer);

    }
    /*
    private SwingNode buildWW() {
        SwingNode node = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            WorldWindowGLJPanel wwj = new WorldWindowGLJPanel();
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            wwj.setModel(m);
            node.setContent(wwj);
        });

        return node;
    }

     */
    public void getCoordinatesFromUser(){

        double latitude1 = Double.parseDouble(lat1.getText());
        double latitude2 = Double.parseDouble(lat2.getText());
        double latitude3 = Double.parseDouble(lat3.getText());
        double latitude4 = Double.parseDouble(lat4.getText());

        double longitude1 = Double.parseDouble(lon1.getText());
        double longitude2 = Double.parseDouble(lon2.getText());
        double longitude3 = Double.parseDouble(lon3.getText());
        double longitude4 = Double.parseDouble(lon4.getText());

        //create some "Position" to build a polyline
        LinkedList<Position> list = new LinkedList<Position>();

        list.add(Position.fromDegrees(latitude1, longitude1, 10000));
        list.add(Position.fromDegrees(latitude2, longitude2, 10000));
        list.add(Position.fromDegrees(latitude3, longitude3, 10000));
        list.add(Position.fromDegrees(latitude4, longitude4, 10000));
        list.add(Position.fromDegrees(latitude1, longitude1, 10000));

        Color color = AccountController.convertStringToAWTColor(currentAccount.getColor());

        Polyline polyline = new Polyline(list);
        polyline.setColor(color);
        polyline.setLineWidth(3.0);

        //create a layer and add polyline
        RenderableLayer layer = new RenderableLayer();
        layer.addRenderable(polyline);

        //add layer to WorldWind
        worldWind.getModel().getLayers().add(layer);

        String coordinatesAsString = formatCoordinatesAsString(list);



        // Create a new Area instance
        Area area = new Area();
        area.setOwner(currentAccount);
        area.setCoordinates(coordinatesAsString);
        area.setColor(currentAccount.getColor());

        // Persist the Area using Hibernate's entity manager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("area.xml"); //?????????
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        em.persist(area);
        em.getTransaction().commit();

        em.close();
        emf.close();



    }
    private String formatCoordinatesAsString(LinkedList<Position> coordinates) {
        StringBuilder builder = new StringBuilder();
        for (Position position : coordinates) {
            builder.append(position.getLatitude().degrees).append(",").append(position.getLongitude().degrees).append(";");
        }
        return builder.toString();
    }


    public void showAllUserAreas() {
        SessionFactory factory = JavaPostgreSql.createSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Fetch all accounts along with their associated areas using the session
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
            query.distinct(true); // Ensures distinct accounts
            query.from(Account.class);
            List<Account> allAccounts = getAllAccounts();

            transaction.commit();

            // Clear existing drawn shapes
            worldWind.getModel().getLayers().removeAll();
            worldWind.setModel(new BasicModel());
            worldWind.getModel().getLayers().add(graticuleLayer);

            // Iterate through all accounts and their associated areas
            for (Account account : allAccounts) {
                for (Area area : account.getAreas()) {
                    List<Position> positions = parseCoordinatesFromString(area.getCoordinates());
                    drawPolyline(area, positions);
                }
            }
        }
        catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }

    private void drawPolyline(Area area, List<Position> positions) {
        Color color = AccountController.convertStringToAWTColor(area.getColor());

        Polyline polyline = new Polyline(positions);
        polyline.setColor(color);
        polyline.setLineWidth(3.0);

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderable(polyline);

        worldWind.getModel().getLayers().add(layer);
    }

    public void showUserAreas() {

        // Clear existing drawn shapes
        worldWind.getModel().getLayers().removeAll();
        worldWind.setModel(new BasicModel());
        worldWind.getModel().getLayers().add(graticuleLayer);

        // Iterate through areas and add Polylines to the map
        for (Area area : currentAccount.getAreas()) {
            ArrayList<Position> positions = parseCoordinatesFromString(area.getCoordinates());
            drawPolyline(area, positions);
        }
    }


    private ArrayList<Position> parseCoordinatesFromString(String coordinatesAsString) {
        ArrayList<Position> positions = new ArrayList<>();

        String[] pointPairs = coordinatesAsString.split(";");
        for (String pointPair : pointPairs) {
            String[] latLon = pointPair.split(",");
            if (latLon.length == 2) {
                double latitude = Double.parseDouble(latLon[0]);
                double longitude = Double.parseDouble(latLon[1]);
                positions.add(Position.fromDegrees(latitude, longitude));
            }
        }
        return positions;
    }


    public void collectUser(Account account){
        currentAccount = account;
    }


    public void resetWorld(){
        lat1.clear();
        lat2.clear();
        lat3.clear();
        lat4.clear();

        lon1.clear();
        lon2.clear();
        lon3.clear();
        lon4.clear();

        worldWind.getModel().getLayers().removeAll();
        worldWind.setModel(new BasicModel());
        worldWind.getModel().getLayers().add(graticuleLayer);

    }
}
