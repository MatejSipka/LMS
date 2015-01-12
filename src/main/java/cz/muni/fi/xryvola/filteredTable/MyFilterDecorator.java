package cz.muni.fi.xryvola.filteredTable;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;
import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by adam on 29.12.14.
 */
public class MyFilterDecorator implements FilterDecorator, Serializable {


    @Override
    public String getEnumFilterDisplayName(Object propertyId, Object value) {
        return null;
    }

    @Override
    public Resource getEnumFilterIcon(Object propertyId, Object value) {
        return null;
    }

    @Override
    public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
        return null;
    }

    @Override
    public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
        return null;
    }

    @Override
    public String getFromCaption() {
        return null;
    }

    @Override
    public String getToCaption() {
        return null;
    }

    @Override
    public String getSetCaption() {
        return null;
    }

    @Override
    public String getClearCaption() {
        return null;
    }

    @Override
    public Resolution getDateFieldResolution(Object o) {
        return null;
    }

    @Override
    public String getDateFormatPattern(Object o) {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public boolean isTextFilterImmediate(Object propertyId) {
        return true;
    }

    @Override
    public int getTextChangeTimeout(Object propertyId) {
        return 10;
    }

    @Override
    public String getAllItemsVisibleString() {
        return "Zobrazit vše";
    }

    @Override
    public NumberFilterPopupConfig getNumberFilterPopupConfig() {
        return null;
    }

    @Override
    public boolean usePopupForNumericProperty(Object o) {
        return false;
    }
}
