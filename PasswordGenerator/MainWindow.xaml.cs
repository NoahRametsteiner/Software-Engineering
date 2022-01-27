using System;
using System.CodeDom;
using System.Collections.Generic;
using System.Linq;
using System.Security.RightsManagement;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace PasswordGenerator
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        Random rand = new Random();
        public String pw = "";
        public int grossb = 0, kleinb = 0, sonderz = 0, ziffern = 0, size = 0;
        public String str = "abcdefghijklmnopqrstuvwxyzöäü" + "ABCDEFGHIJKLMNOPQRSTUFWXYZÖÄÜ" + "1234567890" + "!§$%&/()=?*+#;,:.";

        #region MainWindow

        public MainWindow()
        {
            InitializeComponent();
        }

        #endregion

        #region Gen

        String Gen()
        {
            int count = 0;
            Start:

            kleinb = Convert.ToInt32(low.Text);
            grossb = Convert.ToInt32(big.Text);
            ziffern = Convert.ToInt32(num.Text);
            sonderz = Convert.ToInt32(spe.Text);
            String Password = "";
            int check = 0;
            size = grossb+kleinb+sonderz+ziffern; ///Länge des Passworts

            for (int i = 0; i < size; i++)
            {
                int x = rand.Next(85);
                Password = Password + str[x];
            }

            check = checkreq(Password);
            
            if (check == 0)
            {
                count++;
                goto Start;
            }
            Console.WriteLine(count);
            return Password;
        }

        #endregion

        #region checkreq

        int checkreq(String pwcheck)
        {
            int ckecksum = 0, v=0, x=0, y=0, z=0;

            for (int i = 0; i < size; i++)
            {
                for (int a = 0; a < 29; a++)
                {
                    if (pwcheck[i] == str[a]) { v++; break; }
                }
                for (int a = 29; a < 58; a++)
                {
                    if (pwcheck[i] == str[a]){ x++; break; }
                }
                for (int a = 58; a < 68; a++)
                {
                    if (pwcheck[i] == str[a]) { y++; break; }
                }
                for (int a = 68; a < 85; a++)
                {
                    if (pwcheck[i] == str[a]) { z++; break; }
                }
            }

            if (kleinb == v && grossb == x && ziffern == y && sonderz == z) {ckecksum = 1;}
            else {ckecksum = 0;}

            return ckecksum;
        }

        #endregion

        private void Generate_Click(object sender, RoutedEventArgs e)
        {
            pw = Gen();
            pwfield.Text = pw;
        }
    }
}
