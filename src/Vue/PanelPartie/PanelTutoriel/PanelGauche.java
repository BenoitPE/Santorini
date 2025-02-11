package Vue.PanelPartie.PanelTutoriel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static Utile.Constante.*;

/**
 * JPanel personnalisé qui affiche le parchemin de texte et son contenu pour le tutoriel.
 */
public class PanelGauche extends JPanel {
    private final PanelInfo panel_info;

    public PanelInfo getPanel_info() {
        return panel_info;
    }

    public static class PanelInfo extends JPanel {
        private final Dimension taille_personnage;
        private final Dimension taille_parchemin;
        private final Dimension pos_parchemin;
        private final Dimension pos_personnage;
        private final JTextArea texte_bulle;

        public PanelInfo(Dimension size, PanelTutoriel panel_tutoriel) {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Polices
            panel_tutoriel.setLilyScriptOne(new Font(LILY_SCRIPT, Font.PLAIN, 20));

            // Dimensions

            int bouton_height = (int) (size.height * 0.1);


            taille_personnage = new Dimension(size.height / 6, size.height / 6);
            taille_parchemin = new Dimension(size.width, size.height * 2 / 3 - taille_personnage.height / 2);

            pos_parchemin = new Dimension(0, (int)(size.height*0.2f));
            pos_personnage = new Dimension(0, pos_parchemin.height + taille_parchemin.height - taille_personnage.height * 3 / 4);
            Dimension panel_texte_taille = new Dimension(size.width * 2 / 3, taille_parchemin.height);
            Dimension size_bouton = new Dimension((int) (bouton_height * RATIO_BOUTON_PETIT), bouton_height);
            Dimension texte_bulle_taille = new Dimension(panel_texte_taille.width, panel_texte_taille.height - 2 * size_bouton.height);

            setOpaque(false);
            setMaximumSize(size);


            texte_bulle = new JTextArea(TEXTE_ETAPES[0]);
            texte_bulle.setOpaque(false);
            texte_bulle.setEditable(false);
            texte_bulle.setFont(panel_tutoriel.getLilyScriptOne());
            texte_bulle.setForeground(new Color(82, 60, 43));
            PanelTutoriel.definirTaille(texte_bulle, texte_bulle_taille);
            texte_bulle.setLineWrap(true);
            texte_bulle.setWrapStyleWord(true);

            JPanel panel_texte = new JPanel();
            panel_texte.setOpaque(false);
            PanelTutoriel.definirTaille(panel_texte, panel_texte_taille);
            panel_texte.setAlignmentY(CENTER_ALIGNMENT);
            panel_texte.add(texte_bulle);

            add(Box.createRigidArea(new Dimension(size.width, (int)(pos_parchemin.height*1.2f))));

            add(panel_texte);
            changerTexte(panel_tutoriel.getNumEtape());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            try {
                BufferedImage bg_parchemin = ImageIO.read(new File(CHEMIN_RESSOURCE + "/assets_recurrents/parchemin.png"));
                BufferedImage personnage = ImageIO.read(new File(CHEMIN_RESSOURCE + "/assets_recurrents/aphrodite.png"));

                g2d.drawImage(
                        bg_parchemin,
                        pos_parchemin.width,
                        pos_parchemin.height,
                        taille_parchemin.width,
                        taille_parchemin.height,
                        this
                );
                g2d.drawImage(
                        personnage,
                        pos_personnage.width,
                        pos_personnage.height,
                        taille_personnage.width,
                        taille_personnage.height,
                        this
                );
            } catch (Exception e) {
                System.err.println(ERREUR_IMAGE_FOND + e.getMessage());
                e.printStackTrace();
            }
        }

        public void changerTexte(int num_etape) {
            texte_bulle.setText(TEXTE_ETAPES[num_etape]);
        }
    }

    public PanelGauche(Dimension size, PanelTutoriel panel_tutoriel) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        panel_info = new PanelInfo(size, panel_tutoriel);
        add(panel_info);
    }
}
