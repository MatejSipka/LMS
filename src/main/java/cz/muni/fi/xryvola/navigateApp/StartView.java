package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.services.SuperManager;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Grid;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.DonutRenderer;
import org.vaadin.cssinject.CSSInject;

/**
 * Created by adam on 26.10.14.
 */


public class StartView extends HorizontalLayout implements View {

    private SuperManager superManager;

    private MenuComponent menu;
    private HorizontalLayout content;

    public StartView(){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        setSizeFull();

        initMainMenu();

        if (MyVaadinUI.currUser.getRole().equals("STUDENT")){
            initStartStudentContent();
        }else if (MyVaadinUI.currUser.getRole().equals("TEACHER")){
            initStartTeacherContent();
        }
    }

    private void initMainMenu(){
        menu = new MenuComponent(0);
        menu.setPrimaryStyleName("menu-scroll");
        this.addComponent(menu);

        CSSInject inject = new CSSInject(UI.getCurrent());
        inject.setStyles(".menu-scroll{overflow:hidden;}");

    }

    private void initStartStudentContent(){
        content = new HorizontalLayout();
        this.addComponent(content);
        content.setMargin(true);
        content.setSizeFull();
        setExpandRatio(content, 1.0f);

        Label welcome = new Label("Vítejte");
        welcome.setPrimaryStyleName(ValoTheme.LABEL_HUGE);
        content.addComponent(welcome);

        DataSeries dataSeries = new DataSeries()
                .newSeries()
                .add("First", MyVaadinUI.currUser.getScore());
        dataSeries.newSeries().add("First", 100 - MyVaadinUI.currUser.getScore());

        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.DONUT)
                .setRendererOptions(new DonutRenderer().setStartAngle(-90).setShowDataLabels(true)).setShadow(false);

        Grid grid = new Grid().
        setDrawBorder(false).
        setShadow(false).
        setBackground("transparent");

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setSeriesColors("#000", "#f0f0f0")
                .setGrid(grid);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();

        content.addComponent(chart);
    }

    private void initStartTeacherContent(){
        content = new HorizontalLayout();
        this.addComponent(content);
        content.setMargin(true);
        content.setSizeFull();
        setExpandRatio(content, 1.0f);

        final TextArea notes = new TextArea();
        content.addComponent(notes);
        notes.setSizeFull();
        notes.setCaption("Poznámky");
        notes.setImmediate(true);
        notes.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                //TODO UKLADANI
                System.out.println("NOTES: " + valueChangeEvent.getProperty().getValue());
            }
        });

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
