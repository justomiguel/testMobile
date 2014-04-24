Imports System
Imports System.Threading
Imports System.Windows.Controls
Imports Microsoft.Phone.Controls
Imports Microsoft.Phone.Shell
Imports System.Net
Imports System.Net.Http
Imports System.IO
Imports System.IO.IsolatedStorage
Imports HtmlAgilityPack
Imports System.Globalization
Imports Microsoft.Phone.Net.NetworkInformation
Imports Microsoft.Phone.Tasks
Imports System.Text

Partial Public Class MainPage
    Inherits PhoneApplicationPage
    Dim settings As IsolatedStorageSettings = IsolatedStorageSettings.ApplicationSettings
    ' Constructor
    Public Sub New()
        InitializeComponent()
        If Not settings.Contains("FirstRun") Then
            MessageBox.Show("Hola, parece que es la primera vez que corrés la app Ingeniero 2.0! En esta pantalla vas a poder ingresar tus credenciales de Sysacad para habilitar el módulo de SysAcadMobile, y para loguearte cada vez que quieras cambiar de usuario.", "Buenas y santas!", MessageBoxButton.OK)
            settings.Add("FirstRun", "Me importa un carajo, tomatelas te dije")
        End If
        SupportedOrientations = SupportedPageOrientation.Portrait
        'Código para armar la appbar

        Dim appBarButtonHelp As ApplicationBarIconButton = New ApplicationBarIconButton
        appBarButtonHelp.IconUri = New Uri("/Assets/appbar.home.question.png", UriKind.Relative)
        appBarButtonHelp.Text = "Ayuda"
        ApplicationBar.Buttons.Add(appBarButtonHelp)
        AddHandler appBarButtonHelp.Click, AddressOf appBarButtonHelp_click
        If settings.Contains("legajo") Then
            tbLegajoLogin.Text = TryCast(settings("legajo"), String)
            tbPassLogin.Password = TryCast(settings("pass"), String)
            lbPassHint.Visibility = System.Windows.Visibility.Collapsed
        End If

        'Código para animar la navegación
        Dim navIn = New NavigationInTransition
        navIn.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideDownFadeIn}
        navIn.Forward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideUpFadeIn}
        Dim navOut = New NavigationOutTransition
        navOut.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideDownFadeOut}
        navOut.Backward = New SlideTransition() With {.Mode = SlideTransitionMode.SlideUpFadeOut}
        TransitionService.SetNavigationInTransition(Me, navIn)
        TransitionService.SetNavigationOutTransition(Me, navOut)
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
        SystemTray.ProgressIndicator.IsVisible = value
    End Sub

    Public Sub btLogin_click(ByVal sender As Object, ByVal e As RoutedEventArgs)
        Login(tbLegajoLogin.Text.Trim, tbPassLogin.Password.Trim)
    End Sub

    Public Async Sub Login(legajo As String, password As String)
        SystemTray.ProgressIndicator = New ProgressIndicator
        SystemTray.ProgressIndicator.IsIndeterminate = True
        SystemTray.ProgressIndicator.Text = "Ingresando..."
        setPG(True)
        Dim settings As IsolatedStorageSettings = IsolatedStorageSettings.ApplicationSettings
        If DeviceNetworkInformation.IsNetworkAvailable Then
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = cookies
            handler.UseCookies = True
            Dim httpclient As New HttpClient(handler)
            Dim url As String = "http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp"
            Dim credenciales As New Dictionary(Of String, String)
            credenciales.Add("legajo", legajo)
            credenciales.Add("password", password)
            If Not settings.Contains("legajo") Then
                settings.Add("legajo", legajo)
                settings.Add("pass", password)
            Else
                settings("legajo") = legajo
                settings("pass") = password
            End If
            Dim content As New FormUrlEncodedContent(credenciales)
            Dim resp As HttpResponseMessage = Nothing
            Try
                resp = Await httpclient.PostAsync(url, content)
                resp.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
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
                For Each node As HtmlNode In ndLinks
                    Select Case node.InnerText.Trim
                        Case "Estado acad&eacute;mico"
                            App.urlEstadoAcademico = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                        Case "Exámenes"
                            App.urlExamenes = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                        Case "Cursado / Notas de parciales / Encuestas"
                            App.urlCursado = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                        Case "Correlatividad para cursar"
                            App.urlCorrCursado = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                        Case "Correlatividad para rendir"
                            App.urlCorrRendir = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                        Case "Inscripci&oacute;n a examen"
                            App.urlInscExamen = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                            App.isInscExamenEnabled = True
                        Case "Inscripci&oacute;n a cursado"
                            App.urlInscCursado = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                            App.isInscCursadoEnabled = True
                        Case "Cambio de Contrase&ntilde;a"
                            App.urlCambioPass = New Uri("http://sysacadweb.frre.utn.edu.ar/" + node.Attributes("href").Value)
                    End Select
                Next
                App.ttNombre = nombre
                App.cookies = cookies
                App.isSysacadEnabled = True
                NavigationService.Navigate(New Uri("/MenuPrincipal.xaml", UriKind.Relative))
            Else
                Dim txtError As String = ndError(0).InnerText
                setPG(False)
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
        Else
            setPG(False)
            MessageBox.Show("Hubo un error, seguro que tenés conexión a internet?", "Rayos!", MessageBoxButton.OK)
        End If
    End Sub

    Private Sub btUseOffline_Click(sender As Object, e As RoutedEventArgs) Handles btUseOffline.Click
        App.isSysacadEnabled = False
        App.ttNombre = "Offline"
        NavigationService.Navigate(New Uri("/MenuPrincipal.xaml", UriKind.Relative))
    End Sub
End Class