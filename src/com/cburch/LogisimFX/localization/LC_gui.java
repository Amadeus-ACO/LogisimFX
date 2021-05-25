package com.cburch.LogisimFX.localization;

import javafx.beans.binding.StringBinding;

//implement Singleton
public class LC_gui {

    private static final String packageName = "gui";

    private static Localizer lc;

    private LC_gui(){}

    public static Localizer getInstance(){

        if(lc == null){
            System.out.println("gui localizer created from static");
            //lc = new Localizer(packageName);
        }

        return lc;

    }

    public static StringBinding createStringBinding(final String key, Object... args) {
        return lc.createStringBinding(key, args);
    }

    public static String createComplexString(final String key, String... strings){
        return lc.createComplexString(key, strings);
    }

    public static StringBinding createComplexStringBinding(final String key, String... strings) {
        return lc.createComplexStringBinding(key, strings);
    }

    public static StringBinding castToBind(final String string){
        return lc.castToBind(string);
    }

    public static String get(final String key, final Object... args) {
        return lc.get(key, args);
    }

}
