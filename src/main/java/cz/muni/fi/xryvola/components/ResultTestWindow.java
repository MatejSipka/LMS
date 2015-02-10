package cz.muni.fi.xryvola.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.*;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.canvasoverlays.HorizontalLine;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.LegendPlacements;
import org.dussan.vaadin.dcharts.metadata.SeriesToggles;
import org.dussan.vaadin.dcharts.metadata.locations.LegendLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.LegendRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.*;
import org.dussan.vaadin.dcharts.renderers.legend.EnhancedLegendRenderer;
import org.dussan.vaadin.dcharts.renderers.series.DonutRenderer;
import org.vaadin.cssinject.CSSInject;

import java.util.List;

/**
 * Created by adam on 19.1.15.
 */
public class ResultTestWindow extends Window {

    private SuperManager superManager;

    private Test test;
    private int result;
    private int maxScore;
    private double average;

    VerticalLayout root;
    HorizontalLayout scoreLay;
    VerticalLayout statics;
    HorizontalLayout butts;


    public ResultTestWindow(Test test, int result){
        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        this.test = test;
        this.result = result;

        this.center();
        this.setModal(true);

        maxScore = 0;

        for (Question q : test.getQuestions()){
            for (Answer a : q.getAnswers()){
                if (a.getIsCorrect()) maxScore++;
            }
        }

        root = new VerticalLayout();
        scoreLay = new HorizontalLayout();
        statics = new VerticalLayout();
        butts = new HorizontalLayout();

        initScoreLay();
        initStatics();
        initButts();

        /*
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".scorelay { background-color: green; }");
        scoreLay.setStyleName("scorelay");
        butts.setStyleName("scorelay");
        */

        root.addComponent(scoreLay);
        root.addComponent(statics);
        root.addComponent(butts);

        root.setMargin(true);

        this.setContent(root);
    }

    private void initScoreLay(){

        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".score { font-size: 150px; text-align: center; margin: 0px; padding: 0px; line-height: 70%; margin-top: 50px; } .score-total{ text-align: center; margin: 0px; padding: 0px; text-decoration: overline; } .score-total-lay{ }");

        Label score = new Label("<p class=\"score\">" + String.valueOf(result) + "</p> <br> <p class=\"score-total\">" + "<b>Dosažený počet bodů</b></p>");
        score.setContentMode(ContentMode.HTML);

        VerticalLayout totalScore = new VerticalLayout();
        totalScore.addComponents(score);
        totalScore.setStyleName("score-total-lay");
        totalScore.setMargin(true);

        scoreLay.addComponent(totalScore);

        DCharts chart = initChart();
        scoreLay.addComponent(chart);
        scoreLay.setHeight("250px");

    }

    private DCharts initChart(){
        DataSeries dataSeries = new DataSeries()
                .newSeries()
                .add("Dobře", result);
        dataSeries.newSeries().add("Špatně", maxScore-result);

        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.DONUT)
                .setRendererOptions(new DonutRenderer().setStartAngle(0).setShowDataLabels(true).setSliceMargin(0)).setShadow(true);

        Grid grid = new Grid()
                .setDrawBorder(false)
                .setShadow(false)
                .setBackground("transparent");

        Legend legend = new Legend()
                .setShow(true)
                .setPlacement(LegendPlacements.OUTSIDE_GRID)
                .setLocation(LegendLocations.SOUTH)
                .setRenderer(LegendRenderers.ENHANCED)
                .setRendererOptions(
                        new EnhancedLegendRenderer()
                                .setSeriesToggle(SeriesToggles.SLOW)
                                .setSeriesToggleReplot(false)
                                .setNumberRows(1));

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setSeriesColors("#00A20B", "white")
                .setGrid(grid)
                .setLegend(legend);

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();
        chart.setSizeUndefined();
        return chart;
    }

    private void initStatics(){
        Label max = new Label("MAXIMUM SCORE: " + String.valueOf(maxScore));
        statics.addComponent(max);
        DCharts chart = initHistoryChart();
        statics.addComponent(chart);
        statics.setComponentAlignment(chart, Alignment.TOP_CENTER);
        max.setValue("<b>Plný počet bodů: " + maxScore + " <font color=\"green\"> průměr z předchozích pokusů: " + String.format("%.3g", average) + "</font></b>");
        max.setContentMode(ContentMode.HTML);
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".max-label{ border-top: 1px solid #888888; text-align: center; }");
        max.setStyleName("max-label");
        statics.setComponentAlignment(max, Alignment.BOTTOM_CENTER);

        statics.setHeight("350px");


    }

    private DCharts initHistoryChart(){

        List<Action> history = superManager.getActionManager().getActionByPersonByDocument(((MyVaadinUI)UI.getCurrent()).currUser.getId(), test.getId());

        average = 0.0;

        DataSeries dataSeries = new DataSeries();

        if (history.size() > 8) {
            history = history.subList(history.size() - 8, history.size() - 1);
        }

        for (Action a : history){
            dataSeries.add(a.getResult());
            average += a.getResult();
        }

        average /= history.size();

        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.BAR);

        Axes axes = new Axes()
            .addAxis(new XYaxis()
                    .setRenderer(AxisRenderers.CATEGORY)
                    .setTicks(new Ticks().add("Předešlé pokusy")));

        Highlighter highlighter = new Highlighter()
                .setShow(false);


        CanvasOverlay averageLine = new CanvasOverlay().setShow(true)
                .setObject(new HorizontalLine().setY(average).setLineWidth(4).setColor("green").setShadow(true))
                .setObject(new HorizontalLine().setY(maxScore).setLineWidth(2).setColor("black").setShadow(true));

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setAxes(axes)
                .setHighlighter(highlighter)
                .setCanvasOverlay(averageLine).setSeriesColors("#4D8FF2");

        DCharts chart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();
        chart.setWidth("70%");

        return chart;
    }

    private void initButts(){
        Button conti = new Button("Pokračovat");
        final Button close = new Button("Zavřít");

        conti.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        conti.setStyleName(ValoTheme.BUTTON_QUIET);
        conti.setIcon(FontAwesome.CHEVRON_RIGHT);

        close.setStyleName(ValoTheme.BUTTON_BORDERLESS);
        close.setStyleName(ValoTheme.BUTTON_QUIET);
        close.setIcon(FontAwesome.TIMES);

        butts.addComponents(conti, close);
        butts.setComponentAlignment(conti, Alignment.BOTTOM_CENTER);
        butts.setComponentAlignment(close, Alignment.BOTTOM_CENTER);

        conti.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
            }
        });

        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTCONTENTVIEW);
            }
        });

        butts.setSizeFull();
    }
}
