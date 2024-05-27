package lesser.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lesser.earthquake.json.Feature;
import lesser.earthquake.json.FeatureCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class EarthquakeFrame extends JFrame {

    private JList<String> jlist = new JList<>();
    private EarthquakeService service;
    private Disposable disposable;
    private Feature[] features;

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JRadioButton oneHourButton = new JRadioButton("One Hour");
        JRadioButton thirtyDaysButton = new JRadioButton("30 Days");

        ButtonGroup group = new ButtonGroup();
        group.add(oneHourButton);
        group.add(thirtyDaysButton);

        oneHourButton.setSelected(true);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        radioPanel.add(oneHourButton);
        radioPanel.add(thirtyDaysButton);

        add(radioPanel, BorderLayout.NORTH);
        add(new JScrollPane(jlist), BorderLayout.CENTER);

        service = new EarthquakeServiceFactory().getService();

        oneHourButton.addActionListener(e -> fetchEarthquakeData(service.oneHour()));
        thirtyDaysButton.addActionListener(e -> fetchEarthquakeData(service.significantLast30Days()));

        jlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jlist.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        Feature selectedFeature = features[selectedIndex];
                        double latitude = selectedFeature.geometry.coordinates[1];
                        double longitude = selectedFeature.geometry.coordinates[0];
                        openGoogleMaps(latitude, longitude);
                    }
                }
            }
        });

        fetchEarthquakeData(service.oneHour());
    }

    private void fetchEarthquakeData(Single<FeatureCollection> request) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = request
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace);
    }

    private void handleResponse(FeatureCollection response) {
        features = response.features;
        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        jlist.setListData(listData);
    }

    private void openGoogleMaps(double latitude, double longitude) {
        String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", latitude, longitude);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
       new EarthquakeFrame().setVisible(true);
    }
}
