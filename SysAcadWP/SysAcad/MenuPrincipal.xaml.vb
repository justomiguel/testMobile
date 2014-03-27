
Imports System.Net.Http
Imports HtmlAgilityPack
Imports System.Text
Imports Microsoft.Phone.Tasks
Imports Microsoft.VisualBasic
Imports System.Collections.ObjectModel

Partial Public Class PivotPage1
    Inherits PhoneApplicationPage
    Dim working As Boolean
    Dim appBarButtonHelp As ApplicationBarIconButton = New ApplicationBarIconButton
    Dim AppBarChangePass As ApplicationBarIconButton = New ApplicationBarIconButton

    Public Sub New()
        InitializeComponent()
        If App.isSysacadEnabled = True Then
            'If App.isInscCursadoEnabled = True Then
            '    btInscCursado.Visibility = System.Windows.Visibility.Visible
            'Else
            '    btInscCursado.Visibility = System.Windows.Visibility.Collapsed
            'End If
            'If App.isInscExamenEnabled = True Then
            '    btInscExamen.Visibility = System.Windows.Visibility.Visible
            'Else
            '    btInscExamen.Visibility = System.Windows.Visibility.Collapsed
            'End If
        Else
            btCorrCursado.Visibility = System.Windows.Visibility.Collapsed
            btCorrRendir.Visibility = System.Windows.Visibility.Collapsed
            btCursado.Visibility = System.Windows.Visibility.Collapsed
            btEstadoAcademico.Visibility = System.Windows.Visibility.Collapsed
            btExamenes.Visibility = System.Windows.Visibility.Collapsed
            btInscCursado.Visibility = System.Windows.Visibility.Collapsed
            btInscExamen.Visibility = System.Windows.Visibility.Collapsed
            tbOffline.Visibility = System.Windows.Visibility.Visible
        End If
        appBarButtonHelp.IconUri = New Uri("/Assets/appbar.home.question.png", UriKind.Relative)
        appBarButtonHelp.Text = "Ayuda"
        ApplicationBar.Buttons.Add(appBarButtonHelp)
        AddHandler appBarButtonHelp.Click, AddressOf appBarButtonHelp_click
        appBarChangePass.IconUri = New Uri("/Assets/appbar.key.png", UriKind.Relative)
        AppBarChangePass.Text = "Cambiar Contraseña"
        AddHandler appBarChangePass.Click, AddressOf appBarChangePass_click

    End Sub

    Private Sub setPG(value As Boolean)
        If value = True Then
            SystemTray.ProgressIndicator.IsVisible = True
        Else
            SystemTray.ProgressIndicator.IsVisible = False
        End If
    End Sub

    Protected Overrides Sub onNavigatedTo(e As NavigationEventArgs)
        While Me.NavigationService.BackStack.Any
            Me.NavigationService.RemoveBackEntry()
        End While
    End Sub
    Private Async Sub btEstadoAcademico_Click(sender As Object, e As RoutedEventArgs) Handles btEstadoAcademico.Tap
        If working = Not True Then
            working = True
            SystemTray.ProgressIndicator = New ProgressIndicator
            SystemTray.ProgressIndicator.IsIndeterminate = True
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Try
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                handler.CookieContainer = App.cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                Dim resp = Await httpclient.GetAsync(App.urlEstadoAcademico)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                resp.EnsureSuccessStatusCode()
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlEstadoAcademico, cookies.GetCookieHeader(App.urlEstadoAcademico))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If nderror Is Nothing Then
                    Dim ndMaterias = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim lista As New List(Of App.estAcad)()
                    For i = 1 To (ndMaterias.Count - 1)
                        Dim tempEstAcad As New App.estAcad
                        tempEstAcad.Anio = ndMaterias(i).ChildNodes(0).InnerText.Trim
                        tempEstAcad.Materia = ndMaterias(i).ChildNodes(1).InnerText.Trim
                        tempEstAcad.Estado = HtmlEntity.DeEntitize(ndMaterias(i).ChildNodes(2).InnerText.Trim).Trim
                        If tempEstAcad.Estado = "" Then
                            tempEstAcad.Estado = "No hay datos aún"
                        End If
                        tempEstAcad.Plan = ndMaterias(i).ChildNodes(3).InnerText.Trim
                        lista.Add(tempEstAcad)
                    Next
                    App.listaEstadoAcademico = lista
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/EstadoAcad.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = nderror(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                App.report(ex)
            End Try

        End If

    End Sub

    Private Async Sub btExamenes_Click(sender As Object, e As RoutedEventArgs) Handles btExamenes.Tap
        If working = False Then
            working = True
            SystemTray.ProgressIndicator = New ProgressIndicator
            SystemTray.ProgressIndicator.IsIndeterminate = True
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Try
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                handler.CookieContainer = App.cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                Dim resp = Await httpclient.GetAsync(App.urlExamenes)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                resp.EnsureSuccessStatusCode()
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlExamenes, cookies.GetCookieHeader(App.urlExamenes))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If nderror Is Nothing Then
                    Dim ndExamenes = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim listaExamenes As New List(Of App.examen)()
                    For i = 1 To (ndExamenes.Count - 1)
                        Dim tempExamen As New App.examen
                        tempExamen.Fecha = ndExamenes(i).ChildNodes(0).InnerText.Trim
                        tempExamen.Materia = ndExamenes(i).ChildNodes(1).InnerText.Trim
                        tempExamen.Nota = App.toTitleCase(ndExamenes(i).ChildNodes(2).InnerText.Trim)
                        tempExamen.Codigo = ndExamenes(i).ChildNodes(5).InnerText.Trim
                        tempExamen.Visible = System.Windows.Visibility.Visible
                        listaExamenes.Add(tempExamen)
                    Next
                    App.listaExamenes = listaExamenes
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/Examenes.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = nderror(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If

            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                App.report(ex)
            End Try
        End If

    End Sub

    Protected Overrides Sub onBackKeyPress(e As System.ComponentModel.CancelEventArgs)
        Dim result = MessageBox.Show("Estás seguro que querés salir?", "Seguro?", MessageBoxButton.OKCancel)
        If result = MessageBoxResult.Cancel Then
            e.Cancel = True
        End If
    End Sub

    Private Async Sub btCursado_Click(sender As Object, e As RoutedEventArgs) Handles btCursado.Tap
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
            Try
                Dim resp = Await httpclient.GetAsync(App.urlCursado)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                resp.EnsureSuccessStatusCode()
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlCursado, cookies.GetCookieHeader(App.urlCursado))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If nderror Is Nothing Then
                    Dim ndCursado = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim listaCursado As New List(Of App.cursado)
                    For i = 1 To (ndCursado.Count - 1)
                        Dim tempCursado As New App.cursado
                        tempCursado.Anio = ndCursado(i).ChildNodes(0).InnerText.Trim
                        tempCursado.Materia = ndCursado(i).ChildNodes(1).FirstChild.InnerText.Trim
                        tempCursado.Comision = ndCursado(i).ChildNodes(2).InnerText.Trim
                        tempCursado.Aula = ndCursado(i).ChildNodes(3).InnerText.Trim
                        tempCursado.Horarios = ndCursado(i).ChildNodes(4).InnerText.Trim
                        tempCursado.Notas = ndCursado(i).ChildNodes(5).InnerText.Trim
                        listaCursado.Add(tempCursado)
                    Next
                    Dim comparer As New cursadoComparer
                    listaCursado.Sort(comparer)
                    App.listaCursado = listaCursado
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/Cursado.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = nderror(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch hre As HttpRequestException
                setPG(False)
                working = False
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                App.report(ex)
            End Try

        End If
    End Sub

    Private Async Sub btCorrCursado_Click(sender As Object, e As RoutedEventArgs) Handles btCorrCursado.Tap
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
            Try
                Dim resp = Await httpclient.GetAsync(App.urlCorrCursado)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlCorrCursado, cookies.GetCookieHeader(App.urlCorrCursado))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If nderror Is Nothing Then
                    Dim ndCorrCursado = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim listaCorrCursado As New List(Of App.corr)
                    For i = 1 To (ndCorrCursado.Count - 1)
                        Dim tempCorrCursado As New App.corr
                        tempCorrCursado.Anio = ndCorrCursado(i).ChildNodes(0).InnerText.Trim
                        tempCorrCursado.Materia = ndCorrCursado(i).ChildNodes(1).FirstChild.InnerText.Trim
                        Dim corr As String = HtmlEntity.DeEntitize(ndCorrCursado(i).ChildNodes(2).InnerText).Trim
                        corr.Replace(")", ")\r")
                        tempCorrCursado.Corr = corr
                        If tempCorrCursado.Corr = "Puede cursar" Then
                            tempCorrCursado.Fore = "LightSlateGray"
                        Else
                            tempCorrCursado.Fore = "Red"
                        End If
                        listaCorrCursado.Add(tempCorrCursado)
                    Next
                    Dim comparer As New cursadoComparer
                    App.listaCorrCursado = listaCorrCursado
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/CorrCursado.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = nderror(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch hre As HttpRequestException
                setPG(False)
                working = False
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                App.report(ex)
            End Try
        End If
    End Sub

    Private Async Sub btCorrRendir_Click(sender As Object, e As RoutedEventArgs) Handles btCorrRendir.Tap
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
            Try
                Dim resp = Await httpclient.GetAsync(App.urlCorrRendir)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlCorrRendir, cookies.GetCookieHeader(App.urlCorrRendir))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If nderror Is Nothing Then
                    Dim ndCorrRendir = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim listaCorrRendir As New List(Of App.corr)
                    For i = 1 To (ndCorrRendir.Count - 1)
                        Dim tempCorrRendir As New App.corr
                        tempCorrRendir.Anio = ndCorrRendir(i).ChildNodes(0).InnerText.Trim
                        tempCorrRendir.Materia = ndCorrRendir(i).ChildNodes(1).FirstChild.InnerText.Trim
                        tempCorrRendir.Corr = ndCorrRendir(i).ChildNodes(2).InnerText.Trim
                        If tempCorrRendir.Corr = "Puede inscribirse" Then
                            tempCorrRendir.Fore = "LightSlateGray"
                        Else
                            tempCorrRendir.Fore = "Red"
                        End If
                        listaCorrRendir.Add(tempCorrRendir)
                    Next
                    Dim comparer As New cursadoComparer
                    App.listaCorrExamenes = listaCorrRendir
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/CorrExamenes.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = nderror(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch hre As HttpRequestException
                setPG(False)
                working = False
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                App.report(ex)
            End Try
        End If
    End Sub

    Private Async Sub btInscCursado_Click(sender As Object, e As RoutedEventArgs) Handles btInscCursado.Tap
        If working = False Then
            working = True
            SystemTray.ProgressIndicator = New ProgressIndicator
            SystemTray.ProgressIndicator.IsIndeterminate = True
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Try
                Dim cookies As New CookieContainer
                Dim handler As New HttpClientHandler
                handler.CookieContainer = App.cookies
                handler.UseCookies = True
                Dim httpclient As New HttpClient(handler)
                Dim resp = Await httpclient.GetAsync(App.urlInscCursado)
                Dim bytes = Await resp.Content.ReadAsByteArrayAsync
                Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
                Dim text As String = latin.GetString(bytes, 0, bytes.Length)
                cookies.SetCookies(App.urlInscCursado, cookies.GetCookieHeader(App.urlInscCursado))
                Dim htmlpage As New HtmlDocument
                htmlpage.LoadHtml(text)
                Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
                If ndError Is Nothing Then
                    Dim ndInscExamen = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                    Dim ndLinks = htmlpage.DocumentNode.SelectNodes("//a")
                    Dim listaInscCursado As New List(Of App.inscCursado)
                    For i = 1 To (ndInscExamen.Count - 2)
                        Dim tempInscCursado As New App.inscCursado
                        tempInscCursado.Anio = ndInscExamen(i).ChildNodes(0).InnerText.Trim
                        tempInscCursado.Materia = ndInscExamen(i).ChildNodes(1).InnerText.Trim
                        tempInscCursado.Plan = ndInscExamen(i).ChildNodes(3).InnerText.Trim
                        If ndInscExamen(i).ChildNodes(2).InnerText.Trim = "Inscribir" Then
                            tempInscCursado.Estado = "No inscripto"
                            tempInscCursado.Uri = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndInscExamen(i).ChildNodes(2).ChildNodes(0).Attributes("href").Value)
                        Else
                            tempInscCursado.Estado = ndInscExamen(i).ChildNodes(2).InnerText.Trim.Replace("Eliminar", "")
                            If ndInscExamen(i).ChildNodes(2).InnerText.Trim.IndexOf("Eliminar") > 0 Then
                                tempInscCursado.Uri = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndInscExamen(i).ChildNodes(2).ChildNodes(0).Attributes("href").Value)
                            End If
                        End If
                        tempInscCursado.Codigo = ndInscExamen(i).ChildNodes(4).InnerText.Trim
                        listaInscCursado.Add(tempInscCursado)
                    Next
                    App.listaInscCursado = listaInscCursado
                    setPG(False)
                    working = False
                    NavigationService.Navigate(New Uri("/InscCursado.xaml", UriKind.Relative))
                Else
                    Dim txtError As String = ndError(0).InnerText
                    setPG(False)
                    working = False
                    MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
                End If
            Catch hre As HttpRequestException
                setPG(False)
                working = False
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
            Catch ex As Exception
                setPG(False)
                Dim result = MessageBox.Show("Algo salió mal. Te gustaría enviar un reporte de bug?", "Oh noes!", MessageBoxButton.OKCancel)
                If result = MessageBoxResult.OK Then
                    MessageBox.Show("Te estamos llevando a la pantalla de confección de mails. Vas a ver que ya está el código de error, te agradeceríamos algún comentario al respecto de qué trataste de hacer al encontrarte con el error. Si querés agregar algún pensamiento, por favor, hacelo!")
                    Dim mailTask As New EmailComposeTask
                    mailTask.To = "thelinkin3000@gmail.com"
                    Dim mess As New StringBuilder
                    mess.Append("Hola! Soy un pobre usuario de Ingeniero 2.0 para WP, y como siempre, algo anduvo mal con tu app -_-. El error fue: " + ex.ToString + ".")
                    mess.AppendLine()
                    mess.Append("Adicionalmente, tengo algo para decirte, señor desarrollador de ésta app:")
                    mess.AppendLine()
                    mailTask.Body = mess.ToString
                    mailTask.Subject = "[Reporte de Bug] Ingeniero 2.0 para WP"
                    mailTask.Show()
                End If
            End Try
        End If
    End Sub

    Private Sub Pivot_SelectionChanged(sender As Object, e As SelectionChangedEventArgs)
        If Pivot.SelectedIndex = 0 Then
            Pivot.Title = "Ingeniero 2.0"
            If ApplicationBar.Buttons.Contains(appBarChangePass) Then
                ApplicationBar.Buttons.Remove(AppBarChangePass)
            End If
        Else
            Pivot.Title = App.ttNombre
            If App.isSysacadEnabled = True Then
                If Not ApplicationBar.Buttons.Contains(AppBarChangePass) Then
                    ApplicationBar.Buttons.Add(AppBarChangePass)
                End If
            End If
        End If
    End Sub
    Private Sub appBarChangePass_click(ByVal sender As Object, ByVal e As EventArgs)
        NavigationService.Navigate(New Uri("/CambiarPass.xaml", UriKind.Relative))
    End Sub
    Private Sub appBarButtonHelp_click(ByVal sender As Object, ByVal e As EventArgs)
        NavigationService.Navigate(New Uri("/Consultas.xaml", UriKind.Relative))
    End Sub
    Private Sub btCalendarioAcad_Click(sender As Object, e As RoutedEventArgs) Handles btCalendarioAcad.Tap
        NavigationService.Navigate(New Uri("/CalendarioAcad.xaml", UriKind.Relative))
    End Sub

    Private Sub btHorariosCursado_Click(sender As Object, e As RoutedEventArgs) Handles btHorariosCursado.Tap
        NavigationService.Navigate(New Uri("/HorariosCursado.xaml", UriKind.Relative))
    End Sub
End Class

Public Class cursadoComparer
    Implements IComparer(Of App.cursado)

    Public Function Compare(x As App.cursado, y As App.cursado) As Integer Implements IComparer(Of App.cursado).Compare
        Return x.Anio.CompareTo(y.Anio)
    End Function
End Class
