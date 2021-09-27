package projet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector2d;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import gestionGraph.MesMouseMenus;
import gestionGraph.PopupVertexEdgeMenuMousePlugin;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;

public class Main {

    // CONSTANTES
    public static final int FRAME_WIDTH = 1168;
    public static final int FRAME_HEIGHT = 800;
    public static final int MENU_HEIGHT = 500;
    public static final int MENU_WIDTH = 200;

    // ATTRIBUTS
    private JFrame frame; // La fenêtre
    private JMenuBar menuBar; // Menu en haut horizontal
    private JMenuItem fermer, aide;
    private JButton reconstruire,ajouter,deplacer,supprimer;
    private Graph<Carrefour, Route> g; // Le graph
    private Layout<Carrefour, Route> layout; // Le calque contenant le graph
    private int nodeCount, edgeCount; // Nombre de sommets et d'arretes du graph
    private Factory<Carrefour> vertexFactory; // Gestionnaire des sommets du graph
    private Factory<Route> edgeFactory; // Gestionnaire des arretes du graph
    private VisualizationViewer<Carrefour, Route> vv; // Viewer du graph
    private EditingModalGraphMouse gm; // Gestionnaire des evenements de la souris
    private Image imgFond; // Image de fond du graph
    private float decalX, decalY; // Pour centrer le graph
    private float scale; // Facteur d'agrandissement du graph
    private Carrefour n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, n17, n18, n19, n20, n21,
            n22, n23, n24, n25, n26, n27, n28, n29, n30, n31, n32, n33, n34, n35, n36, n37, n38, n39, n40, n41, n42,
            n43, n44, n45, n46, n47, n48, n49, n50, n51, n52, n53, n54, n55, n56, n57, n58, n59, n60, n61, n62, n63,
            n64, n65, n66, n67, n68, n69, n70, n71, n72, n73, n74, n75, n76; // Les différents noeuds préplacés du graph
    private LinkedList<Carrefour> listNodesSelected, listNodesaAjouter, listTousNodes; // Liste des noeuds sélectionnés
    private LinkedList<Route> listArretesSelected; // Liste des arretes sélectionnés
    private float coefNoeudProche, coefLongNouvelleRoute; // Coefs pour l'algo
    private int noeudRang; // Rang choisi pour la création des noeuds à la main
    private int arreteRang; // Rang choisi pour la création des arrêtes à la main
    private int noeudRangMin; // Pour le slider de paramètrisation des nouveaux noeuds créés à la main
    private int noeudRangMax;
    private int noeudRangInit;
    private int arreteRangMin; // Pour le slider de paramétrisation des nouvelles arrêtes créées à la main
    private int arreteRangMax;
    private int arreteRangInit;
    private int coefNoeudProcheMin; // Pour le slider de paramétrisation du coef de détection des noeuds proches
    private int coefNoeudProcheMax;
    private int coefNoeudProcheInit;
    private int coefLongNouvelleRouteMin; // Pour le slider de paramétrisation du coef de longueure des nouvelles routes
                                          // créées automatiquement
    private int coefLongNouvelleRouteMax;
    private int coefLongNouvelleRouteInit;
    private JSlider sliderNoeudRang, sliderArreteRang, sliderCoefNoeudProche, sliderCoefLongNouvelleRoute; // Les
                                                                                                           // sliders

    // CONSTRUCTEUR
    public Main() {
        createModel();
        createView();
        placeComponents();
        createController();
    }

    // COMMANDES
    public void display() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Impl mentation d'un noeud du graphe
    public class Carrefour {
        int id;
        int hierarchie;

        public Carrefour(int idGiven, int hierarchieGiven) {
            id = idGiven;
            hierarchie = hierarchieGiven;
        }

        public String toString() {
            String nomCarrefour;
            switch (hierarchie) {
                case 0:
                    nomCarrefour = "                            Carrefour " + id;
                    break;
                case 1:
                    nomCarrefour = "                                     Rond point " + id;
                    break;
                default:
                    nomCarrefour = "                            Carrefour " + id;
            }
            return nomCarrefour; // Nom des sommets
        }
    }

    // Impl mentation d'une arrete d'un graphe
    public class Route {
        int id;
        int hierarchie; // 0 == route mineur, 1 == route majeur

        public Route(int hierarchieGiven) {
            id = edgeCount++;
            hierarchie = hierarchieGiven;
        }

        public String toString() {
            String nomRoute;
            switch (hierarchie) {
                case 0:
                    nomRoute = "Rue " + id;
                    break;
                case 1:
                    nomRoute = "Avenue " + id;
                    break;
                default:
                    nomRoute = "Rue " + id;
            }
            return nomRoute; // Nom des arretes
        }
    }

    // Cr ation du graph :
    private void createModel() {
        listNodesSelected = new LinkedList<Carrefour>();
        listArretesSelected = new LinkedList<Route>();
        listNodesaAjouter = new LinkedList<Carrefour>();
        listTousNodes = new LinkedList<Carrefour>();
        coefLongNouvelleRoute = 0.6f; // Sera multiplié à la longeure moyenne des arrtes sélectionnées
        coefNoeudProche = 0.5f; // Sera multiplié à la longeure moyenne des arrtes sélectionnées
        noeudRangMin = 0; // Pour le slider de paramétrisation des nouveaux noeuds créés à la main
        noeudRangMax = 1;
        noeudRangInit = 0;
        arreteRangMin = 0; // Pour le slider de paramétrisation des nouvelles arrêtes créées à la main
        arreteRangMax = 1;
        arreteRangInit = 0;
        coefNoeudProcheMin = 1; // Pour le slider de paramétrisation du coef de détection des noeuds proches
        coefNoeudProcheMax = 100;
        coefNoeudProcheInit = 50;
        coefLongNouvelleRouteMin = 1; // Pour le slider de paramétrisation du coef de longueure des nouvelles routes
                                      // créées automatiquement
        coefLongNouvelleRouteMax = 100;
        coefLongNouvelleRouteInit = 60;
        noeudRang = 0;
        arreteRang = 0;

        // Création du graph :
        creationGraphLimogesVide();
        // creationGraphLimogesComplet();

        // Placement des noeuds :
        placerLesNoeudsLimogesVide();
        // placerLesNoeudsLimogesComplet();

        // Le viewer du graph :
        afficherLeGraphLimogesVide();
        // afficherLeGraphLimogesComplet();
    }

    private void createView() {
        frame = new JFrame("Reconstruction d'environnements urbains - Godé Vincent");
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        fermer = new JMenuItem("Fermer", new ImageIcon("images/fermer.jpg"));
        aide = new JMenuItem("Aide", new ImageIcon("images/aide.jpg"));
        reconstruire = new JButton("Reconstruire");
        ajouter = new JButton("Ajouter");
        deplacer = new JButton("Deplacer");
        supprimer = new JButton("Supprimer");

       /* sliderNoeudRang = new JSlider(noeudRangMin, noeudRangMax, noeudRangInit);
        sliderNoeudRang.setMinorTickSpacing(0);
        sliderNoeudRang.setMajorTickSpacing(1);
        sliderNoeudRang.setPaintTicks(true);
        sliderNoeudRang.setPaintLabels(true);
        sliderNoeudRang.setBorder(BorderFactory.createTitledBorder("Rang carrefour : "));
        sliderNoeudRang.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
        sliderArreteRang = new JSlider(arreteRangMin, arreteRangMax, arreteRangInit);
        sliderArreteRang.setMinorTickSpacing(0);
        sliderArreteRang.setMajorTickSpacing(1);
        sliderArreteRang.setPaintTicks(true);
        sliderArreteRang.setPaintLabels(true);
        sliderArreteRang.setBorder(BorderFactory.createTitledBorder("Rang route : "));
        sliderArreteRang.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
        sliderCoefNoeudProche = new JSlider(coefNoeudProcheMin, coefNoeudProcheMax, coefNoeudProcheInit);
        sliderCoefNoeudProche.setMinorTickSpacing(5);
        sliderCoefNoeudProche.setPaintTicks(true);
        sliderCoefNoeudProche.setPaintLabels(true);
        sliderCoefNoeudProche.setBorder(BorderFactory.createTitledBorder("Distance de recherche : "));
        sliderCoefNoeudProche.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
        sliderCoefLongNouvelleRoute = new JSlider(coefLongNouvelleRouteMin, coefLongNouvelleRouteMax,
                coefLongNouvelleRouteInit);
        sliderCoefLongNouvelleRoute.setMinorTickSpacing(5);
        sliderCoefLongNouvelleRoute.setPaintTicks(true);
        sliderCoefLongNouvelleRoute.setPaintLabels(true);
        sliderCoefLongNouvelleRoute.setBorder(BorderFactory.createTitledBorder("Longueure route : "));
        sliderCoefLongNouvelleRoute.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70)); */
    }

    private void placeComponents() {
        JPanel menu1 = new JPanel();

        {
            menu1.setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
            menu1.setBorder(BorderFactory.createEtchedBorder());

            JLabel l1 = new JLabel("                ");
            menu1.add(l1);

           /* menu1.add(sliderNoeudRang);
            menu1.add(sliderArreteRang);*/

           /* JPanel menu1_1 = new JPanel(new GridLayout(3, 1));
            {
                JMenuBar menuBar = new JMenuBar();
                {
                    gm.getModeMenu().getItem(0).setText("Se déplacer/Zoomer");
                    gm.getModeMenu().getItem(1).setText("Sélectionner/Modifier");
                    gm.getModeMenu().getItem(2).setText("Créer/Supprimer");
                    JMenu modeMenu = gm.getModeMenu();
                    modeMenu.setText("S lectionner un outil");
                    modeMenu.setPreferredSize(new Dimension(156, 30));
                    menuBar.add(modeMenu);
                }
                menu1_1.add(menuBar);

                JLabel l2 = new JLabel(" ");
                menu1_1.add(l2);
                JLabel l3 = new JLabel(" ");
                menu1_1.add(l3);
            }
            menu1.add(menu1_1);*/

            JPanel menu1_3 = new JPanel(new GridLayout(2, 1));
            {
                JLabel l5 = new JLabel("                ");
                menu1_3.add(l5);
                JLabel l6 = new JLabel("                ");
                menu1_3.add(l6);
            }
            menu1.add(menu1_3);

           /* menu1.add(sliderCoefNoeudProche);
            menu1.add(sliderCoefLongNouvelleRoute);*/

            

            JPanel menu1_5 = new JPanel(new GridLayout(1, 1));
            {
                menu1_5.add(ajouter);
                menu1_5.setPreferredSize(new Dimension(156, 30));
            }
            menu1.add(menu1_5);

            JPanel menu1_6 = new JPanel(new GridLayout(1, 1));
            {
                menu1_6.add(deplacer);
                menu1_6.setPreferredSize(new Dimension(156, 30));
            }
            menu1.add(menu1_6);

            JPanel menu1_7 = new JPanel(new GridLayout(1, 1));
            {
                menu1_7.add(supprimer);
                menu1_7.setPreferredSize(new Dimension(156, 30));
            }
            menu1.add(menu1_7);


           /* JPanel menu1_2 = new JPanel(new GridLayout(1, 1));
            {
                menu1_2.add(reconstruire);
                menu1_2.setPreferredSize(new Dimension(156, 30));
            }
            menu1.add(menu1_2);*/


            /*JPanel menu1_4 = new JPanel(new GridLayout(1, 1));
            {
                ImageIcon imgIndications = new ImageIcon("images/indications.jpg");
                JLabel l4 = new JLabel(imgIndications);
                menu1_4.add(l4, BorderLayout.CENTER);
            }
            menu1.add(menu1_4);*/
        }
        frame.add(menu1, BorderLayout.EAST);
        frame.add(vv, BorderLayout.CENTER); // Ajout du graph
        menuBar = new JMenuBar();
        {
            JMenu menuFichier = new JMenu("Fichier");
            {
                menuFichier.add(fermer);
            }
            menuBar.add(menuFichier);
            JMenu menuEdition = new JMenu("A propos");
            {
                menuEdition.add(aide);
            }
            menuBar.add(menuEdition);
        }
        frame.setJMenuBar(menuBar);
    }

    private void createController() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fermer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        aide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Contact :\n" + "Lindsey TOUKAM : lind.toukam@gmail.com");
            }
        });


        reconstruire.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Initialisation de la liste des arretes connectants les noeuds sélectionnés :
                listArretesSelected.clear();
                float somme = 0.0f;
                int cptSomme = 0;
                for (Carrefour c1 : listNodesSelected) {
                    for (Carrefour c2 : listNodesSelected) {
                        if (layout.getGraph().isNeighbor(c1, c2)) {
                            if (!listArretesSelected.contains(layout.getGraph().findEdge(c1, c2))) {
                                listArretesSelected.add(layout.getGraph().findEdge(c1, c2));
                                somme += layout.transform(c1).distance(layout.transform(c2));
                                cptSomme += 1;
                            }
                        }
                    }
                }

                // Calcule de la longueure moyenne de ces arretes :
                float longMoyRoutes = somme / cptSomme;

                // Calcul du point barycentre :
                Point2D.Float baryPt = new Point2D.Float(0.0f, 0.0f);
                int cptBaryPt = 0;
                for (Carrefour c1 : listNodesSelected) { // Pour tout les noeuds voisins du noeud c
                    baryPt.x += (float) layout.transform(c1).getX();
                    baryPt.y += (float) layout.transform(c1).getY();
                    cptBaryPt += 1;
                }
                baryPt.x = (baryPt.x / cptBaryPt);
                baryPt.y = (baryPt.y / cptBaryPt);

                // Cr ation des nouveaux carrefours et routes :
                boolean premiereRoute = true;
                for (Carrefour c : listNodesSelected) { // Pour tous les noeuds sélectionnés
                    boolean noeudPresFound = false; // Un noeud pres a été trouvé ?
                    boolean intersectFound = false; // Une autre arrete a été intersecté ?
                    boolean intersectAvantPresFound = false; // Intersection avec noeud d'origine - noeuds pres trouv  ?
                    float ratio = (float) ((longMoyRoutes * coefLongNouvelleRoute)
                            / layout.transform(c).distance(baryPt));
                    Vector2d vectcFils = new Vector2d(baryPt.x - layout.transform(c).getX(),
                            baryPt.y - layout.transform(c).getY()); // Direction de la nouvelle route

                    // Tant que l'on a pas trouvé de noeud pris ou d'arretes à intersecter :
                    while (!noeudPresFound && !intersectFound) {
                        // Calcul de la position du noeud fils par raprochement du barycentre
                        Point2D.Float cFilsPt = new Point2D.Float(0.0f, 0.0f);
                        cFilsPt.x = (float) (layout.transform(c).getX() + vectcFils.x * ratio);
                        cFilsPt.y = (float) (layout.transform(c).getY() + vectcFils.y * ratio);

                        // Création nouveau noeud fils :
                        Carrefour cFils;
                        Route r;
                        if (premiereRoute) { // La première route est une route principale
                            r = new Route(1);
                        } else {
                            r = new Route(0);
                        }

                        // Si ce nouveau noeud cFils est "près" d'un autre, alors on les joindra :
                        Carrefour cPres = new Carrefour(g.getVertexCount() + 1, 0);
                        listTousNodes = (LinkedList<Carrefour>) listNodesSelected.clone();
                        listTousNodes.addAll(listNodesaAjouter);
                        for (Carrefour c2 : listTousNodes) {
                            if (!noeudPresFound && !c.equals(c2)) { // Si on a trouvé un noeud près, on s'arrete
                                if (!layout.getGraph().isNeighbor(c2, c)) { // Si c2 n'est pas un voisin du noeud
                                                                            // d'origine
                                    if (layout.transform(c2).distance(cFilsPt) < longMoyRoutes * coefNoeudProche) { // Si
                                                                                                                    // le
                                                                                                                    // noeud
                                                                                                                    // fils
                                                                                                                    // est
                                                                                                                    // près
                                                                                                                    // de
                                                                                                                    // c2
                                        // On verifis qu'il n'y a pas d'intersection avec c - c2
                                        Point2D.Float ptIntersect = new Point2D.Float(-1.0f, -1.0f);
                                        for (Route r1 : listArretesSelected) {
                                            if (layout.getGraph().getIncidentCount(r1) == 2 && // Si l'arrete a bien
                                                                                               // seulement deux noeuds
                                                                                               // connect s
                                                !layout.getGraph().isIncident(c2, r1) && // Et si l'arrete n'est pas jointe
                                                                                     // au noeud d'origine
                                                !noeudPresFound) { // Pour eviter un bug
                                                Iterator<Carrefour> it;
                                                it = layout.getGraph().getIncidentVertices(r1).iterator();
                                                Carrefour cExtremite1 = it.next();
                                                Carrefour cExtremite2 = it.next();
                                                ptIntersect.setLocation(
                                                        getIntersection((float) layout.transform(c2).getX(),
                                                                (float) layout.transform(c2).getY(), cFilsPt.x,
                                                                cFilsPt.y, (float) layout.transform(cExtremite1).getX(),
                                                                (float) layout.transform(cExtremite1).getY(),
                                                                (float) layout.transform(cExtremite2).getX(),
                                                                (float) layout.transform(cExtremite2).getY()));
                                                if (!(ptIntersect.x == -1.0f && ptIntersect.y == -1.0f)) { // Si il y a
                                                                                                           // eu
                                                                                                           // intersection
                                                    cFilsPt.setLocation(ptIntersect); // Alors le nouveau noeud fils est
                                                                                      // placé à l'intersection
                                                    intersectAvantPresFound = true;
                                                }
                                            }
                                        }

                                        if (layout.getGraph().getNeighborCount(c2) >= 4) { // Si le carrefour à plus de
                                                                                           // 5 arretes, alors c'est un
                                                                                           // gros carrefour
                                            c2.hierarchie = 1;
                                        }
                                        cPres = c2;
                                        noeudPresFound = true;
                                    }
                                }
                            }
                        }

                        // Si la route c - cFils intersecte une arrete alors on posera le noeud fils à
                        // l'intersection :
                        Point2D.Float ptIntersect = new Point2D.Float(-1.0f, -1.0f);
                        for (Route r1 : listArretesSelected) {
                            if (layout.getGraph().getIncidentCount(r1) == 2 && // Si l'arrete a bien seulement deux
                                                                               // noeuds connectés
                            !layout.getGraph().isIncident(c, r1) && // Et si l'arrete n'est pas jointe au noeud
                                                                    // d'origine
                            !noeudPresFound) { // Pour eviter un bug
                                Iterator<Carrefour> it;
                                it = layout.getGraph().getIncidentVertices(r1).iterator();
                                Carrefour cExtremite1 = it.next();
                                Carrefour cExtremite2 = it.next();
                                ptIntersect.setLocation(getIntersection((float) layout.transform(c).getX(),
                                        (float) layout.transform(c).getY(), cFilsPt.x, cFilsPt.y,
                                        (float) layout.transform(cExtremite1).getX(),
                                        (float) layout.transform(cExtremite1).getY(),
                                        (float) layout.transform(cExtremite2).getX(),
                                        (float) layout.transform(cExtremite2).getY()));
                                if (!(ptIntersect.x == -1.0f && ptIntersect.y == -1.0f)) { // Si il y a eu intersection
                                    cFilsPt.setLocation(ptIntersect); // Alors le nouveau noeud fils est placé à
                                                                      // l'intersection
                                    intersectFound = true;
                                }
                            }
                        }

                        // Création de l'arrete entre le noeud d'origine et le noeud fils :
                        if (noeudPresFound && !intersectAvantPresFound) {
                            g.addEdge(r, c, cPres);
                            listArretesSelected.add(r);
                            System.out.print(c.toString().replaceAll(" ", "") + " --> " + r.toString() + " <-- (près) "
                                    + cPres.toString().replaceAll(" ", "") + ", ");
                        } else {
                            // Création et placement du nouveau noeud fils :
                            cFils = new Carrefour(g.getVertexCount() + 1, 0);
                            g.addVertex(cFils);
                            placerNoeud(layout, cFils, cFilsPt.x, cFilsPt.y);
                            listNodesaAjouter.add(cFils);
                            g.addEdge(r, c, cFils);
                            listArretesSelected.add(r);
                            System.out.print(c.toString().replaceAll(" ", "") + " --> " + r.toString() + " <-- "
                                    + cFils.toString().replaceAll(" ", "") + ", ");
                            // On avance pour construire le chemin :
                            c = cFils;
                        }
                    }
                    premiereRoute = false;
                    System.out.println("fin"); // Fin de la construction du chemin partant du 1er noeud
                }
            }
        });

       /* sliderNoeudRang.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                noeudRang = (int) source.getValue();
            }
        });

        sliderArreteRang.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                arreteRang = (int) source.getValue();
            }
        });

        sliderCoefNoeudProche.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                coefNoeudProche = (float) ((int) source.getValue() / 100.0f);
            }
        });

        sliderCoefLongNouvelleRoute.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                coefLongNouvelleRoute = (float) ((int) source.getValue() / 100.0f);
            }
        });*/
    }

    // Retourne le point d'intersection eventuel entre le segment p0-p1 et le
    // segment p2-p3 :
    public Point2D.Float getIntersection(float p0_x, float p0_y, float p1_x, float p1_y, float p2_x, float p2_y,
            float p3_x, float p3_y) {
        Point2D.Float pt = new Point2D.Float();
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;
        s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;
        s2_y = p3_y - p2_y;
        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) { // Collision
            pt.x = p0_x + (t * s1_x);
            pt.y = p0_y + (t * s1_y);
        } else { // Pas de collision
            pt.x = -1;
            pt.y = -1;
        }
        return pt;
    }

    // Creation du graph de Limoges vide :
    public void creationGraphLimogesVide() {
        g = new SparseMultigraph<Carrefour, Route>();
      /*  n1 = new Carrefour(1, 1);
        n2 = new Carrefour(2, 1);
        n3 = new Carrefour(3, 1);
        n4 = new Carrefour(4, 1);
        n5 = new Carrefour(5, 1);
        n6 = new Carrefour(6, 1);
        n7 = new Carrefour(7, 1);
        n8 = new Carrefour(8, 1);
        n9 = new Carrefour(9, 1);
        n10 = new Carrefour(10, 1);
        n11 = new Carrefour(11, 1);
        n12 = new Carrefour(12, 0);
        n14 = new Carrefour(14, 0);
        n15 = new Carrefour(15, 0);
        n16 = new Carrefour(16, 0);
        n17 = new Carrefour(17, 0);
        n18 = new Carrefour(18, 0);
        n19 = new Carrefour(19, 0);
        n20 = new Carrefour(20, 0);
        n21 = new Carrefour(21, 0);
        g.addVertex(n1);
        g.addVertex(n2);
        g.addVertex(n3);
        g.addVertex(n4);
        g.addVertex(n5);
        g.addVertex(n6);
        g.addVertex(n7);
        g.addVertex(n8);
        g.addVertex(n9);
        g.addVertex(n10);
        g.addVertex(n11);
        g.addVertex(n12);
        g.addVertex(n14);
        g.addVertex(n15);
        g.addVertex(n16);
        g.addVertex(n17);
        g.addVertex(n18);
        g.addVertex(n19);
        g.addVertex(n20);
        g.addVertex(n21);
        g.addEdge(new Route(1), n1, n2);
        g.addEdge(new Route(1), n2, n3);
        g.addEdge(new Route(1), n3, n4);
        g.addEdge(new Route(1), n4, n16);
        g.addEdge(new Route(1), n16, n17);
        g.addEdge(new Route(1), n17, n5);
        g.addEdge(new Route(1), n5, n18);
        g.addEdge(new Route(1), n18, n6);
        g.addEdge(new Route(1), n6, n7);
        g.addEdge(new Route(1), n7, n8);
        g.addEdge(new Route(1), n8, n19);
        g.addEdge(new Route(1), n19, n20);
        g.addEdge(new Route(1), n20, n9);
        g.addEdge(new Route(1), n9, n10);
        g.addEdge(new Route(1), n10, n11);
        g.addEdge(new Route(1), n11, n21);
        g.addEdge(new Route(1), n21, n12);
        g.addEdge(new Route(1), n12, n15);
        g.addEdge(new Route(1), n15, n14);
        g.addEdge(new Route(1), n14, n1);*/
        nodeCount = 21;
        edgeCount = 21;

        // Ce qui se passe lorsque l'on demande à créer un noeud ou une arrete :
        vertexFactory = new Factory<Carrefour>() {
            public Carrefour create() {
                Carrefour c;
                if (noeudRang == 0) {
                    c = new Carrefour(nodeCount++, 0); // Un petit carrefour
                } else {
                    c = new Carrefour(nodeCount++, 1);
                }
                return c;
            }
        };
        edgeFactory = new Factory<Route>() {
            public Route create() {
                Route r;
                if (arreteRang == 0) {
                    r = new Route(0); // Une petite route
                } else {
                    r = new Route(1);
                }
                return r;
            }
        };
    }

    public void placerNoeud(Layout<Carrefour, Route> layout, Carrefour noeud, float x, float y) {
        layout.setLocation(noeud, new Point2D.Float(x, y));
        layout.lock(noeud, true);
    }

    public void placerLesNoeudsLimogesVide() {
        decalX = 0.0f;
        decalY = 30.0f;
        scale = 2.8f;
        layout = new StaticLayout<Carrefour, Route>(g);
      /*  placerNoeud(layout, n1, 29.175f * scale + decalX, 90.328f * scale + decalY);
        placerNoeud(layout, n2, 69.225f * scale + decalX, 35.190f * scale + decalY);
        placerNoeud(layout, n3, 127.759f * scale + decalX, 19.716f * scale + decalY);
        placerNoeud(layout, n4, 169.625f * scale + decalX, 32.996f * scale + decalY);
        placerNoeud(layout, n5, 278.247f * scale + decalX, 71.160f * scale + decalY);
        placerNoeud(layout, n6, 294.988f * scale + decalX, 120.502f * scale + decalY);
        placerNoeud(layout, n7, 282.643f * scale + decalX, 138.058f * scale + decalY);
        placerNoeud(layout, n8, 259.327f * scale + decalX, 172.896f * scale + decalY);
        placerNoeud(layout, n9, 209.676f * scale + decalX, 212.671f * scale + decalY);
        placerNoeud(layout, n10, 144.115f * scale + decalX, 201.150f * scale + decalY);
        placerNoeud(layout, n11, 81.572f * scale + decalX, 172.347f * scale + decalY);
        placerNoeud(layout, n12, 46.186f * scale + decalX, 132.846f * scale + decalY);
        placerNoeud(layout, n14, 35.014f * scale + decalX, 108.205f * scale + decalY);
        placerNoeud(layout, n15, 24.375f * scale + decalX, 126.725f * scale + decalY);
        placerNoeud(layout, n16, 197.557f * scale + decalX, 42.005f * scale + decalY);
        placerNoeud(layout, n17, 245.040f * scale + decalX, 58.358f * scale + decalY);
        placerNoeud(layout, n18, 287.990f * scale + decalX, 93.625f * scale + decalY);
        placerNoeud(layout, n19, 246.222f * scale + decalX, 188.590f * scale + decalY);
        placerNoeud(layout, n20, 225.534f * scale + decalX, 202.184f * scale + decalY);
        placerNoeud(layout, n21, 69.690f * scale + decalX, 161.007f * scale + decalY);*/
    }

    // Creation du graph de Limoges complet :
    public void creationGraphLimogesComplet() {
        g = new SparseMultigraph<Carrefour, Route>();
      /*  n1 = new Carrefour(1, 1);
        n2 = new Carrefour(2, 1);
        n3 = new Carrefour(3, 1);
        n4 = new Carrefour(4, 1);
        n5 = new Carrefour(5, 1);
        n6 = new Carrefour(6, 1);
        n7 = new Carrefour(7, 1);
        n8 = new Carrefour(8, 1);
        n9 = new Carrefour(9, 1);
        n10 = new Carrefour(10, 1);
        n11 = new Carrefour(11, 1);
        n12 = new Carrefour(12, 0);
        n13 = new Carrefour(13, 0);
        n14 = new Carrefour(14, 0);
        n15 = new Carrefour(15, 0);
        n16 = new Carrefour(16, 0);
        n17 = new Carrefour(17, 0);
        n18 = new Carrefour(18, 0);
        n19 = new Carrefour(19, 0);
        n20 = new Carrefour(20, 0);
        n21 = new Carrefour(21, 0);
        n22 = new Carrefour(22, 0);
        n23 = new Carrefour(23, 0);
        n24 = new Carrefour(24, 0);
        n25 = new Carrefour(25, 0);
        n26 = new Carrefour(26, 0);
        n27 = new Carrefour(27, 0);
        n28 = new Carrefour(28, 0);
        n29 = new Carrefour(29, 0);
        n30 = new Carrefour(30, 0);
        n31 = new Carrefour(31, 0);
        n32 = new Carrefour(32, 0);
        n33 = new Carrefour(33, 0);
        n34 = new Carrefour(34, 0);
        n35 = new Carrefour(35, 0);
        n36 = new Carrefour(36, 1);
        n37 = new Carrefour(37, 0);
        n38 = new Carrefour(38, 1);
        n39 = new Carrefour(39, 0);
        n40 = new Carrefour(40, 0);
        n41 = new Carrefour(41, 0);
        n42 = new Carrefour(42, 0);
        n43 = new Carrefour(43, 0);
        n44 = new Carrefour(44, 0);
        n45 = new Carrefour(45, 0);
        n46 = new Carrefour(46, 0);
        n47 = new Carrefour(47, 0);
        n48 = new Carrefour(48, 0);
        n49 = new Carrefour(49, 0);
        n50 = new Carrefour(50, 0);
        n51 = new Carrefour(51, 0);
        n52 = new Carrefour(52, 1);
        n53 = new Carrefour(53, 0);
        n54 = new Carrefour(54, 0);
        n55 = new Carrefour(55, 1);
        n56 = new Carrefour(56, 0);
        n57 = new Carrefour(57, 0);
        n58 = new Carrefour(58, 1);
        n59 = new Carrefour(59, 0);
        n60 = new Carrefour(60, 0);
        n61 = new Carrefour(61, 0);
        n62 = new Carrefour(62, 0);
        n63 = new Carrefour(63, 0);
        n64 = new Carrefour(64, 1);
        n65 = new Carrefour(65, 1);
        n66 = new Carrefour(66, 0);
        n67 = new Carrefour(67, 0);
        n68 = new Carrefour(68, 0);
        n69 = new Carrefour(69, 0);
        n70 = new Carrefour(70, 0);
        n71 = new Carrefour(71, 0);
        n72 = new Carrefour(72, 0);
        n73 = new Carrefour(73, 0);
        n74 = new Carrefour(74, 0);
        n75 = new Carrefour(75, 0);
        n76 = new Carrefour(76, 0); 
        g.addVertex(n1);
        g.addVertex(n2);
        g.addVertex(n3);
        g.addVertex(n4);
        g.addVertex(n5);
        g.addVertex(n6);
        g.addVertex(n7);
        g.addVertex(n8);
        g.addVertex(n9);
        g.addVertex(n10);
        g.addVertex(n11);
        g.addVertex(n12);
        g.addVertex(n13);
        g.addVertex(n14);
        g.addVertex(n15);
        g.addVertex(n16);
        g.addVertex(n17);
        g.addVertex(n18);
        g.addVertex(n19);
        g.addVertex(n20);
        g.addVertex(n21);
        g.addVertex(n22);
        g.addVertex(n23);
        g.addVertex(n24);
        g.addVertex(n25);
        g.addVertex(n26);
        g.addVertex(n27);
        g.addVertex(n28);
        g.addVertex(n29);
        g.addVertex(n30);
        g.addVertex(n31);
        g.addVertex(n32);
        g.addVertex(n33);
        g.addVertex(n34);
        g.addVertex(n35);
        g.addVertex(n36);
        g.addVertex(n37);
        g.addVertex(n38);
        g.addVertex(n39);
        g.addVertex(n40);
        g.addVertex(n41);
        g.addVertex(n42);
        g.addVertex(n43);
        g.addVertex(n44);
        g.addVertex(n45);
        g.addVertex(n46);
        g.addVertex(n47);
        g.addVertex(n48);
        g.addVertex(n49);
        g.addVertex(n50);
        g.addVertex(n51);
        g.addVertex(n52);
        g.addVertex(n53);
        g.addVertex(n54);
        g.addVertex(n55);
        g.addVertex(n56);
        g.addVertex(n57);
        g.addVertex(n58);
        g.addVertex(n59);
        g.addVertex(n60);
        g.addVertex(n61);
        g.addVertex(n62);
        g.addVertex(n63);
        g.addVertex(n64);
        g.addVertex(n65);
        g.addVertex(n66);
        g.addVertex(n67);
        g.addVertex(n68);
        g.addVertex(n69);
        g.addVertex(n70);
        g.addVertex(n71);
        g.addVertex(n72);
        g.addVertex(n73);
        g.addVertex(n74);
        g.addVertex(n75);
        g.addVertex(n76);
        g.addEdge(new Route(1), n1, n2);
        g.addEdge(new Route(1), n2, n3);
        g.addEdge(new Route(1), n3, n4);
        g.addEdge(new Route(1), n4, n16);
        g.addEdge(new Route(1), n16, n17);
        g.addEdge(new Route(1), n17, n5);
        g.addEdge(new Route(1), n5, n18);
        g.addEdge(new Route(1), n18, n6);
        g.addEdge(new Route(1), n6, n7);
        g.addEdge(new Route(1), n7, n8);
        g.addEdge(new Route(1), n8, n19);
        g.addEdge(new Route(1), n19, n20);
        g.addEdge(new Route(1), n20, n9);
        g.addEdge(new Route(1), n9, n10);
        g.addEdge(new Route(1), n10, n11);
        g.addEdge(new Route(1), n11, n21);
        g.addEdge(new Route(1), n21, n12);
        g.addEdge(new Route(1), n12, n15);
        g.addEdge(new Route(1), n12, n72);
        g.addEdge(new Route(1), n72, n13);
        g.addEdge(new Route(1), n13, n14);
        g.addEdge(new Route(1), n15, n14);
        g.addEdge(new Route(1), n14, n1);
        g.addEdge(new Route(1), n1, n27);
        g.addEdge(new Route(1), n27, n28);
        g.addEdge(new Route(0), n28, n29);
        g.addEdge(new Route(1), n28, n50);
        g.addEdge(new Route(0), n28, n25);
        g.addEdge(new Route(0), n29, n2);
        g.addEdge(new Route(0), n29, n51);
        g.addEdge(new Route(0), n51, n30);
        g.addEdge(new Route(0), n51, n50);
        g.addEdge(new Route(0), n30, n2);
        g.addEdge(new Route(0), n30, n31);
        g.addEdge(new Route(1), n31, n3);
        g.addEdge(new Route(0), n31, n4);
        g.addEdge(new Route(0), n31, n32);
        g.addEdge(new Route(1), n31, n52);
        g.addEdge(new Route(1), n32, n4);
        g.addEdge(new Route(1), n32, n52);
        g.addEdge(new Route(0), n32, n33);
        g.addEdge(new Route(0), n33, n16);
        g.addEdge(new Route(0), n33, n53);
        g.addEdge(new Route(0), n33, n34);
        g.addEdge(new Route(0), n34, n17);
        g.addEdge(new Route(0), n34, n35);
        g.addEdge(new Route(0), n35, n55);
        g.addEdge(new Route(0), n35, n5);
        g.addEdge(new Route(0), n35, n36);
        g.addEdge(new Route(0), n36, n55);
        g.addEdge(new Route(0), n36, n18);
        g.addEdge(new Route(0), n36, n58);
        g.addEdge(new Route(0), n36, n37);
        g.addEdge(new Route(0), n37, n38);
        g.addEdge(new Route(0), n37, n7);
        g.addEdge(new Route(0), n38, n7);
        g.addEdge(new Route(1), n38, n58);
        g.addEdge(new Route(0), n38, n59);
        g.addEdge(new Route(1), n38, n8);
        g.addEdge(new Route(0), n39, n19);
        g.addEdge(new Route(0), n39, n59);
        g.addEdge(new Route(0), n39, n60);
        g.addEdge(new Route(0), n39, n40);
        g.addEdge(new Route(0), n40, n20);
        g.addEdge(new Route(0), n40, n41);
        g.addEdge(new Route(0), n40, n60);
        g.addEdge(new Route(0), n41, n42);
        g.addEdge(new Route(0), n41, n61);
        g.addEdge(new Route(0), n42, n62);
        g.addEdge(new Route(0), n62, n61);
        g.addEdge(new Route(0), n61, n60);
        g.addEdge(new Route(0), n60, n64);
        g.addEdge(new Route(0), n61, n64);
        g.addEdge(new Route(0), n62, n63);
        g.addEdge(new Route(0), n63, n64);
        g.addEdge(new Route(0), n63, n43);
        g.addEdge(new Route(0), n63, n44);
        g.addEdge(new Route(0), n43, n10);
        g.addEdge(new Route(0), n43, n44);
        g.addEdge(new Route(0), n44, n45);
        g.addEdge(new Route(0), n45, n11);
        g.addEdge(new Route(0), n45, n22);
        g.addEdge(new Route(0), n45, n46);
        g.addEdge(new Route(0), n45, n73);
        g.addEdge(new Route(0), n22, n21);
        g.addEdge(new Route(0), n22, n23);
        g.addEdge(new Route(0), n22, n12);
        g.addEdge(new Route(0), n23, n46);
        g.addEdge(new Route(0), n23, n24);
        g.addEdge(new Route(0), n23, n72);
        g.addEdge(new Route(0), n24, n13);
        g.addEdge(new Route(0), n24, n46);
        g.addEdge(new Route(0), n24, n25);
        g.addEdge(new Route(0), n25, n26);
        g.addEdge(new Route(0), n25, n28);
        g.addEdge(new Route(0), n26, n13);
        g.addEdge(new Route(0), n26, n27);
        g.addEdge(new Route(0), n46, n47);
        g.addEdge(new Route(0), n46, n65);
        g.addEdge(new Route(0), n47, n66);
        g.addEdge(new Route(0), n47, n67);
        g.addEdge(new Route(0), n47, n48);
        g.addEdge(new Route(0), n48, n67);
        g.addEdge(new Route(0), n48, n49);
        g.addEdge(new Route(0), n49, n25);
        g.addEdge(new Route(0), n49, n69);
        g.addEdge(new Route(0), n49, n50);
        g.addEdge(new Route(0), n49, n68);
        g.addEdge(new Route(1), n50, n52);
        g.addEdge(new Route(1), n52, n53);
        g.addEdge(new Route(0), n53, n68);
        g.addEdge(new Route(1), n53, n54);
        g.addEdge(new Route(0), n54, n69);
        g.addEdge(new Route(1), n54, n76);
        g.addEdge(new Route(0), n54, n55);
        g.addEdge(new Route(0), n76, n66);
        g.addEdge(new Route(1), n76, n56);
        g.addEdge(new Route(0), n56, n55);
        g.addEdge(new Route(1), n56, n57);
        g.addEdge(new Route(0), n57, n36);
        g.addEdge(new Route(0), n57, n75);
        g.addEdge(new Route(1), n57, n58);
        g.addEdge(new Route(0), n75, n71);
        g.addEdge(new Route(0), n71, n65);
        g.addEdge(new Route(0), n30, n52);
        g.addEdge(new Route(0), n67, n69);
        g.addEdge(new Route(0), n66, n65);
        g.addEdge(new Route(0), n65, n45);
        g.addEdge(new Route(1), n65, n74);
        g.addEdge(new Route(1), n74, n64);
        g.addEdge(new Route(0), n74, n73);
        g.addEdge(new Route(0), n64, n70);
        g.addEdge(new Route(0), n70, n59);
        g.addEdge(new Route(0), n59, n58);
        g.addEdge(new Route(0), n70, n71);*/
        nodeCount = 77;
        edgeCount = 135;

        // Ce qui se passe lorsque l'on demande à créer un noeud ou une arrete :
        vertexFactory = new Factory<Carrefour>() {
            public Carrefour create() {
                Carrefour c;
                if (noeudRang == 0) {
                    c = new Carrefour(nodeCount++, 0); // Un petit carrefour
                } else {
                    c = new Carrefour(nodeCount++, 1);
                }
                return c;
            }
        };
        edgeFactory = new Factory<Route>() {
            public Route create() {
                Route r;
                if (arreteRang == 0) {
                    r = new Route(0); // Une petite route
                } else {
                    r = new Route(1);
                }
                return r;
            }
        };
    }

    public void placerLesNoeudsLimogesComplet() {
        decalX = 0.0f;
        decalY = 30.0f;
        scale = 2.8f;
        layout = new StaticLayout<Carrefour, Route>(g);
        placerNoeud(layout, n1, 29.175f * scale + decalX, 90.328f * scale + decalY);
        placerNoeud(layout, n2, 69.225f * scale + decalX, 35.190f * scale + decalY);
        placerNoeud(layout, n3, 127.759f * scale + decalX, 19.716f * scale + decalY);
        placerNoeud(layout, n4, 169.625f * scale + decalX, 32.996f * scale + decalY);
        placerNoeud(layout, n5, 278.247f * scale + decalX, 71.160f * scale + decalY);
        placerNoeud(layout, n6, 294.988f * scale + decalX, 120.502f * scale + decalY);
        placerNoeud(layout, n7, 282.643f * scale + decalX, 138.058f * scale + decalY);
        placerNoeud(layout, n8, 259.327f * scale + decalX, 172.896f * scale + decalY);
        placerNoeud(layout, n9, 209.676f * scale + decalX, 212.671f * scale + decalY);
        placerNoeud(layout, n10, 144.115f * scale + decalX, 201.150f * scale + decalY);
        placerNoeud(layout, n11, 81.572f * scale + decalX, 172.347f * scale + decalY);
        placerNoeud(layout, n12, 46.186f * scale + decalX, 132.846f * scale + decalY);
        placerNoeud(layout, n13, 56.884f * scale + decalX, 113.918f * scale + decalY);
        placerNoeud(layout, n14, 35.014f * scale + decalX, 108.205f * scale + decalY);
        placerNoeud(layout, n15, 24.375f * scale + decalX, 126.725f * scale + decalY);
        placerNoeud(layout, n16, 197.557f * scale + decalX, 42.005f * scale + decalY);
        placerNoeud(layout, n17, 245.040f * scale + decalX, 58.358f * scale + decalY);
        placerNoeud(layout, n18, 287.990f * scale + decalX, 93.625f * scale + decalY);
        placerNoeud(layout, n19, 246.222f * scale + decalX, 188.590f * scale + decalY);
        placerNoeud(layout, n20, 225.534f * scale + decalX, 202.184f * scale + decalY);
        placerNoeud(layout, n21, 69.690f * scale + decalX, 161.007f * scale + decalY);
        placerNoeud(layout, n22, 81.512f * scale + decalX, 144.457f * scale + decalY);
        placerNoeud(layout, n23, 89.589f * scale + decalX, 131.847f * scale + decalY);
        placerNoeud(layout, n24, 95.106f * scale + decalX, 119.632f * scale + decalY);
        placerNoeud(layout, n25, 87.225f * scale + decalX, 101.900f * scale + decalY);
        placerNoeud(layout, n26, 68.837f * scale + decalX, 107.811f * scale + decalY);
        placerNoeud(layout, n27, 49.628f * scale + decalX, 87.781f * scale + decalY);
        placerNoeud(layout, n28, 76.061f * scale + decalX, 84.333f * scale + decalY);
        placerNoeud(layout, n29, 73.762f * scale + decalX, 57.571f * scale + decalY);
        placerNoeud(layout, n30, 111.360f * scale + decalX, 52.318f * scale + decalY);
        placerNoeud(layout, n31, 141.569f * scale + decalX, 43.780f * scale + decalY);
        placerNoeud(layout, n32, 161.270f * scale + decalX, 43.616f * scale + decalY);
        placerNoeud(layout, n33, 200.181f * scale + decalX, 58.064f * scale + decalY);
        placerNoeud(layout, n34, 236.301f * scale + decalX, 69.228f * scale + decalY);
        placerNoeud(layout, n35, 250.256f * scale + decalX, 79.900f * scale + decalY);
        placerNoeud(layout, n36, 239.993f * scale + decalX, 100.220f * scale + decalY);
        placerNoeud(layout, n37, 259.779f * scale + decalX, 121.930f * scale + decalY);
        placerNoeud(layout, n38, 231.088f * scale + decalX, 144.164f * scale + decalY);
        placerNoeud(layout, n39, 237.192f * scale + decalX, 178.082f * scale + decalY);
        placerNoeud(layout, n40, 215.848f * scale + decalX, 193.187f * scale + decalY);
        placerNoeud(layout, n41, 208.460f * scale + decalX, 197.620f * scale + decalY);
        placerNoeud(layout, n42, 201.401f * scale + decalX, 202.217f * scale + decalY);
        placerNoeud(layout, n43, 149.355f * scale + decalX, 191.545f * scale + decalY);
        placerNoeud(layout, n44, 128.669f * scale + decalX, 173.321f * scale + decalY);
        placerNoeud(layout, n45, 108.474f * scale + decalX, 154.604f * scale + decalY);
        placerNoeud(layout, n46, 111.266f * scale + decalX, 136.216f * scale + decalY);
        placerNoeud(layout, n47, 116.684f * scale + decalX, 118.485f * scale + decalY);
        placerNoeud(layout, n48, 119.967f * scale + decalX, 102.395f * scale + decalY);
        placerNoeud(layout, n49, 116.684f * scale + decalX, 93.693f * scale + decalY);
        placerNoeud(layout, n50, 111.430f * scale + decalX, 81.216f * scale + decalY);
        placerNoeud(layout, n51, 106.012f * scale + decalX, 65.783f * scale + decalY);
        placerNoeud(layout, n52, 148.774f * scale + decalX, 61.235f * scale + decalY);
        placerNoeud(layout, n53, 166.360f * scale + decalX, 79.407f * scale + decalY);
        placerNoeud(layout, n54, 179.002f * scale + decalX, 92.213f * scale + decalY);
        placerNoeud(layout, n55, 217.829f * scale + decalX, 85.772f * scale + decalY);
        placerNoeud(layout, n56, 192.465f * scale + decalX, 104.855f * scale + decalY);
        placerNoeud(layout, n57, 202.480f * scale + decalX, 114.870f * scale + decalY);
        placerNoeud(layout, n58, 214.998f * scale + decalX, 128.567f * scale + decalY);
        placerNoeud(layout, n59, 201.166f * scale + decalX, 149.677f * scale + decalY);
        placerNoeud(layout, n60, 198.211f * scale + decalX, 176.274f * scale + decalY);
        placerNoeud(layout, n61, 190.495f * scale + decalX, 180.871f * scale + decalY);
        placerNoeud(layout, n62, 181.957f * scale + decalX, 186.781f * scale + decalY);
        placerNoeud(layout, n63, 159.464f * scale + decalX, 174.632f * scale + decalY);
        placerNoeud(layout, n64, 161.683f * scale + decalX, 158.135f * scale + decalY);
        placerNoeud(layout, n65, 142.638f * scale + decalX, 143.194f * scale + decalY);
        placerNoeud(layout, n66, 134.908f * scale + decalX, 126.858f * scale + decalY);
        placerNoeud(layout, n67, 144.102f * scale + decalX, 108.798f * scale + decalY);
        placerNoeud(layout, n68, 140.654f * scale + decalX, 83.678f * scale + decalY);
        placerNoeud(layout, n69, 164.460f * scale + decalX, 94.514f * scale + decalY);
        placerNoeud(layout, n70, 177.873f * scale + decalX, 154.134f * scale + decalY);
        placerNoeud(layout, n71, 170.898f * scale + decalX, 128.393f * scale + decalY);
        placerNoeud(layout, n72, 52.257f * scale + decalX, 122.314f * scale + decalY);
        placerNoeud(layout, n73, 133.144f * scale + decalX, 156.215f * scale + decalY);
        placerNoeud(layout, n74, 151.878f * scale + decalX, 150.862f * scale + decalY);
        placerNoeud(layout, n75, 190.983f * scale + decalX, 119.637f * scale + decalY);
        placerNoeud(layout, n76, 187.266f * scale + decalX, 100.159f * scale + decalY);
    }

    public void afficherLeGraphLimogesComplet() {
        layout.setSize(new Dimension(900, 700));
        vv = new VisualizationViewer<Carrefour, Route>(layout); // Gestionnaire d'affichage
        vv.setPreferredSize(new Dimension(950, 730));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller()); // Apparence des noms des sommets
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller()); // Apparence des noms des arretes
        Transformer<Carrefour, Shape> vertexShape = new Transformer<Carrefour, Shape>() {
            @Override
            public Shape transform(Carrefour c) {
                Ellipse2D.Float forme;
                switch (c.hierarchie) {
                    case 0:
                        forme = new Ellipse2D.Float(-5, -5, 10, 10);
                        break;
                    case 1:
                        forme = new Ellipse2D.Float(-13, -13, 26, 26);
                        break;
                    default:
                        forme = new Ellipse2D.Float(-5, -5, 10, 10);
                }
                return forme;
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexShape); // Apparence des noeuds
        VertexLabelAsShapeRenderer<Carrefour, Route> vlasr = new VertexLabelAsShapeRenderer<Carrefour, Route>(
                vv.getRenderContext());
        vv.getRenderer().setVertexLabelRenderer(vlasr);

        vv.setBackground(Color.white); // Couleur du fond

        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Carrefour, Route>() { // Apparence des arretes
            private Shape ligne;

            @SuppressWarnings("unchecked")
            public Shape transform(Context<Graph<Carrefour, Route>, Route> context) {
                switch (context.element.hierarchie) {
                    case 0:
                        ligne = new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    case 1:
                        ligne = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 2.4f);
                        break;
                    default:
                        ligne = new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
                }
                return ligne;
            }
        });

        vv.getRenderContext().setLabelOffset(8);

        try {
            imgFond = ImageIO.read(new File("images/fond2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Personnalisation de l'affichage AVANT le reste :
        vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldXform = g2d.getTransform();
                AffineTransform lat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)
                        .getTransform();
                AffineTransform vat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)
                        .getTransform();
                AffineTransform at = new AffineTransform();
                at.concatenate(g2d.getTransform());
                at.concatenate(vat);
                at.concatenate(lat);
                g2d.setTransform(at);
                g2d.translate(-475, -130);
                g2d.scale(0.47f, 0.47f);
                // Option antialiasing :
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(imgFond, 0, 0, vv);
                g2d.setTransform(oldXform);
            }

            public boolean useTransform() {
                return false;
            }
        });

        // Colore joliement les noeuds :
        vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<Carrefour, Route>(Color.white, Color.red,
                Color.white, Color.blue, vv.getPickedVertexState(), false));

        // Personnalisation de l'affichage APRES le reste :
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {
            public boolean useTransform() {
                return true;
            }

            // Utilisation de cette méthode rafraichie régulierement pour mettre à jour la
            // liste des noeuds sélectionnés :
            public void paint(Graphics g) {
                for (Carrefour c : layout.getGraph().getVertices()) {
                    if (vv.getPickedVertexState().isPicked(c)) {
                        if (!listNodesSelected.contains(c)) {
                            listNodesSelected.add(c);
                        }
                    }
                    if (!vv.getPickedVertexState().isPicked(c)) {
                        if (listNodesSelected.contains(c)) {
                            listNodesSelected.remove(c);
                        }
                    }
                }
            }
        });

        // Gestionnaire des evenements de la souris :
        gm = new EditingModalGraphMouse(vv.getRenderContext(), vertexFactory, edgeFactory);
        vv.setGraphMouse(gm);
        PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
        JPopupMenu edgeMenu = new MesMouseMenus.EdgeMenu(frame);
        JPopupMenu vertexMenu = new MesMouseMenus.VertexMenu();
        myPlugin.setEdgePopup(edgeMenu);
        myPlugin.setVertexPopup(vertexMenu);
        gm.remove(gm.getPopupEditingPlugin());
        gm.add(myPlugin);
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Par defaut, on est en mode "d placer"
    }

    public void afficherLeGraphLimogesVide() {
        layout.setSize(new Dimension(900, 700));
        vv = new VisualizationViewer<Carrefour, Route>(layout); // Gestionnaire d'affichage
        vv.setPreferredSize(new Dimension(950, 730));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller()); // Apparence des noms des sommets
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller()); // Apparence des noms des arretes
        Transformer<Carrefour, Shape> vertexShape = new Transformer<Carrefour, Shape>() {
            @Override
            public Shape transform(Carrefour c) {
                Ellipse2D.Float forme;
                switch (c.hierarchie) {
                    case 0:
                        forme = new Ellipse2D.Float(-5, -5, 10, 10);
                        break;
                    case 1:
                        forme = new Ellipse2D.Float(-13, -13, 26, 26);
                        break;
                    default:
                        forme = new Ellipse2D.Float(-5, -5, 10, 10);
                }
                return forme;
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexShape); // Apparence des noeuds
        VertexLabelAsShapeRenderer<Carrefour, Route> vlasr = new VertexLabelAsShapeRenderer<Carrefour, Route>(
                vv.getRenderContext());
        vv.getRenderer().setVertexLabelRenderer(vlasr);

        vv.setBackground(Color.white); // Couleur du fond

        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Carrefour, Route>() { // Apparence des arretes
            private Shape ligne;

            @SuppressWarnings("unchecked")
            public Shape transform(Context<Graph<Carrefour, Route>, Route> context) {
                switch (context.element.hierarchie) {
                    case 0:
                        ligne = new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
                        break;
                    case 1:
                        ligne = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 2.4f);
                        break;
                    default:
                        ligne = new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
                }
                return ligne;
            }
        });

        vv.getRenderContext().setLabelOffset(8);

        try {
            imgFond = ImageIO.read(new File("images/fond2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Personnalisation de l'affichage AVANT le reste :
        vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldXform = g2d.getTransform();
                AffineTransform lat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)
                        .getTransform();
                AffineTransform vat = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)
                        .getTransform();
                AffineTransform at = new AffineTransform();
                at.concatenate(g2d.getTransform());
                at.concatenate(vat);
                at.concatenate(lat);
                g2d.setTransform(at);
                g2d.translate(-475, -130);
                g2d.scale(0.47f, 0.47f);
                // Option antialiasing :
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(imgFond, 0, 0, vv);
                g2d.setTransform(oldXform);
            }

            public boolean useTransform() {
                return false;
            }
        });

        // Colore joliement les noeuds :
        vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<Carrefour, Route>(Color.white, Color.red,
                Color.white, Color.blue, vv.getPickedVertexState(), false));

        // Personnalisation de l'affichage APRES le reste :
        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {
            public boolean useTransform() {
                return true;
            }

            // Utilisation de cette méthode rafraichie régulierement pour mettre à jour la
            // liste des noeuds sélectionnés :
            public void paint(Graphics g) {
                for (Carrefour c : layout.getGraph().getVertices()) {
                    if (vv.getPickedVertexState().isPicked(c)) {
                        if (!listNodesSelected.contains(c)) {
                            listNodesSelected.add(c);
                            // for (Carrefour c2 : listNodesSelected) {
                            // System.out.print(c2.toString().replaceAll(" ", "") + " (" +
                            // layout.transform(c).getX() + "," + layout.transform(c).getY() + ") ");
                            // }
                            // System.out.println(" ");
                        }
                    }
                    if (!vv.getPickedVertexState().isPicked(c)) {
                        if (listNodesSelected.contains(c)) {
                            listNodesSelected.remove(c);
                            // for (Carrefour c2 : listNodesSelected) {
                            // System.out.print(c2.toString().replaceAll(" ", "") + " (" +
                            // layout.transform(c).getX() + "," + layout.transform(c).getY() + ") ");
                            // }
                            // System.out.println(" ");
                        }
                    }
                }
            }
        });

        // Gestionnaire des evenements de la souris :
        gm = new EditingModalGraphMouse(vv.getRenderContext(), vertexFactory, edgeFactory);
        vv.setGraphMouse(gm);
        PopupVertexEdgeMenuMousePlugin myPlugin = new PopupVertexEdgeMenuMousePlugin();
        JPopupMenu edgeMenu = new MesMouseMenus.EdgeMenu(frame);
        JPopupMenu vertexMenu = new MesMouseMenus.VertexMenu();
        myPlugin.setEdgePopup(edgeMenu);
        myPlugin.setVertexPopup(vertexMenu);
        gm.remove(gm.getPopupEditingPlugin());
        gm.add(myPlugin);
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Par defaut, on est en mode "déplacer"
    }

    // POINT D'ENTREE
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().display();
            }
        });
    }
}
