Partial Public Class Page1
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
        llExamenes.ItemsSource = App.listaExamenes

    End Sub
End Class
