Imports System
Imports System.Threading
Imports System.Windows.Controls
Imports Microsoft.Phone.Controls
Imports Microsoft.Phone.Shell
Imports System.Net
Imports System.Net.Http
Imports System.IO
Imports HtmlAgilityPack
Imports System.Globalization
Imports Microsoft.Phone.Net.NetworkInformation





Partial Public Class MainPage
    Inherits PhoneApplicationPage

    ' Constructor
    Public Sub New()
        InitializeComponent()

        SupportedOrientations = SupportedPageOrientation.Portrait
        'Código para armar la appbar
        ApplicationBar = New ApplicationBar
        ApplicationBar.Mode = ApplicationBarMode.Default
        ApplicationBar.Opacity = 1.0
        ApplicationBar.IsVisible = True
        ApplicationBar.IsMenuEnabled = True
        Dim appBarButtonHelp As ApplicationBarIconButton = New ApplicationBarIconButton
        appBarButtonHelp.IconUri = New Uri("/Assets/appbar.home.question.png", UriKind.Relative)
        appBarButtonHelp.Text = "Ayuda"
        ApplicationBar.Buttons.Add(appBarButtonHelp)
        AddHandler appBarButtonHelp.Click, AddressOf appBarButtonHelp_Click
    End Sub

    ' Sample code for building a localized ApplicationBar
    'Private Sub BuildLocalizedApplicationBar()
    '    ' Set the page's ApplicationBar to a new instance of ApplicationBar.
    '    ApplicationBar = New ApplicationBar()

    '    ' Create a new button and set the text value to the localized string from AppResources.
    '    Dim appBarButton As New ApplicationBarIconButton(New Uri("/Assets/AppBar/appbar.add.rest.png", UriKind.Relative))
    '    appBarButton.Text = AppResources.AppBarButtonText
    '    ApplicationBar.Buttons.Add(appBarButton)

    '    ' Create a new menu item with the localized string from AppResources.
    '    Dim appBarMenuItem As New ApplicationBarMenuItem(AppResources.AppBarMenuItemText)
    '    ApplicationBar.MenuItems.Add(appBarMenuItem)
    'End Sub

    Private Sub appBarButtonHelp_click(ByVal sender As Object, ByVal e As EventArgs)
        NavigationService.Navigate(New Uri("/Consultas.xaml", UriKind.Relative))
    End Sub

    Private Sub tbLegajoLogin_GotFocus(sender As Object, e As RoutedEventArgs)
        If tbLegajoLogin.Text = " Legajo" Then
            tbLegajoLogin.Text = ""
        End If
    End Sub
    Private Sub tbLegajoLogin_LostFocus(sender As Object, e As RoutedEventArgs)
        If tbLegajoLogin.Text = "" Then
            tbLegajoLogin.Text = " Legajo"
        End If
    End Sub
    Private Sub tbPassLogin_GotFocus(sender As Object, e As RoutedEventArgs)
        lbPassHint.Visibility = System.Windows.Visibility.Collapsed
    End Sub
    Private Sub tbPassLogin_LostFocus(sender As Object, e As RoutedEventArgs)
        If (tbPassLogin.Password = "") Then
            lbPassHint.Visibility = System.Windows.Visibility.Visible
        Else
            lbPassHint.Visibility = System.Windows.Visibility.Collapsed
        End If
    End Sub
    Private Sub lbPassHint_Tap(sender As Object, e As RoutedEventArgs)
        lbPassHint.Visibility = System.Windows.Visibility.Collapsed
        tbPassLogin.Focus()
    End Sub
    Private Sub setPG(value As Boolean)
        If value = True Then
            SystemTray.ProgressIndicator.IsVisible = True
        Else
            SystemTray.ProgressIndicator.IsVisible = False
        End If
    End Sub

    Private Async Sub btLogin_Click(sender As Object, e As RoutedEventArgs)
        SystemTray.ProgressIndicator = New ProgressIndicator
        SystemTray.ProgressIndicator.IsIndeterminate = True
        SystemTray.ProgressIndicator.Text = "Ingresando..."
        setPG(True)
        If DeviceNetworkInformation.IsNetworkAvailable Then
            Try
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                handler.CookieContainer = cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                Dim url As String = "http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp"
                'Dim body = String.Format("legajo=" + tbLegajoLogin.Text.Trim + "&password=" + tbPassLogin.Password.Trim)
                'Dim credenciales As StringContent = New StringContent(body, System.Text.Encoding.Unicode, "application/x-www-form-urlencoded")
                Dim credenciales As New Dictionary(Of String, String)
                credenciales.Add("legajo", tbLegajoLogin.Text.Trim)
                credenciales.Add("password", tbPassLogin.Password.Trim)
                Dim content As New FormUrlEncodedContent(credenciales)
                Dim resp = Await httpclient.PostAsync(url, content)
                Dim myuri As New Uri(url)
                Dim bytes As Byte() = Await resp.Content.ReadAsByteArrayAsync
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(myuri, cookies.GetCookieHeader(myuri))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If ndError Is Nothing Then
                    Dim ndNombre = htmlpage.DocumentNode.SelectNodes("//td")
                    Dim nombre As String = App.toTitleCase(ndNombre(3).InnerText.Trim.ToLower)
                    setPG(False)
                    MessageBox.Show("Entraste como " + nombre, "Genial!", MessageBoxButton.OK)
                    Dim ndLinks = htmlpage.DocumentNode.SelectNodes("//a")
                    App.urlEstadoAcademico = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(1).Attributes("href").Value)
                    App.urlExamenes = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(2).Attributes("href").Value)
                    App.urlCursado = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(3).Attributes("href").Value)
                    App.urlCorrCursado = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(4).Attributes("href").Value)
                    App.urlCorrRendir = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(5).Attributes("href").Value)
                    App.urlInscExamen = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndLinks(6).Attributes("href").Value)
                    App.ttNombre = nombre
                    App.cookies = cookies
                    NavigationService.Navigate(New Uri("/MenuPrincipal.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = ndError(0).InnerText
                    setPG(False)
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch ex As Exception
                setPG(False)
                MessageBox.Show("Algo salió mal. O el servidor no responde, o quizás pusiste mal tu legajo y contraseña!", "Oh noes!", MessageBoxButton.OK)
            End Try
        Else
            setPG(False)
            MessageBox.Show("Hubo un error, seguro que tenés conexión a internet?", "Rayos!", MessageBoxButton.OK)
        End If

    End Sub
End Class