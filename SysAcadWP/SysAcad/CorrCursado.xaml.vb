Partial Public Class CorrCursado
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        llCorr.ItemsSource = App.listaCorrCursado
        lbNombre.Text = App.ttNombre
    End Sub
End Class
