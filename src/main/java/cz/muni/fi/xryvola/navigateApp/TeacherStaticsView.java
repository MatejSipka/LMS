package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.filteredTable.MyFilterDecorator;
import cz.muni.fi.xryvola.services.*;
import org.dussan.vaadin.dcharts.DCharts;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.DataLabels;
import org.dussan.vaadin.dcharts.metadata.LegendPlacements;
import org.dussan.vaadin.dcharts.metadata.locations.LegendLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.*;
import org.dussan.vaadin.dcharts.renderers.legend.EnhancedLegendRenderer;
import org.dussan.vaadin.dcharts.renderers.series.DonutRenderer;
import org.dussan.vaadin.dcharts.renderers.tick.CanvasAxisTickRenderer;
import org.tepi.filtertable.FilterTable;
import org.vaadin.cssinject.CSSInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by adam on 3.2.15.
 */
public class TeacherStaticsView extends HorizontalLayout implements View {

    private SuperManager superManager;
    private Person teacher;

    private MenuComponent menu;
    private HorizontalSplitPanel content;
    private VerticalLayout left;
    private VerticalLayout right; //RESULT TABLE

    private HorizontalLayout leftTop; //TREES
    private VerticalLayout leftBottom; //CHARTS

    VerticalLayout documentsLay;
    VerticalLayout classLay;
    VerticalLayout studentsLay;

    private Tree tests;
    private Tree classes;
    private Tree students;
    private DCharts barChart;
    private DCharts pieChart;
    private Slider slider;
    private FilterTable actionTable;

    List<Action> actions = new ArrayList<Action>();

    public TeacherStaticsView(){
        this.superManager = ((MyVaadinUI) UI.getCurrent()).getSuperManager();
        this.teacher = ((MyVaadinUI)UI.getCurrent()).getCurrentUser();
        this.setSizeFull();

        this.menu = new MenuComponent(3);
        this.addComponent(menu);

        initContent();
    }

    private void initContent(){
        content = new HorizontalSplitPanel();
        content.setSizeFull();

        left = new VerticalLayout();
        right = new VerticalLayout();

        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".top-border{ border-top: 1px solid  #e0e0e0; } .green-border{ border: 2px solid green; }");

        initLeftTop();
        initLeftBottom();
        left.addComponent(leftBottom);
        left.addComponent(leftTop);

        leftTop.addStyleName("top-border");

        content.setFirstComponent(left);
        content.setSecondComponent(right);
        content.setSplitPosition(60, UNITS_PERCENTAGE);
        this.addComponent(content);
        setExpandRatio(content, 1.0f);

        initRight();
    }

    private void initLeftTop(){
        leftTop = new HorizontalLayout();
        leftTop.setSizeFull();
        leftTop.setMargin(true);

        documentsLay = new VerticalLayout();
        documentsLay.setSizeFull();
        classLay = new VerticalLayout();
        classLay.setSizeFull();
        studentsLay = new VerticalLayout();
        studentsLay.setSizeFull();

        initDocuments();
        initClass();
        initStudents();

        leftTop.addComponents(documentsLay, classLay, studentsLay);
    }

    private void initLeftBottom(){
        leftBottom = new VerticalLayout();

        slider = new Slider();
        slider.setMin(1);
        slider.setMax(6);
        slider.setCaption("Počet výsledků v grafu");
        slider.setImmediate(true);
        slider.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                //TODO
            }
        });

        replotBarChart();
        replotPieChart();

        HorizontalLayout bottCharts = new HorizontalLayout();
        bottCharts.setMargin(true);
        bottCharts.setSizeFull();

        bottCharts.addComponents(barChart);
        bottCharts.addComponent(pieChart);
        bottCharts.setSizeUndefined();
        leftBottom.addComponent(bottCharts);

        leftBottom.addComponent(slider);
        leftBottom.setComponentAlignment(slider, Alignment.TOP_CENTER);
    }

    private void initDocuments(){
        tests = new Tree();
        tests.setCaption("Testy:");

        //TEMP
        tests.addItem(teacher.getTests().get(0).getId());
        tests.setItemCaption(teacher.getTests().get(0).getId(), teacher.getTests().get(0).getName());
        tests.setValue(teacher.getTests().get(0).getId());

        //END TEMP

        for(Test t : teacher.getTests()){
            tests.addItem(t.getId());
            tests.setItemCaption(t.getId(), t.getName());
            tests.setChildrenAllowed(t.getId(), false);
        }
        tests.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                replotBarChart();
                replotPieChart();
            }
        });
        documentsLay.addComponent(tests);
    }

    private void initClass(){
        classes = new Tree();
        classes.setCaption("Třídy:");
        for(Classroom c : teacher.getClassrooms()){
            classes.addItem(c.getId());
            classes.setItemCaption(c.getId(), c.getName());
            classes.setChildrenAllowed(c.getId(), false);
        }
        classes.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                replotBarChart();
                replotPieChart();
                loadStudentsTable();
            }
        });
        classLay.addComponent(classes);
    }

    private void initStudents(){
        students = new Tree();
        students.setCaption("Žáci");
        loadStudentsTable();
        studentsLay.addComponent(students);
    }

    private void loadStudentsTable(){
        students.removeAllItems();
        students.setSizeUndefined();
        if (classes.getValue() != null){
            Classroom classroom = superManager.getClassroomManager().getClassroomById((Long) classes.getValue());
            for (Person student : classroom.getStudents()) {
                students.addItem(student.getId());
                students.setItemCaption(student.getId(), student.getName());
                students.setChildrenAllowed(student.getId(), false);

            }
        }
    }

    private void replotBarChart(){

        Long[] results = {};
        String[] names = {};

        if (tests.getValue() != null && classes.getValue() == null){
            actions = superManager.getActionManager().getActionsByDocument((Long) tests.getValue());
            slider.setMax(actions.size());
            slider.setValue((double) actions.size());
            results = new Long[actions.size()];
            names = new String[actions.size()];
            for (int i = 0; i < actions.size(); i++){
                results[i] = actions.get(i).getResult();
                Person student = superManager.getPersonManager().getPersonById(actions.get(i).getWho());
                names[i] = student.getName();
            }

        }

        DataSeries dataSeries = new DataSeries().add(results);

        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.BAR);


        Axes axes = new Axes()
                .addAxis(new XYaxis()
                        .setRenderer(AxisRenderers.CATEGORY)
                        .setTicks(new Ticks().add(names)).setTickOptions(new CanvasAxisTickRenderer()
                                .setAngle(-90)
                                //.setFontSize("10pt")
                                .setShowMark(false)
                                .setShowGridline(false)));

        Highlighter highlighter = new Highlighter()
                .setShow(false);

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setAxes(axes)
                .setHighlighter(highlighter);

        barChart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();
    }

    private void replotPieChart(){

        Test test = superManager.getTestManager().getTestById((Long) tests.getValue());

        Long maxScore = Long.valueOf(0);

        for (Question q : test.getQuestions()){
            for (Answer a : q.getAnswers()){
                if (a.getIsCorrect()) maxScore++;
            }
        }

        ArrayList<Long> results = new ArrayList<Long>();
        for(Action a : actions){
            results.add(a.getResult());
        }

        ArrayList<Integer> pocet = new ArrayList<Integer>();

        for(Long i = Long.valueOf(1); i < maxScore; i++){
            pocet.add(Collections.frequency(results, i));
        }


        DataSeries dataSeries = new DataSeries();
        dataSeries
                .add("a", pocet.get(0))
                .add("b", pocet.get(1))
                .add("c", pocet.get(2));
        SeriesDefaults seriesDefaults = new SeriesDefaults()
                .setRenderer(SeriesRenderers.DONUT)
                .setRendererOptions(
                        new DonutRenderer()
                                .setSliceMargin(3)
                                .setStartAngle(-90).setShowDataLabels(true).setDataLabels(DataLabels.VALUE)).setShadow(false);
        Legend legend = new Legend()
                .setShow(true)
                .setPlacement(LegendPlacements.OUTSIDE_GRID)
                .setLocation(LegendLocations.SOUTH)
                .setRendererOptions(new EnhancedLegendRenderer().setNumberRows(1))
                .setLabels("Jeden bod", "Dva body", "Tri body");

        Highlighter highlighter = new Highlighter()
                .setShow(true)
                .setShowTooltip(true)
                .setTooltipAlwaysVisible(true)
                .setKeepTooltipInsideChart(true);

        Options options = new Options()
                .setSeriesDefaults(seriesDefaults)
                .setLegend(legend)
                .setHighlighter(highlighter);

        pieChart = new DCharts()
                .setDataSeries(dataSeries)
                .setOptions(options)
                .show();
    }


    private void initRight(){
        actionTable = new FilterTable();
        actionTable.setFilterDecorator(new MyFilterDecorator());
        loadActionTable();
        right.addComponent(actionTable);

        Button exportBut = new Button("Export");
        exportBut.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                Table temp = new Table();
                temp.setVisible(false);
                temp.setContainerDataSource(actionTable.getContainerDataSource());
                right.addComponent(temp);

                ExcelExport excelExport = new ExcelExport(temp);
                excelExport.excludeCollapsedColumns();
                excelExport.export();
            }
        });
        right.addComponent(exportBut);

    }

    private void loadActionTable(){
        actionTable.setFilterDecorator(new MyFilterDecorator());

        actionTable.setFilterBarVisible(true);

        actionTable.setSelectable(true);
        actionTable.setImmediate(true);
        //studentsToAdd.setMultiSelect(true);

        actionTable.setColumnCollapsingAllowed(true);
        actionTable.setColumnReorderingAllowed(true);
        actionTable.setContainerDataSource(buildResultsContainer());
        actionTable.setVisibleColumns((Object[]) new String[] { "Jméno", "Výsledek", "Kdy" });
        actionTable.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

            @Override
            public String generateDescription(Component source, Object itemId,
                                              Object propertyId) {
                return "Just testing ItemDescriptionGenerator";
            }
        });
        actionTable.setPageLength(actionTable.getContainerDataSource().size());
    }

    private Container buildResultsContainer(){
        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Jméno", String.class, null);
        cont.addContainerProperty("Výsledek", Long.class, null);
        cont.addContainerProperty("Kdy", Date.class, null);

        for (Action a : actions){
            cont.addItem(a.getId());
            cont.getContainerProperty(a.getId(), "Jméno").setValue(superManager.getPersonManager().getPersonById(a.getWho()).getName());
            cont.getContainerProperty(a.getId(), "Výsledek").setValue(a.getResult());
            cont.getContainerProperty(a.getId(), "Kdy").setValue(a.getWhen());
        }
        return cont;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
