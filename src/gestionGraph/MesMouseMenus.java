/*
 * MesMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */

package gestionGraph;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import projet.Main.Carrefour;
import projet.Main.Route;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * 
 * @author Dr. Greg M. Bernstein
 */
public class MesMouseMenus {

    public static class EdgeMenu extends JPopupMenu {
        // private JFrame frame;
        public EdgeMenu(final JFrame frame) {
            super("Edge Menu");
            this.add(new DeleteEdgeMenuItem<Route>());
        }

    }

    public static class VertexMenu extends JPopupMenu {
        public VertexMenu() {
            super("Vertex Menu");
            this.add(new DeleteVertexMenuItem<Carrefour>());
        }
    }
}
