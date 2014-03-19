Imports Microsoft.Phone.Tasks

Partial Public Class Page3
    Inherits PhoneApplicationPage

    Public Sub New()
        InitializeComponent()
        Dim appBarSend As ApplicationBarIconButton = New ApplicationBarIconButton
        appBarSend.IconUri = New Uri("/Assets/appbar.email.hardedge.png", UriKind.Relative)
        appBarSend.Text = "Mandar"
        ApplicationBar.Buttons.Add(appBarSend)
        AddHandler appBarSend.Click, AddressOf appBarSend_Click
        lpPara.Items.Add("Unete")
        lpPara.Items.Add("Carlos Pérez (WP8)")
        lpPara.Items.Add("Justo Vargas (Android)")
        lpAsunto.Items.Add("Consulta Académica")
        lpAsunto.Items.Add("Reporte de Bug")
        lpAsunto.Items.Add("Cualquier otra cosa")

    End Sub

    Public Sub appBarSend_Click(ByVal sender As Object, ByVal e As EventArgs)
        Dim emailTask As New EmailComposeTask
        Select Case lpPara.SelectedItem.ToString
            Case "Carlos Pérez (WP8)"
                emailTask.To = "thelinkin3000@gmail.com"
            Case "Justo Vargas (Android)"
                emailTask.To = "justomiguelvargas@gmail.com"
            Case "Unete"
                emailTask.To = ""
        End Select
        Select Case lpAsunto.SelectedItem.ToString
            Case "Consulta Académica"
                emailTask.Subject = "Consulta Académica"
            Case "Reporte de Bug"
                emailTask.Subject = "Reporte de Bug"
            Case "Cualquier otra cosa"
                emailTask.Subject = ""
        End Select
        emailTask.Body = tbMail.Text
        emailTask.Show()
    End Sub


End Class
