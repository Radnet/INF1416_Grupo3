
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FrameLogin extends JFrame {

    public JFrame ThisFrame;

    public FrameLogin(String Title) {
        
        super(Title);
                
        ThisFrame = this;

        setLayout(null);

        //LOG
        //Create DaoLog object
        DaoLog daoLog = new DaoLog();
        daoLog.Autenticacao1Iniciada();
        
        /**
         * *** Setting the attributes of the Frame ****
         */
        JButton BUT_Login = new JButton("OK");

        final JTextField TXT_Log = new JTextField();

        JLabel LB_Login = new JLabel("Login");

        /**
         * ********************************************
         */
        Container Panel = getContentPane();

        /**
         * *** Adjusting the size of attributes ****
         */
        TXT_Log.setBounds(90, 50, 80, 25);

        BUT_Login.setBounds(90, 150, 65, 25);

        LB_Login.setBounds(40, 49, 65, 25);

        /**
         * ******************************************
         */
        /**
         * *** Adding attributes to the panel ****
         */
        Panel.add(BUT_Login);
        Panel.add(TXT_Log);
        Panel.add(LB_Login);

        /**
         * *****************************************
         */
        /**
         * ***************** Setting listeners ************************
         */
        BUT_Login.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Create DAO and DaoLog object
                Dao dao = new Dao();
                DaoLog daoLog = new DaoLog();

                // CReate user object
                User user = User.GetUserObj();
                user.setLogin(TXT_Log.getText());

                // Verify if Login Name exists
                if (dao.IsLoginNameOK(user.getLogin())) {
                    //Verify if user still has OTPs in TANList
                    if (!dao.UserHasOTP(user.getLogin())){
                        JOptionPane.showMessageDialog(ThisFrame, "Todas as OTPs do usuario foram utilizadas");
                    }
                    // Verify if user is blocked
                    else if (dao.IsUserBlocked(user.getLogin())) {
                        
                        //LOG
                        daoLog.AcessoBloqueadoEtapa1(user.getLogin());
                        
                        JOptionPane.showMessageDialog(ThisFrame, "O usuario foi bloqueado por 2 minutos. Aguarde a liberacao.");
                    } 
                    else {
                        
                        //LOG
                        daoLog.AcessoLiberado(user.getLogin());
                        daoLog.Autenticacao1Encerrada();
                        
                        // Open password frame
                        FramePassword FM_Password = new FramePassword("Etapa 2 - Senha");
                        FM_Password.setVisible(true);

                        // Close this frame
                        ThisFrame.dispose();
                    }
                } else {
                
                    //LOG    
                    daoLog.LoginDesconhecido(user.getLogin());
                    
                    JOptionPane.showMessageDialog(ThisFrame, "Login errado, tente novamente.");
                }
            }

        });

        /**
         * *************************************************************
         */
        /**
         * ********************* Centralizing the frame on the screen
         * ********************
         */
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 490) / 2, (screenSize.height - 370) / 2, 400, 250);

        /**
         * ********************************************************************************
         */
        // Makes the size of the screen unchangeable
        setResizable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

}
