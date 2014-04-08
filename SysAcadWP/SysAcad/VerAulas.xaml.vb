Partial Public Class Page8
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        llAulas.ItemsSource = App.listaAulaMateria
    End Sub
End Class
