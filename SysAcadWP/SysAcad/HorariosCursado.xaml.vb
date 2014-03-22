Partial Public Class Page6
    Inherits PhoneApplicationPage
    Dim FiveYears As New List(Of String)

    Public Sub New()
        InitializeComponent()
        lpCarrera.Items.Add("ISI")
        lpCarrera.Items.Add("IQ")
        lpCarrera.Items.Add("IEM")
        lpCarrera.Items.Add("TSP")
        lpCarrera.Items.Add("LAR")
        FiveYears.Add("ISI")
        FiveYears.Add("IEM")
        FiveYears.Add("IQ")
    End Sub

    Private Sub lpCarrera_SelectionChanged(sender As Object, e As SelectionChangedEventArgs)
        Dim i As Integer
        If FiveYears.Contains(lpCarrera.SelectedItem.ToString) Then
            i = 5
        ElseIf lpCarrera.SelectedItem.ToString = "LAR" Then
            i = 4
        Else
            i = 2
        End If
        lpAnio.Items.Clear()
        For j = 1 To i
            lpAnio.Items.Add(j.ToString)
        Next
    End Sub

    Private Sub Button_Click(sender As Object, e As RoutedEventArgs)
        App.uriCursado = New Uri("/Assets/Horarios/" + lpCarrera.SelectedItem.ToString + lpAnio.SelectedItem.ToString + ".png", UriKind.RelativeOrAbsolute)
        NavigationService.Navigate(New Uri("/Horario.xaml", UriKind.Relative))
    End Sub
End Class
