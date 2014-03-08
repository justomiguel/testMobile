Imports System.Net.Http
Imports HtmlAgilityPack
Imports System.Text

Partial Public Class MenuPrincipal
    Inherits PhoneApplicationPage

    Dim working As Boolean = False

    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
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
    Private Async Sub btEstadoAcademico_Click(sender As Object, e As RoutedEventArgs) Handles btEstadoAcademico.Click
        If working = Not True Then
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
            Dim resp = Await httpclient.GetAsync(App.urlEstadoAcademico)
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlEstadoAcademico, cookies.GetCookieHeader(App.urlEstadoAcademico))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim ndMaterias = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
            Dim lista As New List(Of App.estAcad)()
            For i = 1 To (ndMaterias.Count - 1)
                Dim tempEstAcad As New App.estAcad
                tempEstAcad.Anio = ndMaterias(i).ChildNodes(0).InnerText.Trim
                tempEstAcad.Materia = ndMaterias(i).ChildNodes(1).InnerText.Trim
                If ndMaterias(i).ChildNodes(1).InnerText.Trim = "&nbsp;" Then
                    Dim estado As String = "No hay datos aún"
                Else
                    tempEstAcad.Estado = ndMaterias(i).ChildNodes(2).InnerText.Trim
                End If
                tempEstAcad.Plan = ndMaterias(i).ChildNodes(3).InnerText.Trim
                lista.Add(tempEstAcad)
            Next
            App.listaEstadoAcademico = lista
            setPG(False)
            working = False
            NavigationService.Navigate(New Uri("/EstadoAcad.xaml", UriKind.Relative))
        End If
        
    End Sub

    Private Async Sub btExamenes_Click(sender As Object, e As RoutedEventArgs) Handles btExamenes.Click
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
            Dim resp = Await httpclient.GetAsync(App.urlExamenes)
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlExamenes, cookies.GetCookieHeader(App.urlExamenes))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim ndExamenes = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
            Dim listaExamenes As New List(Of App.examen)()
            For i = 1 To (ndExamenes.Count - 1)
                Dim tempExamen As New App.examen
                tempExamen.Fecha = ndExamenes(i).ChildNodes(0).InnerText.Trim
                tempExamen.Materia = ndExamenes(i).ChildNodes(1).InnerText.Trim
                tempExamen.Nota = App.toTitleCase(ndExamenes(i).ChildNodes(2).InnerText.Trim)
                tempExamen.Codigo = ndExamenes(i).ChildNodes(5).InnerText.Trim
                listaExamenes.Add(tempExamen)
            Next
            App.listaExamenes = listaExamenes
            setPG(False)
            working = False
            NavigationService.Navigate(New Uri("/Examenes.xaml", UriKind.Relative))
        End If
        
    End Sub


    Private Async Sub btCursado_Click(sender As Object, e As RoutedEventArgs) Handles btCursado.Click
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
            Dim resp = Await httpclient.GetAsync(App.urlCursado)
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlCursado, cookies.GetCookieHeader(App.urlCursado))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
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
        End If
    End Sub

    Private Async Sub btCorrCursado_Click(sender As Object, e As RoutedEventArgs) Handles btCorrCursado.Click
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
            Dim resp = Await httpclient.GetAsync(App.urlCorrCursado)
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlCorrCursado, cookies.GetCookieHeader(App.urlCorrCursado))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim ndCorrCursado = htmlpage.DocumentNode.SelectNodes("//tr[@class='textoTabla']")
            Dim listaCorrCursado As New List(Of App.corr)
            For i = 1 To (ndCorrCursado.Count - 1)
                Dim tempCorrCursado As New App.corr
                tempCorrCursado.Anio = ndCorrCursado(i).ChildNodes(0).InnerText.Trim
                tempCorrCursado.Materia = ndCorrCursado(i).ChildNodes(1).FirstChild.InnerText.Trim
                tempCorrCursado.Corr = ndCorrCursado(i).ChildNodes(2).InnerText.Trim
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
        End If
    End Sub

    Private Async Sub btCorrRendir_Click(sender As Object, e As RoutedEventArgs) Handles btCorrRendir.Click
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
            Dim resp = Await httpclient.GetAsync(App.urlCorrRendir)
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlCorrRendir, cookies.GetCookieHeader(App.urlCorrRendir))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
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
        End If
    End Sub
End Class

Public Class cursadoComparer
    Implements IComparer(Of App.cursado)

    Public Function Compare(x As App.cursado, y As App.cursado) As Integer Implements IComparer(Of App.cursado).Compare
        Return x.Anio.CompareTo(y.Anio)
    End Function
End Class