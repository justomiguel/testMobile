Imports HtmlAgilityPack
Imports System.Net.Http
Imports System.Text
Imports Microsoft.VisualBasic

Partial Public Class InscExamen
    Inherits PhoneApplicationPage
    Dim working As Boolean = False

    Public Sub New()
        InitializeComponent()
        llInscExamen.ItemsSource = App.listaInscExamen
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

    Private Sub setPG(value As Boolean)
        If value = True Then
            SystemTray.ProgressIndicator.IsVisible = True
        Else
            SystemTray.ProgressIndicator.IsVisible = False
        End If
    End Sub
    Public Async Sub listaInsCursado_SelectionChanged(ByVal sender As Object, ByVal e As EventArgs)
        Dim item As App.inscExamen = llInscExamen.SelectedItem
        If item.Uri IsNot Nothing Then
            If working = False Then
                working = True
                SystemTray.ProgressIndicator = New ProgressIndicator
                SystemTray.ProgressIndicator.IsIndeterminate = True
                SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
                setPG(True)
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                handler.CookieContainer = App.cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                httpclient.DefaultRequestHeaders.Add("referer", App.urlInscCursado.ToString)
                If item.Estado = "No inscripto" Then
                    'Trata de inscribirte
                    Dim resp = Await httpclient.GetAsync(item.Uri)
                    Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                    Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                    Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                    cookies.SetCookies(item.Uri, cookies.GetCookieHeader(item.Uri))
                    Dim htmlpage As New HtmlDocument
                    htmlpage.LoadHtml(text)
                    'MessageBox.Show(text)
                    Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                    If ndError Is Nothing Then
                        Dim ndRadios = htmlpage.DocumentNode.SelectNodes("//input[@name='seleccion']")
                        If ndRadios IsNot Nothing Then
                            'MessageBox.Show("Hay " + ndRadios.Count.ToString + "opciones para inscribirte.")
                            Dim opciones As String()
                            Dim ndInsc = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                            Dim inscripcion As String = ndInsc(0).InnerText.Trim
                            opciones = inscripcion.Split(" ")
                            Dim j As Integer = 1
                            Dim tempLista As List(Of App.opcInscExamen) = New List(Of App.opcInscExamen)
                            For i = 0 To opciones.Count - 1 Step 6
                                Dim opcion As App.opcInscExamen = New App.opcInscExamen
                                opcion.Fecha = opciones(i)
                                opcion.Tribunal = App.toTitleCase(opciones(i + 2).ToLower) + " " + App.toTitleCase(opciones(i + 3).ToLower)
                                opcion.Turno = App.toTitleCase(opciones(i + 5))
                                opcion.Seleccion = j.ToString
                                j += 1
                                tempLista.Add(opcion)
                            Next
                            App.listaOpcInscExamen = tempLista
                            App.materiaActiva = item
                            App.refererExamen = item.Uri.ToString
                            NavigationService.Navigate(New Uri("/InscribirseExamen.xaml", UriKind.RelativeOrAbsolute))
                        End If
                    Else
                        MessageBox.Show(ndError(0).InnerText.Trim, "Whoa, algo no anduvo bien", MessageBoxButton.OK)
                    End If
                    'MessageBox.Show(text)
                Else
                    Dim result = MessageBox.Show("Seguro que querés eliminar la inscripción?", "Are you sure?", MessageBoxButton.OKCancel)
                    If result = MessageBoxResult.OK Then
                        Dim resp = Await httpclient.GetAsync(item.Uri)
                        Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                        Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                        Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                        cookies.SetCookies(item.Uri, cookies.GetCookieHeader(item.Uri))
                        Dim htmlpage As New HtmlDocument
                        htmlpage.LoadHtml(text)
                        Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                        If ndError Is Nothing Then
                            MessageBox.Show("Desinscripción exitosa!", "Yahoo!", MessageBoxButton.OK)
                        Else
                            MessageBox.Show(ndError(0).InnerText, "Whoa, algo salió mal!", MessageBoxButton.OK)
                        End If
                    Else
                        MessageBox.Show("Ah, bueno.", "Ok...", MessageBoxButton.OK)
                    End If
                End If
            End If
            setPG(False)
            working = False
        Else
            MessageBox.Show("Parece que el Sysacad no habilitó la opción para desinscribirse de este examen!", "Whoops!", MessageBoxButton.OK)
        End If

    End Sub
End Class
