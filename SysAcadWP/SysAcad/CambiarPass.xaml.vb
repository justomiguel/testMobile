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
Imports Microsoft.Phone.Tasks
Imports System.Text
Partial Public Class Page4
    Inherits PhoneApplicationPage

    Public working As Boolean

    Public Sub New()
        InitializeComponent()
        working = False
        lbNombre.Text = App.ttNombre
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
    Public Sub setPG(value As Boolean)
        If value = True Then
            SystemTray.ProgressIndicator.IsVisible = True
        Else
            SystemTray.ProgressIndicator.IsVisible = False
        End If
    End Sub
    Public Async Sub changePass()
        If working = False Then
            If tbNewPass.Password = tbNewPassConfirm.Password Then
                working = True
                SystemTray.ProgressIndicator = New ProgressIndicator
                SystemTray.ProgressIndicator.IsIndeterminate = True
                SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                cookies = App.cookies
                handler.CookieContainer = cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                httpclient.DefaultRequestHeaders.Add("referer", App.urlCambioPass.ToString)
                httpclient.DefaultRequestHeaders.Add("origin", "http://sysacadweb.frre.utn.edu.ar")
                Dim url As String = "http://sysacadweb.frre.utn.edu.ar/CambioPassword.asp"
                Dim credenciales As New Dictionary(Of String, String)
                credenciales.Add("passwordActual", tbOldPass.Password.Trim)
                credenciales.Add("password", tbNewPass.Password.Trim)
                credenciales.Add("pruebaPassword", tbNewPassConfirm.Password.Trim)
                credenciales.Add("cambiobutton", "Cambiar")
                Dim content As New FormUrlEncodedContent(credenciales)
                Dim myuri As New Uri(url)
                Try
                    Dim resp = Await httpclient.PostAsync(url, content)
                    resp.EnsureSuccessStatusCode()
                    Dim bytes As Byte() = Await resp.Content.ReadAsByteArrayAsync
                    Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                    Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                    cookies.SetCookies(myuri, cookies.GetCookieHeader(myuri))
                    Dim htmlpage As New HtmlDocument
                    htmlpage.LoadHtml(text)
                    Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                    If ndError Is Nothing Then
                        Dim ndOk = htmlpage.DocumentNode.SelectNodes("//p[@class='textoTabla']")
                        MessageBox.Show(HtmlEntity.DeEntitize(ndOk(0).InnerText).Trim, "Ok!", MessageBoxButton.OK)
                    Else
                        MessageBox.Show(ndError(0).ChildNodes(0).InnerText)
                    End If
                    working = False
                    setPG(False)
                    NavigationService.Navigate(New Uri("/MenuPrincipal.xaml", UriKind.Relative))
                Catch hre As HttpRequestException
                    working = False
                    setPG(False)
                    MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Catch ex As Exception
                    working = False
                    setPG(False)
                    Dim result = MessageBox.Show("Algo salió mal. Te gustaría enviar un reporte de bug?", "Oh noes!", MessageBoxButton.OKCancel)
                    If result = MessageBoxResult.OK Then
                        App.report(ex)
                    End If
                End Try
            Else
                MessageBox.Show("Las contraseñas no coinciden!", "Whoa!", MessageBoxButton.OK)
            End If
        End If
    End Sub
    Private Sub tbOldPass_GotFocus(sender As Object, e As RoutedEventArgs)
        lbOldPass.Visibility = System.Windows.Visibility.Collapsed
    End Sub
    Private Sub tbOldPass_LostFocus(sender As Object, e As RoutedEventArgs)
        If (tbOldPass.Password = "") Then
            lbOldPass.Visibility = System.Windows.Visibility.Visible
        Else
            lbOldPass.Visibility = System.Windows.Visibility.Collapsed
        End If
    End Sub
    Private Sub lbOldPass_Tap(sender As Object, e As RoutedEventArgs)
        lbOldPass.Visibility = System.Windows.Visibility.Collapsed
        tbOldPass.Focus()
    End Sub
    Private Sub tbNewPass_GotFocus(sender As Object, e As RoutedEventArgs)
        lbNewPass.Visibility = System.Windows.Visibility.Collapsed
    End Sub
    Private Sub tbNewPass_LostFocus(sender As Object, e As RoutedEventArgs)
        If (tbNewPass.Password = "") Then
            lbNewPass.Visibility = System.Windows.Visibility.Visible
        Else
            lbNewPass.Visibility = System.Windows.Visibility.Collapsed
        End If
    End Sub
    Private Sub lbNewPass_Tap(sender As Object, e As RoutedEventArgs)
        lbNewPass.Visibility = System.Windows.Visibility.Collapsed
        tbNewPass.Focus()
    End Sub
    Private Sub tbNewPassConfirm_GotFocus(sender As Object, e As RoutedEventArgs)
        lbNewPassConfirm.Visibility = System.Windows.Visibility.Collapsed
    End Sub
    Private Sub tbNewPassConfirm_LostFocus(sender As Object, e As RoutedEventArgs)
        If (tbNewPassConfirm.Password = "") Then
            lbNewPassConfirm.Visibility = System.Windows.Visibility.Visible
        Else
            lbNewPassConfirm.Visibility = System.Windows.Visibility.Collapsed
        End If
    End Sub
    Private Sub lbNewPassConfirm_Tap(sender As Object, e As RoutedEventArgs)
        lbNewPassConfirm.Visibility = System.Windows.Visibility.Collapsed
        tbNewPassConfirm.Focus()
    End Sub

    Private Sub ApplicationBarIconButton_Click(sender As Object, e As EventArgs)
        changePass()
    End Sub
End Class
