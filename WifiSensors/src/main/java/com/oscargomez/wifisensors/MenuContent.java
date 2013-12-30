package com.oscargomez.wifisensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MenuContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    static {
        // Add 3 sample items.
        addItem(new MenuItem("1", "Conexiones", "ConexionesFragment", R.layout.fragment_conexiones_detail));
        addItem(new MenuItem("2", "Sensores", "SensoresFragment", R.layout.fragment_sensores_detail));
        addItem(new MenuItem("3", "Información", "InformacionFragment", R.layout.fragment_informacion_detail));
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MenuItem {
        public String id;
        public String content;
        public String clss;
        public int layout;

        public MenuItem(String id, String content, String clss, int layout) {
            this.id = id;
            this.content = content;
            this.clss = clss;
            this.layout = layout;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
