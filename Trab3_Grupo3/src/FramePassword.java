
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class FramePassword extends JFrame {

    public JFrame ThisFrame;
    public ArrayList<String> list_possible_psws;
    public int trys = 3;
    public int countDigits = 0;

    public FramePassword(String Title) {
        super(Title);

        ThisFrame = this;

        setLayout(null);

        //LOG
        //Create DaoLog object
        final DaoLog daoLog = new DaoLog();
        daoLog.Autenticacao2Iniciada(User.GetUserObj().getLogin());
        
        /**
         * *** Setting the attributes of the Frame ****
         */
        JButton BUT_OK = new JButton("OK");
        JButton BUT_Clear = new JButton("Limpar");

        // 5 buttons representing the password numbers
        final JButton BTN_1 = new JButton();
        final JButton BTN_2 = new JButton();
        final JButton BTN_3 = new JButton();
        final JButton BTN_4 = new JButton();
        final JButton BTN_5 = new JButton();

        final JPasswordField PAW_Pass = new JPasswordField();
        JLabel LB_Password = new JLabel("Senha:");

        /**
         * ********************************************
         */
        Container Panel = getContentPane();

        /**
         * *** Adjusting the size of attributes ****
         */
        PAW_Pass.setBounds(90, 50, 150, 25);
        BUT_Clear.setBounds(250, 50, 85, 25);
        BUT_OK.setBounds(90, 170, 65, 25);

        BTN_1.setBounds(10, 100, 70, 40);
        BTN_2.setBounds(85, 100, 70, 40);
        BTN_3.setBounds(160, 100, 70, 40);
        BTN_4.setBounds(235, 100, 70, 40);
        BTN_5.setBounds(310, 100, 70, 40);

        LB_Password.setBounds(40, 49, 65, 25);

        /**
         * ******************************************
         */
        PopulateNumberBTNs(BTN_1, BTN_2, BTN_3, BTN_4, BTN_5);

        PAW_Pass.setEditable(false);

        /**
         * *** Adding attributes to the panel ****
         */
        Panel.add(BUT_OK);

        Panel.add(BTN_1);
        Panel.add(BTN_2);
        Panel.add(BTN_3);
        Panel.add(BTN_4);
        Panel.add(BTN_5);

        Panel.add(PAW_Pass);
        Panel.add(BUT_Clear);
        Panel.add(LB_Password);

        /**
         * *****************************************
         */
        // Instantiating password lists
        list_possible_psws = new ArrayList<String>();

        /**
         * ***************** Setting listeners ************************
         */
        ActionListener BTN_Listener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                countDigits++;

                // draw "*" on text box
                String pswField = "";
                for (int i = 0; i < countDigits; i++) {
                    pswField = pswField + "*";
                }
                PAW_Pass.setText(pswField);

                // Define the selected numbers
                String num1;
                String num2;

                if (e.getSource() == BTN_1) {
                    num1 = BTN_1.getClientProperty("value1").toString();
                    num2 = BTN_1.getClientProperty("value2").toString();
                } else if (e.getSource() == BTN_2) {
                    num1 = BTN_2.getClientProperty("value1").toString();
                    num2 = BTN_2.getClientProperty("value2").toString();
                } else if (e.getSource() == BTN_3) {
                    num1 = BTN_3.getClientProperty("value1").toString();
                    num2 = BTN_3.getClientProperty("value2").toString();
                } else if (e.getSource() == BTN_4) {
                    num1 = BTN_4.getClientProperty("value1").toString();
                    num2 = BTN_4.getClientProperty("value2").toString();
                } else {
                    num1 = BTN_5.getClientProperty("value1").toString();
                    num2 = BTN_5.getClientProperty("value2").toString();
                }

                // Double the list if it already contains something
                if (list_possible_psws.size() > 0) {
                    int initial_size = list_possible_psws.size();
                    for (int i = 0; i < initial_size; i++) {
                        list_possible_psws.add(list_possible_psws.get(i));
                    }

                    // Adding the number1 to the first half
                    int half_index = list_possible_psws.size() / 2;
                    for (int i = 0; i < half_index; i++) {
                        list_possible_psws.set(i, list_possible_psws.get(i) + num1);
                    }

                    // Adding number2  to second half
                    for (int i = half_index; i < list_possible_psws.size(); i++) {
                        list_possible_psws.set(i, list_possible_psws.get(i) + num2);
                    }

                } else {
                    list_possible_psws.add(num1);
                    list_possible_psws.add(num2);
                }
            }

        };

        // Adding listeners
        BTN_1.addActionListener(BTN_Listener);
        BTN_2.addActionListener(BTN_Listener);
        BTN_3.addActionListener(BTN_Listener);
        BTN_4.addActionListener(BTN_Listener);
        BTN_5.addActionListener(BTN_Listener);

        BUT_OK.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Create DAO object
                Dao dao = new Dao();

                boolean pws_ok_flag = false;
                String user_salt = dao.GetUserSalt(User.GetUserObj().getLogin());
                String user_pwsHash = dao.GetPswHash(User.GetUserObj().getLogin());

                // Verify all possibilities for a match of the one in DB
                for (String psw_pos : list_possible_psws) {
                    try {
                        // Generating digest for password possibility with salt
                        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                        messageDigest.update((psw_pos + user_salt).getBytes());
                        byte[] pwDigest = messageDigest.digest();

                        // Converting the digest to hex
                        String passwordToTest = SharedLibrary.GetHexadecimal(pwDigest);

                        // If user password match, set flag to true
                        if (passwordToTest.equals(user_pwsHash)) {               
                            pws_ok_flag = true;
                            break;
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                if (pws_ok_flag) {     
                    //LOG
                    daoLog.SenhaPositiva(User.GetUserObj().getLogin());
                    daoLog.Autenticacao2Encerrada(User.GetUserObj().getLogin());
                    
                    // OK, go to OTP step
                    FrameTanList FM_OTP = new FrameTanList("Etapa 3 - OTP");
                    FM_OTP.setVisible(true);

                    // Close this frame
                    ThisFrame.dispose();
                } else {     
                    //LOG
                    daoLog.SenhaNegativa(User.GetUserObj().getLogin());
                    //Primeira tentativa
                    if(trys == 3) daoLog.SenhaPrimeiroErro(User.GetUserObj().getLogin());
                       
                    //Segunda tentativa
                    else if(trys == 2) daoLog.SenhaSegundoErro(User.GetUserObj().getLogin());
                    
                    //Terceira tentativa
                    else if(trys == 1) daoLog.SenhaTerceiroErro(User.GetUserObj().getLogin());
                    //END Log
                    
                    trys--;
                    if (trys == 0) {
                        
                        //LOG
                        daoLog.AcessoBloqueadoEtapa2(User.GetUserObj().getLogin());
                        daoLog.Autenticacao2Encerrada(User.GetUserObj().getLogin());
                        
                        // Block User
                        dao.BlockUser(User.GetUserObj().getLogin());

                        JOptionPane.showMessageDialog(ThisFrame, "Senha errada. Suas tentaivas acabaram. Usuario bloqueado por 2 minutos.");

                        // Open Login frame
                        FrameLogin FM_Login = new FrameLogin("Etapa 1 - Login");
                        FM_Login.setVisible(true);

                        // Close this frame
                        ThisFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(ThisFrame, "Senha errada, voce possui " + trys + " tentativas.");
                    }
                }

                // Clear psw possibilities
                list_possible_psws.clear();

                countDigits = 0;
                PAW_Pass.setText("");
            }

        });

        BUT_Clear.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                list_possible_psws.clear();
                countDigits = 0;
                PAW_Pass.setText("");
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

    private void PopulateNumberBTNs(JButton BTN_1, JButton BTN_2, JButton BTN_3, JButton BTN_4, JButton BTN_5) {
        // Create a shuffle number list
        ArrayList<Integer> listValue = new ArrayList<Integer>();

        listValue.add(0);
        listValue.add(1);
        listValue.add(2);
        listValue.add(3);
        listValue.add(4);
        listValue.add(5);
        listValue.add(6);
        listValue.add(7);
        listValue.add(8);
        listValue.add(9);

        Collections.shuffle(listValue);

        // Set buttons names
        BTN_1.setText(Integer.toString(listValue.get(0)) + " ou " + Integer.toString(listValue.get(1)));
        BTN_2.setText(Integer.toString(listValue.get(2)) + " ou " + Integer.toString(listValue.get(3)));
        BTN_3.setText(Integer.toString(listValue.get(4)) + " ou " + Integer.toString(listValue.get(5)));
        BTN_4.setText(Integer.toString(listValue.get(6)) + " ou " + Integer.toString(listValue.get(7)));
        BTN_5.setText(Integer.toString(listValue.get(8)) + " ou " + Integer.toString(listValue.get(9)));

        BTN_1.putClientProperty("value1", listValue.get(0));
        BTN_1.putClientProperty("value2", listValue.get(1));
        BTN_2.putClientProperty("value1", listValue.get(2));
        BTN_2.putClientProperty("value2", listValue.get(3));
        BTN_3.putClientProperty("value1", listValue.get(4));
        BTN_3.putClientProperty("value2", listValue.get(5));
        BTN_4.putClientProperty("value1", listValue.get(6));
        BTN_4.putClientProperty("value2", listValue.get(7));
        BTN_5.putClientProperty("value1", listValue.get(8));
        BTN_5.putClientProperty("value2", listValue.get(9));

    }
}
