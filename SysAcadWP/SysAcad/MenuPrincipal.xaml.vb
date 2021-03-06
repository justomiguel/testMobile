﻿
Imports System.Net.Http
Imports HtmlAgilityPack
Imports System.Text
Imports Microsoft.Phone.Tasks
Imports Microsoft.VisualBasic
Imports System.Collections.ObjectModel
Imports System.Windows.Media
Imports System.Xml.Linq
Imports System.Net.NetworkInformation

Partial Public Class PivotPage1
    Inherits PhoneApplicationPage
    Dim working As Boolean
    Dim appBarButtonHelp As ApplicationBarIconButton = New ApplicationBarIconButton
    Dim AppBarChangePass As ApplicationBarIconButton = New ApplicationBarIconButton
    Dim http As HttpClient
    Dim response As HttpResponseMessage

    Public Function accentColor() As Media.Color
        Return Application.Current.Resources("PhoneAccentColor")
    End Function


    Public Sub New()
        InitializeComponent()
        If App.isSysacadEnabled = True Then
            'If App.isInscCursadoEnabled = True Then
            '    btInscCursado.Visibility = System.Windows.Visibility.Visible
            'Else
            '    btInscCursado.Visibility = System.Windows.Visibility.Collapsed
            'End If
            If App.isInscExamenEnabled = True Then
                btInscExamen.Visibility = System.Windows.Visibility.Visible
            Else
                btInscExamen.Visibility = System.Windows.Visibility.Collapsed
            End If
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
        http = New HttpClient
        appBarButtonHelp.IconUri = New Uri("/Assets/appbar.home.question.png", UriKind.Relative)
        appBarButtonHelp.Text = "Ayuda"
        ApplicationBar.Buttons.Add(appBarButtonHelp)
        AddHandler appBarButtonHelp.Click, AddressOf appBarButtonHelp_click
        AppBarChangePass.IconUri = New Uri("/Assets/appbar.key.png", UriKind.Relative)
        AppBarChangePass.Text = "Cambiar Contraseña"
        AddHandler AppBarChangePass.Click, AddressOf appBarChangePass_click
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

    Sub limpiar(node As XElement)
        If node.Elements.Count > 0 Then
            For Each nodito In node.Elements
                limpiar(nodito)
            Next
        End If
        node.Name = node.Name.ToString.Replace("{http://www.w3.org/2005/Atom}", "")
    End Sub

    Private Sub setPG(value As Boolean)
        SystemTray.ProgressIndicator.IsVisible = value
        working = value
    End Sub

    Protected Overrides Sub onNavigatedTo(e As NavigationEventArgs)
        While Me.NavigationService.BackStack.Any
            Me.NavigationService.RemoveBackEntry()
        End While
        SystemTray.ProgressIndicator = New ProgressIndicator
        SystemTray.ProgressIndicator.IsIndeterminate = True
    End Sub


    Private Async Sub cargarAulas(ByVal sender As Object, ByVal e As EventArgs) Handles btAulas.Tap
        If NetworkInformation.NetworkInterface.GetIsNetworkAvailable Then
            If working = False Then
                SystemTray.ProgressIndicator.Text = "Cargando listado de carreras..."
                setPG(True)
                Dim content As String
                Dim uri As New Uri("https://spreadsheets.google.com/feeds/worksheets/1iv-H96H2l70ZZ8AS0msdFh_mQ8bxxZo5N-9URPVoFCo/public/basic", UriKind.Absolute)
                Try
                    response = Await http.GetAsync(uri)
                    response.EnsureSuccessStatusCode()
                Catch hre As HttpRequestException
                    MessageBox.Show("Algo salió mal. Tratá de nuevo.", "Whoa!", MessageBoxButton.OK)
                    Exit Sub
                End Try
                content = Await response.Content.ReadAsStringAsync
                Dim doc As XElement = XElement.Parse(content)
                limpiar(doc)
                Dim temp As App.carrera
                App.listaCarreras = New List(Of App.carrera)
                For Each fila In doc.Elements("entry")
                    temp = New App.carrera
                    temp.Nombre = fila.<title>.Value.Trim
                    temp.Color = New Media.SolidColorBrush(Media.Colors.White)
                    Select Case temp.Nombre
                        Case "ISI"
                            temp.NombreLargo = "Ingeniería en Sistemas de Información"
                        Case "IQ"
                            temp.NombreLargo = "Ingeniería Química"
                        Case "IEM"
                            temp.NombreLargo = "Ingeniería Electromecánica"
                        Case "LAR"
                            temp.NombreLargo = "Licenciatura en Administración Rural"
                        Case "TSP"
                            temp.NombreLargo = "Tecnicatura Superior en Programación"
                    End Select
                    For Each link In fila.Elements("link")
                        If link.Attribute("rel").Value = "http://schemas.google.com/spreadsheets/2006#listfeed" Then
                            temp.uri = New Uri(link.Attribute("href").Value, UriKind.RelativeOrAbsolute)
                        End If
                    Next
                    App.listaCarreras.Add(temp)
                Next
                setPG(False)
                NavigationService.Navigate(New Uri("/Aulas.xaml", UriKind.RelativeOrAbsolute))
            End If
        Else
            MessageBox.Show("Algo salió mal. Seguro que tenés internet?", "Whoa!", MessageBoxButton.OK)
        End If
        
    End Sub


    Private Async Sub btEstadoAcademico_Click(sender As Object, e As RoutedEventArgs) Handles btEstadoAcademico.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlEstadoAcademico)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlEstadoAcademico, cookies.GetCookieHeader(App.urlEstadoAcademico))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
            If nderror Is Nothing Then
                'Si entró acá es porque el servidor no devolvió error.
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
                NavigationService.Navigate(New Uri("/EstadoAcad.xaml", UriKind.Relative))
            Else
                Dim txtError As String = nderror(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub

    Private Async Sub btExamenes_Click(sender As Object, e As RoutedEventArgs) Handles btExamenes.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlExamenes)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlExamenes, cookies.GetCookieHeader(App.urlExamenes))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim nderror = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
            If nderror Is Nothing Then
                Dim ndExamenes = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                Dim listaExamenes As New List(Of App.examen)()
                App.promSinAplazo = 0
                App.promConAplazo = 0
                App.aprobadas = 0
                App.todas = 0
                For i = 1 To (ndExamenes.Count - 1)
                    Dim tempExamen As New App.examen
                    tempExamen.Fecha = ndExamenes(i).ChildNodes(0).InnerText.Trim
                    tempExamen.Materia = ndExamenes(i).ChildNodes(1).InnerText.Trim
                    tempExamen.Nota = App.toTitleCase(ndExamenes(i).ChildNodes(2).InnerText.Trim)
                    'Código para los promedios.
                    Select Case tempExamen.Nota.Trim.ToLower
                        Case "uno"
                            App.promConAplazo += 1
                            App.todas += 1
                        Case "dos"
                            App.promConAplazo += 2
                            App.todas += 1
                        Case "tres"
                            App.promConAplazo += 3
                            App.todas += 1
                        Case "cuatro"
                            App.promConAplazo += 4
                            App.promSinAplazo += 4
                            App.aprobadas += 1
                            App.todas += 1
                        Case "cinco"
                            App.promConAplazo += 5
                            App.promSinAplazo += 5
                            App.aprobadas += 1
                            App.todas += 1
                        Case "seis"
                            App.promConAplazo += 6
                            App.promSinAplazo += 6
                            App.aprobadas += 1
                            App.todas += 1
                        Case "siete"
                            App.promConAplazo += 7
                            App.promSinAplazo += 7
                            App.aprobadas += 1
                            App.todas += 1
                        Case "ocho"
                            App.promConAplazo += 8
                            App.promSinAplazo += 8
                            App.aprobadas += 1
                            App.todas += 1
                        Case "nueve"
                            App.promConAplazo += 9
                            App.promSinAplazo += 9
                            App.aprobadas += 1
                            App.todas += 1
                        Case "diez"
                            App.promConAplazo += 10
                            App.promSinAplazo += 10
                            App.aprobadas += 1
                            App.todas += 1
                    End Select
                    tempExamen.Codigo = ndExamenes(i).ChildNodes(5).InnerText.Trim
                    tempExamen.Visible = System.Windows.Visibility.Visible
                    listaExamenes.Add(tempExamen)
                Next
                App.promConAplazo = App.promConAplazo / App.todas
                App.promSinAplazo = App.promSinAplazo / App.aprobadas
                App.listaExamenes = listaExamenes
                NavigationService.Navigate(New Uri("/ExamenesPivot.xaml", UriKind.Relative))
            Else
                Dim txtError As String = nderror(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
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
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlCursado)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
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
                NavigationService.Navigate(New Uri("/Cursado.xaml", UriKind.Relative))
            Else
                Dim txtError As String = nderror(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub

    Private Async Sub btCorrCursado_Click(sender As Object, e As RoutedEventArgs) Handles btCorrCursado.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlCorrCursado)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
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
                        tempCorrCursado.Fore = accentColor().ToString
                    Else
                        tempCorrCursado.Fore = "Red"
                    End If
                    listaCorrCursado.Add(tempCorrCursado)
                Next
                Dim comparer As New cursadoComparer
                App.listaCorrCursado = listaCorrCursado
                NavigationService.Navigate(New Uri("/CorrCursado.xaml", UriKind.Relative))
            Else
                Dim txtError As String = nderror(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub

    Private Async Sub btCorrRendir_Click(sender As Object, e As RoutedEventArgs) Handles btCorrRendir.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlCorrRendir)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
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
                        tempCorrRendir.Fore = accentColor.ToString
                    Else
                        tempCorrRendir.Fore = "Red"
                    End If
                    listaCorrRendir.Add(tempCorrRendir)
                Next
                Dim comparer As New cursadoComparer
                App.listaCorrExamenes = listaCorrRendir
                NavigationService.Navigate(New Uri("/CorrExamenes.xaml", UriKind.Relative))
            Else
                Dim txtError As String = nderror(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub

    Private Async Sub btInscCursado_Click(sender As Object, e As RoutedEventArgs) Handles btInscCursado.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                Dim response = Await http.GetAsync(App.urlInscCursado)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
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
                NavigationService.Navigate(New Uri("/InscCursado.xaml", UriKind.Relative))
            Else
                Dim txtError As String = ndError(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub


    Private Async Sub btInscExamen_Click(sender As Object, e As RoutedEventArgs) Handles btInscExamen.Tap
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Hablando con el servidor de Sysacad..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            http = New HttpClient(handler)
            Try
                response = Await http.GetAsync(App.urlInscExamen)
                response.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await response.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlInscExamen, cookies.GetCookieHeader(App.urlInscExamen))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
            If ndError Is Nothing Then
                Dim ndInscExamen = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
                Dim ndLinks = htmlpage.DocumentNode.SelectNodes("//a")
                Dim listaInscExamen As New List(Of App.inscExamen)
                For i = 1 To (ndInscExamen.Count - 2)
                    Dim tempInscExamen As New App.inscExamen
                    tempInscExamen.Anio = ndInscExamen(i).ChildNodes(0).InnerText.Trim
                    tempInscExamen.Materia = ndInscExamen(i).ChildNodes(1).InnerText.Trim
                    tempInscExamen.Plan = ndInscExamen(i).ChildNodes(3).InnerText.Trim
                    If ndInscExamen(i).ChildNodes(2).InnerText.Trim = "Inscribir" Then
                        tempInscExamen.Estado = "No inscripto"
                        tempInscExamen.Uri = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndInscExamen(i).ChildNodes(2).ChildNodes(0).Attributes("href").Value)
                    Else
                        tempInscExamen.Estado = App.toTitleCase(ndInscExamen(i).ChildNodes(2).InnerText.Trim.Replace("Eliminar", "").ToLower)
                        'If ndInscExamen(i).ChildNodes(2).InnerText.Trim.IndexOf("Eliminar") > 0 Then
                        '    tempInscExamen.Uri = New Uri("http://sysacadweb.frre.utn.edu.ar/" + ndInscExamen(i).ChildNodes(2).ChildNodes(0).Attributes("href").Value)
                        'End If
                    End If
                    tempInscExamen.Codigo = ndInscExamen(i).ChildNodes(4).InnerText.Trim
                    listaInscExamen.Add(tempInscExamen)
                Next
                App.listaInscExamen = listaInscExamen
                NavigationService.Navigate(New Uri("/InscExamen.xaml", UriKind.Relative))
            Else
                Dim txtError As String = ndError(0).InnerText
                MessageBox.Show(txtError, "Que raro!", MessageBoxButton.OK)
            End If
            setPG(False)
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
