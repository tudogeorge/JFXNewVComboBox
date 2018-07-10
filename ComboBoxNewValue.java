package comboboxnewvaluetest;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * On editable comboboxes, checks whether the string entered by the user is found in the Items.
 * If not found, then the client code must supply the newValueFn which gets the user-entered string and returns a (new) item.
 */
public class ComboBoxNewValue<T> {
    private ComboBox<T> comboBox;
    private Function<String,T> newValueGetterFn = null;
    private boolean setOldValueWhenAddingFailed;
    private T oldValue;
    private String tempString = null;
    private Comparator<String> strComparator = null;

    public ComboBoxNewValue(ComboBox<T> comboBox, Comparator<String> stringComparator, Function<String, T> newValueFn, Boolean requestValOnEnter, Boolean setOldValueWhenAddingFailed) {
        this.comboBox = comboBox;
        this.newValueGetterFn = newValueFn;
        this.strComparator = stringComparator;
        this.setOldValueWhenAddingFailed = setOldValueWhenAddingFailed;
        setOldValue(comboBox.getValue());
        
        comboBox.addEventFilter(KeyEvent.KEY_PRESSED, requestValOnEnter ? ehdlReq : ehdlDontReq);
        
        comboBox.getSelectionModel().selectedItemProperty().addListener(
            (obsV, oldV, newV) -> {
                setOldValue(oldV);
            }
        );
        comboBox.focusedProperty().addListener((obsV, oldV, newV) -> handleTheEditor());
    }
    
    public ComboBoxNewValue(ComboBox<T> comboBox, Function<String, T> newValueFn, Boolean requestValOnEnter, Boolean setOldValueWhenAddingFailed) {
        this(comboBox, 
            (String o1, String o2) -> o1.compareToIgnoreCase(o2),
            newValueFn,
            requestValOnEnter,
            setOldValueWhenAddingFailed
        );
    }
    
    private Predicate<Event> pdKeyHdlr = (keyEvt) -> ((KeyEvent)keyEvt).getCode()==KeyCode.ENTER;
    
    EventHandler ehdlReq = (keyEvt) -> {
        if(pdKeyHdlr.test(keyEvt))
            handleTheEditor();
    };
    
    EventHandler ehdlDontReq = (keyEvt) -> {
        if(pdKeyHdlr.test(keyEvt)){
            tempString = comboBox.getEditor().getText();
        }
    };
    
    private void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public void setSetOldValueWhenAddingFailed(boolean setOldValueWhenAddingFailed) {
        this.setOldValueWhenAddingFailed = setOldValueWhenAddingFailed;
    }
    
    public T handleTheEditor(){
        String theStr = tempString==null ? comboBox.getEditor().getText() : tempString;
        tempString = null;
        if(theStr==null)
            return null;
        theStr = theStr.trim();
        if(theStr.isEmpty())
            return null;
        
        T rslt = searchByStr(theStr);
        if(rslt==null) {
            T theT = newValueGetterFn.apply(theStr);
            if(theT == null){
                rslt = oldValue;
                Platform.runLater(() -> {
                    comboBox.setValue(setOldValueWhenAddingFailed ? oldValue : null);
                });
            } else {
                rslt = theT;
                Platform.runLater(() -> {
                    comboBox.setValue(theT);
                });
            }
        }
        return rslt;
    }
    
    private T searchByStr(String strToSrch){
        StringConverter strCv = comboBox.getConverter();
        strCv = strCv==null ? new StringConverter(){
            @Override
            public String toString(Object object) {
                return object.toString();
            }
            @Override
            public Object fromString(String string) {
                return null;
            }
        } : strCv;
        
        for(T crtT : comboBox.getItems()){
            if(strComparator.compare(strCv.toString(crtT), strToSrch) == 0)
                return crtT;
        }
        return null;
    }
}
