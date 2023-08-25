package quokka.controller;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LatLonGraticuleLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.render.Polygon;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import quokka.JavaPostgreSql;
import quokka.models.Account;
import quokka.models.Area;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class MapController extends Parent {

    public Button addPointButton;
    public Button drawButton;
    @FXML
    private StackPane MapStackPane;
    @FXML
    private TextField lat;
    @FXML
    private TextField lon;

    @FXML
    private TableColumn<Area, Integer> pointNoColumn;
    @FXML
    private TableColumn<Area, String> coordinatesColumn;

    @FXML
    private TableView<Area> pointsTable;

    /*
    private LinkedList<Area> enemyAreas;
    private LinkedList<Area> ownAreas;
     */


    private static Account currentAccount;
    WorldWindowGLJPanel worldWind = new WorldWindowGLJPanel();
    LatLonGraticuleLayer graticuleLayer = new LatLonGraticuleLayer();

    LinkedList<Position> positions = new LinkedList<>();
 //   RenderableLayer enemyZonesLayer = new RenderableLayer();



    public void initialize() {
        worldWind.setModel(new BasicModel());
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(worldWind);
        MapStackPane.getChildren().add(swingNode);
        graticuleLayer.setEnabled(true);
        worldWind.getModel().getLayers().add(graticuleLayer);

        initializeColumns();

/*
        swingNode.setOnMouseDragOver(event -> {
            Position hoverPosition = worldWind.getCurrentPosition();
            lat.setText(String.valueOf(hoverPosition.getLatitude().degrees));
            lon.setText(String.valueOf(hoverPosition.getLongitude().degrees));
        });
*/

        SwingUtilities.invokeLater(() ->
                swingNode.setOnMouseClicked(event -> {
                    System.out.println("mouse click event");

                    Position clickedPosition = worldWind.getCurrentPosition();
                    System.out.println(clickedPosition);
                  //  System.out.println("Latitude: " + clickedPosition.getLatitude().degrees + "\nLongitude: " + clickedPosition.getLongitude().degrees);
                  //  System.out.println(formatCoordinatesAsString(positions));
                    positions.add(clickedPosition);
                    lat.setText(String.valueOf(clickedPosition.getLatitude().degrees));
                    lon.setText(String.valueOf(clickedPosition.getLongitude().degrees));

                })
        );
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
    public void draw() {

        if (positions != null) {
            positions.add(Position.fromDegrees(positions.get(0).getLatitude().degrees,
                    positions.get(0).getLongitude().degrees,
                    50000));
        }

        drawPolyline(currentAccount.getColor(), positions);
        String coordinatesAsString = formatCoordinatesAsString(positions);

        Area area = new Area();
        area.setOwner(currentAccount);
        area.setCoordinates(coordinatesAsString);
        area.setColor(currentAccount.getColor());

        SessionFactory factory = JavaPostgreSql.createSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(area);
            transaction.commit();
            updateColumns();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }

    private String formatCoordinatesAsString(LinkedList<Position> coordinates) {
        StringBuilder builder = new StringBuilder();

        for (Position position : coordinates) {
            builder.append(String.format("%.6f,%.6f;", position.getLatitude().degrees, position.getLongitude().degrees));
        }
        return builder.toString();
    }



    private void addSinglePoint(LinkedList<Position> positions) {
        double latitude = Double.parseDouble(lat.getText());
        double longitude = Double.parseDouble(lon.getText());

        positions.add(Position.fromDegrees(latitude, longitude));
        drawPolyline(currentAccount.getColor(), positions);
    }


    @FXML
    public void addPointButtonClicked() {
        addSinglePoint(positions);

        lat.clear();
        lon.clear();
    }

    public void showAllUserAreas() {
        SessionFactory factory = JavaPostgreSql.createSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
            query.distinct(true);
            query.from(Account.class);
            List<Account> allAccounts = session.createQuery(query).getResultList();

            transaction.commit();

            worldWind.getModel().getLayers().removeAll();
            worldWind.setModel(new BasicModel());
            worldWind.getModel().getLayers().add(graticuleLayer);

            for (Account account : allAccounts) {
                account.getAreas().size();

                for (Area area : account.getAreas()) {
                    List<Position> positions = parseCoordinatesFromString(area.getCoordinates());
                    drawPolyline(area.getColor(), positions);
                }
            }
            updateColumns();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }


    //TODO: FOR MOUSE CLICKS IT ALWAYS ADDS A POINT TO THE LAST CLICKED POSITION

    private void drawPolyline(String colorCode, List<Position> positions) {
        Color color = AccountController.convertStringToAWTColor(colorCode);

        Polyline polyline = new Polyline(positions);
        polyline.setColor(color);
        polyline.setLineWidth(3.0);

        RenderableLayer layer = new RenderableLayer();
        layer.addRenderable(polyline);

        worldWind.getModel().getLayers().add(layer);
    }


    private LinkedList<Position> parseCoordinatesFromString(String coordinatesAsString) {
        LinkedList<Position> positions = new LinkedList<>();

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


    public void collectUser(Account account) {
        currentAccount = account;
    }

    public void resetWorld() {
        lat.clear();
        lon.clear();
        positions.clear();

        worldWind.getModel().getLayers().removeAll();
        worldWind.setModel(new BasicModel());
        worldWind.getModel().getLayers().add(graticuleLayer);

    }

    public void rotatePositions(double angleDegrees) {
        Position rotationCenter = calculateCentroid(positions);

        LinkedList<Position> rotatedPositions = new LinkedList<>();

        for (Position position : positions) {
            double x = position.getLongitude().degrees - rotationCenter.getLongitude().degrees;
            double y = position.getLatitude().degrees - rotationCenter.getLatitude().degrees;

            double newLongitude = rotationCenter.getLongitude().degrees
                    + x * Math.cos(Math.toDegrees(angleDegrees)) - y * Math.sin(Math.toDegrees(angleDegrees));

            double newLatitude = rotationCenter.getLatitude().degrees
                    + x * Math.sin(Math.toDegrees(angleDegrees)) + y * Math.cos(Math.toDegrees(angleDegrees));

            rotatedPositions.add(Position.fromDegrees(newLatitude, newLongitude, position.getAltitude()));
        }

        positions.clear();
        positions.addAll(rotatedPositions);
    }

    public void rotateLeft() {

    }

    public void rotateRight() {

    }

    public Position calculateCentroid(List<Position> positions) {
        double totalLatitude = 0;
        double totalLongitude = 0;

        for (Position position : positions) {
            totalLatitude += position.getLatitude().degrees;
            totalLongitude += position.getLongitude().degrees;
        }
        double centroidLatitude = totalLatitude / positions.size();
        double centroidLongitude = totalLongitude / positions.size();

        return Position.fromDegrees(centroidLatitude, centroidLongitude, 0);
    }


    private void initializeColumns() {
        List<Area> areas = JavaPostgreSql.getAllAreas();
        ObservableList<Area> areasList = FXCollections.observableArrayList(areas);

        pointNoColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getId()));
        coordinatesColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCoordinates()));

        pointsTable.getColumns().addAll(pointNoColumn, coordinatesColumn);
        pointsTable.setItems(areasList);

        pointsTable.setOnMouseClicked(event -> goToSelectedArea() );

    }

    private void updateColumns() {
        List<Area> areas = JavaPostgreSql.getAllAreas();
        ObservableList<Area> areasList = FXCollections.observableArrayList(areas);
        pointsTable.setItems(areasList);

    }


    //TODO: Add other users' areas as enemy zones to the logged-in account.
    public void showEnemyZones() {


        javafx.scene.paint.Color red = javafx.scene.paint.Color.color(1,0,0,1);
        javafx.scene.paint.Color green = javafx.scene.paint.Color.color(0,1,0,1);


        SessionFactory factory = JavaPostgreSql.createSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Account> query = criteriaBuilder.createQuery(Account.class);
            query.distinct(true);
            query.from(Account.class);
            List<Account> allAccounts = session.createQuery(query).getResultList();
            transaction.commit();

            worldWind.getModel().getLayers().removeAll();
            worldWind.setModel(new BasicModel());
            worldWind.getModel().getLayers().add(graticuleLayer);

            for (Account account : allAccounts) {
                List<Position> corners;
                for (Area area : account.getAreas()) {
                    corners = parseCoordinatesFromString(area.getCoordinates());
                    if (account == currentAccount) {
                        drawPolygons(AccountController.convertColorToString(green), corners);
                    }
                    else {
                        drawPolygons(AccountController.convertColorToString(red), corners);
                    }
                }
            }
            updateColumns();
            worldWind.redrawNow();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }


    //TODO: If a user draws an area over an enemy's zone, give a threat warning.

    public void threatDetected(List<Position> positions){




    }

    public void goToSelectedArea(){
        Area area = pointsTable.getSelectionModel().getSelectedItem();
        if (area != null) {
            LinkedList<Position> coordinates = parseCoordinatesFromString(area.getCoordinates());

            Position center = calculateCentroid(coordinates);
            goToPosition(center);
        }
    }
    private void goToPosition(Position position) {
        worldWind.getView().goTo(position, 10000);
    }

    private void drawPolygons(String colorCode, List<Position> positions) {
        Color color = AccountController.convertStringToAWTColor(colorCode);



        LinkedList<Position> corners = new LinkedList<>();
        for (Position position : positions) {
            position = Position.fromDegrees(position.getLatitude().getDegrees(), position.getLongitude().getDegrees(), 100000);
            corners.add(position);
        }
        Polygon polygon = new Polygon(corners);

        polygon.setAttributes(createPolygonAttributes(color));
        polygon.setAltitudeMode(WorldWind.ABSOLUTE);
        RenderableLayer layer = new RenderableLayer();
        layer.addRenderable(polygon);

        worldWind.getModel().getLayers().add(layer);
        worldWind.redrawNow();
    }
    private ShapeAttributes createPolygonAttributes(Color color) {
        ShapeAttributes attributes = new BasicShapeAttributes();
        attributes.setInteriorMaterial(new Material(color));
        attributes.setInteriorOpacity(0.5);
        attributes.setOutlineOpacity(1.0);
        attributes.setOutlineWidth(2.0);
        return attributes;
    }
}
