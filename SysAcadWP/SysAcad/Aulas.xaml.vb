Imports System.Net.Http
Imports System.Xml.Linq

Partial Public Class Page7
    Inherits PhoneApplicationPage
    Dim working As Boolean = False
    Dim hasFocus As Boolean = False
    Public Sub New()
        InitializeComponent()
        For Each item As App.carrera In App.listaCarreras
            lpCarrera.Items.Add(item.Nombre)
        Next
    End Sub

    Public Async Sub loadMaterias(ByVal sender As Object, e As RoutedEventArgs) Handles btnSetCarrera.Click
        Dim uri As Uri = App.listaCarreras.Item(lpCarrera.SelectedIndex).uri
        If working = False Then
            working = True
            Dim httpclient As New HttpClient
            Dim resp As HttpResponseMessage = Nothing
            Try
                resp = Await httpclient.GetAsync(uri)
                resp.EnsureSuccessStatusCode()
            Catch ex As Exception
                MessageBox.Show("Algo salió mal. Tratá de nuevo.", "Whoa!", MessageBoxButton.OK)
                Exit Sub
            End Try
            Dim res = Await resp.Content.ReadAsStringAsync
            Dim doc As XElement = XElement.Parse(res)
            limpiar(doc)
            Dim materia, aula, div, year As String
            Dim contenido As String()
            Dim templista As List(Of App.aulaMateria) = New List(Of App.aulaMateria)
            For Each fila In doc.Elements("entry")
                materia = fila.<title>.Value
                contenido = fila.<content>.Value.Split(",")
                aula = contenido(0).Trim.Replace("aula: ", "")
                div = contenido(1).Trim.Replace("div: ", "")
                year = contenido(2).Trim.Replace("año: ", "")
                Dim temp As App.aulaMateria = New App.aulaMateria With {.Aula = aula, .Div = div, .Year = year, .Materia = materia, .Visible = System.Windows.Visibility.Collapsed}
                templista.Add(temp)
            Next
            App.listaAulaMateria = templista
            cargarListPickers()
            lpCarrera.IsEnabled = False
            btnSetCarrera.Visibility = System.Windows.Visibility.Collapsed
            spTodo.Visibility = System.Windows.Visibility.Visible
            working = False
        End If
    End Sub

    Sub cargarListPickers()
        Dim listaYear As List(Of String) = New List(Of String)
        Dim listaDiv As List(Of String) = New List(Of String)
        For Each materia As App.aulaMateria In App.listaAulaMateria
            If Not listaYear.Contains(materia.Year) Then
                listaYear.Add(materia.Year)
            End If
            If Not listaDiv.Contains(materia.Div) Then
                listaDiv.Add(materia.Div)
            End If
        Next
        listaYear.Sort()
        listaDiv.Sort()
        lpYear.ItemsSource = listaYear
        lpDiv.ItemsSource = listaDiv

    End Sub

    Sub verAulas(ByVal sender As Object, ByVal e As RoutedEventArgs) Handles btnVerAulas.Click
        Dim isThereAny As Boolean = False
        Dim lista As List(Of App.aulaMateria) = New List(Of App.aulaMateria)
        For Each item As App.aulaMateria In App.listaAulaMateria
            item.Visible = System.Windows.Visibility.Collapsed
        Next
        For Each item As App.aulaMateria In App.listaAulaMateria
            If item.Div = lpDiv.SelectedItem.ToString And item.Year = lpYear.SelectedItem.ToString Then
                item.Visible = System.Windows.Visibility.Visible
                isThereAny = True
            End If
        Next
        If isThereAny Then
            NavigationService.Navigate(New Uri("/VerAulas.xaml", UriKind.RelativeOrAbsolute))
        Else
            MessageBox.Show("No hay ninguna materia con esa combinación!")
        End If
    End Sub

    Sub limpiar(node As XElement)
        If node.Elements.Count > 0 Then
            For Each nodito In node.Elements
                limpiar(nodito)
            Next
        End If
        node.Name = node.Name.ToString.Replace("{http://www.w3.org/2005/Atom}", "")
    End Sub
End Class
