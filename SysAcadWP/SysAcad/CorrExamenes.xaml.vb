Partial Public Class CorrExamenes
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        llCorr.ItemsSource = App.listaCorrExamenes
        lbNombre.Text = App.ttNombre
    End Sub
End Class
