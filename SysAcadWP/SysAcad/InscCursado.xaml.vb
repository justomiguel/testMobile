Imports HtmlAgilityPack
Imports System.Net.Http
Imports System.Text
Imports Microsoft.VisualBasic



Partial Public Class InscCursado
    Inherits PhoneApplicationPage
    Dim working As Boolean = False

    Public Sub New()
        InitializeComponent()
        listaInscCursado.ItemsSource = App.listaInscCursado
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
        Dim item As App.inscCursado = listaInscCursado.SelectedItem
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
                    MessageBox.Show(text)
                    Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                    If ndError Is Nothing Then
                        Dim ndRadios = htmlpage.DocumentNode.SelectNodes("//input[@name='seleccion']")
                        If ndRadios IsNot Nothing Then
                            MessageBox.Show("Hay " + ndRadios.Count.ToString + "opciones para inscribirte.")
                            Dim opciones As List(Of String) = New List(Of String)
                            Dim ndInsc = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                            Dim inscripcion As String = ndInsc(0).InnerText.Trim
                            Dim principio = inscripcion.IndexOf("(", 0)
                            Dim final = inscripcion.IndexOf("(", principio + 1) - 1
                            opciones.Add(inscripcion.Substring(principio, final - principio))
                            If ndRadios.Count > 1 Then
                                For i = 1 To ndRadios.Count - 1
                                    principio = inscripcion.IndexOf("(", final + 1)
                                    If i = ndRadios.Count - 1 Then
                                        final = inscripcion.Length
                                    Else
                                        final = inscripcion.IndexOf("(", principio + 1)
                                    End If
                                    opciones.Add(inscripcion.Substring(principio, final - principio))
                                Next
                            End If
                            For Each opcion As String In opciones
                                MessageBox.Show(opcion)
                            Next
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
        End If
       
    End Sub
End Class
