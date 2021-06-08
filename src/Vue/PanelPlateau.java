package Vue;

import static Utile.Constante.*;

import Listener.EcouteurDeMouvementDeSouris;
import Listener.EcouteurDeSouris;
import Utile.ConfigurationPartie;
import Reseau.Reseau;
import Modele.Jeu;
import Patterns.Observateur;
import Utile.Utile;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe générant la fenêtre de jeu.
 */
public class PanelPlateau extends Panels implements Observateur {

    private Jeu jeu;
    private JeuGraphique jg;
    private Reseau netUser;
    private final ConfigurationPartie config;
    private JGamePanel jgame;

    private final Dimension taille_fenetre;

    private final Font lilyScriptOne;

    private ParametrePanel pp;
    private VictoirePanel victoire_panel;
    private JLabel jt;
    private Bouton on_off_ia;


    /**
     * Initialise la fenêtre de jeu et charge la police et les images en mémoire.
     *
     * @param _taille_fenetre taille de la fenêtre
     * @param config          classe de configuration de la partie
     */
    public PanelPlateau(Dimension _taille_fenetre, ConfigurationPartie config) {
        this.taille_fenetre = _taille_fenetre;
        this.config = config;
        this.lilyScriptOne = new Font(LILY_SCRIPT, Font.PLAIN, 40);
        initialiserPanel();
    }

    /**
     * Constructeur de PanelPlateau chargeant une partie déjà existante.
     *
     * @param _taille_fenetre taille de la fenêtre
     * @param lecteur        nom du fichier à charger
     */
    public PanelPlateau(Dimension _taille_fenetre, Scanner lecteur) {
        String[] param = lecteur.nextLine().split(" ");
        int ia1_mode = Integer.parseInt(param[0]);
        int ia2_mode = Integer.parseInt(param[1]);
        int index_start = Integer.parseInt(param[2]);
        boolean j1_blue = Boolean.parseBoolean(param[3]);

        ConfigurationPartie config = new ConfigurationPartie(ia1_mode, ia2_mode);
        config.setIndexJoueurCommence(index_start);
        config.setJoueur1Bleu(j1_blue);

        this.taille_fenetre = _taille_fenetre;
        this.config = config;
        this.lilyScriptOne = new Font(LILY_SCRIPT, Font.PLAIN, 40);
        initialiserPanel();

        jeu.charger(lecteur);
    }

    /**
     * Constructeur de PanelPlateau prenant en paramètre un netUser, donc soit un client ou un serveur.
     *
     * @param _taille_fenetre taille de la fenêtre
     * @param netUser         client ou serveur
     */
    public PanelPlateau(Dimension _taille_fenetre, Reseau netUser) {
        this(_taille_fenetre, new ConfigurationPartie(0, 0));
        this.netUser = netUser;
        jeu.setNetUser(netUser);
        if (netUser.getNumJoueur() == JOUEUR1) jt.setText("C'est au tour de " + netUser.getNomJoueur());
        else jt.setText("C'est au tour de " + netUser.getNomAdversaire());
    }

    /**
     * Ajoute tous les composants au panel.
     *
     * @see TopPanel
     * @see JGamePanel
     */
    public void initialiserPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // layered pane pour la superposition des panels
        JLayeredPane main_panel = new JLayeredPane();
        main_panel.setOpaque(false);
        main_panel.setLayout(new OverlayLayout(main_panel));

        // panel de base avec le jeu
        JPanel game = new JPanel();
        game.setOpaque(false);
        game.setLayout(new BoxLayout(game, BoxLayout.Y_AXIS));
        game.setMaximumSize(taille_fenetre);

        TopPanel tp = new TopPanel(0.20f);
        JGamePanel jgame = new JGamePanel(0.80f);
        game.add(tp);
        game.add(jgame);

        pp = new ParametrePanel();
        pp.setVisible(false);

        victoire_panel = new VictoirePanel();
        victoire_panel.setVisible(false);

        main_panel.add(game, JLayeredPane.DEFAULT_LAYER);
        main_panel.add(pp, JLayeredPane.POPUP_LAYER);
        main_panel.add(victoire_panel, JLayeredPane.POPUP_LAYER);
        add(main_panel);
    }

    /**
     * Classe gérant les actions "ECHAP" pour afficher les paramètres
     */
    private class ActionEchap extends AbstractAction {
        public ActionEchap() {
            super();
            putValue(SHORT_DESCRIPTION, "Afficher les paramètres");
            putValue(MNEMONIC_KEY, KeyEvent.VK_ESCAPE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!victoire_panel.isVisible()) pp.setVisible(!pp.isVisible());
        }
    }

    /**
     * Crée un JPanel modifié qui génère deux zones de boutons de 20% de la taille de la fenêtre.
     * Génère la grille de jeu.
     *
     * @see JeuGraphique
     * @see Jeu
     */
    private class JGamePanel extends JPanel {
        private int taille_margin;
        private float taille_h;

        /**
         * Constructeur pour JGamePanel. Rajoute des components au JPanel.
         */
        public JGamePanel(float _taille_h) {
            this.taille_h = _taille_h - 0.05f;
            setLayout(new GridBagLayout());


            JPanel container = new JPanel();

            container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
            container.setOpaque(false);
            setOpaque(false);
            setPreferredSize(new Dimension(taille_fenetre.width, (int) (taille_fenetre.height * taille_h)));

            JPanel parametres = new JPanel();

            Dimension size = new Dimension((int) (taille_fenetre.width * 0.2), (int) (taille_fenetre.height * taille_h));
            parametres.setOpaque(false);
            parametres.setPreferredSize(size);
            parametres.setMaximumSize(size);

            int bouton_height = (int) (size.height * 0.1);
            Dimension size_bouton = new Dimension((int) (bouton_height * RATIO_BOUTON_PETIT), bouton_height);
            Bouton bParametres = new Bouton(
                    CHEMIN_RESSOURCE + "/bouton/parametres.png",
                    CHEMIN_RESSOURCE + "/bouton/parametres_hover.png",
                    size_bouton
            );


            ActionEchap echap = new ActionEchap();
            PanelPlateau.this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ECHAP_KEY);
            PanelPlateau.this.getActionMap().put(ECHAP_KEY, echap);

            bParametres.addActionListener(echap);
            parametres.add(bParametres);

            jeu = new Jeu(PanelPlateau.this, config);
            jg = new JeuGraphique(jeu);
            jg.addMouseListener(new EcouteurDeSouris(jg, jeu, PanelPlateau.this));
            jg.addMouseMotionListener(new EcouteurDeMouvementDeSouris(jeu, jg, PanelPlateau.this));

            SidePanelRight side_panel = new SidePanelRight(size);
            side_panel.setMaximumSize(size);
            side_panel.setPreferredSize(size);

            // Calcul de la taille de la grille selon la taille de la fenêtre

            int taille_case = ((int) (taille_fenetre.height * taille_h)) / PLATEAU_LIGNES;

            jg.setPreferredSize(new Dimension(taille_case * PLATEAU_COLONNES, taille_case * PLATEAU_LIGNES));
            jg.setMaximumSize(new Dimension(taille_case * PLATEAU_COLONNES, taille_case * PLATEAU_LIGNES));

            int taille = taille_fenetre.width;

            // place de la grille
            taille -= taille_case * PLATEAU_COLONNES;

            // place des menus
            taille -= taille_fenetre.width * 0.4;

            taille_margin = taille / 4;

            addMargin(container);
            container.add(parametres);
            addMargin(container);
            container.add(jg);
            addMargin(container);
            container.add(side_panel);
            addMargin(container);
            add(container);
        }

        /**
         * Crée un JPanel servant de marge.
         */
        private void addMargin(JPanel c) {
            JPanel j = new JPanel();
            j.setOpaque(false);
            Dimension size = new Dimension(taille_margin, (int) (taille_fenetre.height * taille_h));
            j.setPreferredSize(size);
            j.setMaximumSize(size);
            c.add(j);
        }

    }

    private class SidePanelRight extends JPanel {
        private Bouton acceleration;
        private ArrayList<Integer> niveauAcceleration;
        int index_acceleration;
        static final int TITRE_TAILLE = 30;
        private final Dimension size;
        private final Bouton histo_annuler;
        private final Bouton histo_refaire;

        public SidePanelRight(Dimension size) {
            this.size = size;
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            int height = (int) (size.height * 0.1);

            Dimension size_pane = new Dimension(size.width, height);

            Dimension size_pane_button = new Dimension(size.width, (int) (height + (size.height * 0.05)));
            Dimension size_button = new Dimension((int) (height * RATIO_BOUTON_PETIT), height);

            JPanel panel_historique = creerTitre("Historique", size_pane);

            JPanel histo_bouton = new JPanel();
            histo_bouton.setOpaque(false);
            histo_bouton.setPreferredSize(size_pane_button);
            histo_bouton.setMaximumSize(size_pane_button);

            histo_annuler = new Bouton(CHEMIN_RESSOURCE + "/bouton/arriere.png", CHEMIN_RESSOURCE + "/bouton/arriere_hover.png",
                    size_button, this::actionUndo);
            histo_refaire = new Bouton(CHEMIN_RESSOURCE + "/bouton/avant.png", CHEMIN_RESSOURCE + "/bouton/avant_hover.png",
                    size_button, this::actionRedo);

            histo_bouton.add(histo_annuler);
            histo_bouton.add(histo_refaire);

            JPanel panel_ia = null;
            JPanel ia_bouton = null;
            JPanel panel_vit_ia = null;
            JPanel vit_ia_bouton = null;

            if (config.getIaMode1() != 0) {
                // titre IA
                panel_ia = creerTitre("IA", size_pane);

                // boutons
                // boutons
                ia_bouton = new JPanel();
                ia_bouton.setOpaque(false);
                ia_bouton.setPreferredSize(size_pane_button);
                ia_bouton.setMaximumSize(size_pane_button);

                on_off_ia = new Bouton(CHEMIN_RESSOURCE + "/bouton/running.png", CHEMIN_RESSOURCE + "/bouton/running_hover.png",
                        size_button,
                        PanelPlateau.this::switchOnOffIA);

                ia_bouton.add(on_off_ia);

                // titre
                panel_vit_ia = creerTitre("Vitesse IA", size_pane);

                // boutons
                vit_ia_bouton = new JPanel();
                vit_ia_bouton.setOpaque(false);
                vit_ia_bouton.setPreferredSize(size_pane_button);
                vit_ia_bouton.setMaximumSize(size_pane_button);

                acceleration = new Bouton(CHEMIN_RESSOURCE + "/bouton/x1.png", CHEMIN_RESSOURCE + "/bouton/x1.png",
                        size_button);
                Bouton plus = new Bouton(CHEMIN_RESSOURCE + "/bouton/plus.png", CHEMIN_RESSOURCE + "/bouton/plus.png",
                        size_button,
                        this::accelerationIA);
                Bouton minus = new Bouton(CHEMIN_RESSOURCE + "/bouton/minus.png", CHEMIN_RESSOURCE + "/bouton/minus.png",
                        size_button,
                        this::ralentirIA);

                index_acceleration = 0;
                niveauAcceleration = new ArrayList<>();
                niveauAcceleration.add(1);
                niveauAcceleration.add(2);
                niveauAcceleration.add(4);
                niveauAcceleration.add(8);
                niveauAcceleration.add(16);
                niveauAcceleration.add(32);
                Font lilli_belle_tmp = new Font("Lily Script One", Font.PLAIN, 20);
                acceleration.setFont(lilli_belle_tmp);

                vit_ia_bouton.add(minus);
                vit_ia_bouton.add(acceleration);
                vit_ia_bouton.add(plus);
            }


            add(panel_historique);
            add(histo_bouton);
            if (config.getIaMode1() != 0) {
                add(panel_ia);
                add(ia_bouton);
                add(panel_vit_ia);
                add(vit_ia_bouton);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Utile.dessinePanelBackground(g, size, null);

            histo_annuler.setEnabled(jeu.getHistorique().peutAnnuler());
            histo_refaire.setEnabled(jeu.getHistorique().peutRefaire());
        }

        private JPanel creerTitre(String _t, Dimension _s) {
            JPanel _jpan = new JPanel();
            JLabel _lab = new JLabel(_t);
            _lab.setFont(new Font("Lily Script One", Font.PLAIN, SidePanelRight.TITRE_TAILLE));
            _lab.setForeground(new Color(103, 69, 42));
            _jpan.setLayout(new GridBagLayout());
            _jpan.setOpaque(false);
            _jpan.add(_lab);
            _jpan.setPreferredSize(_s);
            _jpan.setMaximumSize(_s);
            return _jpan;
        }

        public void ralentirIA(ActionEvent e) {
            index_acceleration--;
            if (index_acceleration < 0) {
                index_acceleration = 0;
            }
            jeu.accelererIA(niveauAcceleration.get(index_acceleration));
            changeBoutonVitesse();
        }

        public void accelerationIA(ActionEvent e) {
            index_acceleration++;
            if (index_acceleration >= niveauAcceleration.size()) {
                index_acceleration = niveauAcceleration.size() - 1;
            }
            jeu.accelererIA(niveauAcceleration.get(index_acceleration));
            changeBoutonVitesse();
        }

        private void changeBoutonVitesse() {
            acceleration.changeImage(
                    CHEMIN_RESSOURCE + "/bouton/x" + niveauAcceleration.get(index_acceleration) + ".png",
                    CHEMIN_RESSOURCE + "/bouton/x" + niveauAcceleration.get(index_acceleration) + ".png"
            );
        }

        public void actionUndo(ActionEvent e) {
            jeu.annuler();
            jg.repaint();
        }

        public void actionRedo(ActionEvent e) {
            jeu.refaire();
            jg.repaint();
        }

    }

    public boolean isParametreVisible() {
        return pp.isVisible();
    }

    private class ParametrePanel extends JPanel {

        private class BackgroundPanel extends JPanel {
            public BackgroundPanel(Dimension taille) {
                super();
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setAlignmentX(CENTER_ALIGNMENT);
                setMaximumSize(taille);
                setPreferredSize(taille);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Utile.dessinePanelBackground(g, getSize(), null);
            }
        }

        public ParametrePanel() {
            initialiserComposant();
            setOpaque(false);
            setLayout(new GridBagLayout());
            setMaximumSize(taille_fenetre);
        }

        private void initialiserComposant() {
            Dimension taille_panel = new Dimension((int) (taille_fenetre.width * 0.55), taille_fenetre.height * 2 / 3);
            BackgroundPanel contenu = new BackgroundPanel(taille_panel);

            JLabel parametres_texte = new JLabel("Paramètres");
            parametres_texte.setForeground(new Color(82, 60, 43));
            parametres_texte.setFont(lilyScriptOne);
            parametres_texte.setAlignmentX(CENTER_ALIGNMENT);

            double ratio_marge = 0.03;
            double taille_restante = taille_panel.height - (taille_panel.height * ratio_marge) * 9;
            double height = taille_restante / 6;

            Dimension taille_bouton = new Dimension((int) (height * RATIO_BOUTON_CLASSIQUE), (int) (height));

            /* Boutons*/
            Bouton bReprendre = new Bouton(CHEMIN_RESSOURCE + "/bouton/reprendre.png", CHEMIN_RESSOURCE + "/bouton/reprendre_hover.png",
                    taille_bouton.width,
                    taille_bouton.height);

            Bouton bNouvellePartie = new Bouton(CHEMIN_RESSOURCE + "/bouton/nouvelle_partie.png", CHEMIN_RESSOURCE + "/bouton/nouvelle_partie_hover.png",
                    taille_bouton.width,
                    taille_bouton.height);

            JPanel charger_sauvegarder = new JPanel();
            charger_sauvegarder.setOpaque(false);
            charger_sauvegarder.setPreferredSize(taille_bouton);
            charger_sauvegarder.setMaximumSize(taille_bouton);
            charger_sauvegarder.setLayout(new GridLayout(1, 2));
            Bouton bSauvegarder = new Bouton(CHEMIN_RESSOURCE + "/bouton/sauvegarder.png", CHEMIN_RESSOURCE + "/bouton/sauvegarder_hover.png",
                    taille_bouton.width / 2,
                    taille_bouton.height);
            Bouton bCharger = new Bouton(CHEMIN_RESSOURCE + "/bouton/charger.png", CHEMIN_RESSOURCE + "/bouton/charger_hover.png",
                    taille_bouton.width / 2,
                    taille_bouton.height);

            charger_sauvegarder.add(bSauvegarder);
            charger_sauvegarder.add(bCharger);

            Bouton bQuitter = new Bouton(CHEMIN_RESSOURCE + "/bouton/quitter_partie.png", CHEMIN_RESSOURCE + "/bouton/quitter_partie_hover.png",
                    taille_bouton.width,
                    taille_bouton.height);


            /* Evenements */
            ActionEchap echap = new ActionEchap();
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "echap");
            getActionMap().put("echap", echap);

            bQuitter.addActionListener(PanelPlateau.this::actionQuitter);
            bReprendre.addActionListener(echap);
            bNouvellePartie.addActionListener(PanelPlateau.this::actionBoutonNouvelle);
            bSauvegarder.addActionListener(PanelPlateau.this::actionBoutonSauvergarder);
            bCharger.addActionListener(this::actionCharger);

            /* Adding */
            Dimension margin_taille = new Dimension(taille_panel.width, (int) (taille_panel.height * ratio_marge));
            addMargin(contenu, margin_taille);
            addMargin(contenu, margin_taille);
            contenu.add(parametres_texte);
            addMargin(contenu, margin_taille);
            addMargin(contenu, margin_taille);
            contenu.add(bReprendre);
            addMargin(contenu, margin_taille);
            contenu.add(bNouvellePartie);
            addMargin(contenu, margin_taille);
            contenu.add(charger_sauvegarder);
            addMargin(contenu, margin_taille);
            addMargin(contenu, taille_bouton);
            addMargin(contenu, margin_taille);
            contenu.add(bQuitter);
            addMargin(contenu, margin_taille);
            add(contenu);
        }

        private void addMargin(JPanel parent, Dimension taille) {
            parent.add(Box.createRigidArea(taille));
        }

        public void actionCharger(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(SAVES_PATH);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Sauvegardes", "sav");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                jeu.charger(chooser.getSelectedFile().getName());
                pp.setVisible(false);
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color transparentColor = new Color(0, 0, 0, 0.4f);
            g2d.setColor(transparentColor);
            g2d.fillRect(0, 0, taille_fenetre.width, taille_fenetre.height);
            g2d.setComposite(AlphaComposite.SrcOver);
        }

    }

    private class VictoirePanel extends JPanel {
        private class BackgroundPanel extends JPanel {

            public BackgroundPanel(Dimension taille) {
                super();
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setAlignmentX(CENTER_ALIGNMENT);
                setMaximumSize(taille);
                setPreferredSize(taille);
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Utile.dessineParcheminVictoire(g, getSize(), null);
            }
        }

        JPanel titre_victoire;
        JPanel nb_tours;
        JPanel tmp_reflexion_j1;
        JPanel tmp_reflexion_j2;

        public VictoirePanel() {
            initialiserComposant();
            setOpaque(false);
            setLayout(new GridBagLayout());
            setMaximumSize(taille_fenetre);
        }

        private void initialiserComposant() {
            Dimension real_taille_panel = new Dimension((int) (taille_fenetre.width * 0.35), (int) (taille_fenetre.height * 0.9));
            BackgroundPanel contenu = new BackgroundPanel(real_taille_panel);
            Dimension taille_panel = new Dimension((int) (real_taille_panel.width * 0.75), (int) (real_taille_panel.height * 0.8));

            double ratio_titre = 0.1;
            double ratio_marge = 0.03;
            double ratio_texte = 0.05;
            double taille_restante = taille_panel.height - taille_panel.height * (ratio_titre + (ratio_marge * 10) + (ratio_texte * 4));
            float height_bouton = (float) taille_restante / 4;

            Dimension taille_bouton = new Dimension((int) (height_bouton * RATIO_BOUTON_CLASSIQUE), (int) (height_bouton));
            Bouton bVisualiser = new Bouton(CHEMIN_RESSOURCE + "/bouton/visualiser.png", CHEMIN_RESSOURCE + "/bouton/visualiser.png",
                    taille_bouton, this::actionVisualiser);
            Bouton bSauvegarder = new Bouton(CHEMIN_RESSOURCE + "/bouton/sauvegarder_partie.png", CHEMIN_RESSOURCE + "/bouton/sauvegarder_partie.png",
                    taille_bouton, PanelPlateau.this::actionBoutonSauvergarder);
            Bouton bNouvelle = new Bouton(CHEMIN_RESSOURCE + "/bouton/nouvelle_partie.png", CHEMIN_RESSOURCE + "/bouton/nouvelle_partie.png",
                    taille_bouton, PanelPlateau.this::actionBoutonNouvelle);
            Bouton bQuitter = new Bouton(CHEMIN_RESSOURCE + "/bouton/quitter.png", CHEMIN_RESSOURCE + "/bouton/quitter.png",
                    taille_bouton, PanelPlateau.this::actionQuitter);

            Dimension dim_texte = new Dimension(taille_panel.width, (int) (taille_panel.height * ratio_texte));
            titre_victoire = creerTexte("Victoire du joueur %n", 35,
                    new Dimension(taille_panel.width, (int) (taille_panel.height * ratio_titre)), SwingConstants.CENTER);
            nb_tours = creerTexte("%n tours passés", 20, dim_texte, SwingConstants.CENTER);
            JPanel tmp_reflexion_titre = creerTexte("Temps moyen de réflexion :", 20, dim_texte, SwingConstants.CENTER);
            tmp_reflexion_j1 = creerTexte("%n secondes pour le joueur 1", 20, dim_texte, SwingConstants.LEFT);
            tmp_reflexion_j2 = creerTexte("%n secondes pour le joueur 2", 20, dim_texte, SwingConstants.LEFT);
            Dimension taille_marge = new Dimension(taille_panel.width, (int) (taille_panel.height * ratio_marge));

            addMargin(contenu, taille_marge);
            addMargin(contenu, taille_marge);
            contenu.add(titre_victoire);
            contenu.add(nb_tours);
            addMargin(contenu, taille_marge);
            addMargin(contenu, taille_marge);

            contenu.add(tmp_reflexion_titre);
            addMargin(contenu, taille_marge);
            contenu.add(tmp_reflexion_j1);
            addMargin(contenu, taille_marge);
            contenu.add(tmp_reflexion_j2);
            addMargin(contenu, taille_marge);

            addMargin(contenu, taille_marge);
            contenu.add(bVisualiser);
            addMargin(contenu, taille_marge);
            contenu.add(bSauvegarder);
            addMargin(contenu, taille_marge);
            contenu.add(bNouvelle);
            addMargin(contenu, taille_marge);
            contenu.add(bQuitter);
            addMargin(contenu, taille_marge);
            add(contenu);
        }

        private void actionVisualiser(ActionEvent e) {
            victoire_panel.setVisible(false);
        }

        public void changeTexte(JPanel _jp, String texte) {
            for (Component jc : _jp.getComponents()) {
                JLabel label = (JLabel) jc;
                label.setText(texte);
            }
        }

        private void addMargin(JPanel parent, Dimension taille) {
            parent.add(Box.createRigidArea(taille));
        }

        private JPanel creerTexte(String _t, int _fs, Dimension _s, int alignment) {
            JPanel _jpan = new JPanel();
            JLabel _lab = new JLabel(_t, alignment);
            _lab.setFont(new Font("Lily Script One", Font.PLAIN, _fs));
            _lab.setForeground(new Color(103, 69, 42));
            _lab.setPreferredSize(_s);
            _lab.setMaximumSize(_s);
            _jpan.setLayout(new BorderLayout());
            _jpan.setOpaque(false);
            _jpan.add(_lab, BorderLayout.CENTER);
            _jpan.setPreferredSize(_s);
            _jpan.setMaximumSize(_s);
            return _jpan;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color transparentColor = new Color(0, 0, 0, 0.4f);
            g2d.setColor(transparentColor);
            g2d.fillRect(0, 0, taille_fenetre.width, taille_fenetre.height);
            g2d.setComposite(AlphaComposite.SrcOver);
        }
    }


    /**
     * Crée un JPanel modifié qui ajoute le logo et le texte designant quel joueur joue.
     */
    private class TopPanel extends JPanel {
        /**
         * Constructeur de TopPanel. Ajoute les élements et définis les valeurs des propriétés de chacuns.
         */
        public TopPanel(float taille_h) {
            setOpaque(false);

            setLayout(new GridBagLayout());

            Dimension size = new Dimension(taille_fenetre.width, (int) (taille_fenetre.height * taille_h));
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);


            jt = new JLabel("C'est au tour du Joueur " + (config.getIndexJoueurCommence() + 1));
            jt.setAlignmentX(CENTER_ALIGNMENT);
            jt.setAlignmentY(CENTER_ALIGNMENT);
            jt.setOpaque(false);
            jt.setFont(lilyScriptOne);
            jt.setForeground(Color.WHITE);
            add(jt);
        }
    }

    /**
     * Action d'un bouton pour recréer une nouvelle partie. Ramène au lobby dans le cas d'une partie en réseau.
     */
    private void actionBoutonNouvelle(ActionEvent e) {
        Fenetre f = (Fenetre) SwingUtilities.getWindowAncestor(this);
        if (netUser != null) {
            f.setPanel(new LobbyPanel(netUser));
        } else {
            f.setPanel(new PanelPlateau(taille_fenetre, config));
        }
    }

    /**
     * Action d'un bouton pour revenir au menu. Déconnecte l'utilisateur du réseau.
     */
    private void actionQuitter(ActionEvent e) {
        Fenetre f = (Fenetre) SwingUtilities.getWindowAncestor(this);
        jeu.desactiverIA();
        if (netUser != null) {
            netUser.deconnexion();
        }
        f.displayPanel("menu");
    }

    /**
     * Action d'un bouton pour sauvegarder dans un fichier la partie en cours, ferme les paramètres.
     */
    private void actionBoutonSauvergarder(ActionEvent e) {
        jeu.sauvegarder();
        pp.setVisible(false);
    }

    /**
     * Change le statut de l'IA, à savoir si elle doit être en pause ou en marche. Change aussi le bouton pause/play.
     */
    private void switchOnOffIA(ActionEvent e) {
        jeu.iaSwitch();
        if (jeu.getIaStatut()) {
            on_off_ia.changeImage(CHEMIN_RESSOURCE + "/bouton/running.png", CHEMIN_RESSOURCE + "/bouton/running_hover.png");
        } else {
            on_off_ia.changeImage(CHEMIN_RESSOURCE + "/bouton/stop.png", CHEMIN_RESSOURCE + "/bouton/stop_hover.png");
        }
    }

    /**
     * Affiche le panel de victoire avec les textes correspondant au gagnant
     */
    private void changeVictory() {
        String nom_joueur;
        if (netUser != null) {
            if (netUser.getNumJoueur() == jeu.getJoueurEnCours().getNum_joueur()) nom_joueur = netUser.getNomJoueur();
            else nom_joueur = netUser.getNomAdversaire();
        } else {
            nom_joueur = jeu.getGagnant().getNum_joueur() == JOUEUR1 ? "Joueur 1" : "Joueur 2";
        }
        jt.setText(nom_joueur + " gagne");

        victoire_panel.setVisible(true);
        victoire_panel.changeTexte(victoire_panel.titre_victoire, "Victoire de " + nom_joueur);
        victoire_panel.changeTexte(victoire_panel.nb_tours, jeu.getNbTours() + " tours passés");
        victoire_panel.changeTexte(victoire_panel.tmp_reflexion_j1, "n secondes pour le joueur 1");
        victoire_panel.changeTexte(victoire_panel.tmp_reflexion_j2, "n secondes pour le joueur 2");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Utile.dessineDecorationPlateau(g, getSize(), this, jeu.estJeufini(), config, jeu.getJoueurEnCours().getNum_joueur());
    }
    /**
     * Modifie le texte qui affiche quel joueur doit jouer.
     */
    @Override
    public void miseAjour() {
        if (jeu.estJeufini()) {
            changeVictory();
        } else {
            if (netUser != null) {
                if (netUser.getNumJoueur() == jeu.getJoueurEnCours().getNum_joueur())
                    jt.setText("C'est au tour de " + netUser.getNomJoueur());
                else jt.setText("C'est au tour de " + netUser.getNomAdversaire());
            } else {
                jt.setText(jeu.getJoueurEnCours().getNum_joueur() == JOUEUR1 ? "C'est au tour du Joueur 1" : "C'est au tour du Joueur 2");
            }
        }
        repaint();
    }
}
