Partial Public Class Examenes
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
        ListaLong.ItemsSource = App.listaEstadoAcademico
    End Sub
End Class
