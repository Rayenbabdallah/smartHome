package smarthome;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulatorApp extends Application {
    private CentralController controller;
    private TreeView<String> treeView;
    private TextArea logArea;
    private Label deviceStatusLabel;
    private Button toggleButton;
    private Button triggerButton;
    private Slider valueSlider;
    private BarChart<String, Number> energyChart;
    private Label currentUsageLabel;

    @Override
    public void start(Stage stage) {
        Home home = new Home();
        setupSampleHome(home);
        controller = new CentralController(home);

        treeView = new TreeView<>();
        buildTreeView(home);
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> onTreeSelection(newVal));

        deviceStatusLabel = new Label("Select a device to see details.");
        toggleButton = new Button("Toggle On/Off");
        toggleButton.setDisable(true);
        toggleButton.setOnAction(e -> onToggleDevice());

        triggerButton = new Button("Trigger Sensor");
        triggerButton.setDisable(true);
        triggerButton.setOnAction(e -> onTriggerSensor());

        valueSlider = new Slider(0, 100, 50);
        valueSlider.setDisable(true);
        valueSlider.setShowTickLabels(true);
        valueSlider.setShowTickMarks(true);
        valueSlider.setMajorTickUnit(25);
        valueSlider.setMinorTickCount(4);
        valueSlider.valueProperty().addListener((obs, oldVal, newVal) -> onSliderChanged(newVal.doubleValue()));

        VBox devicePanel = new VBox(10, deviceStatusLabel, toggleButton, triggerButton, new Label("Value:"), valueSlider);
        devicePanel.setPadding(new Insets(10));
        devicePanel.setPrefWidth(320);

        SplitPane devicesSplit = new SplitPane();
        devicesSplit.getItems().addAll(new ScrollPane(treeView), devicePanel);
        devicesSplit.setDividerPositions(0.35);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Period");
        yAxis.setLabel("Energy (kWh)");
        energyChart = new BarChart<>(xAxis, yAxis);
        energyChart.setTitle("Energy Usage");
        currentUsageLabel = new Label();
        updateEnergyChart();
        VBox energyPane = new VBox(10, energyChart, currentUsageLabel);
        energyPane.setPadding(new Insets(10));

        TabPane tabs = new TabPane();
        Tab devicesTab = new Tab("Devices", devicesSplit);
        devicesTab.setClosable(false);
        Tab energyTab = new Tab("Energy Dashboard", energyPane);
        energyTab.setClosable(false);
        tabs.getTabs().addAll(devicesTab, energyTab);

        ChoiceBox<HomeMode> modeChoice = new ChoiceBox<>();
        modeChoice.getItems().addAll(HomeMode.HOME, HomeMode.AWAY, HomeMode.NIGHT);
        modeChoice.setValue(controller.getCurrentMode());
        modeChoice.setOnAction(e -> {
            controller.setMode(modeChoice.getValue());
            refreshAllDeviceStatus();
            updateEnergyChart();
        });
        HBox topBar = new HBox(12, new Label("Home Mode:"), modeChoice);
        topBar.setPadding(new Insets(10));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(4);
        controller.setLogArea(logArea);
        ScrollPane logPane = new ScrollPane(logArea);
        logPane.setFitToWidth(true);
        logPane.setPrefHeight(120);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabs);
        root.setBottom(logPane);

        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);
        stage.setTitle("Smart Home Automation Simulator");
        stage.show();
    }

    private void setupSampleHome(Home home) {
        Room living = new Room("Living Room");
        Room hallway = new Room("Hallway");
        Room kitchen = new Room("Kitchen");
        Room entrance = new Room("Entrance");
        Room garage = new Room("Garage");
        home.addRoom(living);
        home.addRoom(hallway);
        home.addRoom(kitchen);
        home.addRoom(entrance);
        home.addRoom(garage);

        home.addDeviceToRoom(new Light(80), living);
        home.addDeviceToRoom(new SmartTV(), living);
        home.addDeviceToRoom(new SmartSpeaker(), living);
        home.addDeviceToRoom(new Thermostat(22.0), living);

        home.addDeviceToRoom(new Light(0), hallway);
        home.addDeviceToRoom(new MotionSensor(), hallway);

        home.addDeviceToRoom(new SmokeDetector(), kitchen);

        home.addDeviceToRoom(new DoorLock(), entrance);
        home.addDeviceToRoom(new Camera(), entrance);

        home.addDeviceToRoom(new GarageDoor(), garage);
        home.addDeviceToRoom(new MotionSensor(), garage);
    }

    private void buildTreeView(Home home) {
        TreeItem<String> root = new TreeItem<>("Home");
        root.setExpanded(true);
        for (Room room : home.getRooms()) {
            TreeItem<String> roomNode = new TreeItem<>(room.getName());
            roomNode.setExpanded(true);
            for (SmartDevice device : room.getDevices()) {
                roomNode.getChildren().add(new TreeItem<>(formatDeviceLabel(device)));
            }
            root.getChildren().add(roomNode);
        }
        treeView.setRoot(root);
    }

    private String formatDeviceLabel(SmartDevice device) {
        return device.getId() + " - " + device.getStatus();
    }

    private void onTreeSelection(TreeItem<String> item) {
        if (item == null) {
            return;
        }
        TreeItem<String> parent = item.getParent();
        if (parent == null) {
            deviceStatusLabel.setText("Select a room or device.");
            toggleButton.setDisable(true);
            triggerButton.setDisable(true);
            valueSlider.setDisable(true);
            return;
        }
        if ("Home".equals(parent.getValue())) {
            deviceStatusLabel.setText("Room: " + item.getValue() + " (" + item.getChildren().size() + " devices)");
            toggleButton.setDisable(true);
            triggerButton.setDisable(true);
            valueSlider.setDisable(true);
            return;
        }
        SmartDevice device = findDeviceFromNode(item);
        if (device == null) {
            return;
        }
        deviceStatusLabel.setText(formatDeviceLabel(device));
        toggleButton.setDisable(false);
        triggerButton.setDisable(!(device instanceof SensorDevice));
        configureSliderForDevice(device);
    }

    private void configureSliderForDevice(SmartDevice device) {
        valueSlider.setDisable(true);
        valueSlider.setMin(0);
        valueSlider.setMax(100);
        valueSlider.setMajorTickUnit(25);
        if (device instanceof Light) {
            valueSlider.setDisable(false);
            valueSlider.setValue(((Light) device).getBrightness());
        } else if (device instanceof SmartTV) {
            valueSlider.setDisable(false);
            valueSlider.setValue(((SmartTV) device).getVolume());
        } else if (device instanceof SmartSpeaker) {
            valueSlider.setDisable(false);
            valueSlider.setValue(((SmartSpeaker) device).getVolume());
        } else if (device instanceof Thermostat) {
            valueSlider.setDisable(false);
            valueSlider.setMin(10);
            valueSlider.setMax(30);
            valueSlider.setMajorTickUnit(5);
            valueSlider.setValue(((Thermostat) device).getTargetTemperature());
        }
    }

    private void onToggleDevice() {
        SmartDevice device = findSelectedDevice();
        if (device == null) {
            return;
        }
        if (device.isOn()) {
            controller.turnOffDevice(device);
        } else {
            controller.turnOnDevice(device);
        }
        refreshAllDeviceStatus();
        updateEnergyChart();
    }

    private void onTriggerSensor() {
        SmartDevice device = findSelectedDevice();
        if (device instanceof MotionSensor) {
            ((MotionSensor) device).triggerMotion();
        } else if (device instanceof SmokeDetector) {
            ((SmokeDetector) device).triggerSmoke();
        }
        if (device instanceof SensorDevice) {
            controller.sensorTriggered((SensorDevice) device);
        }
        refreshAllDeviceStatus();
        updateEnergyChart();
    }

    private void onSliderChanged(double newValue) {
        SmartDevice device = findSelectedDevice();
        if (device == null) {
            return;
        }
        if (device instanceof Light) {
            ((Light) device).setBrightness((int) newValue);
        } else if (device instanceof SmartTV) {
            ((SmartTV) device).setVolume((int) newValue);
        } else if (device instanceof SmartSpeaker) {
            ((SmartSpeaker) device).setVolume((int) newValue);
        } else if (device instanceof Thermostat) {
            ((Thermostat) device).setTargetTemperature(newValue);
        }
        refreshAllDeviceStatus();
    }

    private SmartDevice findSelectedDevice() {
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getParent() == null || "Home".equals(selected.getParent().getValue())) {
            return null;
        }
        return findDeviceFromNode(selected);
    }

    private SmartDevice findDeviceFromNode(TreeItem<String> node) {
        String id = node.getValue().split(" - ")[0];
        try {
            return controller.findDeviceById(id);
        } catch (DeviceNotFoundException e) {
            return null;
        }
    }

    private void refreshAllDeviceStatus() {
        TreeItem<String> root = treeView.getRoot();
        if (root == null) {
            return;
        }
        for (TreeItem<String> roomNode : root.getChildren()) {
            for (TreeItem<String> deviceNode : roomNode.getChildren()) {
                SmartDevice device = findDeviceFromNode(deviceNode);
                if (device != null) {
                    deviceNode.setValue(formatDeviceLabel(device));
                }
            }
        }
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getParent() != null && !"Home".equals(selected.getParent().getValue())) {
            SmartDevice device = findDeviceFromNode(selected);
            if (device != null) {
                deviceStatusLabel.setText(formatDeviceLabel(device));
            }
        }
    }

    private void updateEnergyChart() {
        energyChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Today", controller.getEnergyToday()));
        series.getData().add(new XYChart.Data<>("Month", controller.getEnergyMonth()));
        energyChart.getData().add(series);
        currentUsageLabel.setText(String.format("Current Consumption: %.2f kW", controller.getCurrentConsumption()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
