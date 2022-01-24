using System;
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
        public MainWindow()
        {
            InitializeComponent();
        }
        Random rand = new Random();

        public String pw = "";

        String Gen()
        {
            String Password="";
            int check=0;
            String str = "abcdefghijklmnopqrstuvwxyzöäü"+"ABCDEFGHIJKLMNOPQRSTUFWXYZÖÄÜ"+"1234567890"+"!§$%&/()=?*+#;,:.";
            int size = 20; ///Länge des Passworts

            for (int i = 0; i < size; i++)
            {
                int x = rand.Next(85);
                Password = Password + str[x];
            }

            check = checkreq(Password);
            
            return Password;
        }

        checkreq(String pwcheck)
        {

        }

        private void Generate_Click(object sender, RoutedEventArgs e)
        {
            pw = Gen();
            Console.WriteLine(pw);
        }
    }
}
