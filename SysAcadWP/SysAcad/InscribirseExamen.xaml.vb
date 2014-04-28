Imports System.Net.Http
Imports HtmlAgilityPack
Imports System.Globalization
Imports Microsoft.Phone.Tasks

Partial Public Class Page9
    Inherits PhoneApplicationPage
    Dim working As Boolean

    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
        lbMateria.Text = App.materiaActiva.Materia
        llOpciones.ItemsSource = App.listaOpcInscExamen
    End Sub

    Protected Overrides Sub onNavigatedTo(e As NavigationEventArgs)
        SystemTray.ProgressIndicator = New ProgressIndicator
        SystemTray.ProgressIndicator.IsIndeterminate = True
    End Sub

    Private Sub setPG(value As Boolean)
        SystemTray.ProgressIndicator.IsVisible = value
        working = value
    End Sub

    Private Async Sub inscribirte(seleccion As String, fecha As String)
        If working = False Then
            SystemTray.ProgressIndicator.Text = "Tratando de inscribirte..."
            setPG(True)
            Dim cookies As New CookieContainer
            Dim handler As New HttpClientHandler
            handler.CookieContainer = App.cookies
            handler.UseCookies = True
            Dim httpclient As New HttpClient(handler)
            Dim selec As Dictionary(Of String, String) = New Dictionary(Of String, String)
            selec.Add("plan", App.materiaActiva.Plan)
            selec.Add("materia", App.materiaActiva.Codigo)
            selec.Add("seleccion", seleccion)
            selec.Add("inscribirse", "Inscribirse")
            httpclient.DefaultRequestHeaders.Add("referer", HttpUtility.UrlEncode(App.materiaActiva.Uri.ToString))
            Dim content As New FormUrlEncodedContent(selec)
            Dim resp As HttpResponseMessage = Nothing
            Try
                resp = Await httpclient.PostAsync(New Uri("http://sysacadweb.frre.utn.edu.ar/inscripcionExamen.asp"), content)
                resp.EnsureSuccessStatusCode()
            Catch hre As HttpRequestException
                setPG(False)
                MessageBox.Show("Algo salió mal con la respuesta del servidor. El error es: " + hre.Message, "Oh, noes!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim bytes = Await resp.Content.ReadAsByteArrayAsync
            Dim latin = System.Text.Encoding.GetEncoding("ISO-8859-1")
            Dim text As String = latin.GetString(bytes, 0, bytes.Length)
            cookies.SetCookies(App.urlInscExamen, cookies.GetCookieHeader(App.urlInscExamen))
            Dim htmlpage As New HtmlDocument
            htmlpage.LoadHtml(text)
            Dim ndError = htmlpage.DocumentNode.SelectNodes("//p[@class='textoError']")
            If ndError Is Nothing Then
                MessageBox.Show("Te inscribiste a " + App.materiaActiva.Materia + " el dia " + fecha + ". De todas maneras te recomendamos que te fijes en la página para confirmar.", "Estás en el horno!", MessageBoxButton.OK)
                Dim addAppointment = MessageBox.Show("Te gustaría agregar un recordatorio en tu calendario?", "En una de esas...", MessageBoxButton.OKCancel)
                If addAppointment = MessageBoxResult.OK Then
                    Dim culture As CultureInfo = New CultureInfo("es-AR")
                    Dim dateExam As DateTime = DateTime.Parse(fecha, culture)
                    Dim appointTask As SaveAppointmentTask = New SaveAppointmentTask
                    appointTask.StartTime = dateExam
                    appointTask.Subject = "[Examen Final] " + App.materiaActiva.Materia
                    appointTask.IsAllDayEvent = True
                    appointTask.Reminder = Reminder.OneDay
                    appointTask.Show()
                Else
                    MessageBox.Show("Confiás en tu memoria. Eso me agrada.")
                End If
                NavigationService.Navigate(New Uri("/MenuPrincipal.xaml", UriKind.RelativeOrAbsolute))
            Else
                Dim txtError As String = ndError(0).InnerText
                MessageBox.Show(txtError, "Algo salió mal", MessageBoxButton.OK)
            End If
            setPG(False)
        End If
    End Sub

    Public Sub SelectionChanged() Handles llOpciones.SelectionChanged
        Dim item As App.opcInscExamen = llOpciones.SelectedItem
        Dim result = MessageBox.Show("Estás seguro que querés inscribirte al examen?", "Seguro?", MessageBoxButton.OKCancel)
        If result = MessageBoxResult.OK Then
            inscribirte(item.Seleccion, item.Fecha)
        Else
            MessageBox.Show("Ok, ok, no pasó nada acá, circulen...", "Ahhhh!", MessageBoxButton.OK)
        End If
    End Sub
End Class
