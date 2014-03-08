Partial Public Class Page2
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        lbNombre.Text = App.ttNombre
        llCursado.ItemsSource = App.listaCursado
    End Sub
End Class
