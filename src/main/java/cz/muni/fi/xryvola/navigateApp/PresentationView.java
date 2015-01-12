package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.ui.themes.ChameleonTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.SlideShowWIndow;
import cz.muni.fi.xryvola.services.PDFGenerator;
import org.vaadin.openesignforms.ckeditor.CKEditorConfig;
import org.vaadin.openesignforms.ckeditor.CKEditorTextField;
import cz.muni.fi.xryvola.services.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 16.10.14.
 */

@Theme("valo")
public class PresentationView extends HorizontalSplitPanel implements View {

    private SuperManager superManager;

    private VerticalLayout presentationMenuLeft;
    private VerticalLayout presentationPageContent;
    private HorizontalLayout slideButts;
    private Tree listOfSlides = new Tree();
    private HorizontalLayout slideMenuLayout;
    private MenuBar slidePageMenu;
    private CKEditorTextField slideContentArea;
    private Presentation presentation;
    private Slide currentSlide;
    private CKEditorConfig editorConfig = new CKEditorConfig();
    private boolean changed;

    public PresentationView(Presentation presentation){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        this.changed = false;
        this.presentation = presentation;
        currentSlide = presentation.getSlides().get(0);

        presentationMenuLeft = new VerticalLayout();
        presentationPageContent = new VerticalLayout();

        this.setFirstComponent(presentationMenuLeft);
        this.setSecondComponent(presentationPageContent);

        this.setSplitPosition(20, UNITS_PERCENTAGE);

        initMenu();
        initPageContent();
    }

    public void initMenu(){

        presentationMenuLeft.setMargin(true);
        //SLIDE BUTTONS
        slideButts = new HorizontalLayout();
        presentationMenuLeft.addComponent(slideButts);

        Button addNewSlide = new Button();
        addNewSlide.setDescription("Přidat slide");
        addNewSlide.setIcon(FontAwesome.PLUS_SQUARE, "Add new slide");
        addNewSlide.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);

        Button deleteSlide = new Button();
        deleteSlide.setDescription("Smazat slide");
        deleteSlide.setIcon(FontAwesome.MINUS_SQUARE, "Delete slide");
        deleteSlide.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);

        Button savePres = new Button();
        savePres.setDescription("Uložit prezentaci");
        savePres.setIcon(FontAwesome.FLOPPY_O, "Save slide");
        savePres.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);

        Button renameSlide = new Button();
        renameSlide.setDescription("Přejmenovat slide");
        renameSlide.setIcon(FontAwesome.FILE_TEXT, "Rename slide");
        renameSlide.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);

        slideButts.addComponent(addNewSlide);
        slideButts.addComponent(deleteSlide);
        slideButts.addComponent(renameSlide);
        slideButts.addComponent(savePres);

        addNewSlide.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                VerticalLayout newSLideNameLay = new VerticalLayout();
                newSLideNameLay.setMargin(true);
                Button accButt = new Button("Vytvořit");
                final TextField slideName = new TextField("Název nového slidu:");
                slideName.focus();
                slideName.setValue("Nový slide");
                newSLideNameLay.addComponent(slideName);
                newSLideNameLay.addComponent(accButt);
                final Window newSlideWindow = new Window();
                newSlideWindow.setContent(newSLideNameLay);
                newSlideWindow.center();
                UI.getCurrent().addWindow(newSlideWindow);

                accButt.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                accButt.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        Slide newSlide = new Slide();
                        newSlide.setName(slideName.getValue());
                        newSlide.setHtmlContent("<h1 align=\"center\">" + slideName.getValue() + "</h1>");
                        presentation.getSlides().add(newSlide);
                        superManager.getPresentationManager().updatePresentation(presentation);
                        presentation = superManager.getPresentationManager().getPresentationById(presentation.getId());

                        slideContentArea.setValue("<h1>" + slideName.getValue() + "</h1>");
                        slideName.setValue("Nový slide");

                        currentSlide = newSlide;
                        loadTree();
                        listOfSlides.select(presentation.getSlides().get(presentation.getSlides().size() - 1).getId());
                        newSlideWindow.close();
                    }
                });
            }
        });

        deleteSlide.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (presentation.getSlides().size() == 1){
                    Notification lastSlide = new Notification("POSLEDNI SLIDE", "Posledni slide nelze smazat");
                    lastSlide.setDelayMsec(600);
                    lastSlide.show(Page.getCurrent());
                } else {
                    presentation.getSlides().remove(getSlide(listOfSlides.getValue()));
                    changed = true;
                    //presentationManager.updatePresentation(presentation);
                    //presentation = presentationManager.getPresentationById(presentation.getId());
                    loadTree();
                    listOfSlides.select(presentation.getSlides().get(0).getId());
                }
            }
        });

        savePres.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                save();
            }
        });

        renameSlide.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                //final Slide s = slideManager.getSlideById((Long) listOfSlides.getValue());
                final Slide s2 = getSlide((Long) listOfSlides.getValue());
                final Window renameWin = new Window("Přejmenovat slide");
                VerticalLayout renameLay = new VerticalLayout();
                renameLay.setMargin(true);
                final TextField renameField = new TextField("Nové jméno slidu:");
                renameField.focus();
                Button acc = new Button("Přejmenovat");
                renameLay.addComponent(renameField);
                renameLay.addComponent(acc);
                renameWin.setContent(renameLay);
                renameWin.center();
                UI.getCurrent().addWindow(renameWin);

                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (renameField.getValue() != "") {
                            //s.setName(renameField.getValue());
                            s2.setName(renameField.getValue());
                            //slideManager.updateSlide(s);
                            loadTree();
                            renameWin.close();
                            listOfSlides.setValue(s2.getId());
                        }
                    }
                });

            }
        });

        presentationMenuLeft.addComponent(listOfSlides);
        listOfSlides.setNullSelectionAllowed(false);
        loadTree();
        listOfSlides.select(presentation.getSlides().get(0).getId());

        listOfSlides.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (listOfSlides.getValue() != null) {
                    presentationPageContent.removeComponent(slideContentArea);
                    loadEditor();
                    currentSlide = getSlide(listOfSlides.getValue());
                    slideContentArea.setValue(currentSlide.getHtmlContent());
                }
            }
        });
    }

    public void initPageContent(){
        slideMenuLayout = new HorizontalLayout();
        slideMenuLayout.setSizeFull();
        presentationPageContent.addComponent(slideMenuLayout);

        //MENU TOP
        slidePageMenu = new MenuBar();
        slideMenuLayout.addComponent(slidePageMenu);
        Button closeBut = new Button("Close");
        closeBut.setDescription("Zavřít");
        closeBut.setIcon(FontAwesome.TIMES);
        closeBut.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        closeBut.addStyleName(ChameleonTheme.BUTTON_ICON_ONLY);
        closeBut.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (changed){
                    final Window saveWin = new Window("Uložit");
                    VerticalLayout saveLay = new VerticalLayout();
                    saveLay.setMargin(true);
                    saveLay.setSpacing(true);
                    saveWin.setModal(true);
                    saveWin.center();
                    saveWin.setContent(saveLay);
                    Label saveLab = new Label("Chcete prezentaci uložit?");
                    saveLay.addComponent(saveLab);
                    HorizontalLayout saveButts = new HorizontalLayout();
                    saveLay.addComponent(saveButts);
                    saveButts.setSpacing(true);
                    Button yes = new Button("Ano");
                    yes.setIcon(FontAwesome.CHECK);
                    Button no = new Button("Ne");
                    no.setIcon(FontAwesome.TIMES);
                    saveButts.addComponents(yes, no);
                    yes.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            save();
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                            saveWin.close();
                        }
                    });
                    no.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                            saveWin.close();
                        }
                    });
                    UI.getCurrent().addWindow(saveWin);
                }else {
                    UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                }
            }
        });
        slideMenuLayout.addComponent(closeBut);
        slideMenuLayout.setComponentAlignment(closeBut, Alignment.TOP_RIGHT);

        MenuBar.Command clearSlideCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                slideContentArea.setValue("");
            }
        };
        MenuBar.Command closeCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                if (changed){
                    final Window saveWin = new Window("Uložit");
                    VerticalLayout saveLay = new VerticalLayout();
                    saveLay.setMargin(true);
                    saveLay.setSpacing(true);
                    saveWin.setModal(true);
                    saveWin.center();
                    saveWin.setContent(saveLay);
                    Label saveLab = new Label("Chcete prezentaci uložit?");
                    saveLay.addComponent(saveLab);
                    HorizontalLayout saveButts = new HorizontalLayout();
                    saveLay.addComponent(saveButts);
                    saveButts.setSpacing(true);
                    Button yes = new Button("Ano");
                    yes.setIcon(FontAwesome.CHECK);
                    Button no = new Button("Ne");
                    no.setIcon(FontAwesome.TIMES);
                    saveButts.addComponents(yes, no);
                    yes.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            save();
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                            saveWin.close();
                        }
                    });
                    no.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                            saveWin.close();
                        }
                    });
                    UI.getCurrent().addWindow(saveWin);
                }else {
                    UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                }
            }
        };
        MenuBar.Command generatePDFCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                PDFGenerator generator = new PDFGenerator();
                generator.generatePresentation(presentation);
                FileResource resource = new FileResource(new File(MyVaadinUI.MYFILEPATH + presentation.getId() + ".pdf"));
                Page.getCurrent().open(resource, null, false);
            }
        };
        MenuBar.Command colorPickerCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                ColorPicker colorPicker = new ColorPicker("Pozadi");
                presentationMenuLeft.addComponent(colorPicker);
                colorPicker.addColorChangeListener(new ColorChangeListener() {
                    @Override
                    public void colorChanged(ColorChangeEvent colorChangeEvent) {
                        String tempHtml = slideContentArea.getValue();
                        presentationPageContent.removeComponent(slideContentArea);
                        String css = "body{background-color: "+ colorChangeEvent.getColor().getCSS() + "}";
                        slideContentArea = new CKEditorTextField();
                        editorConfig.setContentsCss(css);
                        slideContentArea.setConfig(editorConfig);
                        slideContentArea.setValue(tempHtml);
                        slideContentArea.setWidth("210mm");
                        slideContentArea.setHeight("148mm");
                        presentationPageContent.addComponent(slideContentArea);
                        presentationPageContent.setComponentAlignment(slideContentArea, Alignment.MIDDLE_CENTER);
                    }
                });
            }
        };
        MenuBar.Command showPresCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                new SlideShowWIndow(presentation);
            }
        };
        MenuBar.Command savePresCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                save();
            }
        };
        MenuBar.Command fullscreenCmd = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem menuItem) {
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_F11);
                    robot.keyRelease(KeyEvent.VK_F11);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        };
        MenuBar.MenuItem presentationMenuBar = slidePageMenu.addItem("Prezentace", null, null);
        MenuBar.MenuItem saveSlideItem = presentationMenuBar.addItem("Uložit prezentaci", FontAwesome.SAVE, savePresCmd);
        MenuBar.MenuItem exportPDFItem = presentationMenuBar.addItem("Export do PDF", FontAwesome.FILE_PDF_O, generatePDFCmd);
        MenuBar.MenuItem showPresentation = presentationMenuBar.addItem("Spustit prezentaci", FontAwesome.DESKTOP, showPresCmd);
        MenuBar.MenuItem closePresentationItem = presentationMenuBar.addItem("Zavřít", FontAwesome.TIMES, closeCmd);
        MenuBar.MenuItem slideMenuBar = slidePageMenu.addItem("Slide", null, null);
        MenuBar.MenuItem clearSlideItem = slideMenuBar.addItem("Vyčistit slide", FontAwesome.REFRESH, clearSlideCmd);
        MenuBar.MenuItem showMenuBar = slidePageMenu.addItem("Zobrazení", null, null);
        MenuBar.MenuItem fullscreen = showMenuBar.addItem("Celá obrazovka/normální", FontAwesome.ARROWS_ALT, fullscreenCmd);
        //MenuBar.MenuItem backgroundItem = showMenuBar.addItem("Zmenit pozadi", FontAwesome.PENCIL, colorPickerCmd);

        //SLIDE CONTENT - RICH TEXT AREA
        loadEditor();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    private Slide getSlide(Object id){
        for (Slide slide : presentation.getSlides()){
            if (slide.getId() == id){
                return slide;
            }
        }
        return null;
    }

    private void save(){
        currentSlide.setHtmlContent(slideContentArea.getValue());
        superManager.getPresentationManager().updatePresentation(presentation);
        changed = false;
        Notification saveNot = new Notification("Prezentace byla uložena");
        saveNot.show(Page.getCurrent());
    }

    private void loadTree(){
        //slideManager = new SlideManagerImpl();
        listOfSlides.setNullSelectionAllowed(true);
        listOfSlides.removeAllItems();
        listOfSlides.setImmediate(true);

        List<Slide> mySlides = presentation.getSlides();

        for (Slide slide : mySlides){
            listOfSlides.addItem(slide.getId());
            listOfSlides.setItemCaption(slide.getId(), slide.getName());
            listOfSlides.setChildrenAllowed(slide.getId(), false);
        }
        listOfSlides.select(currentSlide.getId());
        listOfSlides.setNullSelectionAllowed(false);
    }

    private void loadEditor(){
        slideContentArea = new CKEditorTextField();
        slideContentArea.setWidth("822px");
        slideContentArea.setHeight("732px");
        slideContentArea.setValue(presentation.getSlides().get(0).getHtmlContent());
        presentationPageContent.addComponent(slideContentArea);
        presentationPageContent.setComponentAlignment(slideContentArea, Alignment.MIDDLE_CENTER);

        editorConfig = new CKEditorConfig();
        editorConfig.enableVaadinSavePlugin();
        editorConfig.enableCtrlSWithVaadinSavePlugin();
        List<String> fonts = new ArrayList<String>();
        fonts.add("Arial");
        fonts.add("Times New Roman");
        fonts.add("Courier New");
        fonts.add("Verdana");
        editorConfig.setFontNames(fonts);
        editorConfig.disableResizeEditor();
        editorConfig.setEnterMode("BR");
        editorConfig.addCustomToolbarLine(
                "{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] }," +
                        "{ name: 'editing', items : [ 'Find','Replace','-','SelectAll' ] }," +
                        "{ name: 'links', items : [ 'Link','Unlink' ] }," +
                        "{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent'," +
                        "'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] }," +
                        "{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] }," +
                        "{ name: 'styles', items : [ 'Font','FontSize' ] }," +
                        "{ name: 'colors', items : [ 'TextColor','BGColor' ] }," +
                        "{ name: 'insert', items : [ 'Image','Table','HorizontalRule','SpecialChar' ] },"
        );
        editorConfig.setContentsCss("");
        slideContentArea.setConfig(editorConfig);
        slideContentArea.setImmediate(true);

        //TEXT AREA WAS CHANGED
        slideContentArea.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (!currentSlide.getHtmlContent().equals(slideContentArea.getValue())) {
                    currentSlide.setHtmlContent(slideContentArea.getValue());
                    changed = true;
                }
            }
        });

        //CTRL+S
        slideContentArea.addVaadinSaveListener(new CKEditorTextField.VaadinSaveListener() {
            @Override
            public void vaadinSave(CKEditorTextField ckEditorTextField) {
                save();
            }
        });

    }
}