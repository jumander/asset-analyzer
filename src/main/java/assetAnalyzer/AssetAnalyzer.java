package assetAnalyzer;

import GUI.GUI;
import GUI.Insets;
import GUI.components.*;
import GUI.components.Console;
import GUI.components.Listners.MultipleSelectListner;
import GUI.layout.HorizontalLayout;
import GUI.layout.VerticalLayout;
import financial.*;
import financial.providers.Handelsbanken;
import financial.providers.TestProvider;
import graphics.Color;
import graphics.OpenGLRenderer;
import graphics.Window;
import org.lwjgl.BufferUtils;
import trading.AlgorithmImporter;
import trading.Optimizer;
import trading.Trader;

import javax.swing.*;
import java.io.*;
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static GUI.components.InputEventListener.KeyEvent.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by johannes on 16/12/17.
 */
public class AssetAnalyzer {
    private Window window;
    private GUI gui;
    private AssetGraph graph;
    private ArrayList<Object> providers = new ArrayList<>();
    private List<Asset> selectedAssets = new CopyOnWriteArrayList<>();
    private List<Asset> traderAssets = selectedAssets;

    private JFileChooser chooser = null;

    private HashMap<String, boolean[]> checkboxes = new HashMap();
    private String lastProvider = null;

    private void start() {
        // Set providers;
        AssetProvider handelsbanken = new Handelsbanken();
        AssetProvider testProvider = new TestProvider();
        providers.add(handelsbanken);
        providers.add(testProvider);

        // Create window
        window = new Window(1920, 1080, "Asset Analyzer");

        // Create renderer
        OpenGLRenderer render = window.getRenderer();
        if (render == null)
            return;

        gui = new GUI(render);
        setMouseListener(gui);
        setKeyListener(gui);

        // Build interface
        gui.getRoot().setBackgroundColor(new Color(40));
        gui.getRoot().setLayout(new VerticalLayout());

        gui.add(buildTopContainer());

        gui.add(new HorizontalLine());

        Console console = new Console();
        console.setHeight(300);
        gui.add(console);


        gui.start();

        System.exit(0);
    }

    private Component buildTopContainer() {
        Container top = new Container();
        top.setLayout(new HorizontalLayout());

        top.add(buildSelectionsContainer());

        top.add(new VerticalLine());

        graph = new AssetGraph();
        top.add(graph);

        return top;
    }

    public Component buildSelectionsContainer() {
        Container selections = new Container();

        Container assets = new Container();
        Container assetSelectionContainer = new Container();


        selections.setLayout(new VerticalLayout());
        assets.setLayout(new VerticalLayout());
        assetSelectionContainer.setLayout(new HorizontalLayout());

        // Select provider
        ListSelect assetSource = new ListSelect("Select asset source");
        assetSource.setMargin(new Insets(10));
        assetSource.setItems(providers);
        assetSelectionContainer.add(assetSource);

        // Select asset
        ListMultipleSelect selectAssets = new ListMultipleSelect("Select assets");
        selectAssets.setMargin(new Insets(10));
        assetSelectionContainer.add(selectAssets);
        assets.add(assetSelectionContainer);

        // Clear selection
        Button clearSelection = new Button("Clear selection");
        clearSelection.setMargin(new Insets(10, 10, 0, 10));
        assets.add(clearSelection);

        // Export selection
        Button exportSelection = new Button("Export selection");
        exportSelection.setMargin(new Insets(10));
        assets.add(exportSelection);


        // Select asset source
        assetSource.setListner((i) -> {
            new Thread(() -> {
                String provider = assetSource.getSelectedItem().toString();
                assetSource.setText(provider);

                String[] names = ((AssetProvider)providers.get(i)).getAssets();

                ArrayList<Object> A = new ArrayList<>(Arrays.asList(names));

                if(lastProvider != null)
                    checkboxes.put(lastProvider, selectAssets.getCheckBoxes());

                selectAssets.setItems(A);

                if(checkboxes.get(provider) != null)
                    selectAssets.setCheckBoxes(checkboxes.get(provider));


                lastProvider = provider;
            }).start();
        });

        // Select asset
        selectAssets.setListner(new MultipleSelectListner() {
            public void checked(int index){
                new Thread(() -> {
                    Console.println("selected: " + selectAssets.getItems().get(index));

                    Asset asset = ((AssetProvider)assetSource.getSelectedItem()).getAsset(index);
                    selectedAssets.add(asset);
                    graph.addAsset(asset);
                }).start();


            }
            public void unchecked(int index){
                new Thread(() -> {
                    Console.println("removed: " + selectAssets.getItems().get(index));
                    Asset asset = ((AssetProvider)assetSource.getSelectedItem()).getAsset(index);

                    selectedAssets.remove(asset);
                    graph.removeAsset(asset);
                }).start();
            }

        });

        // Change appearance of file chooser
        new Thread(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                // Don't care it looks shit
            }
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/user data"));
            chooser = jFileChooser;
        }).start();

        // Export selection
        exportSelection.setListner(() -> {
            // get filename
            new Thread(() -> {
                while(chooser == null)
                {
                    // wait until filechooser has been initialized
                }

                int result = chooser.showSaveDialog(null);
                if(result == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        String filename = file.getName();
                        if(!filename.endsWith(".ass"))
                            filename = filename + ".ass";
                        AssetFileHandler fileHandler = new AssetFileHandler();

                        fileHandler.store(selectedAssets, file.getParent() +"\\"+ filename);

                        Console.println("Successfully exported to the file " + filename);
                    } catch (Exception e) {
                        Console.println("Could not export data. Reason: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();

        });

        // Clear selection
        clearSelection.setListner(() -> {
            graph.removeAllAssets();
            selectedAssets.clear();
            selectAssets.uncheckAll();
        });



        selections.add(assets);
        selections.add(new Container());
        selections.add(new HorizontalLine());
        buildTradingComponent(selections);
        selections.setWidth(350);
        return selections;
    }

    private Exchange selectedExchange = null;

    public void buildTradingComponent(Container parent)
    {
        Container algos = new Container();
        algos.setLayout(new VerticalLayout());

        // trading
        AlgorithmImporter importer = new AlgorithmImporter();
        List<Object> tradingAlgorithms = new ArrayList(importer.getAlgorithms("trading.algorithms", "Trader"));
        List<Object> exchangers  = new ArrayList(importer.getAlgorithms("financial.exchangers", "Exchange"));
        List<Object> optimizerAlgorithms = new ArrayList(importer.getAlgorithms("trading.optimizers", "Optimizer"));

        // Select input data
        ListSelect selectData = new ListSelect("Select input data");
        List<Object> options = new ArrayList<>();
        options.add("Use selected assets");
        options.add("Import assets");
        selectData.setItems(options);
        selectData.setMargin(new Insets(10));
        algos.add(selectData);

        // Select exchange
        ListSelect selectExchange = new ListSelect("Select exchange");
        selectExchange.setItems(exchangers);
        selectExchange.setMargin(new Insets(10));
        algos.add(selectExchange);

        // Select algorithm
        ListSelect selectAlgorithm = new ListSelect("Select trading algorithm");
        selectAlgorithm.setItems(tradingAlgorithms);
        selectAlgorithm.setMargin(new Insets(10));
        algos.add(selectAlgorithm);

        // Enter parameters
        TextInput paramInput = new TextInput("Enter parameters");
        paramInput.setMargin(new Insets(10));
        algos.add(paramInput);

        // run algorithm
        Button start = new Button("Run");
        start.setMargin(new Insets(10));
        algos.add(start);



        selectData.setListner((i) -> {

            if(selectData.getSelectedItem().equals("Use selected assets"))
            {
                traderAssets = selectedAssets;
                selectData.setText("Use selected assets");

                selectExchange.setItems(getAvailableExchanges(traderAssets));
            }
            else {
                new Thread(() -> {
                    while(chooser == null)
                    {
                        // wait until filechooser has been initialized
                    }
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        try {
                            File file = chooser.getSelectedFile();
                            String filename = file.getName();


                            AssetFileHandler fileHandler = new AssetFileHandler();

                            traderAssets = fileHandler.load(file.getParent() +"\\"+ filename);

                            selectData.setText("Using " + filename);
                            selectExchange.setItems(getAvailableExchanges(traderAssets));

                            Console.println("Successfully imported file: " + filename);

                        } catch (Exception e) {
                            Console.println("Could not import data. Reason: " + e.getMessage());
                        }
                    }
                }).start();
            }
            selectExchange.setText("Select exchange");
        });


        selectExchange.setListner((i) -> {
            if(selectedExchange != null)
                new Thread(() -> selectedExchange.close()).start();
            selectedExchange = (Exchange)selectExchange.getSelectedItem();

            selectExchange.setText(selectedExchange.toString());
            new Thread(() -> selectedExchange.open()).start();

        });

        selectAlgorithm.setListner((i) -> {
            selectAlgorithm.setText(selectAlgorithm.getSelectedItem().toString());
        });

        start.setListner(() -> {
            Trader algorithm = (Trader)selectAlgorithm.getSelectedItem();
            String input = paramInput.getInput();
            String[] param = input.split(" ");

            new Thread(() -> {
                Calendar c = Calendar.getInstance();
                algorithm.run(traderAssets, selectedExchange, new Time(2000, 1, 1), new Time(Calendar.getInstance()), 100000, graph, param);
            }).start();
        });

        Container optimizers = new Container();
        optimizers.setLayout(new VerticalLayout());
        ListSelect selectOptimizer = new ListSelect("Select optimizer");
        selectOptimizer.setItems(optimizerAlgorithms);
        selectOptimizer.setMargin(new Insets(10));

        selectOptimizer.setListner((i) -> {
            selectOptimizer.setText(selectOptimizer.getSelectedItem().toString());
        });

        Button optimizerStart = new Button("Run");
        optimizerStart.setMargin(new Insets(10));
        optimizers.add(selectOptimizer);
        optimizers.add(optimizerStart);

        optimizerStart.setListner(() -> {
            new Thread(() -> {
                Optimizer optimizer = (Optimizer)selectOptimizer.getSelectedItem();
                Trader trader = (Trader)selectAlgorithm.getSelectedItem();
                optimizer.run(trader, traderAssets, selectedExchange, new Time(2000, 1, 1), new Time(Calendar.getInstance()), 100000, null, paramInput.getInput());
            }).start();

        });

        parent.add(algos);
        parent.add(new Container());
        parent.add(new HorizontalLine());
        parent.add(optimizers);
    }

    List<Object> getAvailableExchanges(List<Asset> assets)
    {
        AlgorithmImporter importer = new AlgorithmImporter();
        List<Object> exchanges = new ArrayList(importer.getAlgorithms("financial.exchangers", "Exchange"));

        for(int i = exchanges.size()-1; i >= 0; i--)
        {
            for(Asset asset : assets)
            {
                Exchange exchange = (Exchange)exchanges.get(i);

                if(!exchange.getMarkets().toLowerCase().contains(asset.getMarket().toLowerCase()) && !exchange.getMarkets().equals(""))
                {
                    exchanges.remove(i);
                    break;
                }
            }
        }
        return exchanges;
    }

    public static void main(String args[]) {    new AssetAnalyzer().start();    }

    private void setKeyListener(GUI gui)
    {
        window.setCharCallback((window, key) -> {
            InputEventListener.KeyEvent event;

            event = CHAR;

            gui.keyboardEvent(event, key);
        });

        window.setKeyCallback((window, key, scancode, action, mods) -> {
            InputEventListener.KeyEvent event;

            if(action == GLFW_PRESS)
                event = PRESS;
            else if (action == GLFW_RELEASE)
                event = RELEASE;
            else if (action == GLFW_REPEAT)
                event = REPEAT;
            else
                 return;

            gui.keyboardEvent(event, key);
        });

    }


    private void setMouseListener(GUI gui)
    {
        // Setup a mouse move callback
        window.setMouseCursorPosCallback((window, x, y) -> {
            gui.mouseEvent(InputEventListener.MouseEvent.MOVE, (int)x, (int)y, 0, InputEventListener.MouseButton.NONE);

        });

        window.setMouseScrollCallback((window, horiz, vert) -> {

            DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yPos = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xPos, yPos);
            int x = (int) xPos.get(0);
            int y = (int) yPos.get(0);

            gui.mouseEvent(InputEventListener.MouseEvent.SCROLL, x, y, vert, InputEventListener.MouseButton.NONE);
        });


        window.setMouseButtonCallback((window, button, action, mod) -> {
            InputEventListener.MouseButton mouseButton;
            InputEventListener.MouseEvent mouseEvent;
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    mouseButton = InputEventListener.MouseButton.LEFT;
                    break;
                case GLFW_MOUSE_BUTTON_RIGHT:
                    mouseButton = InputEventListener.MouseButton.RIGHT;
                    break;
                case GLFW_MOUSE_BUTTON_MIDDLE:
                    mouseButton = InputEventListener.MouseButton.MIDDLE;
                    break;
                default:
                    System.err.println("Unknown mouse button");
                    mouseButton = InputEventListener.MouseButton.MIDDLE;
            }
            switch (action) {
                case GLFW_PRESS:
                    mouseEvent = InputEventListener.MouseEvent.PRESS;
                    break;
                case GLFW_RELEASE:
                    mouseEvent = InputEventListener.MouseEvent.RELEASE;
                    break;
                default:
                    System.err.println("Unknown mouse event");
                    mouseEvent = InputEventListener.MouseEvent.RELEASE;
            }

            DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yPos = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xPos, yPos);
            int x = (int) xPos.get(0);
            int y = (int) yPos.get(0);

            gui.mouseEvent(mouseEvent, x, y, 0, mouseButton);

        });
    }

}
